package com.management.eventmanagement.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI eventManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Event Management API Documentation")
                        .description("API documentation for all REST endpoints")
                        .version("1.0.0"));
    }
}
