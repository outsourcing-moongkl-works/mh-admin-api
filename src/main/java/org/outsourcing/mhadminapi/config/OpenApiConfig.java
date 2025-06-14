package org.outsourcing.mhadminapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("Authorization");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .info(new Info().title("MH ADMIN API")
                        .description("어드민 페이지 API LIST")
                        .version("v1.0"))
                        .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                        .security(Arrays.asList(securityRequirement))
                        .servers(Arrays.asList(
                        new Server().url("https://admin-api.moongkl.com").description("Production server"),
                        new Server().url("http://localhost:8080").description("Local development server"),
                        new Server().url("http://admin-api.moongkl.com").description("HTTP Production server")
                        ));
                        /*.contact(new Contact()
                                .name("Contact Name")
                                .url("http://your-contact-url.com")
                                .email("contact@email.com"))
                        .license(new License()
                                .name("License Name")
                                .url("http://your-license-url.com")));*/
    }
}
