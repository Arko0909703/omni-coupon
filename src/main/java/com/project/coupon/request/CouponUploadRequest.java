package com.project.coupon.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CouponUploadRequest {

    private String couponName;
    private String batchLotName;
    private MultipartFile file;
}
