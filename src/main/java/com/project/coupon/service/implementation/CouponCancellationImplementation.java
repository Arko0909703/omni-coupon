package com.project.coupon.service.implementation;

import com.project.coupon.constants.CouponConstant;
import com.project.coupon.exceptions.BadApiRequestException;
import com.project.coupon.jpaEntities.TransactionTable;
import com.project.coupon.jpaRepositories.TransactionRepository;
import com.project.coupon.repository.BaseCouponRepository;
import com.project.coupon.repository.BatchCouponRepository;
import com.project.coupon.repository.UniqueCouponRepository;
import com.project.coupon.request.PosInputCancel;
import com.project.coupon.response.PosOutputCancel;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.project.coupon.service.CouponCancellation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class CouponCancellationImplementation implements CouponCancellation {

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
    public PosOutputCancel cancelCoupon(PosInputCancel posInputRequest) throws Exception {
        log.info("Starting cancellation process for receipt number: {}", posInputRequest.getReceiptNumber());

        PosOutputCancel posOutputCancel = PosOutputCancel.builder().channel(posInputRequest.getChannel()).franchise(posInputRequest.getFranchise())
                .fullfillmentMode(posInputRequest.getFullfillmentMode()).store(posInputRequest.getStoreCode())
                .couponCode(posInputRequest.getCouponCode()).receiptNumber(posInputRequest.getReceiptNumber()).date(posInputRequest.getDate())
                .time(posInputRequest.getTime()).build();

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("MyTransaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = transactionManager.getTransaction(def);

            List<TransactionTable> optionalDataFromDB = transactionRepository.findByReceiptNumberIgnoreCase(posInputRequest.getReceiptNumber());
            if (!optionalDataFromDB.isEmpty()) {
                TransactionTable transactionTableDataFromDB = optionalDataFromDB.get(0);

                if (transactionTableDataFromDB.getStatus().equalsIgnoreCase(CouponConstant.CANCELLED)) {
                    throw new BadApiRequestException("Coupon with the given receipt number is already cancelled");
                }

                if (transactionTableDataFromDB.getStatus().equalsIgnoreCase(CouponConstant.SETTLED) && !(transactionTableDataFromDB.getSettleDate().equals(posInputRequest.getDate()) && transactionTableDataFromDB.getSettleTime().isBefore(posInputRequest.getTime()))) {
                    throw new BadApiRequestException("Coupon with the given receipt number which was previously settled cannot be cancelled now");
                }

                log.debug("Transaction found: {}", transactionTableDataFromDB);
                if (!transactionTableDataFromDB.getChannel().equalsIgnoreCase(posInputRequest.getChannel())) {
                    log.error("Channel mismatch: expected {}, found {}", transactionTableDataFromDB.getChannel(), posInputRequest.getChannel());
                    throw new BadApiRequestException("This channel type for this settlement is invalid");
                }

                if (!transactionTableDataFromDB.getFranchise().equalsIgnoreCase(posInputRequest.getFranchise())) {
                    log.error("Franchise mismatch: expected {}, found {}", transactionTableDataFromDB.getFranchise(), posInputRequest.getFranchise());
                    throw new BadApiRequestException("This Franchise for this settlement is invalid");
                }

                if (!transactionTableDataFromDB.getChannelFullfillment().equalsIgnoreCase(posInputRequest.getFullfillmentMode())) {
                    log.error("Fullfillment mode mismatch: expected {}, found {}", transactionTableDataFromDB.getChannelFullfillment(), posInputRequest.getFullfillmentMode());
                    throw new BadApiRequestException("This Fullfillment type for this settlement is invalid");
                }

                if (!transactionTableDataFromDB.getStore().equalsIgnoreCase(posInputRequest.getStoreCode())) {
                    log.error("Store code mismatch: expected {}, found {}", transactionTableDataFromDB.getStore(), posInputRequest.getStoreCode());

                    throw new BadApiRequestException("This store code for this settlement is invalid");
                }

                if (!transactionTableDataFromDB.getCouponCode().equalsIgnoreCase(posInputRequest.getCouponCode())) {
                    log.error("Coupon code mismatch: expected {}, found {}", transactionTableDataFromDB.getCouponCode(), posInputRequest.getCouponCode());
                    throw new BadApiRequestException("This Coupon code for this settlement is invalid");
                }

                posOutputCancel.setCouponInfo(transactionTableDataFromDB.getTermsAndConditions());

                transactionTableDataFromDB.setStatus(CouponConstant.CANCELLED);
                transactionTableDataFromDB.setCancelDate(posInputRequest.getDate());
                transactionTableDataFromDB.setCancelTime(posInputRequest.getTime());
                try {
                    transactionRepository.save(transactionTableDataFromDB);
                    transactionManager.commit(status);
                }
                catch (Exception e){
                    log.error("Error in saving data in database in Transaction Table");
                    transactionManager.rollback(status);
                    throw new Exception("Error occured in saving data in database");
                }
                log.info("Cancellation successful for receipt number: {}", posInputRequest.getReceiptNumber());

            } else {
                log.error("Invalid receipt number: {}", posInputRequest.getReceiptNumber());
                throw new BadApiRequestException("Invalid receipt number");
            }


        posOutputCancel.setStatus(CouponConstant.SUCCESS);
        posOutputCancel.setMessage("Cancellation completed successfully");

        log.info("Cancellation process completed successfully for receipt number: {}", posInputRequest.getReceiptNumber());

        return posOutputCancel;
    }
}
