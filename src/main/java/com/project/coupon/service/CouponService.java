package com.project.coupon.service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.project.coupon.entity.*;
import com.project.coupon.exceptions.BadApiRequestException;
import com.project.coupon.exceptions.CouponException;
import com.project.coupon.exceptions.ExpiredCouponException;
import com.project.coupon.repository.*;
import com.project.coupon.request.*;
import com.project.coupon.response.*;
import lombok.extern.log4j.Log4j2;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.coupon.entity.MySQLBaseCouponEntity;
import com.project.coupon.entity.UniqueCouponEntity;
import com.project.coupon.constants.CouponConstant;
import com.project.coupon.entity.CouponMongoEntity;
import com.project.coupon.repository.BaseCouponRepository;
import com.project.coupon.repository.CouponMongoRepository;
import com.project.coupon.repository.CouponRepository;
import com.project.coupon.repository.UniqueCouponRepo;
import com.project.coupon.utility.Encrypted;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;

@Service
@Log4j2
public class CouponService {
	@Autowired
	private CouponRepository couponRepo;
	@Autowired
	private Encrypted encryptService;
	@Autowired
	CouponMongoRepository mongoRepo;
	@Autowired
	BaseCouponRepository baseRepo;
	@Autowired
	BatchCouponRepository batchCouponRepository;
	@Autowired
	UniqueCouponRepository uniqueCouponRepository;
	@Autowired
	UniqueCouponRepo uniqueCouponRepo;
	@Autowired
	CouponRepository mySQLBaseRepo;
	@Autowired
	BatchCouponEntityRepository batchCouponEntityRepo;

	@Autowired
	private MongoTransactionManager transactionManagerMongo;
	@Autowired
	private SegmentDataRepository segmentDataRepository;

	@Autowired
	private SegmentDetailsRepository segmentDetailsRepository;

	@Autowired
	private PlatformTransactionManager transactionManagerMysql;
	@Autowired
	private SegmentDetailsRepositoryEntity segmentDetailsRepositoryMySQL;
	@Autowired
	private SegmentDataRepositoryEntity segmentDataRepositoryMySQL;
	@Autowired
	private ItemsRepository itemsRepository;
	@Autowired
	private ItemsEntityRepository itemsEntityRepository;
	@Autowired
	private ProductExclusionRepository productExclusionRepository;
	@Autowired
	private ProductEntityExclusionRepsoitory productEntityExclusionRepsoitory;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductEntityRepository productEntityRepository;

	@Autowired
	private MilestoneDetailsRepository milestoneDetailsRepository;
	@Autowired
	private SegmentMilestoneMappingRepository segmentMilestoneMappingRepository;
	@Autowired
	private StoreEntityRepository storeEntityRepository;
	@Autowired
	private StoreRepository storeRepository;

	public String createCoupon(CouponRequest request) {
		String encryptedValue = encryptService.encrypt("Hello");
		int numberOfCoupons = request.getNoOfCoupons();
		for (int i = 0; i < numberOfCoupons; i++) {
			MySQLBaseCouponEntity entity = new MySQLBaseCouponEntity();
			//	entity.setEndDate(request.getEndDate());
			//	entity.setStartDate(request.getStartDate());
			entity.setCouponName(UUID.randomUUID().toString().replace("-", "").substring(0, 30));
			entity.setStatus(false);
			couponRepo.save(entity);
		}
		return "success";
	}

	public String createMongoCoupon(CouponRequest request) {
		
		/*BaseCouponEntity baseEntity=new BaseCouponEntity();
		baseEntity.setBasecouponcode(request.getBaseCouponCode());
		baseEntity.setStartDate(request.getStartDate());
		baseEntity.setNoofcopouns(request.getNoOfCoupons());
		baseEntity.setEndDate(request.getEndDate());
		baseRepo.save(baseEntity);*/
		int numberOfCoupons = request.getNoOfCoupons();
		try {
			for (int i = 0; i < numberOfCoupons; i++) {
				CouponMongoEntity entity = new CouponMongoEntity();
				entity.setBaseCouponCode(request.getBaseCouponCode());
				entity.setEndDate(request.getEndDate());
				entity.setStartDate(request.getStartDate());
				entity.setCouponCode(UUID.randomUUID().toString().replace("-", "").substring(0, 30));
				mongoRepo.save(entity);
			}
			return "success";
		} catch (Exception ex) {
			return "Failure";
		}
	}

	public UniqueCouponResponse createUniqueCoupon(UniqueCouponRequest request) throws Exception {

		if (!request.getApplicableForValue().equalsIgnoreCase(CouponConstant.APPLICABLEFOR_USERSEGMENT)) {
			request.setStatus(CouponConstant.ACTIVE);
		}

		log.info("Start creating unique coupon for : {}", request.getCouponName());
		UniqueCouponResponse response = new UniqueCouponResponse();
		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(request.getCouponName());
		Optional<UniqueCoupon> uniqueCouponopt=uniqueCouponRepository.findByIdIgnoreCase(request.getCouponName());

		String errorMessage = null;
		if (uniqueCouponEntity == null && uniqueCouponopt.isEmpty()) {
			MySQLBaseCouponEntity baseCouponEntity = couponRepo.findByCouponName(request.getBaseCouponCode());
			BaseCoupon baseCoupon=baseRepo.findByIdIgnoreCase(request.getBaseCouponCode()).orElseThrow(()-> new BadApiRequestException("Base Coupon with given base_coupon_code does not exist"));
			if (baseCouponEntity != null) {
				uniqueCouponEntity = new UniqueCouponEntity();
				BeanUtils.copyProperties(request, uniqueCouponEntity);
				uniqueCouponEntity.setCreateDate(LocalDateTime.now());
				DefaultTransactionDefinition def = new DefaultTransactionDefinition();
				def.setName("MyTransaction");
				def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
				TransactionStatus statusMysql = transactionManagerMysql.getTransaction(def);
				try {
					uniqueCouponRepo.save(uniqueCouponEntity);
					transactionManagerMysql.commit(statusMysql);
				} catch (Exception e) {
					log.error("Error in saving data in database in Transaction Table");
					transactionManagerMysql.rollback(statusMysql);
					throw new Exception("Error occured in saving data in database");
				}


				UniqueCoupon uniqueCoupon = UniqueCoupon.builder().uniqueCouponCode(request.getCouponName()).baseCouponCode(request.getBaseCouponCode()).startDate(request.getStartDate())
						.endDate(request.getEndDate()).status(request.getStatus()).description(request.getDescription()).displayName(request.getDisplayName()).termsAndConditions(request.getTnc())
						.tncEndDate(request.getTncEndDate()).sequence(request.getSequence()).totalUsage(request.getTotalUsage()).numberOfUniqueUsers(request.getNoOfUniqueUsers()).weightage(request.getSequence())
						.applicableForValue(request.getApplicableForValue()).build();


				DefaultTransactionDefinition defMongo = new DefaultTransactionDefinition();
				TransactionStatus transactionStatus = transactionManagerMongo.getTransaction(defMongo);
				try {
					uniqueCouponRepository.save(uniqueCoupon);
					transactionManagerMongo.commit(transactionStatus);
				} catch (Exception e) {
					log.error("Error in saving data in database in Mongodb");
					transactionManagerMongo.rollback(transactionStatus);
					throw new Exception("Error occured in saving data in database");
				}


				UniqueCouponDetails details = new UniqueCouponDetails();
				BeanUtils.copyProperties(uniqueCouponEntity, details);
				response.setStatus(CouponConstant.SUCCESS);
				log.info("Unique coupun created successfully for : {}", request.getCouponName());
				response.setMessage("Unique Coupon Created Successfully");
				response.setUniqueCouponDetails(details);
				return response;
			} else {
				log.info("Base Coupon {} does not exist", request.getBaseCouponCode());
				errorMessage = "Base Coupon does not exist";
			}

		} else {
			log.info("Coupon {} already exists", request.getCouponName());
			errorMessage = "Coupon already exists";
		}

		throw new CouponException(errorMessage);
	}


	public UniqueCouponResponse updateUniqueCoupon(UniqueCouponRequest request) throws Exception {

		if (!request.getApplicableForValue().equalsIgnoreCase(CouponConstant.APPLICABLEFOR_USERSEGMENT)) {
			request.setStatus(CouponConstant.ACTIVE);
		}
		log.info("Start updating unique coupon for : {}", request.getCouponName());
		UniqueCouponResponse response = new UniqueCouponResponse();
		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(request.getCouponName());
		UniqueCoupon uniqueCoupon=uniqueCouponRepository.findByIdIgnoreCase(request.getCouponName()).orElseThrow(()-> new BadApiRequestException("Coupon with given coupon_name does not exist"));

		String errorMessage = null;
		if (uniqueCouponEntity != null) {
			MySQLBaseCouponEntity baseCouponEntity = couponRepo.findByCouponName(request.getBaseCouponCode());
			BaseCoupon baseCoupon=baseRepo.findByIdIgnoreCase(request.getBaseCouponCode()).orElseThrow(()-> new BadApiRequestException("Base Coupon with given base_coupon_code does not exist"));

			if (baseCouponEntity != null) {
				BeanUtils.copyProperties(request, uniqueCouponEntity);
				uniqueCouponEntity.setModifiedDate(LocalDateTime.now());
				DefaultTransactionDefinition def = new DefaultTransactionDefinition();
				def.setName("MyTransaction");
				def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

				TransactionStatus status = transactionManagerMysql.getTransaction(def);
				try {
					uniqueCouponRepo.save(uniqueCouponEntity);
					transactionManagerMysql.commit(status);
				} catch (Exception e) {
					log.error("Error in saving data in database in Transaction Table");
					transactionManagerMysql.rollback(status);
					throw new Exception("Error occured in saving data in database");
				}

				uniqueCoupon.setStartDate(request.getStartDate());
				uniqueCoupon.setEndDate(request.getEndDate());
				uniqueCoupon.setStatus(request.getStatus());
				uniqueCoupon.setDescription(request.getDescription());
				uniqueCoupon.setDisplayName(request.getDisplayName());
				uniqueCoupon.setTermsAndConditions(request.getTnc());
				uniqueCoupon.setTncEndDate(request.getTncEndDate());
				uniqueCoupon.setSequence(request.getSequence());
				uniqueCoupon.setTotalUsage(request.getTotalUsage());
				uniqueCoupon.setNumberOfUniqueUsers(request.getNoOfUniqueUsers());
				uniqueCoupon.setApplicableForValue(request.getApplicableForValue());

				DefaultTransactionDefinition defMongo = new DefaultTransactionDefinition();
				TransactionStatus transactionStatus = transactionManagerMongo.getTransaction(defMongo);
				try {
					uniqueCouponRepository.save(uniqueCoupon);
					transactionManagerMongo.commit(transactionStatus);
				} catch (Exception e) {
					log.error("Error in saving data in database in Mongodb");
					transactionManagerMongo.rollback(transactionStatus);
					throw new Exception("Error occured in saving data in database");
				}


				UniqueCouponDetails details = new UniqueCouponDetails();
				BeanUtils.copyProperties(uniqueCouponEntity, details);
				response.setStatus(CouponConstant.SUCCESS);
				log.info("Unique coupon updated successfully for : {}", request.getCouponName());
				response.setMessage("unique coupon updated successfully");
				response.setUniqueCouponDetails(details);
				return response;
			} else {
				log.info("Base Coupon {} does not exist", request.getBaseCouponCode());
				errorMessage = "Base Coupon does not exist";
			}

		} else {
			log.info("Coupon {} does not  exist", request.getCouponName());
			errorMessage = "Coupon does not exist";
		}

		throw new CouponException(errorMessage);
	}

