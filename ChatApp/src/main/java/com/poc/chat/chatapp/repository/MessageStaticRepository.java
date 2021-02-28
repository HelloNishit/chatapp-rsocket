package com.poc.chat.chatapp.repository;

import com.poc.chat.chatapp.message.model.ChatMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class MessageStaticRepository {
    public static final AtomicInteger CHAT_ID = new AtomicInteger();
    ChatMessage message1 =
            new ChatMessage(
                    CHAT_ID.addAndGet(1),
                    UserRepository.NISHIT,
                    UserRepository.NEEL,
                    "Hi",
                    LocalDateTime.now(),
                    false);
    ChatMessage message2 =
            new ChatMessage(
                    CHAT_ID.addAndGet(1),
                    UserRepository.NEEL,
                    UserRepository.NISHIT,
                    "Hi",
                    LocalDateTime.now(),
                    false);
    ChatMessage message3 =
            new ChatMessage(
                    CHAT_ID.addAndGet(1),
                    UserRepository.NEEL,
                    UserRepository.NISHIT,
                    "How are you?",
                    LocalDateTime.now(),
                    false);
    ChatMessage message4 =
            new ChatMessage(
                    CHAT_ID.addAndGet(1),
                    UserRepository.NISHIT,
                    UserRepository.NEEL,
                    "I am good, How are you?",
                    LocalDateTime.now(),
                    false);
    public List<ChatMessage> chatMessageList =
            new ArrayList<>(Arrays.asList(message1, message2, message3, message4));

    public Map<String, Flux<ChatMessage>> USER_CHAT_MESSAGE = new ConcurrentHashMap<>();

    public Flux<ChatMessage> getMessage(String userName, Integer lastMessageId) {
        Flux<ChatMessage> messageFlux = USER_CHAT_MESSAGE.get(userName);
        if (messageFlux == null) {
            messageFlux =
                    Flux.interval(Duration.ZERO, Duration.ofSeconds(5))
                            .flatMap(chatMessage -> getChatMessages(userName, lastMessageId));
            messageFlux.doOnComplete(
                    () -> {
                        USER_CHAT_MESSAGE.remove(userName);
                    });
            USER_CHAT_MESSAGE.put(userName, messageFlux);
        }
        return messageFlux;
    }

    private Flux<ChatMessage> getChatMessages(String userName, Integer lastMessageId) {
        List<ChatMessage> collect =
                chatMessageList.stream()
                        .filter(
                                message -> {
//                                    System.out.println("---> " + message + "userName:" + userName);
                                    return (message.getTo().getUserName().equals(userName)
                                            || message.getFrom().getUserName().equals(userName))
                                            && message.getId() > lastMessageId;
                                })
                        .collect(Collectors.toList());
        return Flux.fromStream(collect.stream())
                .doOnNext(
                        chatMessage -> {
//                            System.out.println(chatMessage);
                            // chatMessage.setDelivered(true);
                        });
    }

    public void addMessage(ChatMessage chatMessage) {
        chatMessageList.add(chatMessage);
        Flux<ChatMessage> messageFlux = USER_CHAT_MESSAGE.get(chatMessage.getTo().getUserName());
        if (messageFlux != null) {
            messageFlux.concatWithValues(chatMessage);
        }
    }
}
