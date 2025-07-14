package fei.song.play_spring_boot_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 配置类
 * 配置 Swagger UI 和 API 文档的基本信息
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Play Spring Boot API")
                        .version("1.0.0")
                        .description("一个基于 Spring Boot 的 RESTful API 项目，采用领域驱动设计 (DDD) 架构模式，提供用户管理、购买历史、活动跟踪和用户档案等功能。")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com")
                                .url("https://github.com/songfei1983/play-spring-boot-api"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("开发环境"),
                        new Server()
                                .url("https://api.example.com")
                                .description("生产环境")))
                .tags(List.of(
                        new Tag()
                                .name("用户管理")
                                .description("用户相关的 CRUD 操作"),
                        new Tag()
                                .name("用户档案")
                                .description("用户档案信息管理"),
                        new Tag()
                                .name("活动跟踪")
                                .description("用户活动轨迹记录和查询"),
                        new Tag()
                                .name("购买历史")
                                .description("用户购买记录管理")));
    }
}