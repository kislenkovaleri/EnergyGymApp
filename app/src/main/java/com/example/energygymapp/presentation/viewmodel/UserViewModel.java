package com.example.energygymapp.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.energygymapp.data.repository.UserRepository;
import com.example.energygymapp.domain.model.User;

import java.util.List;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> chatIdsLiveData = new MutableLiveData<>();

    public UserViewModel() {
        this.userRepository = new UserRepository();
    }

    public void loadUser(String uid) {
        userRepository.loadUser(uid, userLiveData::postValue);
    }

    public void loadChatIds(String uid) {
        userRepository.loadChatIds(uid, chatIdsLiveData::postValue);
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }

    public LiveData<List<String>> getChatIds() {
        return chatIdsLiveData;
    }
}