	public UniqueCouponResponse getUniqueCoupon(String couonName) {
		log.info("Start fetching unique coupon for : {}", couonName);
		UniqueCouponResponse response = new UniqueCouponResponse();
		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(couonName);
		String errorMessage = null;
		if (uniqueCouponEntity != null) {
			UniqueCouponDetails details = new UniqueCouponDetails();
			BeanUtils.copyProperties(uniqueCouponEntity, details);
			response.setStatus(CouponConstant.SUCCESS);
			log.info("Unique coupon fetched successfully for : {}", couonName);
			response.setMessage("unique coupon fetched successfully");
			response.setUniqueCouponDetails(details);
			return response;
		} else {
			log.info("Coupon {} does not  exist", couonName);
			errorMessage = "Coupon does not exist";
		}
		throw new CouponException(errorMessage);
	}

	public BaseCouponResponse createBaseCoupon(BaseCouponRequest request) throws Exception {
		log.info("Start creating coupon for : {}", request.getCouponName());
		BaseCouponResponse response = new BaseCouponResponse();
		MySQLBaseCouponEntity baseCouponEntity = couponRepo.findByCouponName(request.getCouponName());
		Optional<BaseCoupon> baseCoupondb=baseRepo.findByIdIgnoreCase(request.getCouponName());
		String errorMessage = null;
		if (baseCouponEntity == null && baseCoupondb.isEmpty()) {
			baseCouponEntity = new MySQLBaseCouponEntity();
			BeanUtils.copyProperties(request, baseCouponEntity);
			baseCouponEntity.setCreateDate(LocalDateTime.now());

			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setName("MyTransaction");
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

			TransactionStatus status = transactionManagerMysql.getTransaction(def);
			try {
				couponRepo.save(baseCouponEntity);
				transactionManagerMysql.commit(status);
			} catch (Exception e) {
				log.error("Error in saving data in database in Transaction Table");
				transactionManagerMysql.rollback(status);
				throw new Exception("Error occured in saving data in database");
			}


			BaseCoupon baseCoupon = BaseCoupon.builder().baseCouponCode(request.getBaseCouponId()).couponName(request.getCouponName())
					.description(request.getDescription()).termsAndConditions(request.getTnc()).termsAndConditionsExpiryDate(request.getTncEndDate())
					.sequence(request.getSequence()).status(CouponConstant.ACTIVE).dateCreated(LocalDate.now())
					.build();

			DefaultTransactionDefinition defMongo = new DefaultTransactionDefinition();
			TransactionStatus transactionStatus = transactionManagerMongo.getTransaction(defMongo);
			try {
				baseRepo.save(baseCoupon);
				transactionManagerMongo.commit(transactionStatus);
			} catch (Exception e) {
				log.error("Error in saving data in database in Mongodb");
				transactionManagerMongo.rollback(transactionStatus);
				throw new Exception("Error occured in saving data in database");
			}


			BaseCouponDetails details = new BaseCouponDetails();
			BeanUtils.copyProperties(baseCouponEntity, details);
			response.setStatus(CouponConstant.SUCCESS);
			log.info("Base coupun created successfully for : {}", request.getCouponName());
			response.setMessage("Base Coupon Created Successfully");
			response.setBaseCouponDetails(details);
			return response;
		} else {
			log.info("Coupon {} already exists", request.getCouponName());
			errorMessage = "Coupon already exists";
		}

		throw new CouponException(errorMessage);
	}

	public BaseCouponResponse updateBaseCoupon(String baseCouponName, BaseCouponRequest request) throws Exception {
		log.info("Start updating coupon for : {}", baseCouponName);
		BaseCouponResponse response = new BaseCouponResponse();
		MySQLBaseCouponEntity baseCouponEntity = couponRepo.findByCouponName(baseCouponName);
		log.error("Base Coupon with given id does not exist, coupon_id: "+baseCouponName);
		BaseCoupon baseCoupon=baseRepo.findByIdIgnoreCase(request.getCouponName()).orElseThrow(()-> new BadApiRequestException("Base Coupon with given id does not exists"));

		String errorMessage = null;
		if (baseCouponEntity != null) {

			BeanUtils.copyProperties(request, baseCouponEntity);
			baseCouponEntity.setModifiedDate(LocalDateTime.now());

			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setName("MyTransaction");
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

			TransactionStatus status = transactionManagerMysql.getTransaction(def);
			try {
				couponRepo.save(baseCouponEntity);
				transactionManagerMysql.commit(status);
			} catch (Exception e) {
				log.error("Error in saving data in database in Transaction Table");
				transactionManagerMysql.rollback(status);
				throw new Exception("Error occured in saving data in database");
			}

			baseCoupon.setDescription(request.getDescription());
			baseCoupon.setTermsAndConditions(request.getTnc());
			baseCoupon.setTermsAndConditionsExpiryDate(request.getTncEndDate());
			baseCoupon.setSequence(request.getSequence());

			DefaultTransactionDefinition defMongo = new DefaultTransactionDefinition();
			TransactionStatus transactionStatus = transactionManagerMongo.getTransaction(defMongo);
			try {
				baseRepo.save(baseCoupon);
				transactionManagerMongo.commit(transactionStatus);
			} catch (Exception e) {
				log.error("Error in saving data in database in Mongodb");
				transactionManagerMongo.rollback(transactionStatus);
				throw new Exception("Error occured in saving data in database");
			}


			BaseCouponDetails details = new BaseCouponDetails();
			BeanUtils.copyProperties(baseCouponEntity, details);
			response.setStatus(CouponConstant.SUCCESS);
			log.info("Base coupun updated successfully for : {}", request.getCouponName());
			response.setMessage("Base Coupon updated Successfully");
			response.setBaseCouponDetails(details);
			return response;
		} else {
			log.info("Coupon {} does not exist", request.getCouponName());
			errorMessage = "Coupon does not exist";
		}

		throw new CouponException(errorMessage);
	}

	public AllBaseCouponResponse getAllBaseCoupon() {
		log.info("Start fetching all base coupons");
		AllBaseCouponResponse response = new AllBaseCouponResponse();
		List<MySQLBaseCouponEntity> baseCouponEntitys = couponRepo.findAll();
		List<BaseCouponDetails> baseCouponDetails = new ArrayList<>();
		for (MySQLBaseCouponEntity baseCouponEntity : baseCouponEntitys) {
			BaseCouponDetails details = new BaseCouponDetails();
			BeanUtils.copyProperties(baseCouponEntity, details);
			baseCouponDetails.add(details);
		}
		response.setBaseCouponDetails(baseCouponDetails);
		log.info("All base coupon records fetched successfully");
		response.setStatus(CouponConstant.SUCCESS);
		response.setMessage("All base coupon records fetched successfully");
		return response;

	}

	public BaseCouponResponse getBaseCoupon(String couponName) {
		log.info("Start fetching  base coupon for :{}", couponName);
		BaseCouponResponse response = new BaseCouponResponse();
		MySQLBaseCouponEntity baseCouponEntity = couponRepo.findByCouponName(couponName);
		String errorMessage = null;
		if (baseCouponEntity != null) {
			BaseCouponDetails details = new BaseCouponDetails();
			BeanUtils.copyProperties(baseCouponEntity, details);
			response.setBaseCouponDetails(details);
			log.info("Base coupon data fetched successfully for :{}", couponName);
			response.setStatus(CouponConstant.SUCCESS);
			response.setMessage("Base coupon data fetched successfully ");
			return response;
		} else {
			log.info("Coupon {} does not exist", couponName);
			errorMessage = "Coupon does not exist";
		}
		throw new CouponException(errorMessage);
	}

