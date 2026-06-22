package com.navaantrix.vyakhyanLite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class VyakhyanLiteApplication {

	public static void main(String[] args) {
//        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		SpringApplication.run(VyakhyanLiteApplication.class, args);
	}

}
