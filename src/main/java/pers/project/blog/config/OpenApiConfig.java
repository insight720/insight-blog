package pers.project.blog.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * OpenAPI 配置类
 *
 * @author Luo Fei
 * @version 2022/12/27
 */
@Profile("dev")
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI insightBlogApi() {
        return new OpenAPI().info(new Info()
                .title("Insight Blog API")
                .description("Spring Boot Application")
                .contact(new Contact().name("Luo Fei"))
                .version("1.0.0 GA")
                .termsOfService("https://insightblog.cn"));
    }

    @Bean
    @SuppressWarnings("all")
    public GroupedOpenApi defaultApiGroup() {
        return GroupedOpenApi.builder()
                .packagesToScan("pers.project.blog.controller")
                .group("default")
                .pathsToMatch("/**")
                .build();
    }

}
