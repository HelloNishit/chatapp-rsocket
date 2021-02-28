package com.poc.chat.chatapp.controller;

import com.poc.chat.chatapp.message.model.ChatMessage;
import com.poc.chat.chatapp.message.model.User;
import com.poc.chat.chatapp.repository.UserRepository;
import com.poc.chat.chatapp.service.ChatMessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@RestController
public class RestChatController {

  @Autowired private ChatMessageServiceImpl chatMessageService;

  @GetMapping("/add")
  public void add() {
    ChatMessage message = new ChatMessage();
    message.setMessage("this is dummy" + new Date());
    message.setTo(UserRepository.ELIZA);
    message.setFrom(UserRepository.ELIZA);
    chatMessageService.addChatMessage(message);
  }

  @CrossOrigin(origins = "http://localhost:4200")
  @GetMapping("/getUsers")
  public List<User> getUsers() {
    return chatMessageService.getUsers();
  }
}
