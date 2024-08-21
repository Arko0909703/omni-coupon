package com.project.coupon.jpaRepositories;

import com.project.coupon.jpaEntities.CouponIssue;
import com.project.coupon.jpaEntities.IssueQuestion;
import com.project.coupon.response.IssueQuestionGetResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueQuestionRepository extends JpaRepository<IssueQuestion,String> {

    List<IssueQuestion> findByStatus(boolean status);

}
