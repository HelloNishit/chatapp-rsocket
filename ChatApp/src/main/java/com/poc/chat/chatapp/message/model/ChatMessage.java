package com.poc.chat.chatapp.message.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
  private Integer id ;
  private User to;
  private User from;
  private String message;
  private LocalDateTime createdDate;
  private Boolean delivered = Boolean.FALSE;
}
