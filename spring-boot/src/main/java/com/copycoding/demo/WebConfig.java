package com.copycoding.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Bean
    MappingJackson2JsonView jsonView(){
		System.out.println("jsonView 생성");
        return new MappingJackson2JsonView();
    }
	
	@Value("${spring.webservice.intro}")
    private String introPage;
     
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 루트 (/) 로 접근 시 introPage로 이동하는 매핑 추가
        registry.addRedirectViewController("/", introPage);
    }

}
