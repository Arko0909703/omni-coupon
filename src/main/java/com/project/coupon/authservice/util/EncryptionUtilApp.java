package com.project.coupon.authservice.util;

import com.project.coupon.authservice.configs.EncryptionUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class EncryptionUtilApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(EncryptionUtil.class);
        context.refresh();

        EncryptionUtil encryptionUtil = context.getBean(EncryptionUtil.class);

        String email = "satyaki0906@gmail.com";
        String password = "1234";

        System.out.println("Encrypted Email: " + encryptionUtil.encrypt(email));
        System.out.println("Encrypted Password: " + encryptionUtil.encrypt(password));

        context.close();
    }
}
