package com.merantory.YandexSBD;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class YandexSbdApplication {
	public static void main(String[] args) {
		SpringApplication.run(YandexSbdApplication.class, args);
	}
}
