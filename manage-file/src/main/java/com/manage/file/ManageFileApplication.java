package com.manage.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"com.manage.file", "com.manage.common"})
@MapperScan({"com.manage.file.mapper", "com.manage.common.mapper"})
public class ManageFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageFileApplication.class, args);
    }
}
