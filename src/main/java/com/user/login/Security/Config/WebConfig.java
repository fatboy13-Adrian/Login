package com.user.login.Security.Config;                                     //Package declaration
import org.springframework.context.annotation.Configuration;                //Marks this as a configuration class
import org.springframework.web.servlet.config.annotation.CorsRegistry;      //Used to define CORS mappings
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;  //Interface to customize Spring MVC config

@Configuration //Declares this class as a Spring config component
public class WebConfig implements WebMvcConfigurer 
{
  @Override //Overrides method to set CORS rules
  public void addCorsMappings(CorsRegistry registry) 
  { 
    registry.addMapping("/**")                                        //Applies CORS to all endpoints
            .allowedOrigins("http://localhost:3000")                  //Allows frontend origin
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")  //Specifies allowed HTTP methods
            .allowedHeaders("*")                                      //Allows all headers
            .allowCredentials(true);                            //Allows sending credentials (cookies, auth)
  }
}