package com.project.coupon.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelStoreFileSave {

    public void save(MultipartFile file) throws IOException;

}