	public void updateBaseCouponsDateRange(List<String> baseCouponCodes, LocalDate startDate, LocalDate endDate) {
		List<BaseCoupon> baseCoupons = baseRepo.findAllByBaseCouponCodeIn(baseCouponCodes);
		if (baseCoupons.isEmpty()) {
			throw new CouponException("No Base Coupons found with the provided codes.");
		}
		for (BaseCoupon baseCoupon : baseCoupons) {
			baseCoupon.setDateCreated(startDate);
			baseCoupon.setTermsAndConditionsExpiryDate(endDate);
		}
		//Mysql update
		baseRepo.saveAll(baseCoupons);
		List<MySQLBaseCouponEntity> mySQLBaseCoupons = mySQLBaseRepo.findAllByBaseCouponIdIn(baseCouponCodes);
		if (mySQLBaseCoupons.isEmpty()) {
			throw new CouponException("No Base Coupons found with the provided codes.");
		}
		for (MySQLBaseCouponEntity mySQLBaseCoupon : mySQLBaseCoupons) {
			mySQLBaseCoupon.setStartDate(startDate);
			mySQLBaseCoupon.setEndDate(endDate);
		}
		mySQLBaseRepo.saveAll(mySQLBaseCoupons);
	}

	public void updateBatchCouponsDateRange(List<String> batchIds, LocalDate startDate, LocalDate endDate) {
		List<BatchCoupon> batchCoupons = batchCouponRepository.findAllByBatchIdIn(batchIds);
		if (batchCoupons.isEmpty()) {
			throw new CouponException("No Batch Coupons found with the provided IDs.");
		}
		for (BatchCoupon batchCoupon : batchCoupons) {
			batchCoupon.setDateCreated(startDate);
			batchCoupon.setEndDate(endDate);
		}
		batchCouponRepository.saveAll(batchCoupons);
	}

	public void updateUniqueCouponsDateRange(List<String> uniqueCouponCodes, LocalDate startDate, LocalDate endDate) {
		List<UniqueCoupon> uniqueCoupons = uniqueCouponRepository.findAllByUniqueCouponCodeIn(uniqueCouponCodes);
		if (uniqueCoupons.isEmpty()) {
			throw new CouponException("No Unique Coupons found with the provided codes.");
		}
		for (UniqueCoupon uniqueCoupon : uniqueCoupons) {
			uniqueCoupon.setStartDate(startDate);
			uniqueCoupon.setEndDate(endDate);
		}
		uniqueCouponRepository.saveAll(uniqueCoupons);
		// MySQL update
		List<UniqueCouponEntity> mySQLUniqueCoupons = uniqueCouponRepo.findAllByBaseCouponCodeIn(uniqueCouponCodes);
		if (mySQLUniqueCoupons.isEmpty()) {
			throw new CouponException("No Unique Coupons found with the provided codes.");
		}
		for (UniqueCouponEntity mySQLUniqueCoupon : mySQLUniqueCoupons) {
			mySQLUniqueCoupon.setStartDate(startDate);
			mySQLUniqueCoupon.setEndDate(endDate);
		}
		uniqueCouponRepo.saveAll(mySQLUniqueCoupons);
	}

	public void deleteBaseCoupons(List<String> baseCouponCodes) {
		List<BaseCoupon> baseCoupons = baseRepo.findAllByBaseCouponCodeIn(baseCouponCodes);
		if (baseCoupons.isEmpty()) {
			System.out.println("No Base Coupons found for the provided codes: " + baseCouponCodes);
			return;
		}
		for (BaseCoupon baseCoupon : baseCoupons) {
			baseCoupon.setStatus(CouponConstant.DELETED);
		}
		baseRepo.saveAll(baseCoupons);
		// MySQL delete
		List<MySQLBaseCouponEntity> mySQLBaseCoupons = mySQLBaseRepo.findAllByBaseCouponIdIn(baseCouponCodes);
		if (mySQLBaseCoupons.isEmpty()) {
			log.error("No Base Coupons found for the provided codes: " + baseCouponCodes);
			return;
		}
		for (MySQLBaseCouponEntity mySQLBaseCoupon : mySQLBaseCoupons) {
			mySQLBaseCoupon.setStatus(false);
		}
		mySQLBaseRepo.saveAll(mySQLBaseCoupons);
	}

	public void deleteBatchCoupons(List<String> batchIds) {
		List<BatchCoupon> batchCoupons = batchCouponRepository.findAllByBatchIdIn(batchIds);
		for (BatchCoupon batchCoupon : batchCoupons) {
			batchCoupon.setStatus(CouponConstant.DELETED);
		}
		batchCouponRepository.saveAll(batchCoupons);
	}

	public void deleteUniqueCoupons(List<String> uniqueCouponCodes) {
		List<UniqueCoupon> uniqueCoupons = uniqueCouponRepository.findAllByUniqueCouponCodeIn(uniqueCouponCodes);
		for (UniqueCoupon uniqueCoupon : uniqueCoupons) {
			uniqueCoupon.setStatus(CouponConstant.DELETED);
		}
		uniqueCouponRepository.saveAll(uniqueCoupons);
		//Mysql Update
		List<UniqueCouponEntity> mySQLUniqueCoupons = uniqueCouponRepo.findAllByBaseCouponCodeIn(uniqueCouponCodes);
		if (mySQLUniqueCoupons.isEmpty()) {
			System.out.println("No Unique Coupons found for the provided codes: " + uniqueCouponCodes);
			return;
		}
		for (UniqueCouponEntity mySQLUniqueCoupon : mySQLUniqueCoupons) {
			mySQLUniqueCoupon.setStatus(CouponConstant.DELETED);
		}
		uniqueCouponRepo.saveAll(mySQLUniqueCoupons);
	}

	public UserSpecificConstraintsResponse createUserSpecificConstraints(UserSpecificConstraintsRequest request) throws Exception {
		log.info("Start creating user-specific constraints for the coupon");
		UserSpecificConstraintsResponse response = new UserSpecificConstraintsResponse();

		// Fetch the unique coupon entity by some identifier, e.g., couponName
		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(request.getCouponName());
		// Save the entity in MongoDB
		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(request.getCouponName()).orElseThrow(() -> new BadApiRequestException("Coupon with given id does not exist"));

		if (uniqueCouponEntity == null) {
			throw new CouponException("Coupon not found");
		}

//		if (request.isEnableConstraints()) {
//			if (CouponConstant.CONSTRAINT_TYPE_DURATION.equals(request.getConstraintType())) {
//				uniqueCouponEntity.setFirstExpiryDate(LocalDate.now().plusDays(request.getNumberOfDays()));
//				uniqueCouponEntity.setExtendedExpiryDate(LocalDate.now().plusDays(request.getExtendedNumberOfDays()));
//			} else if (CouponConstant.CONSTRAINT_TYPE_EXPIRATION_DATE.equals(request.getConstraintType())) {
//				uniqueCouponEntity.setFirstExpiryDate(request.getFirstExpiryDate());
//				uniqueCouponEntity.setExtendedExpiryDate(request.getExtendedExpiryDate());
//			}
//
//			uniqueCouponEntity.setNumberOfTimesApplicablePerUser(request.getNumberOfTimesApplicablePerUser());
//		} else {
//			uniqueCouponEntity.setFirstExpiryDate(null);
//			uniqueCouponEntity.setExtendedExpiryDate(null);
//			uniqueCouponEntity.setNumberOfTimesApplicablePerUser(null);
//		}
		uniqueCouponEntity.setUserSpecificConstraints(request.isEnableConstraints());
		uniqueCouponEntity.setConstraintType(request.getConstraintType());
		uniqueCouponEntity.setNumberOfDays(request.getNumberOfDays());
		uniqueCouponEntity.setExtendedNumberOfDays(request.getExtendedNumberOfDays());
		uniqueCouponEntity.setFirstExpiryDate(request.getFirstExpiryDate());
		uniqueCouponEntity.setExtendedExpiryDate(request.getExtendedExpiryDate());
		uniqueCouponEntity.setNumberOfTimesApplicablePerUser(request.getNumberOfTimesApplicablePerUser());


		// Save the entity in MySQL
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("MyTransaction");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus statusMysql = transactionManagerMysql.getTransaction(def);
		try {
			uniqueCouponRepo.save(uniqueCouponEntity);
			transactionManagerMysql.commit(statusMysql);
		} catch (Exception e) {
			log.error("Error in saving data in MySQL database");
			transactionManagerMysql.rollback(statusMysql);
			throw new Exception("Error occurred in saving data in MySQL database");
		}

		uniqueCoupon.setUserSpecificConstraints(request.isEnableConstraints());
		uniqueCoupon.setConstraintType(request.getConstraintType());
		uniqueCoupon.setNumberOfDays(request.getNumberOfDays());
		uniqueCoupon.setExtendedNumberOfDays(request.getExtendedNumberOfDays());
		uniqueCoupon.setFirstExpiryDate(request.getFirstExpiryDate());
		uniqueCoupon.setExtendedExpiryDate(request.getExtendedExpiryDate());
		uniqueCoupon.setNumberOfTimesApplicablePerUser(request.getNumberOfTimesApplicablePerUser());

		DefaultTransactionDefinition defMongo = new DefaultTransactionDefinition();
		TransactionStatus transactionStatus = transactionManagerMongo.getTransaction(defMongo);
		try {
			uniqueCouponRepository.save(uniqueCoupon);
			transactionManagerMongo.commit(transactionStatus);
		} catch (Exception e) {
			log.error("Error in saving data in MongoDB");
			transactionManagerMongo.rollback(transactionStatus);
			throw new Exception("Error occurred in saving data in MongoDB");
		}

		response.setStatus(CouponConstant.SUCCESS);
		log.info("User-specific constraints created successfully for the coupon");
		response.setMessage("User-specific constraints created successfully");

		return response;
	}

