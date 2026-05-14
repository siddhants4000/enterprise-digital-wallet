package com.example.enterprise_digital_wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EnterpriseDigitalWalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseDigitalWalletApplication.class, args);
    }
}