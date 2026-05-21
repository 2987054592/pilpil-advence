package com.pilpil.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pilpil Web API")
                        .version("1.0.0")
                        .description("Pilpil 项目 Web 模块接口文档")
                        .contact(new Contact()
                                .name("Pilpil Team")
                                .email("support@pilpil.com")));
    }
}
