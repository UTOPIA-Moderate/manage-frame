package com.manage.job;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"com.manage.job", "com.manage.common"})
@MapperScan({"com.manage.common.mapper"})
public class ManageJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageJobApplication.class, args);
    }
}
