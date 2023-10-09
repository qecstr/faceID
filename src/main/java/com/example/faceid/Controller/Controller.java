package com.example.faceid.Controller;

import com.example.faceid.Bot.BotConfig;
import com.example.faceid.Bot.TelegramBot;
import com.example.faceid.Entity.logReciever;
import com.example.faceid.Entity.logs;
import com.example.faceid.Entity.users;
import com.example.faceid.Repos.userTgRepo;
import com.example.faceid.Repos.usersRepo;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.HttpResource;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.util.Date;
import java.util.Map;
import java.util.logging.SocketHandler;

@RestController
@RequiredArgsConstructor
public class Controller {
    @Autowired
   public usersRepo uS;
    @Autowired
    public userTgRepo uTg;
    final BotConfig bc;
    @Autowired
    TelegramBot tb ;
    @PostMapping("/save")
    public void saveUser(@RequestBody users s){

        uS.save(s);
     //   tb.prepareAndSendMessage(uTg.findByUserName(s.getTelegram()).getChatId(), s.getName());
    }
    @GetMapping("/get")
    public void getUser(){


    }

    @PostMapping("/getLogs")
    @ResponseBody
    public void getLogs(@RequestBody logReciever lR) {
       // System.out.println(lR.getUserName());
        Date date = new Date( lR.getTimeCreated());
        String[] dateq = date.toString().split(" ");
        String temp = "";
        for(String s:dateq ){
            if(s == "ALMT"){break;}
            temp = temp + " "+ s;
        }
        if(uTg.findByUserName(uS.findByWorkid(lR.getWorkId()).getTelegram()) != null) {
            tb.prepareAndSendMessage(uTg.findByUserName(uS.findByWorkid(lR.getWorkId()).getTelegram()).getChatId(), lR.getUserName() + " " + temp);
        }else if(uTg.findByPhoneNumber(uS.findByWorkid(lR.getWorkId()).getPhoneNumber()) != null){
            tb.prepareAndSendMessage(uTg.findByPhoneNumber(uS.findByWorkid(lR.getWorkId()).getPhoneNumber()).getChatId(), lR.getUserName() + " " + temp);
        }
    }

}
