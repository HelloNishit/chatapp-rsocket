package com.poc.chat.chatapp.controller;

import com.poc.chat.chatapp.message.model.ChatMessage;
import com.poc.chat.chatapp.repository.MessageStaticRepository;
import com.poc.chat.chatapp.repository.UserRepository;
import com.poc.chat.chatapp.service.ChatMessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Date;


@Controller
public class ChatController {

    @Autowired
    private ChatMessageServiceImpl chatMessageService;

    @MessageMapping("subscribeChatMessage")
    public Flux<ChatMessage> subscribeChatMessage(String userName) {
        System.out.println(userName + "lastMessageId: " + 0);
        return chatMessageService.getChatMessage(userName,0);
    }

    @MessageMapping("sendSubscribeChatMessage")
    public Mono<Void> sendSubscribeChatMessage(ChatMessage message) {
        message.setId(MessageStaticRepository.CHAT_ID.addAndGet(1));
        message.setDelivered(false);
        message.setCreatedDate(LocalDateTime.now());
        chatMessageService.addChatMessage(message);
        return Mono.empty();
    }
}
