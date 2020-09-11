package com.orangebank.codechallenge;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@SpringBootApplication
public class OrangebankCodechallengeApplication {

	
	/** 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(OrangebankCodechallengeApplication.class, args);
	}

	
	/** 
	 * @return ModelMapper
	 */
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	
	/** 
	 * @return MethodValidationPostProcessor
	 */
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}
}
