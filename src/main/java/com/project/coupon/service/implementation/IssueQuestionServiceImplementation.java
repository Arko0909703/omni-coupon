package com.project.coupon.service.implementation;

import com.project.coupon.constants.CouponConstant;
import com.project.coupon.jpaEntities.IssueQuestion;
import com.project.coupon.jpaRepositories.IssueQuestionRepository;
import com.project.coupon.request.IssueQuestionRequest;
import com.project.coupon.response.CouponFeedbackIssueResponse;
import com.project.coupon.response.IssueQuestionGetResponse;
import com.project.coupon.service.IssueQuestionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class IssueQuestionServiceImplementation implements IssueQuestionService {

    @Autowired
    IssueQuestionRepository issueQuestionRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public CouponFeedbackIssueResponse saveIssueQuestion(IssueQuestionRequest issueQuestionRequest) throws Exception {

        log.info("IssueQuestionRequest value in Method SaveIssueQuestion() "+issueQuestionRequest);
        String id= UUID.randomUUID().toString();

        IssueQuestion issueQuestion=IssueQuestion.builder().id(id).question(issueQuestionRequest.getQuestion().trim()).status(true).dateTime(LocalDateTime.now()).build();

        IssueQuestion issueQuestionSaved=null;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("MyTransaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);
        try{
            issueQuestionSaved=issueQuestionRepository.save(issueQuestion);
            transactionManager.commit(status);
            log.info("IssueQuestionSaved returned data from database: "+issueQuestionSaved);
        } catch (Exception e) {
            // Handle any other exceptions
            log.error("An error occurred: " + e.getMessage());
            transactionManager.rollback(status);
            throw new Exception("Error occured in saving data in database");
        }

        return CouponFeedbackIssueResponse.builder().status(CouponConstant.SUCCESS).Message("Issue Question Saved successfully").build();
    }

    @Override
    public IssueQuestionGetResponse getIssueQuestion() throws Exception {

        log.info("In Method GetIssueQuestion() ");
        List<IssueQuestion> issueQuestionList=null;
        try{
            issueQuestionList=issueQuestionRepository.findByStatus(true);
            log.info("Returned List of IssueQuestionGetResponse data from database: "+issueQuestionList);
        } catch (Exception e) {
            // Handle any other exceptions
            log.error("An error occurred: " + e.getMessage());
            throw new Exception("An error occurred while saving data in database");
        }

        List<String> questions=new ArrayList<>();
        for(IssueQuestion issueQuestion: issueQuestionList) {
            questions.add(issueQuestion.getQuestion());
        }
        IssueQuestionGetResponse issueQuestionGetResponse=new IssueQuestionGetResponse();
        issueQuestionGetResponse.setQuestions(questions);
        return issueQuestionGetResponse;
    }
}
