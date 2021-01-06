package se.experis.com.case2020.lagalt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String origin = System.getenv("origin");
        if(origin != null) {
            registry.addMapping("/**").allowedMethods("*").allowedOrigins(origin);
        } else {
            System.out.println("No environment variable for cors origin found. Allowing localhost:3000");
            registry.addMapping("/**").allowedMethods("*").allowedOrigins("http://localhost:3000");
        }
    }
}