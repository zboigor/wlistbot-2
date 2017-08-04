package com.zboigor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

/**
 * @author Igor Zboichik
 * @since 2017-04-17
 */
@SpringBootApplication
public class MainApplication {

    static {
        ApiContextInitializer.init();
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MainApplication.class);
        app.run();
    }
}