	public CouponApplicabilityResponse createCouponApplicability(CouponApplicabilityRequest request) throws Exception {
		log.info("Start applying coupon applicability");
		CouponApplicabilityResponse response = new CouponApplicabilityResponse();

		// Check if the coupon exists in MySQL
		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(request.getCouponName());
		if (uniqueCouponEntity == null) {
			throw new BadApiRequestException("Coupon not found in MySQL.");
		}

		// Check if the coupon exists in MongoDB
		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(request.getCouponName()).orElseThrow(() -> new BadApiRequestException("Coupon with given does not exists"));


		uniqueCouponEntity.setChannel(request.getChannel());
		uniqueCouponEntity.setChannelFulfillmentType(request.getChannelFulfillmentType());
		uniqueCouponEntity.setFranchise(request.getFranchise());
		uniqueCouponEntity.setStores(request.getStores());
		uniqueCouponEntity.setCities(request.getCities());
		uniqueCouponEntity.setClusters(request.getClusters());
		uniqueCouponEntity.setDayApplicability(request.getDayApplicability());
		uniqueCouponEntity.setMonthApplicability(request.getMonthApplicability());
		uniqueCouponEntity.setTimeslot(request.getTimeslot());
		uniqueCouponEntity.setIshiddenUI(request.isHiddenUI());


		uniqueCoupon.setChannel(request.getChannel());
		uniqueCoupon.setChannelFullfillmentType(request.getChannelFulfillmentType());
		uniqueCoupon.setFranchise(request.getFranchise());
		uniqueCoupon.setStores(request.getStores());
		uniqueCoupon.setCities(request.getCities());
		uniqueCoupon.setClusters(request.getClusters());
		uniqueCoupon.setDayApplicability(request.getDayApplicability());
		uniqueCoupon.setMonthApplicability(request.getMonthApplicability());
		uniqueCoupon.setTimeslot(request.getTimeslot());
		uniqueCoupon.setHiddenUI(request.isHiddenUI());


		// Start MySQL transaction
		DefaultTransactionDefinition defMysql = new DefaultTransactionDefinition();
		defMysql.setName("MyTransaction");
		defMysql.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus statusMysql = transactionManagerMysql.getTransaction(defMysql);

		try {
			// Update coupon applicability in MySQL
//
			uniqueCouponRepo.save(uniqueCouponEntity);
			transactionManagerMysql.commit(statusMysql);
		} catch (Exception e) {
			log.error("Error in saving data in MySQL database", e);
			transactionManagerMysql.rollback(statusMysql);
			throw new Exception("Error occurred in saving data in MySQL database", e);
		}

		// Start MongoDB transaction
		DefaultTransactionDefinition defMongo = new DefaultTransactionDefinition();
		defMongo.setName("MyTransaction");
		defMongo.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus statusMongo = transactionManagerMongo.getTransaction(defMongo);

		try {
			// Update coupon applicability in MongoDB


			uniqueCouponRepository.save(uniqueCoupon);
			transactionManagerMongo.commit(statusMongo);
		} catch (Exception e) {
			log.error("Error in saving data in MongoDB", e);
			transactionManagerMongo.rollback(statusMongo);
			throw new Exception("Error occurred in saving data in MongoDB", e);
		}

		response.setStatus(CouponConstant.SUCCESS);
		response.setMessage("Coupon applicability saved successfully.");
		log.info("Coupon applicability saved successfully");

		return response;
	}


	public CouponConstructResponse createCouponConstruct(CouponConstructRequest request) throws Exception {
		CouponConstructResponse response = new CouponConstructResponse();

		// Check if the coupon ID exists in MySQL
		UniqueCouponEntity uniqueCouponEntity1 = uniqueCouponRepo.findByCouponName(request.getCouponName());
		if (uniqueCouponEntity1 == null) {
			throw new CouponException("Coupon not found in MySQL.");
		}

		// Check if the coupon ID exists in MongoDB

		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(request.getCouponName()).orElseThrow(() -> new BadApiRequestException("Given Coupon is not found in mongodb"));
		;
		if (uniqueCoupon == null) {
			throw new CouponException("Coupon not found in MongoDB.");
		}


		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(request.getCouponName()).orElseThrow(() -> new BadApiRequestException("Coupon with given id does not exist"));

		try {
			uniqueCouponEntity1.setCouponType(request.getConstructType());
			//uniqueCouponEntity1.setFreebieItems(request.getFreebieItemMySQL());
			uniqueCouponEntity1.setFreebieItems(mapToMySQLItems(request.getFreebieItems()));
			uniqueCouponEntity1.setDiscountPercentage(request.getDiscountPercentage());
			uniqueCouponEntity1.setFlatDiscount(request.getFlatDiscount());
			uniqueCouponEntity1.setDiscountCap(request.getDiscountCap());
			uniqueCouponEntity1.setMinimumOrderValue(request.getMinimumOrderValue());
			uniqueCouponEntity1.setProductInclusion(request.getProductInclusion());
			uniqueCouponEntity1.setProductExclusion(request.getProductExclusion());
			uniqueCouponEntity1.setCategoryExclusion(request.getCategoryExclusion());
			uniqueCouponEntity1.setCategoryInclusion(request.getCategoryInclusion());

			// Start MySQL transaction
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setName("MyTransaction");
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			TransactionStatus statusMysql = transactionManagerMysql.getTransaction(def);

			try {
				// Save the entity in MySQL
				uniqueCouponRepo.save(uniqueCouponEntity1);
				transactionManagerMysql.commit(statusMysql);
			} catch (Exception e) {
				transactionManagerMysql.rollback(statusMysql);
				throw new Exception("Error occurred in saving data in MySQL", e);
			}
			log.debug("Starting MongoDB transaction for coupon creation.");

			uniqueCoupon.setCouponType(request.getConstructType());
			uniqueCoupon.setFreebieItems(mapToMongoItems(request.getFreebieItems()));
			uniqueCoupon.setDiscountPercentage(request.getDiscountPercentage());
			uniqueCoupon.setFlatDiscount(request.getFlatDiscount());
			uniqueCoupon.setDiscountCap(request.getDiscountCap());
			uniqueCoupon.setMov(request.getMinimumOrderValue());
			uniqueCoupon.setProductInclusion(request.getProductInclusion());
			uniqueCoupon.setProductExclusion(request.getProductExclusion());
			uniqueCoupon.setCategoryExclusion(request.getCategoryExclusion());
			uniqueCoupon.setCategoryInclusion(request.getCategoryInclusion());

			// Start MongoDB transaction
			DefaultTransactionDefinition defMongo = new DefaultTransactionDefinition();
			TransactionStatus statusMongo = transactionManagerMongo.getTransaction(defMongo);

			try {
				log.info("Saving uniqueCoupon: {}", uniqueCoupon);
				// Save the document in MongoDB
				uniqueCouponRepository.save(uniqueCoupon);
				transactionManagerMongo.commit(statusMongo);
				log.debug("MongoDB transaction committed successfully.");
			} catch (Exception e) {
				log.error("Failed to save data in MongoDB", e);
				transactionManagerMongo.rollback(statusMongo);
				throw new Exception("Error occurred in saving data in MongoDB", e);
			}
			CouponConstructDetails details = CouponConstructDetails.builder()
					.couponName(uniqueCouponEntity1.getCouponName())
					.constructType(uniqueCouponEntity1.getCouponType())
					.freebieItems(mapToItemResponse(request.getFreebieItems())) // Convert to ItemResponse
					.discountPercentage(uniqueCouponEntity1.getDiscountPercentage())
					.flatDiscount(uniqueCouponEntity1.getFlatDiscount())
					.discountCap(uniqueCouponEntity1.getDiscountCap())
					.minimumOrderValue(uniqueCouponEntity1.getMinimumOrderValue())
					.productInclusion(uniqueCouponEntity1.getProductInclusion())
					.productExclusion(uniqueCouponEntity1.getProductExclusion())
					.categoryInclusion(uniqueCouponEntity1.getCategoryInclusion())
					.categoryExclusion(uniqueCouponEntity1.getCategoryExclusion())
					.createdDate(LocalDateTime.now())
					.modifiedDate(null)
					.build();

			response.setStatus(CouponConstant.SUCCESS);
			response.setMessage("Coupon construct created successfully.");
			response.setCouponConstructDetails(details);
		} catch (Exception e) {
			response.setStatus(CouponConstant.FAILURE);
			response.setMessage("Error creating coupon construct: " + e.getMessage());
			throw e;
		}
		return response;
	}


	private List<ItemsEntity> mapToMySQLItems(List<ItemRequest> items) {
		return items.stream().map(item -> ItemsEntity.builder()
						.itemCode(item.getItemCode())
						.itemPrice(item.getItemPrice())
						.itemQuantity(item.getItemQuantity())
						.itemBase(item.getItemBase())
						.itemSize(item.getItemSize())
						.build())
				.collect(Collectors.toList());
	}

	private List<Items> mapToMongoItems(List<ItemRequest> items) {
		return items.stream().map(item -> Items.builder()
						.id(UUID.randomUUID().toString()) // Ensure ID is generated here
						.itemCode(item.getItemCode())
						.itemPrice(item.getItemPrice())
						.itemQuantity(item.getItemQuantity())
						.itemBase(item.getItemBase())
						.itemSize(item.getItemSize())
						.build())
				.collect(Collectors.toList());
	}

	private List<ItemResponse> mapToItemResponse(List<ItemRequest> items) {
		return items.stream().map(item -> ItemResponse.builder()
						.itemCode(item.getItemCode())
						.itemPrice(item.getItemPrice())
						.itemQuantity(item.getItemQuantity())
						.itemBase(item.getItemBase())
						.itemSize(item.getItemSize())
						.build())
				.collect(Collectors.toList());
	}


