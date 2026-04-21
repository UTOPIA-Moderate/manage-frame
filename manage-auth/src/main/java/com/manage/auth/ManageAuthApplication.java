package com.manage.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.manage.auth", "com.manage.common"})
@MapperScan({"com.manage.auth.mapper", "com.manage.common.mapper"})
public class ManageAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageAuthApplication.class, args);
    }
}
