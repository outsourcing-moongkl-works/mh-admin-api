package org.outsourcing.mhadminapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@OpenAPIDefinition(info = @Info(title = "MH ADMIN API", description = "어드민 페이지 API LIST", version = "1.0"))
@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

}
