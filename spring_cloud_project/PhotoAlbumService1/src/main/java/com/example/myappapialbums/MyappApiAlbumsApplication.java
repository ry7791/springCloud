package com.example.myappapialbums;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MyappApiAlbumsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyappApiAlbumsApplication.class, args);
    }


}
