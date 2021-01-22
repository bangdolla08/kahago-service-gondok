package com.kahago.kahagoservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

/**
 * @author Hendro yuwono
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket init() {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(Principal.class, Pageable.class, Authentication.class)
                .select()
                .paths(PathSelectors.ant("/api/**"))
                .apis(RequestHandlerSelectors.basePackage("com.kahago.kahagoservice.controller"))
                .build()
                .apiInfo(apiDetails())
                .ignoredParameterTypes(getClass());
    }

    private ApiInfo apiDetails() {
        return new ApiInfo(
                "Kaha Go API",
                "Integration Apps API",
                "1.0",
                "",
                new Contact("PT Kaha Holiday Internatonal", "http://kahago.com/", "admin@kahago.com"),
                "",
                "http://kahago.com",
                Collections.emptyList()
        );
    }

}
