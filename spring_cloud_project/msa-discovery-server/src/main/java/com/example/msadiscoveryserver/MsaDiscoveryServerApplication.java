package com.example.msadiscoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MsaDiscoveryServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsaDiscoveryServerApplication.class, args);
	}

}
