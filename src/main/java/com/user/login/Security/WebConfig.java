package com.user.login.Security;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000") // Allow requests from frontend
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE") // Allow necessary methods
            .allowedHeaders("*")
            .allowCredentials(true);
  }
}
