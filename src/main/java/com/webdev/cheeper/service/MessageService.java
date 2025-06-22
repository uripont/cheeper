package com.webdev.cheeper.service;

import com.webdev.cheeper.model.Message;
import com.webdev.cheeper.repository.MessageRepository;
import java.util.Date;

public class MessageService {
    private MessageRepository messageRepository;

    public MessageService() {
        this.messageRepository = new MessageRepository();
    }

    public Message saveMessage(Integer roomId, Integer senderId, String content) {
        Message message = new Message();
        message.setRoomId(roomId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setCreatedAt(new Date());

        messageRepository.save(message);
        return message;
    }
}
