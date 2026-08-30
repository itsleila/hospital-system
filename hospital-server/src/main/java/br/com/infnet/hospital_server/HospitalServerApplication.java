package br.com.infnet.hospital_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class HospitalServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalServerApplication.class, args);
	}

}
