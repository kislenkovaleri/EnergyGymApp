package com.example.energygymapp.presentation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.energygymapp.databinding.FragmentChatsBinding;
import com.example.energygymapp.domain.model.Chat;
import com.example.energygymapp.presentation.activity.AiChatActivity;
import com.example.energygymapp.presentation.activity.ChatActivity;
import com.example.energygymapp.presentation.adapter.ChatsAdapter;
import com.example.energygymapp.presentation.viewmodel.UserViewModel;
import com.example.energygymapp.data.repository.ChatsRepository;
import com.example.energygymapp.data.repository.UserRepository;
import com.example.energygymapp.data.repository.AuthRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private UserViewModel userViewModel;
    private ChatsAdapter chatsAdapter;
    private String currentUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUid = AuthRepository.getCurrentUserId();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        chatsAdapter = new ChatsAdapter(chat -> {
            /*
            При нажатии на элемент чата пользователь переходит в активность чата.
            Мы через intent extras передаем в активность чата chatId, id и имя пользователя, с которым
            идет переписка
             */
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_CHAT_ID, chat.getChatId());
            String otherUid = chat.getUid1().equals(currentUid) ? chat.getUid2() : chat.getUid1();
            intent.putExtra(ChatActivity.EXTRA_OTHER_UID, otherUid);
            intent.putExtra(ChatActivity.EXTRA_OTHER_NAME, chat.getOtherPersonName());
            startActivity(intent);
        });

        binding.rvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChats.setAdapter(chatsAdapter);

        if (currentUid != null) {
            userViewModel.loadChatIds(currentUid);
            userViewModel.getChatIds().observe(getViewLifecycleOwner(), (chatIds) -> {
                Collections.reverse(chatIds);
                loadChats(chatIds);
            });
        }

        binding.llAiChat.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AiChatActivity.class);
            startActivity(intent);
        });
    }

    private void loadChats(List<String> chatIds) {
        if (chatIds == null || chatIds.isEmpty()) {
            binding.tvNoChats.setVisibility(View.VISIBLE);
            binding.rvChats.setVisibility(View.GONE);
            return;
        }
        binding.tvNoChats.setVisibility(View.GONE);
        binding.rvChats.setVisibility(View.VISIBLE);

        ChatsRepository chatsRepository = new ChatsRepository();
        UserRepository userRepository = new UserRepository();

        chatsRepository.loadChatsByIds(chatIds, chats -> {
            for (Chat chat : chats) {
                String otherUid = chat.getUid1().equals(currentUid) ? chat.getUid2() : chat.getUid1();
                userRepository.loadUser(otherUid, otherUser -> {
                    chat.setOtherPersonName(otherUser.getName());
                    chat.setOtherPersonImageUrl(otherUser.getProfileImageUrl());

                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded()) return;
                        chatsAdapter.setChats(new ArrayList<>(chats));
                    });
                });
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
