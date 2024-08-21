package com.project.coupon.service.implementation;

import com.project.coupon.constants.CouponConstant;
import com.project.coupon.exceptions.BadApiRequestException;
import com.project.coupon.jpaEntities.CouponIssue;
import com.project.coupon.jpaEntities.QuestionAnswer;
import com.project.coupon.jpaRepositories.CouponIssueRepository;
import com.project.coupon.request.CouponIssueRequest;
import com.project.coupon.request.QuestionAnswerRequest;
import com.project.coupon.response.CouponFeedbackIssueResponse;
import com.project.coupon.service.CouponIssueService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class CouponIssueServiceImplementation implements CouponIssueService
{

    @Autowired
    CouponIssueRepository couponIssueRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public CouponFeedbackIssueResponse saveCouponIssue(CouponIssueRequest couponIssueRequest) throws Exception {

        log.info("CouponIssuenRequest value in Method saveCouponIssue() "+couponIssueRequest);
        String id= UUID.randomUUID().toString();
        CouponIssue couponIssue=CouponIssue.builder().id(id).couponCode(couponIssueRequest.getCouponCode()).dateTime(couponIssueRequest.getDateTime())
                .channel(couponIssueRequest.getChannel()).comments(couponIssueRequest.getComments()).mobileNumber(couponIssueRequest.getMobileNumber())
                .customerName(couponIssueRequest.getCustomerName()).build();

        List<QuestionAnswer> questionAnswerList=new ArrayList<>();
        for(QuestionAnswerRequest questionAnswerRequest: couponIssueRequest.getIssue())
        {
            String qAId=UUID.randomUUID().toString();
            QuestionAnswer questionAnswer= QuestionAnswer.builder().question(questionAnswerRequest.getQuestion()).answer(questionAnswerRequest.getAnswer()).id(qAId).build();
            questionAnswer.setCouponIssue(couponIssue);
            questionAnswerList.add(questionAnswer);
        }
        couponIssue.setIssue(questionAnswerList);

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("MyTransaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            CouponIssue couponIssueSaved=couponIssueRepository.save(couponIssue);
            log.info("CouponIssue returned data from database: "+couponIssueSaved.toString());
            transactionManager.commit(status);
        }catch (Exception e) {
            log.error("Error in saving data in database in Transaction Table");
            transactionManager.rollback(status);
            throw new Exception("Error occured in saving data in database");
        }

        return CouponFeedbackIssueResponse.builder().status(CouponConstant.SUCCESS).Message("Coupon Issue Saved Successfully").build();

    }
}
