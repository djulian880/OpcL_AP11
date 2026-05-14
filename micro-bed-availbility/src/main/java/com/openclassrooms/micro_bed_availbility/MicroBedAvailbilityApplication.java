package com.openclassrooms.micro_bed_availbility;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.openclassrooms.micro_bed_availbility")
@EnableDiscoveryClient
public class MicroBedAvailbilityApplication {



	public static void main(String[] args) {

		SpringApplication.run(MicroBedAvailbilityApplication.class, args);

	}


}
