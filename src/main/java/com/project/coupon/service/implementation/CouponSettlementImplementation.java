package com.project.coupon.service.implementation;

import com.project.coupon.constants.CouponConstant;
import com.project.coupon.exceptions.BadApiRequestException;
import com.project.coupon.jpaEntities.TransactionTable;
import com.project.coupon.jpaRepositories.TransactionRepository;
import com.project.coupon.repository.BaseCouponRepository;
import com.project.coupon.repository.BatchCouponRepository;
import com.project.coupon.repository.UniqueCouponRepository;
import com.project.coupon.request.PosInputSettle;
import com.project.coupon.response.PosOutputSettle;
import com.project.coupon.service.CouponSettlement;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class CouponSettlementImplementation implements CouponSettlement {

    @Autowired
    private UniqueCouponRepository uniqueCouponRepository;

    @Autowired
    private BaseCouponRepository baseCouponRepository;

    @Autowired
    private BatchCouponRepository batchCouponRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public PosOutputSettle settleCoupon(PosInputSettle posInputRequest) throws Exception {

        PosOutputSettle posOutputSettle = PosOutputSettle.builder().channel(posInputRequest.getChannel()).franchise(posInputRequest.getFranchise())
                .fullfillmentMode(posInputRequest.getFullfillmentMode()).store(posInputRequest.getStoreCode()).netAmount(posInputRequest.getNetAmount())
                .couponCode(posInputRequest.getCouponCode()).receiptNumber(posInputRequest.getReceiptNumber()).date(posInputRequest.getDate())
                .time(posInputRequest.getTime()).build();


            List<TransactionTable> optionalDataFromDB = transactionRepository.findByReceiptNumberIgnoreCase(posInputRequest.getReceiptNumber());
            if (!optionalDataFromDB.isEmpty()) {
                TransactionTable transactionTableDataFromDB = optionalDataFromDB.get(0);

                if (transactionTableDataFromDB.getStatus().equalsIgnoreCase(CouponConstant.SETTLED)) {
                    throw new BadApiRequestException("Coupon with the given receipt number is already settled");
                }

                if (transactionTableDataFromDB.getStatus().equalsIgnoreCase(CouponConstant.CANCELLED)) {
                    throw new BadApiRequestException("Coupon with the given receipt number is already cancelled");
                }

                if (!transactionTableDataFromDB.getChannel().equalsIgnoreCase(posInputRequest.getChannel())) {
                    throw new BadApiRequestException("This channel type for this settlement is invalid");
                }

                if (!transactionTableDataFromDB.getFranchise().equalsIgnoreCase(posInputRequest.getFranchise())) {
                    throw new BadApiRequestException("This Franchise for this settlement is invalid");
                }

                if (!transactionTableDataFromDB.getChannelFullfillment().equalsIgnoreCase(posInputRequest.getFullfillmentMode())) {
                    throw new BadApiRequestException("This Fullfillment type for this settlement is invalid");
                }

                if (!transactionTableDataFromDB.getStore().equalsIgnoreCase(posInputRequest.getStoreCode())) {
                    throw new BadApiRequestException("This store code for this settlement is invalid");
                }

                if (!transactionTableDataFromDB.getCouponCode().equalsIgnoreCase(posInputRequest.getCouponCode())) {
                    throw new BadApiRequestException("This Coupon code for this settlement is invalid");
                }

                if (!transactionTableDataFromDB.getMobileNumber().equals(posInputRequest.getNumber())) {
                    throw new BadApiRequestException("This Mobile Number for this settlement is invalid");
                }

                posOutputSettle.setDiscountAmount(transactionTableDataFromDB.getDiscountAmount());

                transactionTableDataFromDB.setStatus(CouponConstant.SETTLED);
                transactionTableDataFromDB.setSettleDate(posInputRequest.getDate());
                transactionTableDataFromDB.setSettleTime(posInputRequest.getTime());

                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setName("MyTransaction");
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

                TransactionStatus status = transactionManager.getTransaction(def);
                try {
                    transactionRepository.save(transactionTableDataFromDB);
                    transactionManager.commit(status);
                }
                catch (Exception e){
                    log.error("Error in saving data in database in Transaction Table");
                    transactionManager.rollback(status);
                    throw new Exception("Error occured in saving data in database");
                }
            } else {
                throw new BadApiRequestException("Invalid receipt number");
            }


        posOutputSettle.setStatus(CouponConstant.SUCCESS);
        posOutputSettle.setMessage("Settlement completed successfully");

        return posOutputSettle;
    }
}
