package com.numgenerator.numgenerator.numgenerator.config;

import com.numgenerator.numgenerator.numgenerator.models.Result;
import com.numgenerator.numgenerator.numgenerator.models.ResultState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author adithyar
 */
@Configuration
public class AppConfig {

	@Bean
	public ConcurrentHashMap<String, Result> idToResult(){
		return new ConcurrentHashMap<>();
	}

	@Bean
	public ConcurrentHashMap<String, String> idToState(){
		return new ConcurrentHashMap<>();
	}
}
