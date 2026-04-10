package com.example.energygymapp.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.energygymapp.data.repository.ChatsRepository;
import com.example.energygymapp.data.repository.UserRepository;
import com.example.energygymapp.domain.model.Message;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatsRepository chatsRepository;
    private final UserRepository userRepository;
    private final MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> chatIdLiveData = new MutableLiveData<>();

    private String chatId;

    public ChatViewModel(String chatId) {
        this.chatId = chatId;
        this.chatsRepository = new ChatsRepository();
        this.userRepository = new UserRepository();
    }

    public void openChat(String currentUid, String otherUid) {
        chatsRepository.createOrOpenChat(currentUid, otherUid, userRepository, chatId -> {
            ChatViewModel.this.chatId = chatId;
            chatIdLiveData.postValue(chatId);
            listenToMessages();
        });
    }

    public void listenToMessages() {
        chatsRepository.listenToMessages(chatId, messagesLiveData::postValue);
    }

    public void sendMessage(String senderUid, String content) {
        chatsRepository.sendMessage(chatId, senderUid, content);
    }

    @Override
    protected void onCleared() {
        chatsRepository.stopMessagesListening(chatId);
        super.onCleared();
    }

    public LiveData<List<Message>> getMessages() {
        return messagesLiveData;
    }

    public LiveData<String> getChatId() {
        return chatIdLiveData;
    }

    // так как в эту ViewModel нам нужно передать аргумент (chatId), создаем для нее кастомный ViewModelFactory
    public static class Factory implements ViewModelProvider.Factory {

        private final String chatId;

        public Factory(String chatId) {
            this.chatId = chatId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ChatViewModel.class)) {
                return (T) new ChatViewModel(chatId);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
