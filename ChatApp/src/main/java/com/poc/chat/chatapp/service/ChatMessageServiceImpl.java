package com.poc.chat.chatapp.service;

import com.poc.chat.chatapp.message.model.ChatMessage;
import com.poc.chat.chatapp.message.model.User;
import com.poc.chat.chatapp.repository.MessageStaticRepository;
import com.poc.chat.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;


@Service
public class ChatMessageServiceImpl {

    @Autowired
    private MessageStaticRepository messageStaticRepository;

    @Autowired
    private UserRepository userRepository;

    public Flux<ChatMessage> getChatMessage(String user,Integer lastMessageId) {
        return messageStaticRepository.getMessage(user,lastMessageId);
    }

    public void addChatMessage(ChatMessage chatMessage) {
        messageStaticRepository.addMessage(chatMessage);
    }

    public List<User> getUsers(){
        return userRepository.getUsers();
    }
}
