package com.axlero.dashboardbuilder.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI documentation with HTTP Bearer JWT authentication support.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI dashboardBuilderOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server()
                        .url("http://localhost:8080")
                        .description("Local development server")))
                .info(new Info()
                        .title("Axlero Dynamic Dashboard Builder API")
                        .version("1.0.0")
                        .description("REST API documentation for the Dynamic Data Dashboard Builder."))
                .tags(List.of(
                        new Tag().name("Authentication")
                                .description("Register or sign in before calling protected endpoints."),
                        new Tag().name("Workspaces")
                                .description("Create or select a workspace for your dashboard and sales data."),
                        new Tag().name("Sales Data")
                                .description("Import workspace sales records before building data-backed widgets."),
                        new Tag().name("Dashboards")
                                .description("Create and manage dashboards in a workspace."),
                        new Tag().name("Widgets")
                                .description("Add widgets to a dashboard and retrieve their data.")
                ))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }

    @Bean
    public OpenApiCustomizer dashboardBuilderWorkflowTags() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }

            openApi.getPaths().forEach((path, pathItem) -> {
                String tag = workflowTag(path);
                if (tag != null) {
                    pathItem.readOperations().forEach(operation -> operation.setTags(List.of(tag)));
                }
            });
        };
    }

    private String workflowTag(String path) {
        if (path.startsWith("/api/auth/")) {
            return "Authentication";
        }
        if (path.contains("/dashboards/") && path.contains("/widgets")) {
            return "Widgets";
        }
        if (path.contains("/dashboards")) {
            return "Dashboards";
        }
        if (path.startsWith("/api/workspaces/") && path.contains("/sales")) {
            return "Sales Data";
        }
        if (path.startsWith("/api/workspaces")) {
            return "Workspaces";
        }
        return null;
    }
}
