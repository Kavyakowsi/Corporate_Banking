package com.example.corporatebanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
        "com.example.corporatebanking.entity"
})
@EnableJpaRepositories(basePackages = {
        "com.example.corporatebanking.repository"
})
public class CorporateBankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorporateBankingApplication.class, args);
    }
}