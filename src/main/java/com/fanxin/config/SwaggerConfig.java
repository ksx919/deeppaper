package com.fanxin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DeepPaper API")
                        .description("DeepPaper 后端API接口文档")
                        .version("1.0")
                        .contact(new Contact()
                                .name("江在程")
                                .email("1983262652@qq.com")));
    }
}
