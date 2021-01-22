package com.kahago.kahagoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAsync
@EnableFeignClients
//@EnableScheduling
@EnableTransactionManagement
public class KahagoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(KahagoServiceApplication.class, args);
	}

}
