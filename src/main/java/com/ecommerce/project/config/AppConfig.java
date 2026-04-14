package com.ecommerce.project.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    /*Applications often consist of similar but different object models,
    where the data in two models may be similar but the structure and concerns of the models are different.
    Object mapping makes it easy to convert one model to another, allowing separate models to remain segregated.
     */
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
