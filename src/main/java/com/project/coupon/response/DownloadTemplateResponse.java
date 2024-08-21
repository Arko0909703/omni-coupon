package com.project.coupon.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DownloadTemplateResponse {
    private String status;
    private String message;
    private String downloadLink;

    // Constructors, Getters, and Setters
    public DownloadTemplateResponse() {}

    public DownloadTemplateResponse(String status, String message, String downloadLink) {
        this.status = status;
        this.message = message;
        this.downloadLink = downloadLink;
    }
}
