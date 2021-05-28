package com.copycoding.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Bean
    MappingJackson2JsonView jsonView(){
		System.out.println("jsonView 생성");
        return new MappingJackson2JsonView();
    }

}
