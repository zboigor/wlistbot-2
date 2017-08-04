package com.zboigor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.concurrent.Executors;

/**
 * @author Igor Zboichik
 * @since 2017-04-16
 */
@Configuration
public class AsyncConfig {

    @Bean
    public TaskScheduler taskExecutor () {
        return new ConcurrentTaskScheduler(
                Executors.newScheduledThreadPool(3));
    }
}
