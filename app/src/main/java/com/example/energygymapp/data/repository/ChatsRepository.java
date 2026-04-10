package com.example.energygymapp.data.repository;

import androidx.annotation.NonNull;

import com.example.energygymapp.data.constants.DbConstants;
import com.example.energygymapp.domain.model.Chat;
import com.example.energygymapp.domain.model.Message;
import com.example.energygymapp.domain.util.OnResultListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsRepository {

    private final DatabaseReference databaseReference;

    private ValueEventListener messagesListener;

    public ChatsRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static String buildChatId(String uid1, String uid2) {
        String[] uids = {uid1, uid2};
        Arrays.sort(uids);
        return uids[0] + uids[1];
    }

    public void createOrOpenChat(String currentUid, String otherUid, UserRepository userRepository,
                                  OnResultListener<String> listener) {
        String chatId = buildChatId(currentUid, otherUid);
        DatabaseReference chatRef = databaseReference.child(DbConstants.NODE_CHATS).child(chatId);

        // все запросы на Realtime Database асинхронные => добавляем listener, который сработает, когда БД вернет результат
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Map<String, Object> chatData = new HashMap<>();
                    chatData.put(DbConstants.FIELD_UID1, currentUid);
                    chatData.put(DbConstants.FIELD_UID2, otherUid);
                    chatData.put(DbConstants.FIELD_LAST_MESSAGE, "");

                    // Добавляем successListener, чтобы быть уверенными, что чат корректно сохранился в БД
                    chatRef.setValue(chatData).addOnSuccessListener(aVoid -> {
                        userRepository.addChatId(currentUid, chatId);
                        userRepository.addTrainerChatId(otherUid, chatId);
                        listener.onSuccess(chatId);
                    }).addOnFailureListener(e -> listener.onSuccess(chatId));
                } else {
                    listener.onSuccess(chatId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onSuccess(chatId);
            }
        });
    }

    public void sendMessage(String chatId, String senderUid, String content) {
        DatabaseReference messagesRef = databaseReference
                .child(DbConstants.NODE_CHATS)
                .child(chatId)
                .child(DbConstants.NODE_MESSAGES);

        String messageId = messagesRef.push().getKey();
        if (messageId == null) return;

        Map<String, Object> messageData = new HashMap<>();
        messageData.put(DbConstants.FIELD_SENT_BY, senderUid);
        messageData.put(DbConstants.FIELD_SENT_AT, ServerValue.TIMESTAMP);
        messageData.put(DbConstants.FIELD_CONTENT, content);

        messagesRef.child(messageId).setValue(messageData);

        databaseReference.child(DbConstants.NODE_CHATS).child(chatId)
                .child(DbConstants.FIELD_LAST_MESSAGE).setValue(content);
    }

    public void listenToMessages(String chatId, OnResultListener<List<Message>> listener) {
        if (messagesListener == null) {
            messagesListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Message> messages = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Message message = new Message();
                        message.setId(child.getKey());
                        if (child.hasChild(DbConstants.FIELD_SENT_BY)) {
                            message.setSentBy(child.child(DbConstants.FIELD_SENT_BY).getValue(String.class));
                        }
                        if (child.hasChild(DbConstants.FIELD_SENT_AT)) {
                            Object sentAt = child.child(DbConstants.FIELD_SENT_AT).getValue();
                            if (sentAt instanceof Long) {
                                message.setSentAt((Long) sentAt);
                            }
                        }
                        if (child.hasChild(DbConstants.FIELD_CONTENT)) {
                            message.setContent(child.child(DbConstants.FIELD_CONTENT).getValue(String.class));
                        }
                        messages.add(message);
                    }
                    listener.onSuccess(messages);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    listener.onFailure();
                }
            };
        }
        // начинаем слушать любые обновления данных в Chats/[chatId]/messages, на каждое обновление данных срабатывает messagesListener
        databaseReference.child(DbConstants.NODE_CHATS).child(chatId)
                .child(DbConstants.NODE_MESSAGES)
                .orderByChild(DbConstants.FIELD_SENT_AT)
                .addValueEventListener(messagesListener);
    }

    public void stopMessagesListening(String chatId) {
        if (messagesListener != null) {
            databaseReference.child(DbConstants.NODE_CHATS).child(chatId)
                    .child(DbConstants.NODE_MESSAGES)
                    .removeEventListener(messagesListener);
        }
    }

    public void loadChatsByIds(List<String> chatsIds, OnResultListener<List<Chat>> listener) {
        databaseReference.child(DbConstants.NODE_CHATS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            List<Chat> chats = new ArrayList<>();
                            for (String chatId : chatsIds) {
                                DataSnapshot chatSnapshot = snapshot.child(chatId);
                                if (!chatSnapshot.exists()) continue;

                                Chat chat = new Chat();
                                chat.setChatId(chatId);
                                if (chatSnapshot.hasChild(DbConstants.FIELD_UID1)) {
                                    chat.setUid1(chatSnapshot.child(DbConstants.FIELD_UID1).getValue(String.class));
                                }
                                if (chatSnapshot.hasChild(DbConstants.FIELD_UID2)) {
                                    chat.setUid2(chatSnapshot.child(DbConstants.FIELD_UID2).getValue(String.class));
                                }
                                if (chatSnapshot.hasChild(DbConstants.FIELD_LAST_MESSAGE)) {
                                    chat.setLastMessage(chatSnapshot.child(DbConstants.FIELD_LAST_MESSAGE).getValue(String.class));
                                }
                                chats.add(chat);
                            }
                            listener.onSuccess(chats);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure();
                    }
                });
    }
}
