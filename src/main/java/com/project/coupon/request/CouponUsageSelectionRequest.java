package com.project.coupon.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponUsageSelectionRequest {
    private String couponName;
    private String usageType; // "system-generated" or "coupon-upload"
    private String prefix;
    private String suffix;
    private int quantity;
    private String batchLotName;
    private MultipartFile file; // Only used if usageType is "coupon-upload"
}
