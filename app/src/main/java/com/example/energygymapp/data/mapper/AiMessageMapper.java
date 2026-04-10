package com.example.energygymapp.data.mapper;

import com.example.energygymapp.domain.entity.AiMessageEntity;
import com.example.energygymapp.domain.model.Message;

import java.util.ArrayList;
import java.util.List;

public final class AiMessageMapper {

    private AiMessageMapper() { }

    public static Message toMessage(AiMessageEntity entity) {
        return new Message(
                entity.getId(),
                entity.getSentBy(),
                entity.getSentAt(),
                entity.getContent()
        );
    }

    public static AiMessageEntity toEntity(Message message, String userId) {
        return new AiMessageEntity(
                message.getId(),
                userId,
                message.getSentBy(),
                message.getSentAt(),
                message.getContent()
        );
    }

    public static List<Message> toMessageList(List<AiMessageEntity> entities) {
        List<Message> messages = new ArrayList<>(entities.size());
        for (AiMessageEntity entity : entities) {
            messages.add(toMessage(entity));
        }
        return messages;
    }
}
