package com.project.coupon.service.implementation;

import com.project.coupon.constants.CouponConstant;
import com.project.coupon.entity.*;
import com.project.coupon.exceptions.*;
import com.project.coupon.jpaEntities.JpaItems;
import com.project.coupon.jpaEntities.ProductMaster;
import com.project.coupon.jpaEntities.StoreMaster;
import com.project.coupon.jpaEntities.TransactionTable;
import com.project.coupon.jpaRepositories.ProductMasterRepository;
import com.project.coupon.jpaRepositories.StoreMasterRepository;
import com.project.coupon.jpaRepositories.TransactionRepository;
import com.project.coupon.repository.*;
import com.project.coupon.request.OrderItems;
import com.project.coupon.request.PosInputRequest;
import com.project.coupon.response.ItemResponseInfo;
import com.project.coupon.response.PosOutputResponse;
import com.project.coupon.service.CouponRedeem;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CouponRedeemImplementation implements CouponRedeem {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UniqueCouponRepository uniqueCouponRepository;

    @Autowired
    private BaseCouponRepository baseCouponRepository;

    @Autowired
    private BatchCouponRepository batchCouponRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Autowired
    private StoreMasterRepository storeMasterRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private SegmentDataRepository segmentDataRepository;

    @Override
    public PosOutputResponse redeemCoupon(PosInputRequest posInputRequest) throws Exception {
        log.info("Starting coupon redemption process for coupon code: {}", posInputRequest.getCouponCode());

        String id = UUID.randomUUID().toString();
        //THis one is pending do it , later
        TransactionTable transactionTable = TransactionTable.builder().redemptionNumber(id).couponCode(posInputRequest.getCouponCode()).mobileNumber(posInputRequest.getNumber())
                .name(posInputRequest.getName()).dateTime(LocalDateTime.of(posInputRequest.getDate(), posInputRequest.getTime())).store(posInputRequest.getStoreCode())
                .netAmount(posInputRequest.getNetAmount()).channel(posInputRequest.getChannel()).channelFullfillment(posInputRequest.getFullfillmentMode()).franchise(posInputRequest.getFranchise())
                .receiptNumber(posInputRequest.getReceiptNumber()).transactionType(posInputRequest.getTransactionType()).dateApplicability(posInputRequest.getDate().getDayOfMonth())
                .dayApplicability(posInputRequest.getDate().getDayOfWeek().toString())
                .build();

        List<OrderItems> orderItemsList = posInputRequest.getOrderDetails();
        List<JpaItems> jpaItemsList = new ArrayList<>();

        for (int i = 0; i < orderItemsList.size(); i++) {
            JpaItems jpaItems = new JpaItems();
            OrderItems orderItems = orderItemsList.get(i);
            jpaItems.setItemCode(orderItems.getItemCode());
            jpaItems.setSize(orderItems.getSize());
            jpaItems.setBase(orderItems.getBase());
            jpaItems.setPrice(orderItems.getPrice());
            jpaItems.setQuantity(orderItems.getQuantity());
            jpaItems.setDealId(orderItems.getDealId());
            jpaItems.setTransactionTable(transactionTable);
            jpaItemsList.add(jpaItems);
        }

        transactionTable.setOrderItems(jpaItemsList);

        PosOutputResponse posOutputResponse = PosOutputResponse.builder().store(posInputRequest.getStoreCode()).channel(posInputRequest.getChannel())
                .fullfillmentMode(posInputRequest.getFullfillmentMode()).receiptNumber(posInputRequest.getReceiptNumber()).couponCode(posInputRequest.getCouponCode())
                .date(posInputRequest.getDate()).time(posInputRequest.getTime()).redemptionRefNumber(id).netAmount(posInputRequest.getNetAmount()).franchise(posInputRequest.getFranchise()).build();

        String reciptNumber = posInputRequest.getReceiptNumber();

//        if (transactionRepository.findByIdIgnoreCase(reciptNumber).isPresent()) {
//            throw new BadApiRequestException("Receipt number is already used");
//        }

            if (uniqueCouponRepository.existsByIdIgnoreCase(posInputRequest.getCouponCode())) {
                log.debug("Coupon code found in UniqueCoupon repository: {}", posInputRequest.getCouponCode());
                UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(posInputRequest.getCouponCode()).get();

                BaseCoupon baseCoupon = baseCouponRepository.findByIdIgnoreCase(uniqueCoupon.getBaseCouponCode()).get();
                transactionTable.setBaseCouponCode(baseCoupon.getBaseCouponCode());
                transactionTable.setTermsAndConditions(uniqueCoupon.getTermsAndConditions());
                posOutputResponse.setCouponInfo(uniqueCoupon.getTermsAndConditions());
                posOutputResponse.setCouponSource(baseCoupon.getBaseCouponCode());

                if (!baseCoupon.getStatus().equalsIgnoreCase(CouponConstant.ACTIVE)) {
                    log.error("Base Coupon is not Active , Status: " + baseCoupon.getStatus());
                    throw new BadApiRequestException("Base Coupon is not active, Status: " + baseCoupon.getStatus());
                }

                if (!uniqueCoupon.getStatus().equalsIgnoreCase(CouponConstant.ACTIVE)) {
                    log.error("Unique Coupon is not Active, Status: " + uniqueCoupon.getStatus());
                    throw new BadApiRequestException("Unique Coupon is not active, Status: " + uniqueCoupon.getStatus());
                }

                if (baseCoupon.getTermsAndConditionsExpiryDate().isBefore(uniqueCoupon.getEndDate())) {
                    log.error("Base Coupon has expired on {}", baseCoupon.getTermsAndConditionsExpiryDate());

                    throw new ExpiredCouponException("Base Coupon has expired", baseCoupon.getTermsAndConditionsExpiryDate());
                }

                if (uniqueCoupon.getStartDate().isAfter(LocalDate.now())) {
                    log.error("Coupon is not activated yet but created on {}", uniqueCoupon.getStartDate());
                    throw new NotActiveCouponException("Coupon is not activated yet but created", uniqueCoupon.getStartDate());
                }

                if (uniqueCoupon.getEndDate().isBefore(LocalDate.now())) {
                    log.error("Coupon has expired on {}", uniqueCoupon.getEndDate());
                    throw new ExpiredCouponException("Coupon has expired", uniqueCoupon.getEndDate());
                }

                transactionTable.setStartDate(uniqueCoupon.getStartDate());
                transactionTable.setEndDate(uniqueCoupon.getEndDate());

                if (transactionRepository.getNumberOfColumnsWithCouponCode(posInputRequest.getCouponCode()) >= uniqueCoupon.getTotalUsage()) {
                    log.error("Maximum limit to redeem coupon has been reached for coupon code: {}", posInputRequest.getCouponCode());

                    throw new BadApiRequestException(" Maximum limit to redeem coupon has been reached ");
                }

                List<TransactionTable> transactionTableDbData = transactionRepository.findByStatusAndCouponCodeAndMobileNumber(posInputRequest.getCouponCode(), posInputRequest.getNumber());
                log.info("Transaction Table Data: {}", transactionTableDbData);
                if (!transactionTableDbData.isEmpty() && transactionTableDbData.size() >= uniqueCoupon.getNumberOfTimesApplicablePerUser()) {
                    log.error("Maximum limit to redeem coupon per user has been reached for user: {}", posInputRequest.getNumber());
                    throw new BadApiRequestException("Maximum limit to redeem coupon per user has been reached for current user");
                }

                //New users cannot redeem coupons
                if(transactionTableDbData.isEmpty() && transactionRepository.getNumberOfColumnswithDistintMobileNumber(posInputRequest.getCouponCode())>=uniqueCoupon.getNumberOfUniqueUsers()){

                    log.error("Maximum limit to redeem coupon for number of unique user has been reached for this coupon {}",posInputRequest.getCouponCode());
                    throw new BadApiRequestException("Maximum limit to redeem coupon for number of unique user has been reached for this coupon");
                }

                log.info("Now checking for type of coupon: {}", uniqueCoupon.getType());

                posOutputResponse.setMov(uniqueCoupon.getMov());
                    posOutputResponse.setDiscountType(uniqueCoupon.getCouponType());

                    transactionTable.setCouponType(uniqueCoupon.getCouponType());
                    transactionTable.setCouponUsage(uniqueCoupon.getCouponUsage());
                    transactionTable.setMov(uniqueCoupon.getMov());

                if (uniqueCoupon.getApplicableForValue().equalsIgnoreCase(CouponConstant.APPLICABLEFORVALUE_ALL)){

                    if ((uniqueCoupon.getExtendedNumberOfDays() != 0 && uniqueCoupon.getUserAttachedDate().plusDays(uniqueCoupon.getExtendedNumberOfDays() + uniqueCoupon.getNumberOfDays()).isBefore(LocalDate.now())) || (uniqueCoupon.getExtendedNumberOfDays() == 0 && uniqueCoupon.getNumberOfDays() != 0 && uniqueCoupon.getUserAttachedDate().plusDays(uniqueCoupon.getNumberOfDays()).isBefore(LocalDate.now()))) {
                        LocalDate date = uniqueCoupon.getUserAttachedDate().plusDays(uniqueCoupon.getNumberOfDays());
                        if (uniqueCoupon.getExtendedNumberOfDays() != 0) {
                            date = uniqueCoupon.getUserAttachedDate().plusDays(uniqueCoupon.getExtendedNumberOfDays() + uniqueCoupon.getNumberOfDays());
                        }
                        log.error("Coupon code expired on {}", date);
                        throw new ExpiredCouponException("Coupon code expired", date);
                    }
                }
                else if (uniqueCoupon.getApplicableForValue().equalsIgnoreCase(CouponConstant.APPLICABLEFOR_USERSEGMENT)) {
                        SegmentDetails dbSegmentUsers = uniqueCoupon.getApplicableForSegments();
                        List<SegmentData> segmentDataList=segmentDataRepository.findBySegmentAttached(dbSegmentUsers);
                        //List<Users> dbSegmentUsers = dbSegment.getUsersList();
//                        if (segmentDataList.stream().noneMatch(user -> posInputRequest.getNumber().equals(user.getMobileNumber()))) {
//                            log.error("This coupon cannot be redeemed by this user: {}", posInputRequest.getNumber());
//
//                            throw new BadApiRequestException("This coupon cannot be redeemed by this user");
//                        }

                        SegmentData segmentDataCheck=new SegmentData();
                        boolean check=false;
                        for(SegmentData data:  segmentDataList){
                            if(data.getMobileNumber().equals(posInputRequest.getNumber())) {
                                check=true;
                                segmentDataCheck=data;
                                break;
                            }
                        }

                        if(!check){
                            log.error("This coupon cannot be redeemed by this user: {}", posInputRequest.getNumber());
                            throw new BadApiRequestException("This coupon cannot be redeemed by this user");
                        }

                        if ((uniqueCoupon.getExtendedNumberOfDays() != 0 && segmentDataCheck.getSegmentAttachedDate().plusDays(uniqueCoupon.getExtendedNumberOfDays() + uniqueCoupon.getNumberOfDays()).isBefore(LocalDate.now())) || (uniqueCoupon.getExtendedNumberOfDays() == 0 && uniqueCoupon.getNumberOfDays() != 0 && segmentDataCheck.getSegmentAttachedDate().plusDays(uniqueCoupon.getNumberOfDays()).isBefore(LocalDate.now()))) {
                            LocalDate date = segmentDataCheck.getSegmentAttachedDate().plusDays(uniqueCoupon.getNumberOfDays());
                            if (uniqueCoupon.getExtendedNumberOfDays() != 0) {
                                date = segmentDataCheck.getSegmentAttachedDate().plusDays(uniqueCoupon.getExtendedNumberOfDays() + uniqueCoupon.getNumberOfDays());
                            }
                            log.error("Coupon code expired on {}", date);
                            throw new ExpiredCouponException("Coupon code expired", date);
                        }
                }

                //New User Segment code implementation in future

                if ((uniqueCoupon.getExtendedExpiryDate() != null && uniqueCoupon.getExtendedExpiryDate().isBefore(LocalDate.now())) || (uniqueCoupon.getExtendedExpiryDate() == null && uniqueCoupon.getFirstExpiryDate() != null && uniqueCoupon.getFirstExpiryDate().isBefore(LocalDate.now()))) {
                    LocalDate date = uniqueCoupon.getFirstExpiryDate();
                    if (uniqueCoupon.getExtendedExpiryDate() != null) {
                        date = uniqueCoupon.getExtendedExpiryDate();
                    }
                    log.error("Coupon code expired on {}", date);
                    throw new ExpiredCouponException("Coupon code expired ", date);
                }

                    if (filterData(uniqueCoupon.getChannel(), posInputRequest.getChannel())) {
                        log.error("This coupon is not applicable for this Channel: {}", posInputRequest.getChannel());
                        throw new BadApiRequestException("This coupon is not applicable for this Channel");
                    }


                    if (filterData(uniqueCoupon.getChannelFullfillmentType(), posInputRequest.getFullfillmentMode())) {
                        log.error("This coupon is not applicable for this FullFillmentMode type: {}", posInputRequest.getFullfillmentMode());
                        throw new BadApiRequestException("This coupon is not applicable for this FullFillmentMode type");
                    }

                    if (filterData(uniqueCoupon.getFranchise(), posInputRequest.getFranchise())) {
                        log.error("This coupon is not applicable for this Franchise: {}", posInputRequest.getFranchise());
                        throw new BadApiRequestException("This coupon is not applicable for this Franchise");
                    }

                    if (filterData(uniqueCoupon.getStores(), posInputRequest.getStoreCode())) {
                        log.error("This coupon is not applicable for this Store: {}", posInputRequest.getStoreCode());
                        throw new BadApiRequestException("This coupon is not applicable for this Store");
                    }

                    //city check

                    StoreMaster storeMasterList = storeMasterRepository.findByIdIgnoreCase(posInputRequest.getStoreCode()).orElseThrow(() -> new BadApiRequestException("Store Code is Invalid"));
                    String city = storeMasterList.getCity();
                    String cluster = storeMasterList.getCluster();

                    if (filterData(uniqueCoupon.getCities(), city)) {
                        log.error("This coupon is not applicable for this City: {}", city);
                        throw new BadApiRequestException("This coupon is not applicable for this City");
                    }

                    if (filterData(uniqueCoupon.getClusters(), cluster)) {
                        log.error("This coupon is not applicable for this cluster: {}", cluster);
                        throw new BadApiRequestException("This coupon is not applicable for this cluster");
                    }

                    String dayofWeek = posInputRequest.getDate().getDayOfWeek().toString();
                    if (!uniqueCoupon.getDayApplicability().contains(dayofWeek)) {
                        log.error("This coupon is not applicable for this day of Week: {}", dayofWeek);
                        throw new BadApiRequestException("This coupon is not applicable for this day of Week");
                    }

                    int month = posInputRequest.getDate().getMonth().getValue();
                    if (!uniqueCoupon.getMonthApplicability().contains(month)) {
                        log.error("This coupon is not applicable for this month: {}", posInputRequest.getDate().getMonth().toString());
                        throw new BadApiRequestException("This coupon is not applicable for this Month");
                    }

                    //Day part

                    if (uniqueCoupon.getTimeslot().getStartTime().isAfter(LocalTime.now()) || uniqueCoupon.getTimeslot().getEndTime().isBefore(LocalTime.now())) {
                        log.error("This coupon is not applicable for this Time of day");
                        throw new BadApiRequestException("This coupon is not applicable for this Time of day");
                    }

                    List<OrderItems> getItemsFromRequest = posInputRequest.getOrderDetails();
                    List<OrderItems> validItems = getValidItems(getItemsFromRequest, uniqueCoupon.getProductInclusion(), uniqueCoupon.getProductExclusion(), uniqueCoupon.getCategoryInclusion(), uniqueCoupon.getCategoryExclusion());

                    List<String> validItemsCode = getValidItemsCode(validItems);

                    //calculating discount
                    double totalPriceOfValidItemsBeforeDiscount = 0;

                    if (!validItems.isEmpty()) {
                        totalPriceOfValidItemsBeforeDiscount = validItems.stream().mapToDouble(OrderItems::getPrice).sum();
                    }

                    double finalDiscountAmount = 0;

                    if (uniqueCoupon.getMov() > totalPriceOfValidItemsBeforeDiscount) {
                        log.error("Minimum order value is not satisfied, required: {}", uniqueCoupon.getMov());
                        throw new MovInvalidException("Minimum order value is not satisfied", uniqueCoupon.getMov(),uniqueCoupon.getProductInclusion(),uniqueCoupon.getProductExclusion(),uniqueCoupon.getCategoryInclusion(),uniqueCoupon.getCategoryExclusion());
                    }

                    if (uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_PERCENTAGE)) {
                        double discountAmount = totalPriceOfValidItemsBeforeDiscount * uniqueCoupon.getDiscountPercentage() / 100;
                        finalDiscountAmount = Math.min(discountAmount, uniqueCoupon.getDiscountCap());
                        posOutputResponse.setDiscountMax(uniqueCoupon.getDiscountCap());
                        posOutputResponse.setCouponValue(uniqueCoupon.getDiscountPercentage());
                        posOutputResponse.setDiscountAmount(finalDiscountAmount);
                        transactionTable.setCouponValue(uniqueCoupon.getDiscountPercentage());
                        transactionTable.setDiscountAmount(finalDiscountAmount);

                        List<ItemResponseInfo> itemResponseInfos = new ArrayList<>();
                        int noOfItems = posInputRequest.getOrderDetails().size();
                        int noOfValidItems = validItems.size();
                        double discountPerItem = finalDiscountAmount / noOfValidItems;
                        for (int i = 0; i < noOfItems; i++) {
                            OrderItems orderItems = posInputRequest.getOrderDetails().get(i);
                            ItemResponseInfo itemResponseInfo = new ItemResponseInfo();
                            itemResponseInfo.setLineId(orderItems.getLineId());
                            itemResponseInfo.setItemCode(orderItems.getItemCode());
                            itemResponseInfo.setOrderItemPrice(orderItems.getPrice());
                            itemResponseInfo.setQuantity(orderItems.getQuantity());
                            itemResponseInfo.setDealId(orderItems.getDealId());
                            itemResponseInfo.setIsFreebie(false);
                            itemResponseInfo.setItemBase(orderItems.getBase());
                            itemResponseInfo.setItemSize(orderItems.getSize());

                            if (validItemsCode.contains(orderItems.getItemCode())) {
                                itemResponseInfo.setDiscount(discountPerItem);
                            } else {
                                itemResponseInfo.setDiscount(0);
                            }

                            itemResponseInfos.add(itemResponseInfo);
                        }

                        posOutputResponse.setAppliedCouponItemDetails(itemResponseInfos);


                    } else if (uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_FLAT)) {
                        finalDiscountAmount = uniqueCoupon.getFlatDiscount();
                        posOutputResponse.setDiscountMax(finalDiscountAmount);
                        posOutputResponse.setCouponValue(finalDiscountAmount);
                        transactionTable.setDiscountAmount(finalDiscountAmount);
                        transactionTable.setCouponValue(finalDiscountAmount);
                        posOutputResponse.setDiscountAmount(finalDiscountAmount);

                        List<ItemResponseInfo> itemResponseInfos = new ArrayList<>();
                        int noOfItems = posInputRequest.getOrderDetails().size();
                        int noOfValidItems = validItems.size();
                        double discountPerItem = finalDiscountAmount / noOfValidItems;
                        for (int i = 0; i < noOfItems; i++) {
                            OrderItems orderItems = posInputRequest.getOrderDetails().get(i);
                            ItemResponseInfo itemResponseInfo = new ItemResponseInfo();
                            itemResponseInfo.setLineId(orderItems.getLineId());
                            itemResponseInfo.setItemCode(orderItems.getItemCode());
                            itemResponseInfo.setOrderItemPrice(orderItems.getPrice());
                            itemResponseInfo.setQuantity(orderItems.getQuantity());
                            itemResponseInfo.setDealId(orderItems.getDealId());
                            itemResponseInfo.setIsFreebie(false);
                            itemResponseInfo.setItemBase(orderItems.getBase());
                            itemResponseInfo.setItemSize(orderItems.getSize());

                            if (validItemsCode.contains(orderItems.getItemCode())) {
                                itemResponseInfo.setDiscount(discountPerItem);
                            } else {
                                itemResponseInfo.setDiscount(0);
                            }

                            itemResponseInfos.add(itemResponseInfo);
                        }

                        posOutputResponse.setAppliedCouponItemDetails(itemResponseInfos);

                    } else if (uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_FREEBIE)) {
                        finalDiscountAmount = 0;

                        List<ItemResponseInfo> itemResponseInfos = new ArrayList<>();
                        for (int i = 0; i < uniqueCoupon.getFreebieItems().size(); i++) {
                            Items items = uniqueCoupon.getFreebieItems().get(i);
                            ItemResponseInfo itemResponseInfo = new ItemResponseInfo();
                            itemResponseInfo.setLineId(posInputRequest.getOrderDetails().get(0).getLineId());
                            itemResponseInfo.setItemCode(items.getItemCode());
                            itemResponseInfo.setOrderItemPrice(items.getItemPrice());
                            itemResponseInfo.setQuantity(items.getItemQuantity());
                            itemResponseInfo.setItemBase(items.getItemBase());
                            itemResponseInfo.setItemSize(items.getItemSize());
                            itemResponseInfo.setIsFreebie(true);
                            itemResponseInfo.setDiscount(0);

                            itemResponseInfos.add(itemResponseInfo);
                        }

                        posOutputResponse.setAppliedCouponItemDetails(itemResponseInfos);
                        posOutputResponse.setDiscountMax(finalDiscountAmount);
                        posOutputResponse.setCouponValue(finalDiscountAmount);
                        transactionTable.setDiscountAmount(finalDiscountAmount);
                        transactionTable.setCouponValue(finalDiscountAmount);
                        posOutputResponse.setDiscountAmount(finalDiscountAmount);
                    }

                    posOutputResponse.setStatus(CouponConstant.SUCCESS);
                    posOutputResponse.setMessage("Coupon Redeemed Succesfully");
                    //posOutputResponse.setStatus("redeemed");
                    transactionTable.setStatus(CouponConstant.REDEEMED);

                    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                    def.setName("MyTransaction");
                    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

                    TransactionStatus status = transactionManager.getTransaction(def);

                    try {
                        transactionRepository.save(transactionTable);
                        transactionManager.commit(status);
                    }
                    catch (Exception e){
                        log.error("Error in saving data in database in Transaction Table");
                        transactionManager.rollback(status);
                        throw new Exception("Error occured in saving data in database");
                    }

                    log.info("Coupon redemption completed successfully for coupon code: {}", posInputRequest.getCouponCode());
                    return posOutputResponse;

