package com.manage.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ManageGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageGatewayApplication.class, args);
    }
}
