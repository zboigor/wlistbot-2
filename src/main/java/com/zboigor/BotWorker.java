package com.zboigor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.generics.BotSession;
import com.zboigor.telegram.TelegramApi;

import javax.annotation.PreDestroy;

/**
 * @author Igor Zboichik
 * @since 2017-03-12
 */
@Component
public class BotWorker implements ApplicationRunner {

    @Autowired
    private TelegramApi bot;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
			throw new IllegalStateException(e);
        }
    }

}
