package com.zboigor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

/**
 * @author Igor Zboichik
 * @since 2017-03-14
 */
@Component
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@Data
public class AppProperties {

    private final Bot bot = new Bot();
    private final DB db = new DB();

    @Data
    public static class Bot {
        private String name;
        private String token;
    }

    @Data
    public static class DB {
        private String url;
        private String username;
        private String password;
    }
}
