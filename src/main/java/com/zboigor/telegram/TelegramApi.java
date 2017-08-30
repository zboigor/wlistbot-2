package com.zboigor.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import com.zboigor.config.AppProperties;
import com.zboigor.model.SpamTrigger;
import com.zboigor.service.BanVoteService;
import com.zboigor.service.SpamTriggerService;
import com.zboigor.util.Pair;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Igor Zboichik
 * @since 2017-08-03
 */
@Component
public class TelegramApi extends TelegramLongPollingBot {

    public static final String BAN = "BAN";
    public static final String NOT_BAN = "NOT_BAN";

    public static final long ADMIN_CHAT_ID = 88403602;
    public static final long DMSOL_CHAT_ID = 73212301;
    public static final int BOT_ID = 366958976;

    private final AppProperties appProperties;
    private final Keyboards keyboards;
    private final BanVoteService banVoteService;
    private final SpamTriggerService spamTriggerService;

    private static final String USER_LINK = "[%s](tg://user?id=%d)";
    private static final String VOTE_BAN_MESSAGE = "Vote for ban.\nInitiator: %s";

    private List<SpamTrigger> triggers;

    private boolean stopped = false;

    public TelegramApi(AppProperties appProperties, Keyboards keyboards, BanVoteService banVoteService, SpamTriggerService spamTriggerService) {
        this.appProperties = appProperties;
        this.keyboards = keyboards;
        this.banVoteService = banVoteService;
        this.spamTriggerService = spamTriggerService;
    }

