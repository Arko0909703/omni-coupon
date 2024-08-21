package com.project.coupon.service.implementation;

import com.project.coupon.constants.CouponConstant;
import com.project.coupon.entity.*;
import com.project.coupon.exceptions.BadApiRequestException;
import com.project.coupon.exceptions.ResponseMessage;
import com.project.coupon.jpaEntities.DataLakeTable;
import com.project.coupon.jpaEntities.ProductMaster;
import com.project.coupon.jpaEntities.StoreMaster;
import com.project.coupon.jpaEntities.TransactionTable;
import com.project.coupon.jpaRepositories.DataLakeDataRepository;
import com.project.coupon.jpaRepositories.ProductMasterRepository;
import com.project.coupon.jpaRepositories.StoreMasterRepository;
import com.project.coupon.jpaRepositories.TransactionRepository;
import com.project.coupon.repository.*;
import com.project.coupon.request.CouponUsageRequest;
import com.project.coupon.request.UpdateStatusRequest;
import com.project.coupon.response.*;
import com.project.coupon.service.GetCoupons;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Log4j2
public class GetCouponsImplementation implements GetCoupons {


    @Autowired
    private UniqueCouponRepository uniqueCouponRepository;

    @Autowired
    private BaseCouponRepository baseCouponRepository;

    @Autowired
    private BatchCouponRepository batchCouponRepository;

    @Autowired
    private SegmentDataRepository segmentRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoTransactionManager transactionManager;

    @Autowired
    private SegmentMilestoneMappingRepository segmentMilestoneMappingRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MilestoneDetailsRepository milestoneDetailsRepository;

    @Autowired
    private StoreMasterRepository storeMasterRepository;

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Autowired
    private DataLakeDataRepository dataLakeDataRepository;

    private static final String IMAGE_DIRECTORY = "/var/www/html/coupons/images/";

    private CouponResponseWithNumber convertToCouponResponseWithNumber(UniqueCoupon uniqueCoupon) {

        CouponResponseWithNumber response=CouponResponseWithNumber.builder().couponCode(uniqueCoupon.getUniqueCouponCode()).couponName(uniqueCoupon.getDisplayName())
                .couponType(uniqueCoupon.getCouponType()).channel(uniqueCoupon.getChannel()).termsAndCondition(uniqueCoupon.getTermsAndConditions())
                .couponStatus(uniqueCoupon.getStatus()).startDate(uniqueCoupon.getUserAttachedDate()).couponDescription(uniqueCoupon.getDescription())
                .weightage(uniqueCoupon.getWeightage()).build();

        if(uniqueCoupon.getExtendedExpiryDate()!=null) {
            response.setExpiryDate(uniqueCoupon.getExtendedExpiryDate());
        }
        else if(uniqueCoupon.getFirstExpiryDate()!=null) {
            response.setExpiryDate(uniqueCoupon.getFirstExpiryDate());
        }
        else if(uniqueCoupon.getExtendedNumberOfDays()!=0){
            response.setExpiryDate(uniqueCoupon.getUserAttachedDate().plusDays(uniqueCoupon.getNumberOfDays()+uniqueCoupon.getExtendedNumberOfDays()));
        }
        else{
            response.setExpiryDate(uniqueCoupon.getUserAttachedDate().plusDays(uniqueCoupon.getNumberOfDays()));
        }

        Path imagePath = Paths.get(IMAGE_DIRECTORY + uniqueCoupon.getUniqueCouponCode()+".png");
        if(Files.exists(imagePath)){
            response.setImgUrl("/coupons/images/"+uniqueCoupon.getUniqueCouponCode()+".png");
        }
        else{
            response.setImgUrl(null);
        }

        return response;
    }

