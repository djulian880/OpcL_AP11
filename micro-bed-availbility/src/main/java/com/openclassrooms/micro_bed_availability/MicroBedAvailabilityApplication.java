package com.openclassrooms.micro_bed_availability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.openclassrooms.micro_bed_availability")
@EnableDiscoveryClient
public class MicroBedAvailabilityApplication {



	public static void main(String[] args) {

		SpringApplication.run(MicroBedAvailabilityApplication.class, args);

	}


}
