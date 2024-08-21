package com.project.coupon.service;

import com.project.coupon.jpaEntities.IssueQuestion;
import com.project.coupon.request.IssueQuestionRequest;
import com.project.coupon.response.CouponFeedbackIssueResponse;
import com.project.coupon.response.IssueQuestionGetResponse;

import java.util.List;

public interface IssueQuestionService {

    CouponFeedbackIssueResponse saveIssueQuestion(IssueQuestionRequest issueQuestionRequest) throws Exception;
    IssueQuestionGetResponse getIssueQuestion() throws Exception;

}
