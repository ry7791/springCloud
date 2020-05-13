package com.example.myappzuulgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
public class MyappZuulGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyappZuulGatewayApplication.class, args);
	}

}
