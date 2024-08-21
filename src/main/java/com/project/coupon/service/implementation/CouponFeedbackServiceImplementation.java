package com.project.coupon.service.implementation;

import com.project.coupon.constants.CouponConstant;
import com.project.coupon.exceptions.BadApiRequestException;
import com.project.coupon.jpaEntities.CouponFeedback;
import com.project.coupon.jpaEntities.QuestionAnswer;
import com.project.coupon.jpaRepositories.CouponFeedbackRepository;
import com.project.coupon.request.CouponFeedbackRequest;
import com.project.coupon.request.QuestionAnswerRequest;
import com.project.coupon.response.CouponFeedbackIssueResponse;
import com.project.coupon.service.CouponFeedbackService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class CouponFeedbackServiceImplementation implements CouponFeedbackService {

    @Autowired
    CouponFeedbackRepository couponFeedbackRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public CouponFeedbackIssueResponse saveCouponFeedback(CouponFeedbackRequest couponFeedbackRequest) throws Exception {

        log.info("CouponFeedbackRequest value in Method SaveCouponFeedback() : "+couponFeedbackRequest);
        String id = UUID.randomUUID().toString();
        CouponFeedback couponFeedback=CouponFeedback.builder().id(id).channel(couponFeedbackRequest.getChannel()).dateTime(couponFeedbackRequest.getDateTime())
                .customerName(couponFeedbackRequest.getCustomerName()).mobileNumber(couponFeedbackRequest.getMobileNumber()).comments(couponFeedbackRequest.getComments())
                .couponCode(couponFeedbackRequest.getCouponCode()).build();

        List<QuestionAnswer> questionAnswerList=new ArrayList<>();
        for(QuestionAnswerRequest questionAnswerRequest: couponFeedbackRequest.getQuestionAnswer())
        {
            String qAId=UUID.randomUUID().toString();
            QuestionAnswer questionAnswer= QuestionAnswer.builder().question(questionAnswerRequest.getQuestion()).answer(questionAnswerRequest.getAnswer()).id(qAId).build();
            questionAnswer.setCouponFeedback(couponFeedback);
            questionAnswerList.add(questionAnswer);
        }
        couponFeedback.setQuestionAnswer(questionAnswerList);

        CouponFeedback couponFeedbackSaved=null;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("MyTransaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            couponFeedbackSaved=couponFeedbackRepository.save(couponFeedback);
            log.info("Coupon Feedback saved data from database: "+couponFeedbackSaved.toString());
            transactionManager.commit(status);
        } catch (Exception e) {
            log.error("Error in saving data in database in Transaction Table");
            transactionManager.rollback(status);
            throw new Exception("Error occured in saving data in database");
        }
        return CouponFeedbackIssueResponse.builder().status(CouponConstant.SUCCESS).Message("Coupon Feedback Submitted Successfully").build();
    }
}