    @PostConstruct
    public void postConstruct() {
        triggers = spamTriggerService.loadAll();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Message replyToMessage = message.getReplyToMessage();

            if (!stopped) {
                processVoteActivation(message, replyToMessage);

                if (!processTriggerAdding(message) && !processTriggerRemoving(message)) {
                    checkMessageForSpam(message);
                }
            }
            processStartStop(message);
        } else if (update.hasCallbackQuery()) {
            if (!stopped) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                Message message = callbackQuery.getMessage();
                Long chatId = message.getChatId();
                Integer messageId = message.getMessageId();
                Integer userId = callbackQuery.getFrom().getId();
                String queryData = callbackQuery.getData();

                processVote(queryData, chatId, messageId, userId, callbackQuery, message);
            }
        }
    }

    private String getVoteMessage(final User user) {
        return String.format(VOTE_BAN_MESSAGE, getUserName(user));
    }

    private void processStartStop(Message message) {
        Long chatId = message.getChatId();
        Integer userId = message.getFrom().getId();
        String messageText = message.getText();

        if (Objects.equals(chatId, userId.longValue()) && (chatId == ADMIN_CHAT_ID || chatId == DMSOL_CHAT_ID)) {
            if ("/stop".equalsIgnoreCase(messageText)) {
                stopped = true;
                sendMessage(chatId, "Bot is stopped");
            } else if ("/start".equalsIgnoreCase(messageText)) {
                stopped = false;
                sendMessage(chatId, "Bot is started");
            }
        }
    }

    private void checkMessageForSpam(Message message) {
        String messageText = message.getText();

        for (SpamTrigger spamTrigger : triggers) {
            Long chatId = message.getChatId();
            if (spamTrigger.getIsGlobal() || Objects.equals(spamTrigger.getChatId(), chatId)) {
                if (messageText != null && messageText.contains(spamTrigger.getTriggerText())) {
                    Integer messageId = message.getMessageId();
                    sendReplyMessage(chatId, "User `" + getUserName(message.getFrom()) + "` is banned for spam", messageId);
                    deleteMessage(chatId, messageId);
                    banUser(chatId, message.getFrom().getId());
                }
            }
        }
    }

    private String getUserName(User user) {
        return String.format(USER_LINK, user.getFirstName(), user.getId());
    }

    private boolean processTriggerAdding(Message message) {
        String messageText = message.getText();
        if (messageText != null && messageText.toLowerCase().startsWith("/add_spam_trigger ")) {
            Integer userId = message.getFrom().getId();
            Long chatId = message.getChatId();
            if (checkUserIsAdmin(chatId, userId) && messageText.length() > messageText.indexOf(' ')) {
                String trigger = message.getText().substring(message.getText().indexOf(' ') + 1);
                triggers.add(spamTriggerService.add(chatId, trigger));
                sendMessage(chatId, "Spam trigger `" + trigger + "` added");
            }
            return true;
        }
        return false;
    }

    private boolean processTriggerRemoving(Message message) {
        String messageText = message.getText();
        if (messageText != null && messageText.toLowerCase().startsWith("/remove_spam_trigger ")) {
            Integer userId = message.getFrom().getId();
            Long chatId = message.getChatId();
            if (checkUserIsAdmin(chatId, userId) && messageText.length() > messageText.indexOf(' ')) {
                String trigger = message.getText().substring(message.getText().indexOf(' ') + 1);
                spamTriggerService.remove(chatId, trigger);
                triggers = triggers.stream().filter(spamTrigger -> !(
                    Objects.equals(spamTrigger.getChatId(), chatId) &&
                        Objects.equals(spamTrigger.getTriggerText(), trigger) &&
                        Objects.equals(spamTrigger.getIsGlobal(), false)))
                    .collect(Collectors.toList());
                sendMessage(chatId, "Spam trigger `" + trigger + "` removed");
            }
            return true;
        }
        return false;
    }

    private void processVoteActivation(Message message, Message replyToMessage) {
       String messageText = message.getText();
        if (replyToMessage != null && ("/spam".equalsIgnoreCase(messageText) || "/voteban".equalsIgnoreCase(messageText))) {
            sendReplyMessageWithKeyboard(message.getChatId(), getVoteMessage(message.getFrom()),
                replyToMessage.getMessageId(), keyboards.getSpamVotingKeyboard(1, 0));
            banVoteService.vote(message.getChatId(), message.getMessageId(), message.getFrom().getId(), true);
        }
    }

    private void processVote(String queryData, Long chatId, Integer messageId, Integer userId, CallbackQuery callbackQuery,
                             Message message) {
        Message spamMessage = message.getReplyToMessage();
        if ((BAN.equalsIgnoreCase(queryData) || NOT_BAN.equalsIgnoreCase(queryData))
                && !spamMessage.getFrom().getId().equals(userId)) {
            banVoteService.vote(chatId, messageId, userId, BAN.equals(queryData));
            sendCallbackQueryAnswer(callbackQuery.getId(), "Your vote accepted", false);
            Pair<Integer, Integer> actualVotes = banVoteService.getActualVotes(chatId, messageId);

            Integer banVotes = actualVotes.getFirst();
            Integer notBanVotes = actualVotes.getSecond();
            sendEditMessageText(chatId, messageId, message.getText(), keyboards.getSpamVotingKeyboard(banVotes, notBanVotes));
            if (banVotes - notBanVotes > 4 && !spamMessage.getFrom().getId().equals(BOT_ID)) {
                deleteMessage(chatId, spamMessage.getMessageId());
                banUser(chatId, spamMessage.getFrom().getId());
            }
        }
    }

    public InlineKeyboardButton createInlineKeyboardButton(String text, String callback) {
        return new InlineKeyboardButton().setText(text).setCallbackData(callback);
    }

    public List<InlineKeyboardButton> createInlineKeyboardButtonRow(String text, String callback) {
        return Collections.singletonList(createInlineKeyboardButton(text, callback));
    }

    public void sendMessageWithInlineKeyboard(Long chatId, String messageText, InlineKeyboardMarkup keyboard) {
        SendMessage sendMessage = new SendMessage()
            .setChatId(chatId)
            .setText(messageText)
            .setReplyMarkup(keyboard)
            .enableMarkdown(true);
        send(sendMessage);
    }

    public void sendMessageWithKeyboard(Integer chatId, String messageText, ReplyKeyboard keyboard) {
        sendMessageWithKeyboard(Long.valueOf(chatId), messageText, keyboard);
    }

    public void sendMessageWithKeyboard(Long chatId, String messageText, ReplyKeyboard keyboard) {
        SendMessage sendMessage = new SendMessage()
            .setChatId(chatId)
            .setText(messageText)
            .setReplyMarkup(keyboard)
            .enableMarkdown(true);
        send(sendMessage);
    }

    public void sendReplyMessageWithKeyboard(Long chatId, String messageText, Integer replyTo, ReplyKeyboard keyboard) {
        SendMessage sendMessage = new SendMessage()
            .setChatId(chatId)
            .setText(messageText)
            .setReplyMarkup(keyboard)
            .setReplyToMessageId(replyTo)
            .enableMarkdown(true);
        send(sendMessage);
    }

    public void sendReplyMessage(Long chatId, String messageText, Integer replyTo) {
        SendMessage sendMessage = new SendMessage()
            .setChatId(chatId)
            .setText(messageText)
            .setReplyToMessageId(replyTo)
            .enableMarkdown(true);
        send(sendMessage);
    }

    public void sendEditMessageText(Long chatId, Integer messageId, String messageText, InlineKeyboardMarkup keyboard) {
        EditMessageText editMessageText = new EditMessageText()
            .setChatId(chatId)
            .setMessageId(messageId)
            .setText(messageText)
            .setReplyMarkup(keyboard)
            .enableMarkdown(true);
        try {
            editMessageText(editMessageText);
        } catch (TelegramApiException e) {
            sendException(e);
        }
    }

    public void sendEditMessageInlineKeyboard(Long chatId, Integer messageId, InlineKeyboardMarkup keyboard) {
        try {
            editMessageReplyMarkup(new EditMessageReplyMarkup()
                .setChatId(chatId)
                .setMessageId(messageId)
                .setReplyMarkup(keyboard));
        } catch (TelegramApiException e) {
            sendException(e);
        }
    }

    public void sendCallbackQueryAnswer(String callbackQueryId, String messageText, boolean showAlert) {
        try {
            answerCallbackQuery(new AnswerCallbackQuery().setCallbackQueryId(callbackQueryId).setShowAlert(showAlert).setText(messageText));
        } catch (TelegramApiException e) {
            sendException(e);
        }
    }

    public Message sendMessage(Long chatId, String message) {
        return send(new SendMessage().setChatId(chatId).setText(message).enableMarkdown(true));
    }

    public Message sendMessage(String chatId, String message) {
        return send(new SendMessage().setChatId(chatId).setText(message).enableHtml(true));
    }

    public Message sendMessage(Integer chatId, String message) {
        return sendMessage(Long.valueOf(chatId), message);
    }

    public Message send(SendMessage message) {
        try {
            return sendMessage(message);
        } catch (TelegramApiException e) {
            sendException(e);
        }
        return null;
    }

    public Boolean deleteMessage(Long chatId, Integer messageId) {
        try {
            return deleteMessage(new DeleteMessage(chatId, messageId));
        } catch (TelegramApiException e) {
            sendException(e);
        }
        return false;
    }

    public Boolean banUser(Long chatId, Integer userId) {
        try {
            return kickMember(new KickChatMember(chatId, userId));
        } catch (TelegramApiException e) {
            sendException(e);
        }
        return false;
    }

    public Boolean checkUserIsAdmin(Long chatId, Integer userId) {
        try {
            List<ChatMember> chatAdministrators = getChatAdministrators(new GetChatAdministrators().setChatId(chatId));
            return chatAdministrators.stream().anyMatch(chatMember -> Objects.equals(chatMember.getUser().getId(), userId) &&
                ("creator".equalsIgnoreCase(chatMember.getStatus()) || (chatMember.getCanDeleteMessages() != null && chatMember.getCanDeleteMessages())));
        } catch (TelegramApiException e) {
            sendException(e);
        }
        return false;
    }

    public void sendException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        if (!"Error editing message text".equals(e.getMessage())) {
            try {
                sendMessage(new SendMessage().setChatId(ADMIN_CHAT_ID).setText(sw.toString()));
            } catch (TelegramApiException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return appProperties.getBot().getName();
    }

    @Override
    public String getBotToken() {
        return appProperties.getBot().getToken();
    }
}