	public BaseResponse uploadSegmentforApplicableFor(MultipartFile file, String couponCode) throws Exception {

		String fileName = file.getOriginalFilename();
		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		String campaignName = "";
		List<SegmentData> segmentDataList = new ArrayList<>();
		List<SegmentDataEntity> segmentDataListMySQL = new ArrayList<>();

		SegmentDetails segmentDetails = new SegmentDetails();
		SegmentDetailsEntity segmentDetailsMySQL = new SegmentDetailsEntity();

		boolean check = false;
		for (Row row : sheet) {

			// Skip header row
			if (row.getRowNum() == 0) {
				continue;
			}
			SegmentDataEntity segmentDataMySQL = new SegmentDataEntity();
			SegmentData segmentData = new SegmentData();
			String id = UUID.randomUUID().toString();
			segmentData.setId(id);
			if (row.getCell(0) != null) {
				segmentData.setMobileNumber(String.valueOf(row.getCell(0)));
				segmentDataMySQL.setMobileNumber(String.valueOf(row.getCell(0)));
			} else {
				throw new BadApiRequestException("Number field is not valid");
			}

			if (!check) {
				if (row.getCell(1) != null) {
					campaignName = String.valueOf(row.getCell(1));
					String id2 = UUID.randomUUID().toString();
					segmentDetails.setId(id2);
					segmentDetails.setSegmentName(fileName + "_" + campaignName);
					segmentDetailsMySQL.setSegmentName(fileName + "_" + campaignName);
					check = true;
				} else {
					throw new BadApiRequestException("Campaign field is not valid");
				}
			}

			segmentData.setSegmentAttached(segmentDetails);
			segmentData.setSegmentAttachedDate(LocalDate.now());
			segmentDataMySQL.setSegmentAttached(segmentDetailsMySQL);
			segmentDataMySQL.setSegmentAttachedDate(LocalDate.now());
			segmentDataList.add(segmentData);
			segmentDataListMySQL.add(segmentDataMySQL);
		}
		if (segmentDetailsRepositoryMySQL.existsBySegmentNameIgnoreCase(segmentDetailsMySQL.getSegmentName())) {
			throw new BadApiRequestException("Segment with given name already exists");
		}

		if (segmentDetailsRepository.existsBySegmentNameIgnoreCase(segmentDetails.getSegmentName())) {
			throw new BadApiRequestException("Segment with given name already exists");
		}
		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(couponCode);
		if (uniqueCouponEntity == null) {
			throw new BadApiRequestException("Coupon with given id not found in MySQL");
		}
		uniqueCouponEntity.setApplicableForSegments(segmentDetailsMySQL);
		uniqueCouponEntity.setApplicableForValue(CouponConstant.APPLICABLEFOR_USERSEGMENT);
		uniqueCouponEntity.setStatus(CouponConstant.ACTIVE);
		uniqueCouponEntity.setUserAttachedDate(LocalDate.now());

		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(couponCode).orElseThrow(() -> new BadApiRequestException("Coupon with given id not found"));
		uniqueCoupon.setApplicableForSegments(segmentDetails);
		uniqueCoupon.setApplicableForValue(CouponConstant.APPLICABLEFOR_USERSEGMENT);
		uniqueCoupon.setStatus(CouponConstant.ACTIVE);
		uniqueCoupon.setUserAttachedDate(LocalDate.now());

		DefaultTransactionDefinition defMySQL = new DefaultTransactionDefinition();
		TransactionStatus transactionStatusMySQL = transactionManagerMysql.getTransaction(defMySQL);

		DefaultTransactionDefinition defMongo = new DefaultTransactionDefinition();
		TransactionStatus transactionStatus = transactionManagerMongo.getTransaction(defMongo);

		try {
			segmentDetailsRepositoryMySQL.save(segmentDetailsMySQL);
			segmentDataRepositoryMySQL.saveAll(segmentDataListMySQL);
			uniqueCouponRepo.save(uniqueCouponEntity);
			transactionManagerMysql.commit(transactionStatusMySQL);

			segmentDetailsRepository.save(segmentDetails);
			segmentDataRepository.saveAll(segmentDataList);
			uniqueCouponRepository.save(uniqueCoupon);
			transactionManagerMongo.commit(transactionStatus);
		} catch (Exception e) {
			log.error("Error in saving data in database in Mongodb");
			transactionManagerMongo.rollback(transactionStatus);

//			transactionManagerMysql.rollback(transactionStatusMySQL);
			throw new Exception("Error occured in saving data in database");
		}
		return BaseResponse.builder().status(CouponConstant.SUCCESS).message("Segment uploaded successfully").build();
	}

	public BaseResponse uploadSegmentForBatchCoupons(MultipartFile file, String couponCode, String uniquePrefix, String uniquePostfix) throws Exception {

		String fileName = file.getOriginalFilename();
		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		String campaignName = "";

		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(couponCode).orElseThrow(() -> new BadApiRequestException("Unique coupon with given code does not exists"));
		uniqueCoupon.setStatus(CouponConstant.ACTIVE);
		uniqueCoupon.setUserAttachedDate(LocalDate.now());

		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(couponCode);
		if (uniqueCouponEntity == null) {
			throw new BadApiRequestException("Coupon with given id not found in MySQL");
		}
		uniqueCouponEntity.setStatus(CouponConstant.ACTIVE);
		uniqueCouponEntity.setUserAttachedDate(LocalDate.now());

		LocalDate endDate = null;

		if (uniqueCoupon.getFirstExpiryDate() != null) {
			endDate = uniqueCoupon.getFirstExpiryDate();
		}

		if (uniqueCoupon.getExtendedExpiryDate() != null) {
			endDate = uniqueCoupon.getExtendedExpiryDate();
		}

		if (uniqueCoupon.getNumberOfDays() != 0) {
			endDate = LocalDate.now().plusDays(uniqueCoupon.getNumberOfDays());
		}

		if (uniqueCoupon.getExtendedNumberOfDays() != 0) {
			endDate = LocalDate.now().plusDays(uniqueCoupon.getNumberOfDays() + uniqueCoupon.getExtendedNumberOfDays());
		}

		LocalDate endDateMySQL = null;

		if (uniqueCouponEntity.getFirstExpiryDate() != null) {
			endDateMySQL = uniqueCouponEntity.getFirstExpiryDate();
		}

		if (uniqueCouponEntity.getExtendedExpiryDate() != null) {
			endDateMySQL = uniqueCouponEntity.getExtendedExpiryDate();
		}

		if (uniqueCouponEntity.getNumberOfDays() != null && uniqueCouponEntity.getNumberOfDays() != 0) {
			endDateMySQL = LocalDate.now().plusDays(uniqueCouponEntity.getNumberOfDays());
		}

		if (uniqueCouponEntity.getExtendedNumberOfDays() != null && uniqueCouponEntity.getExtendedNumberOfDays() != 0) {
			endDateMySQL = LocalDate.now().plusDays(uniqueCouponEntity.getNumberOfDays() + uniqueCouponEntity.getExtendedNumberOfDays());
		}
		List<BatchCoupon> batchCouponList = new ArrayList<>();
		List<BatchCouponEntity> batchCouponListMySQL = new ArrayList<>();

		boolean check = false;

		long i = uniqueCoupon.getBatchCouponLastIndex();
		for (Row row : sheet) {


			// Skip header row
			if (row.getRowNum() == 0) {
				continue;
			}
			i++;
			String batchId = uniquePrefix + couponCode + uniquePostfix + "-" + i;

			BatchCoupon batchCoupon = BatchCoupon.builder().batchId(batchId).uniqueCouponId(couponCode).status(CouponConstant.ACTIVE)
					.dateCreated(LocalDate.now()).endDate(endDate).build();
			BatchCouponEntity batchCouponMySQL = new BatchCouponEntity();
			batchCouponMySQL.setBatchId(batchId);
			batchCouponMySQL.setUniqueCouponId(couponCode);
			batchCouponMySQL.setStatus(CouponConstant.ACTIVE);
			batchCouponMySQL.setDateCreated(LocalDate.now());
			batchCouponMySQL.setEndDate(endDateMySQL);


			if (row.getCell(0) != null) {
				batchCoupon.setMobileNumber(String.valueOf(row.getCell(0)));
				batchCouponMySQL.setMobileNumber(String.valueOf(row.getCell(0)));
			} else {
				throw new BadApiRequestException("Number field is not valid");
			}
			if (!check) {
				if (row.getCell(1) != null) {
					campaignName = String.valueOf(row.getCell(1));
					check = true;
				} else {
					throw new BadApiRequestException("Campaign field is not valid");
				}
			}
			String lotName = fileName + "_" + campaignName;
			uniqueCoupon.setLotName(lotName);
			batchCoupon.setLotName(lotName);
			batchCouponList.add(batchCoupon);
			batchCouponMySQL.setLotName(lotName);
			batchCouponListMySQL.add(batchCouponMySQL);
			uniqueCouponEntity.setLotName(lotName);
		}
		uniqueCoupon.setBatchCouponLastIndex(i + 1);

		DefaultTransactionDefinition defMongo = new DefaultTransactionDefinition();
		TransactionStatus transactionStatus = transactionManagerMongo.getTransaction(defMongo);

		DefaultTransactionDefinition defMySQL = new DefaultTransactionDefinition();
		TransactionStatus transactionStatusMySQL = transactionManagerMysql.getTransaction(defMySQL);


		try {
			uniqueCouponRepository.save(uniqueCoupon);
			batchCouponRepository.saveAll(batchCouponList);
			transactionManagerMongo.commit(transactionStatus);

			uniqueCouponRepo.save(uniqueCouponEntity);
			batchCouponEntityRepo.saveAll(batchCouponListMySQL);
			transactionManagerMysql.commit(transactionStatusMySQL);
		} catch (Exception e) {
			log.error("Error in saving data in database in Mongodb");
			transactionManagerMongo.rollback(transactionStatus);
			transactionManagerMysql.rollback(transactionStatusMySQL);
			throw new Exception("Error occured in saving data in database");
		}

		return BaseResponse.builder().status(CouponConstant.SUCCESS).message("BatchCoupon with uploaded segment created successfully").build();
	}