    @Override
    public List<CouponResponseWithNumber> getCouponUsingMobileNumber(String mobileNumber) {

        List<UniqueCoupon> allCoupons = uniqueCouponRepository.findByApplicableForValueIgnoreCaseAndHiddenUI(CouponConstant.APPLICABLEFORVALUE_ALL,false);

        log.info("All Coupons value list: "+allCoupons.toString());

        List<UniqueCoupon> filteredCoupons=new ArrayList<>();
        for(UniqueCoupon coupon: allCoupons){

            if(coupon.getStatus().equalsIgnoreCase(CouponConstant.DEACTIVATED) || coupon.getStatus().equalsIgnoreCase(CouponConstant.DELETED)){
                continue;
            }

            if (transactionRepository.getNumberOfColumnsWithCouponCode(coupon.getUniqueCouponCode()) >= coupon.getTotalUsage() &&
                    transactionRepository.existsByCouponCodeAndMobileNumber(coupon.getUniqueCouponCode(),mobileNumber)){
                coupon.setStatus(CouponConstant.REDEEMED);
            }

            //New users cannot get coupons
            if (transactionRepository.getNumberOfColumnsWithCouponCode(coupon.getUniqueCouponCode()) >= coupon.getTotalUsage() &&
                    !transactionRepository.existsByCouponCodeAndMobileNumber(coupon.getUniqueCouponCode(),mobileNumber)){
                continue;
            }

            List<TransactionTable> transactionTableDbData = transactionRepository.findByStatusAndCouponCodeAndMobileNumber(coupon.getUniqueCouponCode(), mobileNumber);
            log.info("Transaction Table Data: {}", transactionTableDbData);

            if (!transactionTableDbData.isEmpty() && transactionTableDbData.size() >= coupon.getNumberOfTimesApplicablePerUser()){
                coupon.setStatus(CouponConstant.REDEEMED);
            }

            //New Users cannot get this coupon
            if(transactionTableDbData.isEmpty() &&
                    transactionRepository.getNumberOfColumnswithDistintMobileNumber(coupon.getUniqueCouponCode())>=coupon.getNumberOfUniqueUsers()){
                continue;
            }

            filteredCoupons.add(coupon);
        }

        log.info("Filtered coupons value list: "+filteredCoupons.toString());

        List<UniqueCoupon> applicableCoupons = new ArrayList<>(filteredCoupons);


        // Fetch all segments associated with the mobile number
        List<SegmentData> userSegments = segmentRepository.findByMobileNumber(mobileNumber);
        log.info("User Segments Data: "+userSegments.toString());
        Set<SegmentDetails> segmentDetails=userSegments.stream().map(SegmentData::getSegmentAttached).collect(Collectors.toSet());
        log.info("segment details data: "+segmentDetails.toString());
        if ( !segmentDetails.isEmpty()) {
            // Fetch coupons for each of the user's segments
            List<UniqueCoupon> segmentCoupons = uniqueCouponRepository.findByApplicableForSegmentsInAndCouponUsageIgnoreCaseAndHiddenUI(segmentDetails.stream().toList(),CouponConstant.COUPONUSAGE_MULTIUSE,false);
            log.info("segments coupons info: "+segmentCoupons);

            Set<UniqueCoupon> setCoupons= new HashSet<>(segmentCoupons);

            for(UniqueCoupon coupon: setCoupons){

//                if (!transactionTableDbData.isEmpty() && transactionTableDbData.size() >= coupon.getNumberOfTimesApplicablePerUser()){
//                    coupon.setStatus(CouponConstant.REDEEMED);
//                }
//
//                if (transactionRepository.getNumberOfColumnsWithCouponCode(coupon.getUniqueCouponCode()) >= coupon.getTotalUsage()){
//                    coupon.setStatus(CouponConstant.REDEEMED);
//                }
//                if(transactionRepository.getNumberOfColumnswithDistintMobileNumber(coupon.getUniqueCouponCode())>=coupon.getNumberOfUniqueUsers()){
//                    continue;
//                }


                if(coupon.getStatus().equalsIgnoreCase(CouponConstant.DEACTIVATED) || coupon.getStatus().equalsIgnoreCase(CouponConstant.DELETED)){
                    continue;
                }

                if (transactionRepository.getNumberOfColumnsWithCouponCode(coupon.getUniqueCouponCode()) >= coupon.getTotalUsage() &&
                        transactionRepository.existsByCouponCodeAndMobileNumber(coupon.getUniqueCouponCode(),mobileNumber)){
                    coupon.setStatus(CouponConstant.REDEEMED);
                }

                //New users cannot get coupons
                if (transactionRepository.getNumberOfColumnsWithCouponCode(coupon.getUniqueCouponCode()) >= coupon.getTotalUsage() &&
                        !transactionRepository.existsByCouponCodeAndMobileNumber(coupon.getUniqueCouponCode(),mobileNumber)){
                    continue;
                }

                List<TransactionTable> transactionTableDbData = transactionRepository.findByStatusAndCouponCodeAndMobileNumber(coupon.getUniqueCouponCode(), mobileNumber);
                log.info("Transaction Table Data: {}", transactionTableDbData);

                if (!transactionTableDbData.isEmpty() && transactionTableDbData.size() >= coupon.getNumberOfTimesApplicablePerUser()){
                    coupon.setStatus(CouponConstant.REDEEMED);
                }

                if(transactionTableDbData.isEmpty() &&
                        transactionRepository.getNumberOfColumnswithDistintMobileNumber(coupon.getUniqueCouponCode())>=coupon.getNumberOfUniqueUsers()){
                    continue;
                }

                SegmentDetails segmentDetail=coupon.getApplicableForSegments();
                List<SegmentData> segmentDataList=segmentRepository.findBySegmentAttached(segmentDetail);
                List<SegmentData> filteredSegments=segmentDataList.stream().filter(segmentData -> segmentData.getMobileNumber().equals(mobileNumber))
                        .map(segmentData -> {
                            coupon.setUserAttachedDate(segmentData.getSegmentAttachedDate());
                            return segmentData;
                        }).toList();

            }
            applicableCoupons.addAll(setCoupons);
        }

        log.info("Before Batch Coupons, applicableCoupons value: "+applicableCoupons.toString());

        List<BatchCoupon> batchCoupons=batchCouponRepository.findByMobileNumber(mobileNumber);
        log.info("List of batchcoupons: "+batchCoupons.toString());
        Set<UniqueCoupon> batchUniqueCoupon=batchCoupons.stream().map(batchCoupon -> {

            if (transactionRepository.existsByCouponCodeAndMobileNumber(batchCoupon.getBatchId(), mobileNumber)) {
                batchCoupon.setStatus(CouponConstant.REDEEMED);
            }

            Optional<UniqueCoupon> uniqueCouponOpt= uniqueCouponRepository.findByIdIgnoreCase(batchCoupon.getUniqueCouponId());
            if(uniqueCouponOpt.isEmpty()){
                throw new BadApiRequestException("Unique Coupon is not present with associated with Batch coupon");
            }
            UniqueCoupon uniqueCoupon= uniqueCouponOpt.get();
            uniqueCoupon.setUniqueCouponCode(batchCoupon.getBatchId());
            uniqueCoupon.setUserAttachedDate(batchCoupon.getDateCreated());
            uniqueCoupon.setStatus(batchCoupon.getStatus());
            return uniqueCoupon;
        }).collect(Collectors.toSet()) ;

        applicableCoupons.addAll(batchUniqueCoupon);
        log.info("After Batch Coupons, applicableCoupons value: "+applicableCoupons.toString());

        List<CouponResponseWithNumber> couponResponseWithNumberList =applicableCoupons.stream().filter(uniqueCoupon -> !uniqueCoupon.getCouponUsage().equalsIgnoreCase(CouponConstant.COUPONUSAGE_SINGLEUSE) || !uniqueCoupon.isMilestone() || uniqueCoupon.getStatus().equalsIgnoreCase(CouponConstant.DELETED) || uniqueCoupon.getStatus().equalsIgnoreCase(CouponConstant.DEACTIVATED)).map(this::convertToCouponResponseWithNumber).toList();

//        List<UniqueCoupon> dynamicCoupons=uniqueCouponRepository.findByTypeIgnoreCase(CouponConstant.TYPECOUPON_DYNAMIC);
//
//        for(UniqueCoupon coupon: dynamicCoupons) {
//            CouponResponseWithNumber response=CouponResponseWithNumber.builder().couponName(coupon.getDisplayName()).couponCode(coupon.getUniqueCouponCode())
//                    .termsAndCondition(coupon.getTermsAndConditions()).couponStatus(coupon.getStatus())
//                    .build();
//            if(coupon.getTypeConstructs()!=null) {
//                for(Constructs constructs: coupon.getTypeConstructs() ) {
//                    boolean check=false;
//                    SegmentDetails segment=constructs.getUploadSegment();
//                    List<SegmentData> segmentData=segmentRepository.findByMobileNumber(mobileNumber);
//
//                    for(SegmentDetails users: constructs.getUploadSegment()) {
//                        if(users.getMobileNumber().equals(mobileNumber)) {
//                            check=true;
//                            response.setCouponType(constructs.getCouponType());
//                            response.setChannel(constructs.getChannel());
//                            response.setStartDate(constructs.getUserAttachedDate());
//
//                            if(constructs.getExtendedExpiryDate()!=null) {
//                                response.setExpiryDate(constructs.getExtendedExpiryDate());
//                            }
//                            else if(constructs.getFirstExpiryDate()!=null) {
//                                response.setExpiryDate(constructs.getFirstExpiryDate());
//                            }
//                            else if(constructs.getExtendedNumberOfDays()!=0){
//                                response.setExpiryDate(constructs.getUserAttachedDate().plusDays(constructs.getNumberOfDays()+constructs.getExtendedNumberOfDays()));
//                            }
//                            else{
//                                response.setExpiryDate(constructs.getUserAttachedDate().plusDays(constructs.getNumberOfDays()));
//                            }
//                            break;
//                        }
//                    }
//                    if(check) {
//                        couponResponseWithNumberList.add(response);
//                    }
//                }
//            }
//        }

        if(couponResponseWithNumberList.isEmpty()) {

            throw new BadApiRequestException("No coupon is available for user");
        }
        return couponResponseWithNumberList;
    }

