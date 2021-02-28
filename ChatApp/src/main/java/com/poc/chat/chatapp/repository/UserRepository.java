package com.poc.chat.chatapp.repository;

import com.poc.chat.chatapp.message.model.User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserRepository {
    public static User JON = new User(1, "Jon");
    public static User SMITH = new User(2, "Smith");
    public static User NISHIT = new User(3, "Nishit");
    public static User NEEL =  new User(4, "Neel");
    public static User ELIZA =  new User(5, "Eliza");
    public static List<User> users = Arrays.asList(JON, SMITH, NISHIT, NEEL, ELIZA);

    public List<User> getUsers() {
        return users;
    }
}