//                //Type check
//                if (uniqueCoupon.getType().equalsIgnoreCase(CouponConstant.TYPECOUPON_NORMAL)) {
//
//                    posOutputResponse.setMov(uniqueCoupon.getMov());
//                    posOutputResponse.setDiscountType(uniqueCoupon.getCouponType());
//
//                    transactionTable.setCouponType(uniqueCoupon.getCouponType());
//                    transactionTable.setCouponUsage(uniqueCoupon.getCouponUsage());
//                    transactionTable.setMov(uniqueCoupon.getMov());
//
//                    if ((uniqueCoupon.getExtendedExpiryDate() != null && uniqueCoupon.getExtendedExpiryDate().isBefore(LocalDate.now())) || (uniqueCoupon.getExtendedExpiryDate() == null && uniqueCoupon.getFirstExpiryDate() != null && uniqueCoupon.getFirstExpiryDate().isBefore(LocalDate.now()))) {
//                        LocalDate date = uniqueCoupon.getFirstExpiryDate();
//                        if (uniqueCoupon.getExtendedExpiryDate() != null) {
//                            date = uniqueCoupon.getExtendedExpiryDate();
//                        }
//                        log.error("Coupon code expired on {}", date);
//                        throw new ExpiredCouponException("Coupon code expired ", date);
//                    }
//
//                    if ((uniqueCoupon.getExtendedNumberOfDays() != 0 && uniqueCoupon.getUserAttachedDate().plusDays(uniqueCoupon.getExtendedNumberOfDays() + uniqueCoupon.getNumberOfDays()).isBefore(LocalDate.now())) || (uniqueCoupon.getExtendedNumberOfDays() == 0 && uniqueCoupon.getNumberOfDays() != 0 && uniqueCoupon.getUserAttachedDate().plusDays(uniqueCoupon.getNumberOfDays()).isBefore(LocalDate.now()))) {
//                        LocalDate date = uniqueCoupon.getUserAttachedDate().plusDays(uniqueCoupon.getNumberOfDays());
//                        if (uniqueCoupon.getExtendedNumberOfDays() != 0) {
//                            date = uniqueCoupon.getUserAttachedDate().plusDays(uniqueCoupon.getExtendedNumberOfDays() + uniqueCoupon.getNumberOfDays());
//                        }
//                        log.error("Coupon code expired on {}", date);
//                        throw new ExpiredCouponException("Coupon code expired", date);
//                    }
//
//                    if (!uniqueCoupon.getApplicableForValue().equalsIgnoreCase(CouponConstant.APPLICABLEFORVALUE_ALL)) {
//                        List<Segment> dbSegmentUsers = uniqueCoupon.getApplicableForSegments();
//                        //List<Users> dbSegmentUsers = dbSegment.getUsersList();
//                        if (dbSegmentUsers.stream().noneMatch(user -> posInputRequest.getNumber().equals(user.getMobileNumber()))) {
//                            log.error("This coupon cannot be redeemed by this user: {}", posInputRequest.getNumber());
//
//                            throw new BadApiRequestException("This coupon cannot be redeemed by this user");
//                        }
//                    }
//
//                    if (filterData(uniqueCoupon.getChannel(), posInputRequest.getChannel())) {
//                        log.error("This coupon is not applicable for this Channel: {}", posInputRequest.getChannel());
//                        throw new BadApiRequestException("This coupon is not applicable for this Channel");
//                    }
//
//
//                    if (filterData(uniqueCoupon.getChannelFullfillmentType(), posInputRequest.getFullfillmentMode())) {
//                        log.error("This coupon is not applicable for this FullFillmentMode type: {}", posInputRequest.getFullfillmentMode());
//                        throw new BadApiRequestException("This coupon is not applicable for this FullFillmentMode type");
//                    }
//
//                    if (filterData(uniqueCoupon.getFranchise(), posInputRequest.getFranchise())) {
//                        log.error("This coupon is not applicable for this Franchise: {}", posInputRequest.getFranchise());
//                        throw new BadApiRequestException("This coupon is not applicable for this Franchise");
//                    }
//
//                    if (filterData(uniqueCoupon.getStores(), posInputRequest.getStoreCode())) {
//                        log.error("This coupon is not applicable for this Store: {}", posInputRequest.getStoreCode());
//                        throw new BadApiRequestException("This coupon is not applicable for this Store");
//                    }
//
//                    //city check
//
//                    StoreMaster storeMasterList = storeMasterRepository.findByIdIgnoreCase(posInputRequest.getStoreCode()).orElseThrow(() -> new BadApiRequestException("Store Code is Invalid"));
//                    String city = storeMasterList.getCity();
//                    String cluster = storeMasterList.getCluster();
//
//                    if (filterData(uniqueCoupon.getCities(), city)) {
//                        log.error("This coupon is not applicable for this City: {}", city);
//                        throw new BadApiRequestException("This coupon is not applicable for this City");
//                    }
//
//                    if (filterData(uniqueCoupon.getClusters(), cluster)) {
//                        log.error("This coupon is not applicable for this cluster: {}", cluster);
//                        throw new BadApiRequestException("This coupon is not applicable for this cluster");
//                    }
//
//                    String dayofWeek = posInputRequest.getDate().getDayOfWeek().toString();
//                    if (!uniqueCoupon.getDayApplicability().contains(dayofWeek)) {
//                        log.error("This coupon is not applicable for this day of Week: {}", dayofWeek);
//                        throw new BadApiRequestException("This coupon is not applicable for this day of Week");
//                    }
//
//                    int month = posInputRequest.getDate().getMonth().getValue();
//                    if (!uniqueCoupon.getMonthApplicability().contains(month)) {
//                        log.error("This coupon is not applicable for this month: {}", posInputRequest.getDate().getMonth().toString());
//                        throw new BadApiRequestException("This coupon is not applicable for this Month");
//                    }
//
//                    //Day part
//
//                    if (uniqueCoupon.getTimeslot().getStartTime().isAfter(LocalTime.now()) || uniqueCoupon.getTimeslot().getEndTime().isBefore(LocalTime.now())) {
//                        log.error("This coupon is not applicable for this Time of day");
//                        throw new BadApiRequestException("This coupon is not applicable for this Time of day");
//                    }
//
//                    List<OrderItems> getItemsFromRequest = posInputRequest.getOrderDetails();
//                    List<OrderItems> validItems = getValidItems(getItemsFromRequest, uniqueCoupon.getProductInclusion(), uniqueCoupon.getProductExclusion(), uniqueCoupon.getCategoryInclusion(), uniqueCoupon.getCategoryExclusion());
//                    List<String> validItemsCode = getValidItemsCode(validItems);
//
//                    //calculating discount
//                    double totalPriceOfValidItemsBeforeDiscount = 0;
//
//                    if (!validItems.isEmpty()) {
//                        totalPriceOfValidItemsBeforeDiscount = validItems.stream().mapToDouble(OrderItems::getPrice).sum();
//                    }
//
//                    double finalDiscountAmount = 0;
//
//                    if (uniqueCoupon.getMov() > totalPriceOfValidItemsBeforeDiscount) {
//                        log.error("Minimum order value is not satisfied, required: {}", uniqueCoupon.getMov());
//                        throw new MovInvalidException("Minimum order value is not satisfied", uniqueCoupon.getMov());
//                    }
//
//                    if (uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_PERCENTAGE)) {
//                        double discountAmount = totalPriceOfValidItemsBeforeDiscount * uniqueCoupon.getDiscountPercentage() / 100;
//                        finalDiscountAmount = Math.min(discountAmount, uniqueCoupon.getDiscountCap());
//                        posOutputResponse.setDiscountMax(uniqueCoupon.getDiscountCap());
//                        posOutputResponse.setCouponValue(uniqueCoupon.getDiscountPercentage());
//                        posOutputResponse.setDiscountAmount(finalDiscountAmount);
//                        transactionTable.setCouponValue(uniqueCoupon.getDiscountPercentage());
//                        transactionTable.setDiscountAmount(finalDiscountAmount);
//
//                        List<ItemResponseInfo> itemResponseInfos = new ArrayList<>();
//                        int noOfItems = posInputRequest.getOrderDetails().size();
//                        int noOfValidItems = validItems.size();
//                        double discountPerItem = finalDiscountAmount / noOfValidItems;
//                        for (int i = 0; i < noOfItems; i++) {
//                            OrderItems orderItems = posInputRequest.getOrderDetails().get(i);
//                            ItemResponseInfo itemResponseInfo = new ItemResponseInfo();
//                            itemResponseInfo.setLineId(orderItems.getLineId());
//                            itemResponseInfo.setItemCode(orderItems.getItemCode());
//                            itemResponseInfo.setOrderItemPrice(orderItems.getPrice());
//                            itemResponseInfo.setQuantity(orderItems.getQuantity());
//                            itemResponseInfo.setDealId(orderItems.getDealId());
//                            itemResponseInfo.setIsFreebie(false);
//                            itemResponseInfo.setItemBase(orderItems.getBase());
//                            itemResponseInfo.setItemSize(orderItems.getSize());
//
//                            if (validItemsCode.contains(orderItems.getItemCode())) {
//                                itemResponseInfo.setDiscount(discountPerItem);
//                            } else {
//                                itemResponseInfo.setDiscount(0);
//                            }
//
//                            itemResponseInfos.add(itemResponseInfo);
//                        }
//
//                        posOutputResponse.setAppliedCouponItemDetails(itemResponseInfos);
//
//
//                    } else if (uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_FLAT)) {
//                        finalDiscountAmount = uniqueCoupon.getFlatDiscount();
//                        posOutputResponse.setDiscountMax(finalDiscountAmount);
//                        posOutputResponse.setCouponValue(finalDiscountAmount);
//                        transactionTable.setDiscountAmount(finalDiscountAmount);
//                        transactionTable.setCouponValue(finalDiscountAmount);
//                        posOutputResponse.setDiscountAmount(finalDiscountAmount);
//
//                        List<ItemResponseInfo> itemResponseInfos = new ArrayList<>();
//                        int noOfItems = posInputRequest.getOrderDetails().size();
//                        int noOfValidItems = validItems.size();
//                        double discountPerItem = finalDiscountAmount / noOfValidItems;
//                        for (int i = 0; i < noOfItems; i++) {
//                            OrderItems orderItems = posInputRequest.getOrderDetails().get(i);
//                            ItemResponseInfo itemResponseInfo = new ItemResponseInfo();
//                            itemResponseInfo.setLineId(orderItems.getLineId());
//                            itemResponseInfo.setItemCode(orderItems.getItemCode());
//                            itemResponseInfo.setOrderItemPrice(orderItems.getPrice());
//                            itemResponseInfo.setQuantity(orderItems.getQuantity());
//                            itemResponseInfo.setDealId(orderItems.getDealId());
//                            itemResponseInfo.setIsFreebie(false);
//                            itemResponseInfo.setItemBase(orderItems.getBase());
//                            itemResponseInfo.setItemSize(orderItems.getSize());
//
//                            if (validItemsCode.contains(orderItems.getItemCode())) {
//                                itemResponseInfo.setDiscount(discountPerItem);
//                            } else {
//                                itemResponseInfo.setDiscount(0);
//                            }
//
//                            itemResponseInfos.add(itemResponseInfo);
//                        }
//
//                        posOutputResponse.setAppliedCouponItemDetails(itemResponseInfos);
//
//                    } else if (uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_FREEBIE)) {
//                        finalDiscountAmount = 0;
//
//                        List<ItemResponseInfo> itemResponseInfos = new ArrayList<>();
//                        for (int i = 0; i < uniqueCoupon.getFreebieItems().size(); i++) {
//                            Items items = uniqueCoupon.getFreebieItems().get(i);
//                            ItemResponseInfo itemResponseInfo = new ItemResponseInfo();
//                            itemResponseInfo.setLineId(posInputRequest.getOrderDetails().get(0).getLineId());
//                            itemResponseInfo.setItemCode(items.getItemCode());
//                            itemResponseInfo.setOrderItemPrice(items.getItemPrice());
//                            itemResponseInfo.setQuantity(items.getItemQuantity());
//                            itemResponseInfo.setItemBase(items.getItemBase());
//                            itemResponseInfo.setItemSize(items.getItemSize());
//                            itemResponseInfo.setIsFreebie(true);
//                            itemResponseInfo.setDiscount(0);
//
//                            itemResponseInfos.add(itemResponseInfo);
//                        }
//
//                        posOutputResponse.setAppliedCouponItemDetails(itemResponseInfos);
//                        posOutputResponse.setDiscountMax(finalDiscountAmount);
//                        posOutputResponse.setCouponValue(finalDiscountAmount);
//                        transactionTable.setDiscountAmount(finalDiscountAmount);
//                        transactionTable.setCouponValue(finalDiscountAmount);
//                        posOutputResponse.setDiscountAmount(finalDiscountAmount);
//                    }
//
//                    posOutputResponse.setStatus(CouponConstant.SUCCESS);
//                    posOutputResponse.setMessage("Coupon Redeemed Succesfully");
//                    //posOutputResponse.setStatus("redeemed");
//                    transactionTable.setStatus(CouponConstant.REDEEMED);
//
//                    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//                    def.setName("MyTransaction");
//                    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//
//                    TransactionStatus status = transactionManager.getTransaction(def);
//
//                    try {
//                        transactionRepository.save(transactionTable);
//                        transactionManager.commit(status);
//                    }
//                    catch (Exception e){
//                        log.error("Error in saving data in database in Transaction Table");
//                        transactionManager.rollback(status);
//                        throw new Exception("Error occured in saving data in database");
//                    }
//
//                    log.info("Coupon redemption completed successfully for coupon code: {}", posInputRequest.getCouponCode());
//                    return posOutputResponse;
//                } else if (uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.TYPECOUPON_DYNAMIC)) {
//                    List<Constructs> constructsList = uniqueCoupon.getTypeConstructs();
//                    Optional<Constructs> matchedConstructOpt = constructsList.stream()
//                            .filter(constructs -> constructs.getUploadSegment() != null)
//                            .filter(constructs -> constructs.getUploadSegment().stream()
//                                    .anyMatch(user -> user.getMobileNumber().equals(posInputRequest.getNumber())))
//                            .findFirst();
//
//                    if (matchedConstructOpt.isEmpty()) {
//                        log.error("This Coupon is not applicable for this user: {}", posInputRequest.getNumber());
//                        throw new BadApiRequestException("This Coupon is not applicable for this user");
//                    }
//
//                    Constructs matchedConstruct = matchedConstructOpt.get();
//
//                    posOutputResponse.setMov(matchedConstruct.getMov());
//                    posOutputResponse.setDiscountType(matchedConstruct.getCouponType());
//
//                    transactionTable.setCouponType(matchedConstruct.getCouponType());
//                    transactionTable.setCouponUsage(matchedConstruct.getCoupon_usage());
//                    transactionTable.setMov(matchedConstruct.getMov());
//
//                    if ((matchedConstruct.getFirstExpiryDate() != null && matchedConstruct.getFirstExpiryDate().isBefore(LocalDate.now())) || (matchedConstruct.getExtendedExpiryDate() != null && matchedConstruct.getExtendedExpiryDate().isBefore(LocalDate.now()))) {
//                        LocalDate date = matchedConstruct.getFirstExpiryDate();
//
//                        if (matchedConstruct.getExtendedExpiryDate() != null) {
//                            date = matchedConstruct.getExtendedExpiryDate();
//                        }
//                        log.error("Coupon code expired on {}", date);
//                        throw new ExpiredCouponException("Coupon code expired", date);
//                    }
//
//                    if ((matchedConstruct.getNumberOfDays() != 0 && (matchedConstruct.getUserAttachedDate().plusDays(uniqueCoupon.getNumberOfDays()).isBefore(LocalDate.now()))) || (uniqueCoupon.getExtendedNumberOfDays() != 0 && matchedConstruct.getUserAttachedDate().plusDays(uniqueCoupon.getExtendedNumberOfDays() + uniqueCoupon.getNumberOfDays()).isBefore(LocalDate.now()))) {
//                        LocalDate date = matchedConstruct.getUserAttachedDate().plusDays(uniqueCoupon.getNumberOfDays());
//                        if (uniqueCoupon.getExtendedNumberOfDays() != 0) {
//                            date = matchedConstruct.getUserAttachedDate().plusDays(uniqueCoupon.getExtendedNumberOfDays() + uniqueCoupon.getNumberOfDays());
//                        }
//                        log.error("Coupon code expired on {}", date);
//                        throw new ExpiredCouponException("Coupon code expired", date);
//                    }
//
//                    if (!matchedConstruct.getChannel().contains(posInputRequest.getChannel())) {
//                        log.error("This coupon is not applicable for this Channel: {}", posInputRequest.getChannel());
//                        throw new BadApiRequestException("This coupon is not applicable for this Channel");
//                    }
//
//                    if (filterData(matchedConstruct.getChannelFullfillmentType(), posInputRequest.getChannel())) {
//                        log.error("This coupon is not applicable for this channel type: {}", posInputRequest.getChannel());
//                        throw new BadApiRequestException("This coupon is not applicable for this channel type");
//                    }
//
//                    if (filterData(matchedConstruct.getFranchise(), posInputRequest.getFranchise())) {
//                        log.error("This coupon is not applicable for this Franchise: {}", posInputRequest.getFranchise());
//                        throw new BadApiRequestException("This coupon is not applicable for this Franchise");
//                    }
//
//                    if (filterData(matchedConstruct.getStores(), posInputRequest.getStoreCode())) {
//                        log.error("This coupon is not applicable for this Store: {}", posInputRequest.getStoreCode());
//                        throw new BadApiRequestException("This coupon is not applicable for this Store");
//                    }
//
//                    StoreMaster storeMasterList = storeMasterRepository.findByIdIgnoreCase(posInputRequest.getStoreCode()).orElseThrow(() -> new BadApiRequestException("Store Code is Invalid"));
//                    String city = storeMasterList.getCity();
//                    String cluster = storeMasterList.getCluster();
//
//                    if (filterData(matchedConstruct.getCities(), city)) {
//                        log.error("This coupon is not applicable for this City: {}", city);
//                        throw new BadApiRequestException("This coupon is not applicable for this City");
//                    }
//
//                    if (filterData(matchedConstruct.getClusters(), cluster)) {
//                        log.error("This coupon is not applicable for this cluster: {}", cluster);
//                        throw new BadApiRequestException("This coupon is not applicable for this cluster");
//                    }
//
//                    String dayofWeek = posInputRequest.getDate().getDayOfWeek().toString();
//                    if (filterData(matchedConstruct.getDayApplicability(), (dayofWeek))) {
//                        log.error("This coupon is not applicable for this day of Week: {}", dayofWeek);
//                        throw new BadApiRequestException("This coupon is not applicable for this day of Week");
//                    }
//
//                    int month = posInputRequest.getDate().getMonth().getValue();
//                    if (!matchedConstruct.getMonthApplicability().contains(month)) {
//                        log.error("This coupon is not applicable for this Month: {}", month);
//                        throw new BadApiRequestException("This coupon is not applicable for this Month");
//                    }
//
//                    if (matchedConstruct.getTimeslot().getStartTime().isAfter(LocalTime.now()) || matchedConstruct.getTimeslot().getEndTime().isBefore(LocalTime.now())) {
//                        log.error("This coupon is not applicable for this Time of day");
//                        throw new BadApiRequestException("This coupon is not applicable for this day of month");
//                    }
//
//                    List<OrderItems> getItemsFromRequest = posInputRequest.getOrderDetails();
//                    List<OrderItems> validItems = getValidItems(getItemsFromRequest, matchedConstruct.getProductInclusion(), matchedConstruct.getProductExclusion(), matchedConstruct.getCategoryInclusion(), matchedConstruct.getCategoryExclusion());
//                    List<String> validItemsCode = getValidItemsCode(validItems);
//
//                    //calculating discount
//                    double totalPriceOfValidItemsBeforeDiscount = 0;
//
//                    if (!validItems.isEmpty()) {
//                        totalPriceOfValidItemsBeforeDiscount = validItems.stream().mapToDouble(OrderItems::getPrice).sum();
//                    }
//
//                    double finalDiscountAmount = 0;
//
//                    if (matchedConstruct.getMov() > totalPriceOfValidItemsBeforeDiscount) {
//                        log.error("You do not have sufficient item in your cart, required MOV: {}", matchedConstruct.getMov());
//                        throw new BadApiRequestException("You do not have sufficient item in your cart");
//                    }
//
//                    if (matchedConstruct.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_PERCENTAGE)) {
//                        double discountAmount = totalPriceOfValidItemsBeforeDiscount * matchedConstruct.getDiscountPercentage() / 100;
//                        finalDiscountAmount = Math.min(discountAmount, matchedConstruct.getDiscountCap());
//                        posOutputResponse.setDiscountMax(matchedConstruct.getDiscountCap());
//                        posOutputResponse.setCouponValue(matchedConstruct.getDiscountPercentage());
//                        transactionTable.setDiscountAmount(finalDiscountAmount);
//                        transactionTable.setCouponValue(matchedConstruct.getDiscountPercentage());
//                        posOutputResponse.setDiscountAmount(finalDiscountAmount);
//
//                        List<ItemResponseInfo> itemResponseInfos = new ArrayList<>();
//                        int noOfItems = posInputRequest.getOrderDetails().size();
//                        int noOfValidItems = validItems.size();
//                        double discountPerItem = finalDiscountAmount / noOfValidItems;
//                        for (int i = 0; i < noOfItems; i++) {
//                            OrderItems orderItems = posInputRequest.getOrderDetails().get(i);
//                            ItemResponseInfo itemResponseInfo = new ItemResponseInfo();
//                            itemResponseInfo.setLineId(orderItems.getLineId());
//                            itemResponseInfo.setItemCode(orderItems.getItemCode());
//                            itemResponseInfo.setOrderItemPrice(orderItems.getPrice());
//                            itemResponseInfo.setQuantity(orderItems.getQuantity());
//                            itemResponseInfo.setDealId(orderItems.getDealId());
//                            itemResponseInfo.setIsFreebie(false);
//                            itemResponseInfo.setItemBase(orderItems.getBase());
//                            itemResponseInfo.setItemSize(orderItems.getSize());
//
//                            if (validItemsCode.contains(orderItems.getItemCode())) {
//                                itemResponseInfo.setDiscount(discountPerItem);
//                            } else {
//                                itemResponseInfo.setDiscount(0);
//                            }
//
//                            itemResponseInfos.add(itemResponseInfo);
//                        }
//
//                        posOutputResponse.setAppliedCouponItemDetails(itemResponseInfos);
//
//
//                    } else if (matchedConstruct.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_FLAT)) {
//                        finalDiscountAmount = matchedConstruct.getFlatDiscount();
//                        posOutputResponse.setDiscountMax(finalDiscountAmount);
//                        posOutputResponse.setCouponValue(finalDiscountAmount);
//                        transactionTable.setDiscountAmount(finalDiscountAmount);
//                        transactionTable.setCouponValue(finalDiscountAmount);
//                        posOutputResponse.setDiscountAmount(finalDiscountAmount);
//
//                        List<ItemResponseInfo> itemResponseInfos = new ArrayList<>();
//                        int noOfItems = posInputRequest.getOrderDetails().size();
//                        int noOfValidItems = validItems.size();
//                        double discountPerItem = finalDiscountAmount / noOfValidItems;
//                        for (int i = 0; i < noOfItems; i++) {
//                            OrderItems orderItems = posInputRequest.getOrderDetails().get(i);
//                            ItemResponseInfo itemResponseInfo = new ItemResponseInfo();
//                            itemResponseInfo.setLineId(orderItems.getLineId());
//                            itemResponseInfo.setItemCode(orderItems.getItemCode());
//                            itemResponseInfo.setOrderItemPrice(orderItems.getPrice());
//                            itemResponseInfo.setQuantity(orderItems.getQuantity());
//                            itemResponseInfo.setDealId(orderItems.getDealId());
//                            itemResponseInfo.setIsFreebie(false);
//                            itemResponseInfo.setItemBase(orderItems.getBase());
//                            itemResponseInfo.setItemSize(orderItems.getSize());
//
//                            if (validItemsCode.contains(orderItems.getItemCode())) {
//                                itemResponseInfo.setDiscount(discountPerItem);
//                            } else {
//                                itemResponseInfo.setDiscount(0);
//                            }
//
//                            itemResponseInfos.add(itemResponseInfo);
//                        }
//
//                        posOutputResponse.setAppliedCouponItemDetails(itemResponseInfos);
//
//                    } else if (matchedConstruct.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_FREEBIE)) {
//
//                        List<ItemResponseInfo> itemResponseInfos = new ArrayList<>();
//                        for (int i = 0; i < matchedConstruct.getFreebieItems().size(); i++) {
//                            Items items = matchedConstruct.getFreebieItems().get(i);
//                            ItemResponseInfo itemResponseInfo = new ItemResponseInfo();
//                            itemResponseInfo.setLineId(posInputRequest.getOrderDetails().get(0).getLineId());
//                            itemResponseInfo.setItemCode(items.getItemCode());
//                            itemResponseInfo.setOrderItemPrice(items.getItemPrice());
//                            itemResponseInfo.setQuantity(items.getItemQuantity());
//                            itemResponseInfo.setItemBase(items.getItemBase());
//                            itemResponseInfo.setItemSize(items.getItemSize());
//                            itemResponseInfo.setIsFreebie(true);
//                            itemResponseInfo.setDiscount(0);
//
//                            itemResponseInfos.add(itemResponseInfo);
//                        }
//
//                        posOutputResponse.setAppliedCouponItemDetails(itemResponseInfos);
//                        finalDiscountAmount = 0;
//                        posOutputResponse.setDiscountMax(finalDiscountAmount);
//                        posOutputResponse.setCouponValue(finalDiscountAmount);
//                        transactionTable.setDiscountAmount(finalDiscountAmount);
//                        transactionTable.setCouponValue(finalDiscountAmount);
//                        posOutputResponse.setDiscountAmount(finalDiscountAmount);
//                    }
//
//                    posOutputResponse.setStatus(CouponConstant.SUCCESS);
//                    posOutputResponse.setMessage("Coupon Redeemed Succesfully");
//                    //posOutputResponse.setStatus("redeemed");
//                    transactionTable.setStatus(CouponConstant.REDEEMED);
//                    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//                    def.setName("MyTransaction");
//                    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//
//                    TransactionStatus status = transactionManager.getTransaction(def);
//                    try {
//                        transactionRepository.save(transactionTable);
//                        transactionManager.commit(status);
//                    }
//                    catch (Exception e){
//                        log.error("Error in saving data in database in Transaction Table");
//                        transactionManager.rollback(status);
//                        throw new Exception("Error occured in saving data in database");
//                    }
//                    log.info("Coupon redemption completed successfully for coupon code: {}", posInputRequest.getCouponCode());
//                    return posOutputResponse;
//
//                }

            } else if (batchCouponRepository.existsByIdIgnoreCase(posInputRequest.getCouponCode())) {
                log.debug("Coupon code found in BatchCoupon repository: {}", posInputRequest.getCouponCode());
                BatchCoupon batchCoupon = batchCouponRepository.findByIdIgnoreCase(posInputRequest.getCouponCode()).get();
                UniqueCoupon uniqueCoupon = uniqueCouponRepository.findByIdIgnoreCase(batchCoupon.getUniqueCouponId()).get();

                BaseCoupon baseCoupon = baseCouponRepository.findByIdIgnoreCase(uniqueCoupon.getBaseCouponCode()).get();
                transactionTable.setBaseCouponCode(baseCoupon.getBaseCouponCode());
                transactionTable.setTermsAndConditions(uniqueCoupon.getTermsAndConditions());
                transactionTable.setStartDate(uniqueCoupon.getStartDate());
                transactionTable.setStartDate(uniqueCoupon.getEndDate());
                posOutputResponse.setMov(uniqueCoupon.getMov());
                posOutputResponse.setCouponSource(baseCoupon.getBaseCouponCode());
                posOutputResponse.setCouponInfo(uniqueCoupon.getTermsAndConditions());

                transactionTable.setCouponType(uniqueCoupon.getCouponType());
                transactionTable.setCouponUsage(uniqueCoupon.getCouponUsage());
                transactionTable.setMov(uniqueCoupon.getMov());

                if (!baseCoupon.getStatus().equalsIgnoreCase(CouponConstant.ACTIVE)) {
                    log.error("Base Coupon is not Active , Status: " + baseCoupon.getStatus());
                    throw new BadApiRequestException("Base Coupon is not active, Status: " + baseCoupon.getStatus());
                }

                if (!uniqueCoupon.getStatus().equalsIgnoreCase(CouponConstant.ACTIVE)) {
                    log.error("Unique Coupon is not Active, Status: " + uniqueCoupon.getStatus());
                    throw new BadApiRequestException("Unique Coupon is not active, Status: " + uniqueCoupon.getStatus());
                }

                if (!batchCoupon.getStatus().equalsIgnoreCase(CouponConstant.ACTIVE)) {
                    log.error("Batch Coupon is not Active, Status: " + batchCoupon.getStatus());
                    throw new BadApiRequestException("Batch Coupon is not active, Status: " + batchCoupon.getStatus());
                }


                if (baseCoupon.getTermsAndConditionsExpiryDate().isBefore(uniqueCoupon.getEndDate())) {
                    log.error("Base Coupon has expired on {}", baseCoupon.getTermsAndConditionsExpiryDate());
                    throw new ExpiredCouponException("Base Coupon has expired ", baseCoupon.getTermsAndConditionsExpiryDate());

                }

                if (uniqueCoupon.getStartDate().isAfter(LocalDate.now())) {
                    log.error("Coupon is not activated yet but created on {}", uniqueCoupon.getStartDate());
                    throw new NotActiveCouponException("Coupon is not activated yet but created", uniqueCoupon.getStartDate());
                }

                if (uniqueCoupon.getEndDate().isBefore(LocalDate.now())) {
                    log.error("Coupon has expired on {}", uniqueCoupon.getEndDate());
                    throw new ExpiredCouponException("Coupon has expired", uniqueCoupon.getEndDate());
                }

                if ((uniqueCoupon.getFirstExpiryDate() != null && uniqueCoupon.getFirstExpiryDate().isBefore(LocalDate.now())) || (uniqueCoupon.getExtendedExpiryDate() != null && uniqueCoupon.getExtendedExpiryDate().isBefore(LocalDate.now()))) {
                    LocalDate date = uniqueCoupon.getFirstExpiryDate();
                    if (uniqueCoupon.getExtendedExpiryDate() != null) {
                        date = uniqueCoupon.getExtendedExpiryDate();
                    }
                    log.error("Coupon code expired on {}", date);
                    throw new ExpiredCouponException("Coupon has expired", date);

                }

                if ((uniqueCoupon.getNumberOfDays() != 0 && batchCoupon.getDateCreated().plusDays(uniqueCoupon.getNumberOfDays()).isBefore(LocalDate.now())) || (uniqueCoupon.getExtendedNumberOfDays() != 0 && batchCoupon.getDateCreated().plusDays(uniqueCoupon.getExtendedNumberOfDays() + uniqueCoupon.getNumberOfDays()).isBefore(LocalDate.now()))) {
                    LocalDate date = batchCoupon.getDateCreated().plusDays(uniqueCoupon.getNumberOfDays());
                    if (uniqueCoupon.getExtendedNumberOfDays() != 0) {
                        date = batchCoupon.getDateCreated().plusDays(uniqueCoupon.getExtendedNumberOfDays() + uniqueCoupon.getNumberOfDays());
                    }
                    log.error("Coupon code expired on {}", date);
                    throw new ExpiredCouponException("Coupon has expired", date);

                }

                if (transactionRepository.existsByCouponCodeAndMobileNumber(posInputRequest.getCouponCode(), posInputRequest.getNumber())) {
                    log.error("Coupon has already been redeemed by user: {}", posInputRequest.getNumber());
                    throw new BadApiRequestException("Coupon has already been redeemed");

                }

                if (!batchCoupon.getMobileNumber().equals(posInputRequest.getNumber())) {
                    log.error("This coupon cannot be redeemed by this user: {}", posInputRequest.getNumber());
                    throw new BadApiRequestException("This coupon cannot be redeemed by this user");

                }


                if (filterData(uniqueCoupon.getChannel(), (posInputRequest.getChannel()))) {
                    log.error("This coupon is not applicable for this Channel: {}", posInputRequest.getChannel());
                    throw new BadApiRequestException("This coupon is not applicable for this Channel");
                }


                if (filterData(uniqueCoupon.getChannelFullfillmentType(), posInputRequest.getFullfillmentMode())) {
                    log.error("This coupon is not applicable for this FullfillmentMode type: {}", posInputRequest.getFullfillmentMode());
                    throw new BadApiRequestException("This coupon is not applicable for this FullfillmentMode type");

                }

                if (filterData(uniqueCoupon.getFranchise(), posInputRequest.getFranchise())) {
                    log.error("This coupon is not applicable for this Franchise: {}", posInputRequest.getFranchise());
                    throw new BadApiRequestException("This coupon is not applicable for this Franchise");

                }

                if (filterData(uniqueCoupon.getStores(), posInputRequest.getStoreCode())) {
                    log.error("This coupon is not applicable for this Store: {}", posInputRequest.getStoreCode());
                    throw new BadApiRequestException("This coupon is not applicable for this Store");

                }

                StoreMaster storeMasterList = storeMasterRepository.findByIdIgnoreCase(posInputRequest.getStoreCode()).orElseThrow(() -> new BadApiRequestException("Store Code is Invalid"));
                String city = storeMasterList.getCity();
                String cluster = storeMasterList.getCluster();

                if (filterData(uniqueCoupon.getCities(), city)) {
                    log.error("This coupon is not applicable for this City: {}", city);

                    throw new BadApiRequestException("This coupon is not applicable for this City");
                }

                if (filterData(uniqueCoupon.getClusters(), cluster)) {
                    log.error("This coupon is not applicable for this cluster: {}", cluster);
                    throw new BadApiRequestException("This coupon is not applicable for this cluster");
                }

                String dayofWeek = posInputRequest.getDate().getDayOfWeek().toString();
                if (filterData(uniqueCoupon.getDayApplicability(), (dayofWeek))) {
                    log.error("This coupon is not applicable for this day of Week: {}", dayofWeek);
                    throw new BadApiRequestException("This coupon is not applicable for this day of Week");

                }

                int month = posInputRequest.getDate().getMonth().getValue();
                if (!uniqueCoupon.getMonthApplicability().contains(month)) {
                    log.error("This coupon is not applicable for this  Month: {}", month);
                    throw new BadApiRequestException("This coupon is not applicable for this Month");

                }

                //Day part
                if (uniqueCoupon.getTimeslot().getStartTime().isAfter(LocalTime.now()) || uniqueCoupon.getTimeslot().getEndTime().isBefore(LocalTime.now())) {
                    log.error("This coupon is not applicable for this Time of day");
                    throw new BadApiRequestException("This coupon is not applicable for this Time of day");
                }

                //calculating discount
                List<OrderItems> getItemsFromRequest = posInputRequest.getOrderDetails();
                List<OrderItems> validItems = getValidItems(getItemsFromRequest, uniqueCoupon.getProductInclusion(), uniqueCoupon.getProductExclusion(), uniqueCoupon.getCategoryInclusion(), uniqueCoupon.getCategoryExclusion());
                List<String> validItemsCode = getValidItemsCode(validItems);

                //calculating discount
                double totalPriceOfValidItemsBeforeDiscount = 0;

                if (!validItems.isEmpty()) {
                    totalPriceOfValidItemsBeforeDiscount = validItems.stream().mapToDouble(OrderItems::getPrice).sum();
                }

                double finalDiscountAmount = 0;

                if (uniqueCoupon.getMov() > totalPriceOfValidItemsBeforeDiscount) {
                    log.error("Minimum order value is not satisfied, required: {}", uniqueCoupon.getMov());
                    throw new MovInvalidException("Minimum order value is not satisfied", uniqueCoupon.getMov(),uniqueCoupon.getProductInclusion(),uniqueCoupon.getProductExclusion(),uniqueCoupon.getCategoryInclusion(),uniqueCoupon.getCategoryExclusion());

                }

                if (uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_PERCENTAGE)) {
                    double discountAmount = totalPriceOfValidItemsBeforeDiscount * uniqueCoupon.getDiscountPercentage() / 100;
                    finalDiscountAmount = Math.min(discountAmount, uniqueCoupon.getDiscountCap());
                    posOutputResponse.setDiscountMax(uniqueCoupon.getDiscountCap());
                    posOutputResponse.setCouponValue(uniqueCoupon.getDiscountPercentage());
                    transactionTable.setDiscountAmount(finalDiscountAmount);
                    transactionTable.setCouponValue(uniqueCoupon.getDiscountPercentage());
                    posOutputResponse.setDiscountAmount(finalDiscountAmount);

                    List<ItemResponseInfo> itemResponseInfos = new ArrayList<>();
                    int noOfItems = posInputRequest.getOrderDetails().size();
                    int noOfValidItems = validItems.size();
                    double discountPerItem = finalDiscountAmount / noOfValidItems;
                    for (int i = 0; i < noOfItems; i++) {
                        OrderItems orderItems = posInputRequest.getOrderDetails().get(i);
                        ItemResponseInfo itemResponseInfo = new ItemResponseInfo();
                        itemResponseInfo.setLineId(orderItems.getLineId());
                        itemResponseInfo.setItemCode(orderItems.getItemCode());
                        itemResponseInfo.setOrderItemPrice(orderItems.getPrice());
                        itemResponseInfo.setQuantity(orderItems.getQuantity());
                        itemResponseInfo.setDealId(orderItems.getDealId());
                        itemResponseInfo.setIsFreebie(false);
                        itemResponseInfo.setItemBase(orderItems.getBase());
                        itemResponseInfo.setItemSize(orderItems.getSize());

                        if (validItemsCode.contains(orderItems.getItemCode())) {
                            itemResponseInfo.setDiscount(discountPerItem);
                        } else {
                            itemResponseInfo.setDiscount(0);
                        }

                        itemResponseInfos.add(itemResponseInfo);
                    }

                    posOutputResponse.setAppliedCouponItemDetails(itemResponseInfos);


                } else if (uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_FLAT)) {

                    finalDiscountAmount = uniqueCoupon.getFlatDiscount();
                    posOutputResponse.setDiscountMax(finalDiscountAmount);
                    posOutputResponse.setCouponValue(finalDiscountAmount);
                    transactionTable.setDiscountAmount(finalDiscountAmount);
                    transactionTable.setCouponValue(finalDiscountAmount);
                    posOutputResponse.setDiscountAmount(finalDiscountAmount);

                    List<ItemResponseInfo> itemResponseInfos = new ArrayList<>();
                    int noOfItems = posInputRequest.getOrderDetails().size();
                    int noOfValidItems = validItems.size();
                    double discountPerItem = finalDiscountAmount / noOfValidItems;
                    for (int i = 0; i < noOfItems; i++) {
                        OrderItems orderItems = posInputRequest.getOrderDetails().get(i);
                        ItemResponseInfo itemResponseInfo = new ItemResponseInfo();
                        itemResponseInfo.setLineId(orderItems.getLineId());
                        itemResponseInfo.setItemCode(orderItems.getItemCode());
                        itemResponseInfo.setOrderItemPrice(orderItems.getPrice());
                        itemResponseInfo.setQuantity(orderItems.getQuantity());
                        itemResponseInfo.setDealId(orderItems.getDealId());
                        itemResponseInfo.setIsFreebie(false);
                        itemResponseInfo.setItemBase(orderItems.getBase());
                        itemResponseInfo.setItemSize(orderItems.getSize());

                        if (validItemsCode.contains(orderItems.getItemCode())) {
                            itemResponseInfo.setDiscount(discountPerItem);
                        } else {
                            itemResponseInfo.setDiscount(0);
                        }

                        itemResponseInfos.add(itemResponseInfo);
                    }

                    posOutputResponse.setAppliedCouponItemDetails(itemResponseInfos);


                } else if (uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_FREEBIE)) {
                    finalDiscountAmount = 0;
                    posOutputResponse.setDiscountMax(finalDiscountAmount);
                    posOutputResponse.setCouponValue(finalDiscountAmount);
                    transactionTable.setDiscountAmount(finalDiscountAmount);
                    transactionTable.setCouponValue(finalDiscountAmount);
                    posOutputResponse.setDiscountAmount(finalDiscountAmount);

                    List<ItemResponseInfo> itemResponseInfos = new ArrayList<>();
                    for (int i = 0; i < uniqueCoupon.getFreebieItems().size(); i++) {
                        Items items = uniqueCoupon.getFreebieItems().get(i);
                        ItemResponseInfo itemResponseInfo = new ItemResponseInfo();
                        itemResponseInfo.setLineId(posInputRequest.getOrderDetails().get(0).getLineId());
                        itemResponseInfo.setItemCode(items.getItemCode());
                        itemResponseInfo.setOrderItemPrice(items.getItemPrice());
                        itemResponseInfo.setQuantity(items.getItemQuantity());
                        itemResponseInfo.setItemBase(items.getItemBase());
                        itemResponseInfo.setItemSize(items.getItemSize());
                        itemResponseInfo.setIsFreebie(true);
                        itemResponseInfo.setDiscount(0);

                        itemResponseInfos.add(itemResponseInfo);
                    }

                    posOutputResponse.setAppliedCouponItemDetails(itemResponseInfos);
                }

                posOutputResponse.setStatus(CouponConstant.SUCCESS);
                posOutputResponse.setMessage("Coupon Redeemed Succesfully");
                batchCoupon.setStatus(CouponConstant.REDEEMED);
                transactionTable.setStatus(CouponConstant.REDEEMED);
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setName("MyTransaction");
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

                TransactionStatus status = transactionManager.getTransaction(def);

                try {
                    transactionRepository.save(transactionTable);
                    transactionManager.commit(status);
                }
                catch (Exception e){
                    log.error("Error in saving data in database in Transaction Table");
                    transactionManager.rollback(status);
                    throw new Exception("Error occured in saving data in database");
                }

                log.info("Coupon redemption completed successfully for coupon code: {}", posInputRequest.getCouponCode());
                return posOutputResponse;
            }
            log.error("Wrong Coupon Code: {}", posInputRequest.getCouponCode());
            throw new BadApiRequestException("Wrong Coupon Code");
        }



    public List<OrderItems> getValidItems(List<OrderItems> getItemsFromRequest, List<String> productInclusion, List<String> productExclusion,List<String> categoryInclusion, List<String> categoryExclusion) {
        Set<String> productInclusionSet = productInclusion != null ? Set.copyOf(productInclusion) : Set.of();
        Set<String> productExclusionSet = productExclusion != null ? Set.copyOf(productExclusion) : Set.of();
        Set<String> categoryInclusionSet=categoryInclusion!=null ? Set.copyOf(categoryInclusion):Set.of();
        Set<String> categoryExclusionSet=categoryInclusion!=null ? Set.copyOf(categoryExclusion):Set.of();

        List<OrderItems> validItems = getItemsFromRequest.stream()
                .filter(item -> {
                    if (productExclusionSet!=null &&  filterDataFromSet(productExclusionSet,item.getItemCode())) {
                        return false;
                    }

                    Optional<ProductMaster> productMasterOpt = productMasterRepository.findByIdIgnoreCase(item.getItemCode());
                    if (productMasterOpt.isPresent()) {
                        ProductMaster productMaster = productMasterOpt.get();
                        List<String> dealsAndMeals=new ArrayList<String>(){
                            {
                                add("MEAL");
                                add("DEAL");
                                add("MEAL/DEAL");
                            }
                        };
                         if( !filterData(dealsAndMeals,productMaster.getCategory()) || ( categoryExclusionSet!=null && filterDataFromSet(categoryExclusionSet,productMaster.getCategory())) ) {
                            return false;
                        }
                    }
                    return true;
                }) // Exclude items in productExclusion
                .filter(item -> item.getDealId() == null || item.getDealId().isEmpty()) // Exclude items with non-null dealId
                .toList();

        boolean hasInclusionItem = validItems.stream()
                .anyMatch(item -> {
                    Optional<ProductMaster> productMasterOpt = productMasterRepository.findByIdIgnoreCase(item.getItemCode());
                    if (productMasterOpt.isPresent()) {
                        ProductMaster productMaster = productMasterOpt.get();
                        return productInclusionSet.contains(item.getItemCode()) || filterDataFromSet( categoryInclusionSet,productMaster.getCategory());
                    }
                    return false;
                }); // Check if any item is in productInclusion or categoryInclusion

        if ( productInclusionSet.isEmpty() || hasInclusionItem) {
            return validItems;
        } else {
            return List.of(); // Return an empty list if no valid items are in productInclusion
        }
    }

    private List<String> getValidItemsCode(List<OrderItems> itemList) {
        return itemList.stream().map(OrderItems::getItemCode).collect(Collectors.toList());
    }

    private Boolean filterData(List<String> dataList,String data) {
        return dataList.stream()
                .noneMatch(item -> item.equalsIgnoreCase(data));
    }

    private Boolean filterDataFromSet(Set<String> dataList,String data) {
        return dataList.stream().anyMatch(item -> item.equalsIgnoreCase(data));
    }



}
