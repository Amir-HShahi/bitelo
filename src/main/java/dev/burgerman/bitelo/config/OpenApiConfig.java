package dev.burgerman.bitelo.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

        @Bean
        OpenAPI customOpenAPI() {
                final String securitySchemeName = "bearerAuth";

                return new OpenAPI()
                                // Apply bearer security globally (Swagger UI lock icon)
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .components(new Components()
                                                .addSecuritySchemes(securitySchemeName,
                                                                new SecurityScheme()
                                                                                .name(securitySchemeName)
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")))
                                .info(new Info()
                                                .title("Bitelo API Documentation")
                                                .description("Complete documentation of API paths, requests, and responses")
                                                .version("0.1"))
                                .servers(List.of(
                                                new Server().url("http://localhost:8080").description("Development"),
                                                new Server().url("https://bitelo.burgerman.dev")
                                                                .description("Production")));
        }
}