	public BaseResponse handleCouponTypeSelection(String couponType, String couponName) {
		// Check if the coupon exists in MySQL
		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(couponName);
		if (uniqueCouponEntity == null) {
			return BaseResponse.builder().status(CouponConstant.FAILURE).message("Coupon not found in MySQL.").build();
		}

		// Check if the coupon exists in MongoDB
		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(couponName).orElseThrow(() -> new BadApiRequestException("Coupon with given unique coupon code does not exist"));

		String typeCoupon = "";

		// Handle coupon creation or updating based on type
		if (CouponConstant.COUPONUSAGE_MULTIUSE.equalsIgnoreCase(couponType)) {
			// Update and save as multi-use coupon
			typeCoupon = CouponConstant.COUPONUSAGE_MULTIUSE;

			// Set the coupon type
			uniqueCouponEntity.setCouponType(CouponConstant.COUPONUSAGE_MULTIUSE);
			uniqueCoupon.setCouponUsage(CouponConstant.COUPONUSAGE_MULTIUSE);

		} else if (CouponConstant.COUPONUSAGE_SINGLEUSE.equalsIgnoreCase(couponType)) {
			// Simply return success for single-use, as it's just a selection
			typeCoupon = CouponConstant.COUPONUSAGE_SINGLEUSE;

			// Set the coupon type
			uniqueCouponEntity.setCouponType(CouponConstant.COUPONUSAGE_SINGLEUSE);
			uniqueCoupon.setCouponType(CouponConstant.COUPONUSAGE_SINGLEUSE);

		} else {
			return BaseResponse.builder().status(CouponConstant.FAILURE).message("Invalid coupon type provided. Only 'multi-use' and 'single-use' are allowed.").build();
		}

		DefaultTransactionDefinition defMySQL = new DefaultTransactionDefinition();
		TransactionStatus transactionStatusMySQL = transactionManagerMysql.getTransaction(defMySQL);

		DefaultTransactionDefinition defMongo = new DefaultTransactionDefinition();
		TransactionStatus transactionStatusMongo = transactionManagerMongo.getTransaction(defMongo);

		try {
			uniqueCouponRepo.save(uniqueCouponEntity);
			uniqueCouponRepository.save(uniqueCoupon);

			transactionManagerMysql.commit(transactionStatusMySQL);
			transactionManagerMongo.commit(transactionStatusMongo);
		} catch (Exception e) {
			transactionManagerMysql.rollback(transactionStatusMySQL);
			transactionManagerMongo.rollback(transactionStatusMongo);
			throw new RuntimeException("Error occurred while saving multi-use coupon in databases", e);
		}

		return BaseResponse.builder().status(CouponConstant.SUCCESS).message(typeCoupon + " coupon selected. Proceed to the next step.").build();
	}


	// Create system-generated single-use coupons
	public BaseResponse createSystemGeneratedCoupons(CouponUsageSelectionRequest request) throws Exception {
		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(request.getCouponName());
		if (uniqueCouponEntity == null) {
			throw new BadApiRequestException("Coupon not found in MySQL.");
		}

		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(request.getCouponName())
				.orElseThrow(() -> new BadApiRequestException("Coupon not found in MongoDB."));


		if (!uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONUSAGE_SINGLEUSE) || !uniqueCouponEntity.getCouponType().equalsIgnoreCase(CouponConstant.COUPONUSAGE_SINGLEUSE)) {
			log.error(request.getCouponName() + " is not saved as Single Use in any one of databases. Value should be consistent in both databases");
			throw new BadApiRequestException("Given coupon type is not SINGLEUSE");
		}

		uniqueCouponEntity.setPrefix(request.getPrefix());
		uniqueCouponEntity.setSuffix(request.getSuffix());
		uniqueCouponEntity.setBatchLotName(request.getBatchLotName());
		uniqueCouponEntity.setQuantity(request.getQuantity());
		uniqueCouponEntity.setUsageType(CouponConstant.COUPONUSAGETYPE_SYSTEMGENERATED);


		uniqueCoupon.setCouponPrefix(request.getPrefix());
		uniqueCoupon.setCouponSuffix(request.getSuffix());
		uniqueCoupon.setLotQuantity(request.getQuantity());
		uniqueCoupon.setLotName(request.getBatchLotName());
		uniqueCoupon.setUsageType(CouponConstant.COUPONUSAGETYPE_SYSTEMGENERATED);


		DefaultTransactionDefinition defMySQL = new DefaultTransactionDefinition();
		TransactionStatus transactionStatusMySQL = transactionManagerMysql.getTransaction(defMySQL);

		DefaultTransactionDefinition defMongo = new DefaultTransactionDefinition();
		TransactionStatus transactionStatusMongo = transactionManagerMongo.getTransaction(defMongo);

		try {

			uniqueCouponRepo.save(uniqueCouponEntity);
			uniqueCouponRepository.save(uniqueCoupon);

			transactionManagerMysql.commit(transactionStatusMySQL);
			transactionManagerMongo.commit(transactionStatusMongo);
		} catch (Exception e) {
			transactionManagerMysql.rollback(transactionStatusMySQL);
			transactionManagerMongo.rollback(transactionStatusMongo);
			throw new Exception("Error occurred while saving system-generated coupons in databases", e);
		}

