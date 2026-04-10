package com.example.energygymapp.data.repository;

import androidx.annotation.NonNull;

import com.example.energygymapp.data.constants.DbConstants;
import com.example.energygymapp.domain.model.User;
import com.example.energygymapp.domain.util.OnResultListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Задача этого репозитория - предоставить функционал для работы с директорией пользователя в БД
 * Такие функции как:
 * - сохранить нового пользователя в БД
 * - загрузить пользователя из БД
 * - загрузить список чатов пользователя из БД
 */
public class UserRepository {

    private final DatabaseReference databaseReference;

    public UserRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void saveUserData(String uid, String name, String email, String phoneNumber,
                             String profileImageUrl, OnResultListener<Void> listener) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        String trialEndDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(calendar.getTime());

        Map<String, Object> userData = new HashMap<>();
        userData.put(DbConstants.FIELD_NAME, name);
        userData.put(DbConstants.FIELD_EMAIL, email);
        userData.put(DbConstants.FIELD_PHONE_NUMBER, phoneNumber);
        userData.put(DbConstants.FIELD_PROFILE_IMAGE_URL, profileImageUrl);
        userData.put(DbConstants.FIELD_SUBSCRIPTION_ACTIVE_UNTIL, trialEndDate);

        databaseReference.child(DbConstants.NODE_CLIENTS).child(uid)
                .setValue(userData)
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(e -> listener.onFailure());
    }

    public void loadUser(String uid, OnResultListener<User> listener) {
        databaseReference.child(DbConstants.NODE_CLIENTS).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = new User();
                            user.setUid(uid);
                            if (snapshot.hasChild(DbConstants.FIELD_NAME)) {
                                user.setName(snapshot.child(DbConstants.FIELD_NAME).getValue(String.class));
                            }
                            if (snapshot.hasChild(DbConstants.FIELD_EMAIL)) {
                                user.setEmail(snapshot.child(DbConstants.FIELD_EMAIL).getValue(String.class));
                            }
                            if (snapshot.hasChild(DbConstants.FIELD_PHONE_NUMBER)) {
                                user.setPhoneNumber(snapshot.child(DbConstants.FIELD_PHONE_NUMBER).getValue(String.class));
                            }
                            if (snapshot.hasChild(DbConstants.FIELD_PROFILE_IMAGE_URL)) {
                                user.setProfileImageUrl(snapshot.child(DbConstants.FIELD_PROFILE_IMAGE_URL).getValue(String.class));
                            }
                            if (snapshot.hasChild(DbConstants.FIELD_SUBSCRIPTION_ACTIVE_UNTIL)) {
                                user.setSubscriptionActiveUntil(snapshot.child(DbConstants.FIELD_SUBSCRIPTION_ACTIVE_UNTIL).getValue(String.class));
                            }
                            listener.onSuccess(user);
                        } else {
                            // Не найден в Clients — проверяем узел Trainers
                            databaseReference.child(DbConstants.NODE_TRAINERS).child(uid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot trainerSnapshot) {
                                            if (trainerSnapshot.exists()) {
                                                User user = new User();
                                                user.setUid(uid);
                                                user.setTrainer(true);
                                                if (trainerSnapshot.hasChild(DbConstants.FIELD_NAME)) {
                                                    user.setName(trainerSnapshot.child(DbConstants.FIELD_NAME).getValue(String.class));
                                                }
                                                if (trainerSnapshot.hasChild(DbConstants.FIELD_PHONE_NUMBER)) {
                                                    user.setPhoneNumber(trainerSnapshot.child(DbConstants.FIELD_PHONE_NUMBER).getValue(String.class));
                                                }
                                                if (trainerSnapshot.hasChild(DbConstants.FIELD_PROFILE_IMAGE_URL)) {
                                                    user.setProfileImageUrl(trainerSnapshot.child(DbConstants.FIELD_PROFILE_IMAGE_URL).getValue(String.class));
                                                }
                                                listener.onSuccess(user);
                                            } else {
                                                listener.onFailure();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            listener.onFailure();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure();
                    }
                });
    }

    public void loadChatIds(String uid, OnResultListener<List<String>> listener) {
        databaseReference.child(DbConstants.NODE_CLIENTS).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            List<String> chatIds = new ArrayList<>();
                            for (DataSnapshot child : snapshot.child(DbConstants.FIELD_CHATS_IDS).getChildren()) {
                                String chatId = child.getValue(String.class);
                                if (chatId != null) {
                                    chatIds.add(chatId);
                                }
                            }
                            listener.onSuccess(chatIds);
                        } else {
                            // если пользователя нет в клиентах, то это тренер
                            databaseReference.child(DbConstants.NODE_TRAINERS).child(uid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                List<String> chatIds = new ArrayList<>();
                                                for (DataSnapshot child : snapshot.child(DbConstants.FIELD_CHATS_IDS).getChildren()) {
                                                    String chatId = child.getValue(String.class);
                                                    if (chatId != null) {
                                                        chatIds.add(chatId);
                                                    }
                                                }
                                                listener.onSuccess(chatIds);
                                            } else {
                                                listener.onFailure();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            listener.onFailure();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure();
                    }
                });
    }

    public void addChatId(String uid, String chatId) {
        databaseReference.child(DbConstants.NODE_CLIENTS).child(uid)
                .child(DbConstants.FIELD_CHATS_IDS)
                .push()
                .setValue(chatId);
    }

    public void addTrainerChatId(String trainerUid, String chatId) {
        databaseReference.child(DbConstants.NODE_TRAINERS).child(trainerUid)
                .child(DbConstants.FIELD_CHATS_IDS)
                .push()
                .setValue(chatId);
    }
}
