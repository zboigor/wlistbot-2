package com.zboigor.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class Keyboards {

    public InlineKeyboardButton createInlineKeyboardButton(String text, String callback) {
        return new InlineKeyboardButton().setText(text).setCallbackData(callback);
    }

    public List<InlineKeyboardButton> createInlineKeyboardButtonRow(String text, String callback) {
        return Collections.singletonList(createInlineKeyboardButton(text, callback));
    }

    public InlineKeyboardMarkup getSpamVotingKeyboard(final int banVotes, final int notBanVotes) {
        List<List<InlineKeyboardButton>> keys = new ArrayList<>();
        keys.add(Arrays.asList(
            createInlineKeyboardButton("Ban: " + banVotes, TelegramApi.BAN),
            createInlineKeyboardButton("Not ban: " + notBanVotes, TelegramApi.NOT_BAN)
        ));
        return new InlineKeyboardMarkup().setKeyboard(keys);
    }
}