		return BaseResponse.builder().status(CouponConstant.SUCCESS).message("System-generated coupons created successfully.").build();
	}


	//Mongo work to do , IMPORTANT TO DI IT PRIORITY TOMMORROW
	public BaseResponse uploadCouponFile(CouponUsageSelectionRequest request) throws Exception {
		List<UniqueCouponEntity> uniqueCouponEntities = new ArrayList<>();
		List<UniqueCoupon> uniqueCouponList = new ArrayList<>();
		log.trace("Starting to process uploadCouponFile for couponName: {}", request.getCouponName());
		// Fetch the unique coupon entity from MySQL
		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(request.getCouponName());
		if (uniqueCouponEntity == null) {
			throw new BadApiRequestException("Coupon with given name not found in MySQL");
		}

		// Fetch the unique coupon document from MongoDB
		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(request.getCouponName()).orElseThrow(() -> new BadApiRequestException("Coupon with given coupon code does not exist"));

		if (!uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONUSAGE_SINGLEUSE) || !uniqueCouponEntity.getCouponType().equalsIgnoreCase(CouponConstant.COUPONUSAGE_SINGLEUSE)) {
			log.error(request.getCouponName() + " is not saved as Single Use in any one of databases. Value should be consistent in both databases");
			throw new BadApiRequestException("Given coupon type is not SINGLEUSE");
		}

		uniqueCouponEntity.setUsageType(CouponConstant.COUPONUSAGETYPE_COUPONUPLOAD);
		uniqueCoupon.setUsageType(CouponConstant.COUPONUSAGETYPE_COUPONUPLOAD);

		try (InputStream inputStream = request.getFile().getInputStream()) {
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			Sheet sheet = workbook.getSheetAt(0);
			log.trace("Successfully opened Excel file for processing");

			String prefix = null;
			String suffix = null;
			int quantity = 0;
			String batchLotName = null;

			boolean check = false;
			for (Row row : sheet) {

				// Skip header row
				if (row.getRowNum() == 0) {
					continue;
				}

				if (row.getCell(0) != null) {
					prefix = row.getCell(0).getStringCellValue();
				} else {
					throw new BadApiRequestException("Prefix field is not valid");
				}


				if (row.getCell(1) != null) {
					suffix = row.getCell(1).getStringCellValue();
				} else {
					throw new BadApiRequestException("Suffix field is not valid");
				}

				if (row.getCell(2) != null) {
					quantity = (int) row.getCell(2).getNumericCellValue();
					log.info("Quantity is : {}", "cell 2 datatype" + row.getCell(2).getCellType());
				} else {
					throw new BadApiRequestException("Quantity field is not valid");
				}

				if (row.getCell(3) != null) {
					batchLotName = row.getCell(3).getStringCellValue();
				} else {
					throw new BadApiRequestException("Batch/Lot Name field is not valid");
				}
				uniqueCouponEntity.setPrefix(prefix);
				uniqueCouponEntity.setSuffix(suffix);
				uniqueCouponEntity.setQuantity(quantity);
				uniqueCouponEntity.setBatchLotName(batchLotName);

				uniqueCoupon.setCouponPrefix(prefix);
				uniqueCoupon.setCouponSuffix(suffix);
				uniqueCoupon.setLotQuantity(quantity);
				uniqueCoupon.setLotName(batchLotName);

			}
		} catch (Exception e) {
			log.error("Error processing the file: {}", e.getMessage(), e);
			throw new Exception("Error processing the file", e);
		}

		DefaultTransactionDefinition defMySQL = new DefaultTransactionDefinition();
		TransactionStatus transactionStatusMySQL = transactionManagerMysql.getTransaction(defMySQL);

		DefaultTransactionDefinition defMongo = new DefaultTransactionDefinition();
		TransactionStatus transactionStatusMongo = transactionManagerMongo.getTransaction(defMongo);

		try {
			// Save batch coupons and unique coupon entity in MySQL
			uniqueCouponRepo.save(uniqueCouponEntity);

			// Save batch coupons and unique coupon document in MongoDB
			uniqueCouponRepository.save(uniqueCoupon);
			transactionManagerMysql.commit(transactionStatusMySQL);
			transactionManagerMongo.commit(transactionStatusMongo);

		} catch (Exception e) {
			log.error("Error in saving data in databases");
			transactionManagerMysql.rollback(transactionStatusMySQL);
			transactionManagerMongo.rollback(transactionStatusMongo);
			throw new Exception("Error occurred in saving data in databases", e);
		}

		return BaseResponse.builder()
				.status(CouponConstant.SUCCESS)
				.message("Coupons uploaded successfully")
				.build();
	}


	public BaseResponse uploadCouponImage(MultipartFile file, String code) throws Exception {

		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(code).orElseThrow(() -> new BadApiRequestException("Coupon with given coupon code not found in database"));

		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(code);
		if (uniqueCouponEntity == null) {
			throw new BadApiRequestException("Coupon with given coupon code not found in MYSQL");
		}

		if (file.isEmpty()) {
			throw new BadApiRequestException("File is empty");
		}

		String contentType = file.getContentType();
		if (contentType == null ||
				!(contentType.equals("image/png") || contentType.equals("image/jpeg"))) {
			throw new BadApiRequestException("File must be a PNG or JPEG image");
		}

		// Optional: You can also check the file extension as an extra measure
		String fileName = file.getOriginalFilename();
		if (fileName != null && !(fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))) {
			throw new BadApiRequestException("File extension must be .png, .jpg, or .jpeg");
		}

		String UPLOAD_DIR = "/var/www/html/coupons/images/";

		// Ensure the directory exists
		Path uploadPath = Paths.get(UPLOAD_DIR);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		// Determine the file extension
		String originalFileName = file.getOriginalFilename();
		String fileExtension = "";
		if (originalFileName != null && originalFileName.contains(".")) {
			fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
		}

		String newFileName = code.toUpperCase();
		// Create the full file path
		Path filePath = uploadPath.resolve(newFileName + fileExtension);

		try {
			// Save the file
			Files.copy(file.getInputStream(), filePath);
		} catch (Exception e) {
			log.error("Error in saving coupon image file in path." + e.getStackTrace());
			throw new Exception("Error occured!");
		}

		uniqueCoupon.setImageName(newFileName + fileExtension);
		uniqueCouponEntity.setImageName(newFileName + fileExtension);

		DefaultTransactionDefinition defMySQL = new DefaultTransactionDefinition();
		TransactionStatus transactionStatusMySQL = transactionManagerMysql.getTransaction(defMySQL);

		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("MyTransaction");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		TransactionStatus status = transactionManagerMongo.getTransaction(def);
		try {
			uniqueCouponRepo.save(uniqueCouponEntity);
			uniqueCouponRepository.save(uniqueCoupon);
			transactionManagerMysql.commit(transactionStatusMySQL);
			transactionManagerMongo.commit(status);
		} catch (Exception e) {
			log.error("Error in saving data in datbases");
			transactionManagerMysql.rollback(transactionStatusMySQL);
			transactionManagerMongo.rollback(status);
			throw new Exception("Error occured in saving data in database");
		}

		return BaseResponse.builder().status(CouponConstant.SUCCESS).message("Image uploaded succesfully").build();

	}

	public void processFreebieFile(MultipartFile file, String uniquecouponname) throws Exception {
		List<ItemRequest> itemsList = new ArrayList<>();
		try (Workbook workbook = getWorkbook(file)) {
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				if (row.getRowNum() == 0) continue; // Skip header row
				ItemRequest item = ItemRequest.builder()
						.itemCode(getStringCellValue(row.getCell(0)))
						.itemPrice(getNumericCellValue(row.getCell(1)))
						.itemQuantity((int) getNumericCellValue(row.getCell(2)))
						.itemBase(getStringCellValue(row.getCell(3)))
						.itemSize(getStringCellValue(row.getCell(4)))
						.build();
				itemsList.add(item);
			}
		}
		saveFreebieItemsToDatabase(uniquecouponname, itemsList);
	}

	public void processProductInclusionFile(MultipartFile file, String uniquecouponname) throws Exception {
		List<Product> productList = new ArrayList<>();
		try (Workbook workbook = getWorkbook(file)) {
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				if (row.getRowNum() == 0) continue; // Skip header row
				Product product = Product.builder()
						.productCode(getStringCellValue(row.getCell(0)))
						.productName(getStringCellValue(row.getCell(1)))
						.category(getStringCellValue(row.getCell(2)))
						.build();
				productList.add(product);
			}
		}
		saveProductInclusionItemsToDatabase(uniquecouponname, productList);

	}

	public void processProductExclusionFile(MultipartFile file, String uniquecouponname) throws Exception {
		List<Product> productList = new ArrayList<>();
		try (Workbook workbook = getWorkbook(file)) {
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				if (row.getRowNum() == 0) continue; // Skip header row
				Product product = Product.builder()
						.productCode(getStringCellValue(row.getCell(0)))
						.productName(getStringCellValue(row.getCell(1)))
						.category(getStringCellValue(row.getCell(2)))
						.build();
				productList.add(product);
			}
		}
		saveProductExclusionItemsToDatabase(uniquecouponname, productList);
	}

	public void processStoreFile(MultipartFile file, String uniquecouponname) throws Exception {
		List<StoreRequest> storeRequests = new ArrayList<>();
		try (Workbook workbook = getWorkbook(file)) {
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				if (row.getRowNum() == 0) continue; // Skip header row
				StoreRequest storeRequest = StoreRequest.builder()
						.storeCode(getStringCellValue(row.getCell(0)))
						.storeName(getStringCellValue(row.getCell(1)))
						.build();
				storeRequests.add(storeRequest);
			}
		}
		saveStoreItemsToDatabase(uniquecouponname, storeRequests);
	}

	private Workbook getWorkbook(MultipartFile file) throws IOException {
		String fileName = file.getOriginalFilename();
		if (fileName != null && fileName.endsWith(".xls")) {
			return new HSSFWorkbook(file.getInputStream());
		} else if (fileName != null && fileName.endsWith(".xlsx")) {
			return new XSSFWorkbook(file.getInputStream());
		} else {
			throw new IllegalArgumentException("Invalid file type. Only .xls and .xlsx files are accepted.");
		}
	}

	private String getStringCellValue(Cell cell) {
		if (cell == null) {
			return null;
		}

		switch (cell.getCellType()) {
			case STRING:
				return cell.getStringCellValue().trim();
			case NUMERIC:
				return String.valueOf((int) cell.getNumericCellValue()); // Convert numeric to string
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case FORMULA:
				return cell.getCellFormula();
			default:
				return "";
		}
	}


	private double getNumericCellValue(Cell cell) {
		return cell != null ? cell.getNumericCellValue() : 0.0;
	}

	public BaseResponse createMilestoneCoupon(MilestoneRequest milestoneRequest) {

		MilestoneDetails milestoneDetails = MilestoneDetails.builder().journeyName(milestoneRequest.getJourneyName()).startDate(milestoneRequest.getStartDate())
				.endDate(milestoneRequest.getExpiryDate()).createdDate(LocalDate.now()).status(CouponConstant.CREATED).lastUpdatedBy(milestoneRequest.getLastUpdatedBy())
				.sequential(milestoneRequest.isSequential()).build();

		List<UniqueCoupon> uniqueCoupons = new ArrayList<>();

		List<SegmentMilestoneMapping> segmentMilestoneMappings = new ArrayList<>();
		for (MilestoneCouponRequest request : milestoneRequest.getMilestoneCouponRequestList()) {

			String id = UUID.randomUUID().toString();
			SegmentMilestoneMapping segmentMilestoneMapping = SegmentMilestoneMapping.builder().customerSegmentId(id).couponCode(request.getCouponId())
					.couponDisplayName(request.getCouponName()).milestoneName(request.getMilestoneName()).milestoneNumber(request.getMilestoneNumber())
					.milestoneDetails(milestoneDetails).status(CouponConstant.CREATED).journeyDateCreated(milestoneDetails.getCreatedDate())
					.numbersOfOrders(request.getNumberOfOrders()).build();
			segmentMilestoneMappings.add(segmentMilestoneMapping);

			UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(request.getCouponId()).orElseThrow(() -> new BadApiRequestException(request.getCouponId() + " not found in database"));
			uniqueCoupon.setMilestone(true);
			uniqueCoupons.add(uniqueCoupon);
		}
		milestoneDetailsRepository.save(milestoneDetails);
		segmentMilestoneMappingRepository.saveAll(segmentMilestoneMappings);
		uniqueCouponRepository.saveAll(uniqueCoupons);
		return BaseResponse.builder().status(CouponConstant.SUCCESS).message("Milestone Coupons created successfully").build();
	}

	public BaseResponse uploadSegmentforMilestoneCoupons(MultipartFile file, String journeyName) throws Exception {

		String fileName = file.getOriginalFilename();
		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		String campaignName = "";
		List<SegmentData> segmentDataList = new ArrayList<>();
		//List<SegmentDataEntity> segmentDataListMySQL = new ArrayList<>();

		SegmentDetails segmentDetails = new SegmentDetails();
		//SegmentDetailsEntity segmentDetailsMySQL = new SegmentDetailsEntity();

		boolean check = false;
		for (Row row : sheet) {

			// Skip header row
			if (row.getRowNum() == 0) {
				continue;
			}
			//SegmentDataEntity segmentDataMySQL = new SegmentDataEntity();
			SegmentData segmentData = new SegmentData();
			String id = UUID.randomUUID().toString();
			segmentData.setId(id);
			if (row.getCell(0) != null) {
				segmentData.setMobileNumber(String.valueOf(row.getCell(0)));
				//segmentDataMySQL.setMobileNumber(String.valueOf(row.getCell(0)));
			} else {
				throw new BadApiRequestException("Number field is not valid");
			}

			if (!check) {
				if (row.getCell(1) != null) {
					campaignName = String.valueOf(row.getCell(1));
					String id2 = UUID.randomUUID().toString();
					segmentDetails.setId(id2);
					segmentDetails.setSegmentName(fileName + "_" + campaignName);
					//segmentDetailsMySQL.setSegmentName(fileName + "_" + campaignName);
					check = true;
				} else {
					throw new BadApiRequestException("Campaign field is not valid");
				}
			}

			segmentData.setSegmentAttached(segmentDetails);
			segmentData.setSegmentAttachedDate(LocalDate.now());
			//segmentDataMySQL.setSegmentAttached(segmentDetailsMySQL);
			//segmentDataMySQL.setSegmentAttachedDate(LocalDate.now());
			segmentDataList.add(segmentData);
			//segmentDataListMySQL.add(segmentDataMySQL);
		}
//		if (segmentDetailsRepositoryMySQL.existsBySegmentNameIgnoreCase(segmentDetailsMySQL.getSegmentName())) {
//			throw new BadApiRequestException("Segment with given name already exists");
//		}

		if (segmentDetailsRepository.existsBySegmentNameIgnoreCase(segmentDetails.getSegmentName())) {
			throw new BadApiRequestException("Segment with given name already exists");
		}

		MilestoneDetails milestoneDetails = milestoneDetailsRepository.findByIdIgnoreCase(journeyName).orElseThrow(() -> new BadApiRequestException("Journey with given name do not exists"));
		milestoneDetails.setSegment(segmentDetails);
		milestoneDetails.setStatus(CouponConstant.ACTIVE);

		segmentDetailsRepository.save(segmentDetails);
		segmentDataRepository.saveAll(segmentDataList);
		milestoneDetailsRepository.save(milestoneDetails);


//		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(couponCode);
//		if (uniqueCouponEntity == null) {
//			throw new BadApiRequestException("Coupon with given id not found in MySQL");
//		}
//		uniqueCouponEntity.setApplicableForSegments(segmentDetailsMySQL);
//		uniqueCouponEntity.setApplicableForValue(CouponConstant.APPLICABLEFOR_USERSEGMENT);
//		uniqueCouponEntity.setStatus(CouponConstant.ACTIVE);
//		uniqueCouponEntity.setUserAttachedDate(LocalDate.now());
//
//		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(couponCode).orElseThrow(() -> new BadApiRequestException("Coupon with given id not found"));
//		uniqueCoupon.setApplicableForSegments(segmentDetails);
//		uniqueCoupon.setApplicableForValue(CouponConstant.APPLICABLEFOR_USERSEGMENT);
//		uniqueCoupon.setStatus(CouponConstant.ACTIVE);
//		uniqueCoupon.setUserAttachedDate(LocalDate.now());
//
//		DefaultTransactionDefinition defMySQL = new DefaultTransactionDefinition();
//		TransactionStatus transactionStatusMySQL = transactionManagerMysql.getTransaction(defMySQL);
//
//		DefaultTransactionDefinition defMongo = new DefaultTransactionDefinition();
//		TransactionStatus transactionStatus = transactionManagerMongo.getTransaction(defMongo);
//
//		try {
//			segmentDetailsRepositoryMySQL.save(segmentDetailsMySQL);
//			segmentDataRepositoryMySQL.saveAll(segmentDataListMySQL);
//			uniqueCouponRepo.save(uniqueCouponEntity);
//			transactionManagerMysql.commit(transactionStatusMySQL);
//
//			segmentDetailsRepository.save(segmentDetails);
//			segmentDataRepository.saveAll(segmentDataList);
//			uniqueCouponRepository.save(uniqueCoupon);
//			transactionManagerMongo.commit(transactionStatus);
//		} catch (Exception e) {
//			log.error("Error in saving data in database in Mongodb");
//			transactionManagerMongo.rollback(transactionStatus);
//
////			transactionManagerMysql.rollback(transactionStatusMySQL);
//			throw new Exception("Error occured in saving data in database");
//		}
		return BaseResponse.builder().status(CouponConstant.SUCCESS).message("Segment uploaded successfully").build();
	}

	private void saveFreebieItemsToDatabase(String uniquecouponname, List<ItemRequest> itemsList) {
		// MySQL saving logic
		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(uniquecouponname);
		if (uniqueCouponEntity != null) {
			uniqueCouponEntity.setFreebieItems(mapToMySQLItems(itemsList));
			uniqueCouponRepo.save(uniqueCouponEntity);
		}

		// MongoDB saving logic
		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(uniquecouponname).orElse(null);
		if (uniqueCoupon != null) {
			uniqueCoupon.setFreebieItems(mapToMongoItems(itemsList));
			uniqueCouponRepository.save(uniqueCoupon);
		}
	}

	private void saveProductInclusionItemsToDatabase(String uniquecouponname, List<Product> productList) {
		// MySQL saving logic
		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(uniquecouponname);
		if (uniqueCouponEntity != null) {
			uniqueCouponEntity.setProductInclusion(mapToMySQLProducts(productList));
			uniqueCouponRepo.save(uniqueCouponEntity);
		}
		List<ProductEntity> productEntities = mapToMySQLProductEntities(productList);
		for (ProductEntity productEntity : productEntities) {
			productEntity.setCouponName(uniquecouponname);
			productEntityRepository.save(productEntity);  // Save each product entity in the product table
		}


		// MongoDB saving logic
		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(uniquecouponname).orElse(null);
		if (uniqueCoupon != null) {
			uniqueCoupon.setProductInclusion(mapToMongoProducts(productList));
			uniqueCouponRepository.save(uniqueCoupon);
		}
		List<Products> mongoProducts = mapToMongoProducts(productList, uniquecouponname);
		for (Products product : mongoProducts) {
			productRepository.save(product);  // Save each product entity in MongoDB
		}
	}

	private void saveProductExclusionItemsToDatabase(String uniquecouponname, List<Product> productList) {
		// MySQL saving logic
		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(uniquecouponname);
		if (uniqueCouponEntity != null) {
			uniqueCouponEntity.setProductExclusion(mapToMySQLProducts(productList));
			uniqueCouponRepo.save(uniqueCouponEntity);
		}
		List<ProductEntityExlcusion> productEntities = mapToMySQLProductEntities1(productList);
		for (ProductEntityExlcusion productEntity : productEntities) {
			productEntity.setCouponName(uniquecouponname);
			productEntityExclusionRepsoitory.save(productEntity);  // Save each product entity in the product table
		}


		// MongoDB saving logic
		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(uniquecouponname).orElse(null);
		if (uniqueCoupon != null) {
			uniqueCoupon.setProductExclusion(mapToMongoProducts(productList));
			uniqueCouponRepository.save(uniqueCoupon);
		}
		List<ProductsExclusion> mongoProducts = mapToMongoProducts1(productList, uniquecouponname);
		for (ProductsExclusion product : mongoProducts) {
			productExclusionRepository.save(product);  // Save each product entity in MongoDB
		}
	}

	private void saveStoreItemsToDatabase(String uniquecouponname, List<StoreRequest> storeRequests) {
		// MySQL saving logic
		UniqueCouponEntity uniqueCouponEntity = uniqueCouponRepo.findByCouponName(uniquecouponname);
		if (uniqueCouponEntity != null) {
			// Save store codes to the UniqueCouponEntity (MySQL)
			List<String> storeCodes = storeRequests.stream()
					.map(StoreRequest::getStoreCode)
					.collect(Collectors.toList());
			uniqueCouponEntity.setStores(storeCodes);
			uniqueCouponRepo.save(uniqueCouponEntity);
		}

		// Save detailed store information in MySQL StoreEntity table
		List<StoreEntity> storeEntities = mapStoreRequestsToMySQLStores(storeRequests);
		for (StoreEntity storeEntity : storeEntities) {
			storeEntity.setCouponName(uniquecouponname);
			storeEntityRepository.save(storeEntity);  // Save each store entity in the Store table
		}

		// MongoDB saving logic
		UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(uniquecouponname).orElse(null);
		if (uniqueCoupon != null) {
			// Save store codes to the UniqueCoupon (MongoDB)
			uniqueCoupon.setStores(storeRequests.stream()
					.map(StoreRequest::getStoreCode)
					.collect(Collectors.toList()));
			uniqueCouponRepository.save(uniqueCoupon);
		}

		// Save detailed store information in MongoDB Store collection
		List<Store> mongoStores = mapStoreRequestsToMongoStores(storeRequests, uniquecouponname);
		for (Store store : mongoStores) {
			storeRepository.save(store);  // Save each store entity in MongoDB
		}
	}

	private List<String> mapToMySQLProducts(List<Product> productList) {
		return productList.stream()
				.map(Product::getProductCode)  // Extracting the productCode
				.collect(Collectors.toList());
	}

	private List<ProductEntity> mapToMySQLProductEntities(List<Product> productList) {
		// This will map to full ProductEntity objects
		return productList.stream()
				.map(product -> ProductEntity.builder()
						.productCode(product.getProductCode())
						.productName(product.getProductName())
						.categoryCode(product.getCategory())
						.build())
				.collect(Collectors.toList());
	}

	private List<String> mapToMongoProducts(List<Product> productList) {
		return productList.stream()
				.map(Product::getProductCode)  // Extracting the productCode
				.collect(Collectors.toList());
	}

	private List<Products> mapToMongoProducts(List<Product> productList, String couponName) {
		return productList.stream().map(product -> Products.builder()
						.id(UUID.randomUUID().toString())
						.productCode(product.getProductCode())
						.productName(product.getProductName())
						.category(product.getCategory())
						.couponCode(couponName)  // Set the coupon name to associate the product with the coupon
						.build())
				.collect(Collectors.toList());
	}

	private List<ProductEntityExlcusion> mapToMySQLProductEntities1(List<Product> productList) {
		// This will map to full ProductEntity objects
		return productList.stream()
				.map(product -> ProductEntityExlcusion.builder()
						.productCode(product.getProductCode())
						.productName(product.getProductName())
						.couponName(product.getCategory())
						.build())
				.collect(Collectors.toList());
	}

	private List<ProductsExclusion> mapToMongoProducts1(List<Product> productList, String couponName) {
		return productList.stream().map(product -> ProductsExclusion.builder()
						.id(UUID.randomUUID().toString())
						.productCode(product.getProductCode())
						.productName(product.getProductName())
						.category(product.getCategory())
						.couponCode(couponName)  // Set the coupon name to associate the product with the coupon
						.build())
				.collect(Collectors.toList());
	}

	private List<StoreEntity> mapStoreRequestsToMySQLStores(List<StoreRequest> storeRequests) {
		return storeRequests.stream().map(storeRequest -> StoreEntity.builder()
						.storeCode(storeRequest.getStoreCode())
						.storeName(storeRequest.getStoreName())
						.build())
				.collect(Collectors.toList());
	}

	private List<Store> mapStoreRequestsToMongoStores(List<StoreRequest> storeRequests, String uniquecouponname) {
		return storeRequests.stream().map(storeRequest -> Store.builder()
						.id(UUID.randomUUID().toString())
						.storeCode(storeRequest.getStoreCode())
						.storeName(storeRequest.getStoreName())
						.couponCode(uniquecouponname)
						.build())
				.collect(Collectors.toList());


	}
}