package com.rwanda.gov.erp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {

    private static final List<String> TAG_ORDER = List.of(
            "Authentication",
            "Employee Management",
            "Employment Management",
            "Payroll Management",
            "Deduction Rate Management"
    );

    @Bean
    public OpenApiCustomizer tagOrderCustomizer() {
        return openApi -> {
            if (openApi.getTags() == null || openApi.getTags().isEmpty()) {
                return;
            }
            Map<String, Tag> tagsByName = new LinkedHashMap<>();
            for (Tag tag : openApi.getTags()) {
                tagsByName.putIfAbsent(tag.getName(), tag);
            }
            List<Tag> orderedTags = new ArrayList<>();
            for (String name : TAG_ORDER) {
                Tag tag = tagsByName.remove(name);
                if (tag != null) {
                    orderedTags.add(tag);
                }
            }
            orderedTags.addAll(tagsByName.values());
            openApi.setTags(orderedTags);
        };
    }
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")))
                .info(new Info()
                        .title("ERP Payroll Management System API")
                        .version("1.0.0")
                        .description("Enterprise Resource Planning - Payroll and Employee Management System for Rwanda Government")
                        .contact(new Contact()
                                .name("Rwanda Government")
                                .email("info@gov.rw"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .tags(Arrays.asList(
                        new Tag().name("Authentication").description("Authentication APIs for employee registration and login"),
                        new Tag().name("Employee Management").description("APIs for managing employees"),
                        new Tag().name("Employment Management").description("APIs for managing employment records"),
                        new Tag().name("Payroll Management").description("APIs for payroll generation and management"),
                        new Tag().name("Deduction Rate Management").description("APIs for managing tax and deduction rates")
                ));
    }
}