    @Override
    public CouponResponseWithCode getCouponUsingCouponCode(String couponCode) {

        UniqueCoupon uniqueCoupon= uniqueCouponRepository.findByIdIgnoreCase(couponCode).orElseThrow(()-> new BadApiRequestException("Wrong Coupon Code"));
        log.info("Unique Coupon Val in getCouponUsingCouponCode method: "+ uniqueCoupon);

        CouponResponseWithCode couponResponseWithCode= CouponResponseWithCode.builder()
                .couponCode(uniqueCoupon.getUniqueCouponCode()).couponName(uniqueCoupon.getDisplayName()).couponDescription(uniqueCoupon.getDescription())
                .couponType(uniqueCoupon.getCouponType()).channel(uniqueCoupon.getChannel()).termsAndCondition(uniqueCoupon.getTermsAndConditions()).status(uniqueCoupon.getStatus())
                .startDate(uniqueCoupon.getStartDate()).expiryDate(uniqueCoupon.getEndDate())
                .build();

        couponResponseWithCode.setImgUrl("/coupons/images/"+uniqueCoupon.getUniqueCouponCode()+".png");

        log.info("Coupon Response with code val : "+ couponResponseWithCode);
        return couponResponseWithCode;
    }


    private CouponResponseForFilterCoupons convertToCouponResponseForFilterCoupons(String couponCode,String couponName,String status,LocalDate dateCreated,LocalDate endDate) {

        return CouponResponseForFilterCoupons.builder().couponCode(couponCode).couponName(couponName)
                .dateCreated(dateCreated).status(status).endDate(endDate).build();
    }

