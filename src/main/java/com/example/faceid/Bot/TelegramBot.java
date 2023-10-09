package com.example.faceid.Bot;


import com.example.faceid.Bot.BotConfig;
import com.example.faceid.Entity.userTg;
import com.example.faceid.Entity.users;
import com.example.faceid.Repos.userTgRepo;
import com.example.faceid.Repos.usersRepo;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    public  usersRepo repo;
    @Autowired
    public userTgRepo userTgRepo;
    final BotConfig config;
    static final String HELP_TEXT = "tester";

    static final String ERROR_TEXT = "Error occurred: ";

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
    @Override
    public String getBotToken(){
        return config.getToken();
    }

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/register", "show logs"));
     //   listofCommands.add(new BotCommand("/help", "info how to use this bot"));
        listofCommands.add(new BotCommand("/getUsers", "getUsers"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }

    }
    @Override
    public void onUpdateReceived(Update update) {



        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();


            switch (messageText) {
                case "/start":


                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());


                    break;

                case "/help":

                    sendMessage(chatId, HELP_TEXT);
                    break;

                case "/register":
                   //logsMessage(chatId);


                   registerUser(chatId,update.getMessage());
                    break;
                case "/getUsers":
                   userMsg(chatId,repo.findAll());
                   break;
                case "":

                    break;

                default:
                      if(isValid(String.valueOf(update.getMessage().getText()))){
                          sendMessage(chatId,"Вы успешно зарегестрированы");
                          registerUserByPhone(chatId,update.getMessage());
                      }else{
                          sendMessage(chatId,"Телефон номера введен не верно пожалуйста введите в формате +77078889900");
                }
            }

        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
          // var id = update.getCallbackQuery().getId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();


            if (callbackData.equals("PHONE")) {


                 toNumber(chatId,"Пожалуйста ведите номер телефона в формате +77078889900.");





            } else if (callbackData.equals("TLG")) {
                registerUserByTelegram(chatId, update.getCallbackQuery());
            }

        }
    }

    private void startCommandReceived(long chatId, String name) {


        String answer = ("Hi, " + name + ", nice to meet you!" );
        log.info("Replied to user " + name);


        sendMessage(chatId, answer);
    }
    private void logsMessage(long chatId){
        SendMessage message = new SendMessage();
        message.setText("sad");
        message.setChatId(String.valueOf(chatId));
        executeMessage(message);

    }
    public void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setChatId(chatId);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("/register");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);

        message.setReplyMarkup(keyboardMarkup);
        executeMessage(message);
    }
    public void toNumber(long chatId, String textToSend) {
        ForceReplyKeyboard fk = new ForceReplyKeyboard();
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setChatId(chatId);
        fk.setForceReply(true);
        message.setReplyToMessageId(message.getReplyToMessageId());

        executeMessage(message);

    }

    private void executeMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }
    private void registerUser(Long chatId ,Message msg) {

        if(userTgRepo.findByChatId(msg.getChatId()) == null){
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText("Как вы хотите зарегестрироваться");
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();

        yesButton.setText("Через номер телефона");
        yesButton.setCallbackData("PHONE");

        var noButton = new InlineKeyboardButton();

        noButton.setText("Через Никнейм Телеграмма");
        noButton.setCallbackData("TLG");

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);

        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message);


        /*    var chatId = msg.getChatId();
            var chat = msg.getChat();

            userTg user = new userTg();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());

            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userTgRepo.save(user);
            log.info("user saved: " + user);*/
        }else{
            sendMessage(chatId,"Вы уже зарегистрированы");
        }
    }
    public void prepareAndSendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Пришел: "+textToSend);
        executeMessage(message);
    }
    private void userMsg(long chatId, List<users> u){
        SendMessage message = new SendMessage();
        String r = "";
        for(int i =0;i<u.size();i++){
            r = r +" \n"+ u.get(i).getTelegram();
        }

        message.setText(r);
        message.setChatId(String.valueOf(chatId));
        executeMessage(message);
        
    }


    private void registerUserByPhone(Long chatId ,Message msg) {

        var chat = msg.getChat();

        userTg user = new userTg();

        user.setChatId(chatId);
        user.setFirstName(chat.getFirstName());
        user.setLastName(chat.getLastName());
        user.setUserName(chat.getUserName());
        user.setPhoneNumber(Long.valueOf(msg.getText()));
        user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

        userTgRepo.save(user);
        log.info("user saved: " + user);
    }

    private void registerUserByTelegram(Long chatId , CallbackQuery cq) {

        var chat = cq.getMessage().getChat();

        userTg user = new userTg();

        user.setChatId(chatId);
        user.setFirstName(chat.getFirstName());
        user.setLastName(chat.getLastName());
        user.setUserName(chat.getUserName());

        user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

        userTgRepo.save(user);
        log.info("user saved: " + user);
    }
    private boolean isValid(String phoneNumber){
        Pattern pattern = Pattern.compile("^\\+7[0-9]{10}$");
        System.out.println(phoneNumber);
        Matcher matcher = pattern.matcher(phoneNumber);
        System.out.println(matcher.matches());
        return matcher.matches();

    }
}