    @Override
    public Page<CouponResponseForFilterCoupons> getFilteredCoupons(LocalDate dateCreated, LocalDate startDate, LocalDate endDate, String franchise, String city,
                                                                   String storeCode, String couponType, String couponStatus, int page, int size,String couponCategory) {

        Query query = new Query();

        if (franchise != null && !franchise.isEmpty()) {

            query.addCriteria(Criteria.where("franchise").regex(Pattern.compile(franchise, Pattern.CASE_INSENSITIVE)));
        }
        if (city != null && !city.isEmpty()) {
            query.addCriteria(Criteria.where("cities").regex(Pattern.compile(city, Pattern.CASE_INSENSITIVE)));
        }
        if (storeCode != null && !storeCode.isEmpty()) {
            query.addCriteria(Criteria.where("stores").regex(Pattern.compile(storeCode, Pattern.CASE_INSENSITIVE)));
        }
        if (couponType != null && !couponType.isEmpty()) {
            query.addCriteria(Criteria.where("couponType").regex(Pattern.compile(couponType, Pattern.CASE_INSENSITIVE)));
        }

        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.ASC, "dateCreated"));

        List<UniqueCoupon> uniqueCouponsList = mongoTemplate.find(query, UniqueCoupon.class);
        System.out.println("UniqueCouonlist: "+ uniqueCouponsList);

        List<CouponResponseForFilterCoupons> couponResponses;

        switch (couponCategory.toLowerCase()){
            case "base":
                Set<String> baseCouponIdSet= uniqueCouponsList.stream().map(UniqueCoupon::getBaseCouponCode).collect(Collectors.toSet());
                Query baseQuery=new Query();
                if(!baseCouponIdSet.isEmpty()) {
                    // Convert the set to a list of regex patterns with case insensitivity
                    Set<Pattern> regexPatterns = baseCouponIdSet.stream()
                            .map(code -> Pattern.compile("^" + Pattern.quote(code) + "$", Pattern.CASE_INSENSITIVE))
                            .collect(Collectors.toSet());
                    baseQuery.addCriteria(Criteria.where("_id").in(regexPatterns));
                }

                if(dateCreated!=null) {
                    baseQuery.addCriteria(Criteria.where("dateCreated").gte(Date.from(dateCreated.atStartOfDay(ZoneOffset.UTC).toInstant())));
                }

                if(endDate!=null) {
                    baseQuery.addCriteria(Criteria.where("termsAndConditionsExpiryDate").lte(Date.from(endDate.atStartOfDay(ZoneOffset.UTC).toInstant())));
                }

                if(!couponStatus.isEmpty()) {
                    baseQuery.addCriteria(Criteria.where("status").regex(Pattern.compile(couponStatus, Pattern.CASE_INSENSITIVE)));
                }
                baseQuery.with(pageable);
                List<BaseCoupon> filteredBaseCouponsData = mongoTemplate.find(baseQuery, BaseCoupon.class);
                couponResponses=filteredBaseCouponsData.stream().map(coupon -> { return convertToCouponResponseForFilterCoupons(coupon.getBaseCouponCode(),coupon.getCouponName(),coupon.getStatus(),coupon.getDateCreated(),coupon.getTermsAndConditionsExpiryDate());}).toList();

                break;
            case "unique":
                System.out.println("Inside unique ");
                if (dateCreated != null) {
                    query.addCriteria(Criteria.where("dateCreated").gte(Date.from(dateCreated.atStartOfDay(ZoneOffset.UTC).toInstant())));
                }
                if (startDate != null) {
                    query.addCriteria(Criteria.where("startDate").gte(Date.from(startDate.atStartOfDay(ZoneOffset.UTC).toInstant())));
                }
                if (endDate != null) {
                    query.addCriteria(Criteria.where("endDate").lte(Date.from(endDate.atStartOfDay(ZoneOffset.UTC).toInstant())));
                }
                if (couponStatus != null && !couponStatus.isEmpty()) {
                    query.addCriteria(Criteria.where("status").regex(Pattern.compile(couponStatus, Pattern.CASE_INSENSITIVE)));
                }
                query.with(pageable);
                System.out.println("Generated Query: " + query.toString());

                List<UniqueCoupon> uniqueCoupons = mongoTemplate.find(query, UniqueCoupon.class);
                System.out.println("UniqueCoupons Data: "+uniqueCoupons);
                couponResponses = uniqueCoupons.stream()
                        .map(coupon -> { return convertToCouponResponseForFilterCoupons(coupon.getUniqueCouponCode(),coupon.getDisplayName(),coupon.getStatus(),coupon.getDateCreated(),coupon.getEndDate());}).toList();
                break;
            case "batch":
                Set<String> lotNameSet=uniqueCouponsList.stream().map(UniqueCoupon::getLotName).collect(Collectors.toSet());
                Query batchQuery=new Query();
                if(!couponStatus.isEmpty()) {
                    batchQuery.addCriteria(Criteria.where("status").regex(Pattern.compile(couponStatus, Pattern.CASE_INSENSITIVE)));
                }

                if( !lotNameSet.isEmpty()) {
                    // Convert the set to a list of regex patterns with case insensitivity
                    Set<Pattern> regexPatterns = lotNameSet.stream()
                            .map(code -> Pattern.compile("^" + Pattern.quote(code) + "$", Pattern.CASE_INSENSITIVE))
                            .collect(Collectors.toSet());
                    batchQuery.addCriteria(Criteria.where("lotName").in(regexPatterns));
                }

                if(dateCreated!=null) {
                    batchQuery.addCriteria(Criteria.where("dateCreated").gte(Date.from(dateCreated.atStartOfDay(ZoneOffset.UTC).toInstant())));
                }

                if(endDate!=null) {
                    batchQuery.addCriteria(Criteria.where("endDate").lte(Date.from(endDate.atStartOfDay(ZoneOffset.UTC).toInstant())));
                }
                batchQuery.with(pageable);
                List<BatchCoupon> filteredBatchCouponsData = mongoTemplate.find(batchQuery, BatchCoupon.class);
                couponResponses= filteredBatchCouponsData.stream().map(coupon -> { return convertToCouponResponseForFilterCoupons(coupon.getBatchId(),coupon.getLotName(),coupon.getStatus(),coupon.getDateCreated(),coupon.getEndDate());}).toList();
                break;
            default:
                throw new BadApiRequestException("Invalid coupon category: " + couponCategory);
        }

        return PageableExecutionUtils.getPage(
                couponResponses,
                pageable,
                () -> mongoTemplate.count(query.skip(0).limit(0), UniqueCoupon.class)
        );
    }

    @Override
    public Page<CouponResponseForFilterCoupons> searchCoupons(String code, String couponCategory,int page,int size) {

        List<CouponResponseForFilterCoupons> couponResponseForFilterCoupons=new ArrayList<>();
        long totalElements=0;
        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.ASC, "dateCreated"));
        if(couponCategory.equalsIgnoreCase(CouponConstant.COUPONCATEGORY_BASE)) {
            Page<BaseCoupon> pagedBaseCouponData=baseCouponRepository.findByBaseCouponCodeContainingIgnoreCase(code,pageable);
            List<BaseCoupon> baseCouponsFromDB=pagedBaseCouponData.getContent();
            couponResponseForFilterCoupons=baseCouponsFromDB.stream().map(baseCoupon -> { return convertToCouponResponseForFilterCoupons(baseCoupon.getBaseCouponCode(),baseCoupon.getCouponName(),baseCoupon.getStatus(),baseCoupon.getDateCreated(),baseCoupon.getTermsAndConditionsExpiryDate());}).toList();
            totalElements=pagedBaseCouponData.getTotalElements();
        } else if (couponCategory.equalsIgnoreCase(CouponConstant.COUPONCATEGORY_UNIQUE)) {
            Page<UniqueCoupon> pagedUniqueCoupons = uniqueCouponRepository.findByIdContainingIgnoreCase(code,pageable);
            totalElements=pagedUniqueCoupons.getTotalElements();
            List<UniqueCoupon> uniqueCoupons=pagedUniqueCoupons.getContent();
            couponResponseForFilterCoupons=uniqueCoupons.stream()
                    .map(coupon -> { return convertToCouponResponseForFilterCoupons(coupon.getUniqueCouponCode(),coupon.getDisplayName(),coupon.getStatus(),coupon.getDateCreated(),coupon.getEndDate());}).toList();
        }
        else if (couponCategory.equalsIgnoreCase(CouponConstant.COUPONCATEGORY_BATCH)){
            Page<BatchCoupon> pagedBatchCoupons=batchCouponRepository.findByBatchIdContainingIgnoreCase(code,pageable);
            totalElements=pagedBatchCoupons.getTotalElements();
            List<BatchCoupon> batchCouponsData=pagedBatchCoupons.getContent();
            couponResponseForFilterCoupons=batchCouponsData.stream().map(coupon -> { return convertToCouponResponseForFilterCoupons(coupon.getBatchId(),coupon.getLotName(),coupon.getStatus(),coupon.getDateCreated(),coupon.getEndDate());}).toList();
        }
        else{
            throw new BadApiRequestException("Invalid Coupon Category "+ couponCategory);
        }
        return new PageImpl<>(couponResponseForFilterCoupons,pageable,totalElements) ;
    }

    @Override
    public Page<CouponResponseForFilterCoupons> getBaseCoupons(int page, int size) {

        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.ASC, "dateCreated"));
        Page<BaseCoupon> pagedBaseCouponData=baseCouponRepository.findAll(pageable);
        List<BaseCoupon> baseCouponsFromDB=pagedBaseCouponData.getContent();
        List<CouponResponseForFilterCoupons> couponResponseForFilterCoupons=baseCouponsFromDB.stream().map(baseCoupon -> { return convertToCouponResponseForFilterCoupons(baseCoupon.getBaseCouponCode(),baseCoupon.getCouponName(),baseCoupon.getStatus(),baseCoupon.getDateCreated(),baseCoupon.getTermsAndConditionsExpiryDate());}).toList();
        long totalElements=pagedBaseCouponData.getTotalElements();
        return new PageImpl<>(couponResponseForFilterCoupons,pageable,totalElements) ;
    }

    @Override
    public Page<CouponResponseForFilterCoupons> getUniqueCoupons(int page, int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.ASC, "dateCreated"));
        Page<UniqueCoupon> pagedBatchCouponData=uniqueCouponRepository.findAll(pageable);
        List<UniqueCoupon> uniqueCouponsFromDB=pagedBatchCouponData.getContent();
        List<CouponResponseForFilterCoupons> couponResponseForFilterCoupons=uniqueCouponsFromDB.stream().map(coupon -> { return convertToCouponResponseForFilterCoupons(coupon.getUniqueCouponCode(),coupon.getDisplayName(),coupon.getStatus(),coupon.getDateCreated(),coupon.getEndDate());}).toList();
        long totalElements=pagedBatchCouponData.getTotalElements();
        return new PageImpl<>(couponResponseForFilterCoupons,pageable,totalElements) ;
    }

    @Override
    public Page<CouponResponseForFilterCoupons> getBatchCoupons(int page, int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.ASC, "dateCreated"));
        Page<BatchCoupon> pagedBatchCouponData=batchCouponRepository.findAll(pageable);
        List<BatchCoupon> batchCouponsFromDB=pagedBatchCouponData.getContent();
        List<CouponResponseForFilterCoupons> couponResponseForFilterCoupons=batchCouponsFromDB.stream().map(coupon -> { return convertToCouponResponseForFilterCoupons(coupon.getBatchId(),coupon.getLotName(),coupon.getStatus(),coupon.getDateCreated(),coupon.getEndDate());}).toList();
        long totalElements=pagedBatchCouponData.getTotalElements();
        return new PageImpl<>(couponResponseForFilterCoupons,pageable,totalElements) ;
    }


    //Not completed , need updation as per creation of milestone
    @Override
    public MilestoneResponse getMilestoneCoupons(String number) {

        //Order check here to be applied

        List<SegmentData> segments=segmentRepository.findByMobileNumber(number);

        List<DataLakeTable> dataLakeTables=dataLakeDataRepository.findByMobileNumber(number);

        //This is wrong check this too
        if(segments.isEmpty() && dataLakeTables.isEmpty()) {
            SegmentMilestoneMapping segmentMilestoneMapping=segmentMilestoneMappingRepository.findById("12340").orElseThrow(()->new BadApiRequestException("ON Boarding Journey .milestones_segment_mapping code is wrong"));
            SegmentMilestoneMappingResponse milestones=SegmentMilestoneMappingResponse.builder().consumed(false).couponCode(segmentMilestoneMapping.getCouponCode()).milestoneNumber(1).build();
            CouponResponseWithCode couponResponseWithCode= getCouponUsingCouponCode(segmentMilestoneMapping.getCouponCode());
            milestones.setCouponData(couponResponseWithCode);
            return MilestoneResponse.builder().status(CouponConstant.SUCCESS).presentMilestone(1).milestones(new ArrayList<>(){ {add(milestones);}}).build();
        }

        //If status code is 200 but status message is failure then , user do not exists in any segment and is also not a new user. so no milestone is attached to user
        if(segments.isEmpty()){
            return MilestoneResponse.builder().status(CouponConstant.SUCCESS).presentMilestone(0).build();
        }

        log.info("Segment Found: "+segments.toString());
        List<SegmentDetails> segmentDetails=segments.stream().map(SegmentData::getSegmentAttached).toList();

        List<MilestoneDetails>  milestoneDetails=milestoneDetailsRepository.findBySegmentIn(segmentDetails);

        List<MilestoneDetails> modifiedMilestoneDetails=milestoneDetails.stream().filter(data-> !data.getStatus().equalsIgnoreCase(CouponConstant.DELETED) && !data.getStatus().equalsIgnoreCase(CouponConstant.DEACTIVATED))
                .map(data-> {
            if(data.getStartDate().isBefore(LocalDate.now()) && data.getEndDate().isAfter(LocalDate.now())){
                data.setStatus(CouponConstant.ACTIVE);
            } else if (data.getEndDate().isBefore(LocalDate.now())) {
                data.setStatus(CouponConstant.EXPIRED);
            }
            else if(data.getStartDate().isAfter(LocalDate.now())){
                data.setStatus(CouponConstant.CREATED);
            }
            return data;
        }).toList();

        List<MilestoneDetails> savedMilestoneDetailsData= milestoneDetailsRepository.saveAll(modifiedMilestoneDetails);


        List<SegmentMilestoneMapping> segmentMilestoneMappings=segmentMilestoneMappingRepository.findByMilestoneDetailsIn(savedMilestoneDetailsData);
        List<SegmentMilestoneMapping> sortedSegmentMilestoneMappings = segmentMilestoneMappings.stream()
                .filter(mapping -> CouponConstant.ACTIVE.equalsIgnoreCase(mapping.getMilestoneDetails().getStatus()))
                .sorted(Comparator.comparing(SegmentMilestoneMapping::getJourneyDateCreated).thenComparing(mapping -> mapping.getMilestoneDetails().getStartDate()))
                .toList();

        String journeyName=(!sortedSegmentMilestoneMappings.isEmpty())?sortedSegmentMilestoneMappings.get(0).getMilestoneDetails().getJourneyName():"";

        if(journeyName.isEmpty()){
            log.error("Error : No Journey found for this user : "+number);
            throw  new BadApiRequestException("No active journey found for this user");
        }

        log.info("Segment Milestone Mapping Found: "+" "+sortedSegmentMilestoneMappings.size()+" , Data "+sortedSegmentMilestoneMappings.toString());

        List<SegmentMilestoneMappingResponse> segmentMilestoneMappingResponses=new ArrayList<>();

        int presentMilestoneCheck=0;

        List<SegmentMilestoneMapping> trimmedSegmentMilestoneMappings=sortedSegmentMilestoneMappings.stream()
                .filter(mapping-> journeyName.equalsIgnoreCase(mapping.getMilestoneDetails().getJourneyName())).toList();

        log.info("Trimmed Segment Milestone Mapping Found: "+" "+trimmedSegmentMilestoneMappings.size()+" , Data "+trimmedSegmentMilestoneMappings.toString());

        boolean lockCheck=false;

        for(int i=0;i<trimmedSegmentMilestoneMappings.size();i++) {
            SegmentMilestoneMapping segmentMilestoneMapping=trimmedSegmentMilestoneMappings.get(i);
            SegmentMilestoneMappingResponse segmentMilestoneMappingResponse=SegmentMilestoneMappingResponse.builder().couponCode(segmentMilestoneMapping.getCouponCode())
                    .milestoneNumber(segmentMilestoneMapping.getMilestoneNumber()).milestoneName(segmentMilestoneMapping.getMilestoneName()).build();

            //Implement isLocked(using sequential) and checkwith orders

            if(Boolean.TRUE.equals(transactionRepository.existsByCouponCodeAndMobileNumber(segmentMilestoneMapping.getCouponCode(),number))){
                segmentMilestoneMappingResponse.setConsumed(true);
                segmentMilestoneMappingResponse.setLocked(false);
                presentMilestoneCheck=i+1;
            }
            else if( !dataLakeTables.isEmpty() && dataLakeTables.get(0).getNumberOfOrders()>=segmentMilestoneMapping.getNumbersOfOrders() && !segmentMilestoneMapping.getMilestoneDetails().isSequential()){
                    segmentMilestoneMappingResponse.setConsumed(false);
                    segmentMilestoneMappingResponse.setLocked(false);
            }
            else if(!dataLakeTables.isEmpty() && dataLakeTables.get(0).getNumberOfOrders()>=segmentMilestoneMapping.getNumbersOfOrders() && segmentMilestoneMapping.getMilestoneDetails().isSequential()){
                segmentMilestoneMappingResponse.setConsumed(false);
                if(lockCheck){
                    segmentMilestoneMappingResponse.setLocked(true);
                }
                else{
                    lockCheck=!lockCheck;
                    segmentMilestoneMappingResponse.setLocked(false);
                }
            }
            else{
                segmentMilestoneMappingResponse.setConsumed(false);
                segmentMilestoneMappingResponse.setLocked(true);
            }
            CouponResponseWithCode couponResponseWithCode= getCouponUsingCouponCode(segmentMilestoneMapping.getCouponCode());
            segmentMilestoneMappingResponse.setCouponData(couponResponseWithCode);
            segmentMilestoneMappingResponses.add(segmentMilestoneMappingResponse);
        }

        return MilestoneResponse.builder().status(CouponConstant.SUCCESS).milestones(segmentMilestoneMappingResponses)
                .presentMilestone(presentMilestoneCheck).journeyName(journeyName).build();
    }

    @Override
    public Page<MilestoneDetailsResponse> getMilestoneDetails(int page, int size) {

        Pageable pageable=PageRequest.of(page,size, Sort.by(Sort.Direction.ASC, "dateCreated"));
        Page<MilestoneDetails> pagedMilestoneDetails=milestoneDetailsRepository.findAll(pageable);
        List<MilestoneDetails> milestoneDetails=pagedMilestoneDetails.getContent();
        List<MilestoneDetailsResponse> milestoneDetailsResponses=milestoneDetails.stream()
                .map(data-> MilestoneDetailsResponse.builder().journeyName(data.getJourneyName())
                        .dateCreated(data.getCreatedDate()).endDate(data.getEndDate()).lastUpdatedBy(data.getLastUpdatedBy()).status(data.getStatus()).build()).toList();

        long totalElements=pagedMilestoneDetails.getTotalElements();
        return new PageImpl<>(milestoneDetailsResponses,pageable,totalElements);
    }

    @Override
    public ResponseMessage setCouponsStatus(UpdateStatusRequest updateStatusRequest) throws Exception {

        String couponCategory = updateStatusRequest.getCouponCategory();
        List<String> couponCode=updateStatusRequest.getCouponCode();
        String status=updateStatusRequest.getStatus();


            if (couponCategory.equalsIgnoreCase(CouponConstant.COUPONCATEGORY_BASE)) {
                for (String code : couponCode) {
                    BaseCoupon baseCoupon = baseCouponRepository.findByIdIgnoreCase(code).orElseThrow(() -> new BadApiRequestException(code + " coupon code from Base Coupon List is invalid"));
                    baseCoupon.setStatus(status);
                    if(status.equalsIgnoreCase(CouponConstant.DEACTIVATED)) {
                        List<UniqueCoupon> uniqueCouponsList = uniqueCouponRepository.findByBaseCouponCodeIgnoreCase(baseCoupon.getBaseCouponCode());
                        List<BatchCoupon> batchCouponList = new ArrayList<>();
                        List<UniqueCoupon> updatedUniqueCouponList = uniqueCouponsList.stream().map(uniqueCoupon -> {
                            uniqueCoupon.setStatus(status);
                            batchCouponList.addAll(batchCouponRepository.findByUniqueCouponIdIgnoreCase(uniqueCoupon.getUniqueCouponCode()));
                            return uniqueCoupon;
                        }).toList();
                        List<BatchCoupon> updatedBatchCouponList = batchCouponList.stream().peek(batchCoupon -> batchCoupon.setStatus(status)).toList();

                        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                        TransactionStatus transactionStatus = transactionManager.getTransaction(def);

                        try {
                            uniqueCouponRepository.saveAll(updatedUniqueCouponList);
                            batchCouponRepository.saveAll(updatedBatchCouponList);
                            baseCouponRepository.save(baseCoupon);
                            transactionManager.commit(transactionStatus);
                        }
                        catch (Exception e){
                            log.error("Error in saving data in database in Mongodb");
                            transactionManager.rollback(transactionStatus);
                            throw new Exception("Error occured in saving data in database");
                        }
                    }

                }
            } else if (couponCategory.equalsIgnoreCase(CouponConstant.COUPONCATEGORY_UNIQUE)) {
                for (String code : couponCode) {
                    UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(code).orElseThrow(() -> new BadApiRequestException(code + " coupon code from Unique Coupon List is invalid"));
                    uniqueCoupon.setStatus(status);
                    if(status.equalsIgnoreCase(CouponConstant.DEACTIVATED)) {
                        List<BatchCoupon> batchCouponList = batchCouponRepository.findByUniqueCouponIdIgnoreCase(uniqueCoupon.getUniqueCouponCode());
                        List<BatchCoupon> updatedBatchCouponList = batchCouponList.stream().peek(batchCoupon -> batchCoupon.setStatus(status)).toList();
                        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                        TransactionStatus transactionStatus = transactionManager.getTransaction(def);
                        try {
                            batchCouponRepository.saveAll(updatedBatchCouponList);
                            uniqueCouponRepository.save(uniqueCoupon);
                            transactionManager.commit(transactionStatus);
                        }
                        catch (Exception e){
                            log.error("Error in saving data in database in Mongodb");
                            transactionManager.rollback(transactionStatus);
                            throw new Exception("Error occured in saving data in database");
                        }
                    }

                }
            } else if (couponCategory.equalsIgnoreCase(CouponConstant.COUPONCATEGORY_BATCH)) {
                for (String code : couponCode) {
                    BatchCoupon batchCoupon = batchCouponRepository.findByIdIgnoreCase(code).orElseThrow(() -> new BadApiRequestException(code + " coupon code from Batch Coupon List is invalid"));
                    batchCoupon.setStatus(status);
                    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                    TransactionStatus transactionStatus = transactionManager.getTransaction(def);
                    try {
                        batchCouponRepository.save(batchCoupon);
                        transactionManager.commit(transactionStatus);
                    }
                    catch (Exception e){
                        log.error("Error in saving data in database in Mongodb");
                        transactionManager.rollback(transactionStatus);
                        throw new Exception("Error occured in saving data in database");
                    }
                }
            } else {
                throw new BadApiRequestException("Invalid Coupon Category");
            }

        return ResponseMessage.builder().status(CouponConstant.SUCCESS).message("Status saved successfully").build();
    }

    @Override
    public GetCouponUsageResponse getUniqueCouponUsage(String code) {
        UniqueCoupon uniqueCoupon=uniqueCouponRepository.findByIdIgnoreCase(code).orElseThrow(()-> new BadApiRequestException("Given Coupon code not found"));
        GetCouponUsageResponse couponUsageResponse=GetCouponUsageResponse.builder().couponName(code).couponUsage(uniqueCoupon.getCouponUsage())
                .usageType(uniqueCoupon.getUsageType()).uniqueCouponPrefix(uniqueCoupon.getCouponPrefix()).uniqueCouponSuffix(uniqueCoupon.getCouponSuffix())
                .batchName(uniqueCoupon.getLotName()).quantity(uniqueCoupon.getLotQuantity()).build();

        return couponUsageResponse;
    }

    @Override
    public GetCouponApplicabilityResponse getUniqueCouponApplicability(String code) {

        UniqueCoupon uniqueCoupon=uniqueCouponRepository.findByIdIgnoreCase(code).orElseThrow(()-> new BadApiRequestException("Given Coupon code not found"));
        GetCouponApplicabilityResponse getCouponApplicabilityResponse=GetCouponApplicabilityResponse.builder().couponCode(uniqueCoupon.getUniqueCouponCode()).channel(uniqueCoupon.getChannel())
                .channelFulfillmentType(uniqueCoupon.getChannelFullfillmentType()).franchise(uniqueCoupon.getFranchise()).stores(uniqueCoupon.getStores()).cities(uniqueCoupon.getCities())
                .clusters(uniqueCoupon.getClusters()).dayApplicability(uniqueCoupon.getDayApplicability()).monthApplicability(uniqueCoupon.getMonthApplicability())
                .build();


        TimeSlotApplicability timeSlotApplicability=TimeSlotApplicability.builder().startTime(uniqueCoupon.getTimeslot().getStartTime()).endTime(uniqueCoupon.getTimeslot().getEndTime()).build();

        getCouponApplicabilityResponse.setTimeslot(timeSlotApplicability);

        return getCouponApplicabilityResponse;
    }

    @Override
    public CouponUserSpecificConstraintsResponse getUniqueCouponUserSpecificConstraints(String code) {

        UniqueCoupon uniqueCoupon=uniqueCouponRepository.findByIdIgnoreCase(code).orElseThrow(()-> new BadApiRequestException("Given Coupon code not found"));

        CouponUserSpecificConstraintsResponse couponUserSpecificConstraintsResponse=CouponUserSpecificConstraintsResponse.builder().couponCode(code)
                .enableConstraints(uniqueCoupon.isUserSpecificConstraints()).constraintType(uniqueCoupon.getConstraintType()).numberOfDays(uniqueCoupon.getNumberOfDays())
                .extendedNumberOfDays(uniqueCoupon.getExtendedNumberOfDays()).firstExpiryDate(uniqueCoupon.getFirstExpiryDate())
                .extendedExpiryDate(uniqueCoupon.getExtendedExpiryDate()).numberOfTimesApplicablePerUser(uniqueCoupon.getNumberOfTimesApplicablePerUser()).build();

        return couponUserSpecificConstraintsResponse;
    }

    @Override
    public UniqueCouponConstructResponse getUniqueCouponConstruts(String code) {

        UniqueCoupon uniqueCoupon=uniqueCouponRepository.findByIdIgnoreCase(code).orElseThrow(()-> new BadApiRequestException("Given Coupon code not found"));
        UniqueCouponConstructResponse uniqueCouponConstructResponse=UniqueCouponConstructResponse.builder().couponCode(uniqueCoupon.getUniqueCouponCode())
                .constructType(uniqueCoupon.getCouponType()).freebieItemMongo(uniqueCoupon.getFreebieItems()).discountPercentage(uniqueCoupon.getDiscountPercentage())
                .flatDiscount(uniqueCoupon.getFlatDiscount()).discountCap(uniqueCoupon.getDiscountCap()).minimumOrderValue(uniqueCoupon.getMov()).productExclusion(uniqueCoupon.getProductExclusion())
                .productInclusion(uniqueCoupon.getProductInclusion())
                .freebieItemFile(generateDownloadLink(CouponConstant.TEMPLATE_FREEBIE, "freebie_template.xlsx"))
                .productExclusionFile(generateDownloadLink(CouponConstant.TEMPLATE_PRODUCT_EXCLUSION, "exclusion_template.xlsx"))
                .productInclusionFile(generateDownloadLink(CouponConstant.TEMPLATE_PRODUCT_INCLUSION, "inclusion_template.xlsx"))
                .build();
        return uniqueCouponConstructResponse;
    }
    private String generateDownloadLink(String type, String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/coupon/download_general/")
                .path(type)
                .toUriString();
    }


    @Override
    public List<CategoryProductResponse> getProductsGroupedByCategory() {
        List<ProductMaster> products = productMasterRepository.findAllOrderedByCategory();

        if(products.isEmpty()){
            log.error("Error in fetching data from Product Master database");
            throw new BadApiRequestException("Error in fetching data from Product Master");
        }

        // Map to CategoryProductResponse
        Map<String, List<ItemsData>> groupedByCategory = new LinkedHashMap<>();

        for (ProductMaster product : products) {
            // Skip products with the category "Do not consider"
            if ("Do not consider".equalsIgnoreCase(product.getCategory())) {
                continue;
            }
            String category = product.getCategory();
            ItemsData itemData = new ItemsData();
            itemData.setItemCode(product.getItemCode());
            itemData.setItemName(product.getName());
            itemData.setSize(product.getSize());

            groupedByCategory
                    .computeIfAbsent(category, k -> new ArrayList<>())
                    .add(itemData);
        }

        List<CategoryProductResponse> responseList = new ArrayList<>();

        for (Map.Entry<String, List<ItemsData>> entry : groupedByCategory.entrySet()) {
            CategoryProductResponse response = new CategoryProductResponse();
            response.setCategory(entry.getKey());
            response.setItemsData(entry.getValue());
            responseList.add(response);
        }

        return responseList;
    }

    @Override
    public StoreMasterResponse getOperationalStores(){

        List<StoreMaster> operationalStores = storeMasterRepository.findAllOperationalStores();

        if(operationalStores.isEmpty()){
            log.error("Error in fetching data from Store Master database ");
            throw new BadApiRequestException("Error in fetching data from database");
        }

        // Map StoreMaster entities to StoreMaster DTOs
        List<StoreMasterData> storeMasterDTOList = operationalStores.stream().map(store -> {
            StoreMasterData dto = new StoreMasterData();
            dto.setStoreCode(store.getStoreCode());
            dto.setCity(store.getCity());
            dto.setCluster(store.getCluster());
            return dto;
        }).collect(Collectors.toList());

        // Create the response object
        StoreMasterResponse response = new StoreMasterResponse();
        response.setStatus(CouponConstant.SUCCESS);
        response.setStoreMasterContent(storeMasterDTOList);

        return response;
    }









}
