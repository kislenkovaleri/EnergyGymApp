package com.example.energygymapp.presentation.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.energygymapp.databinding.ActivityChatBinding;
import com.example.energygymapp.presentation.adapter.ChatMessagesAdapter;
import com.example.energygymapp.presentation.viewmodel.ChatViewModel;
import com.example.energygymapp.data.repository.AuthRepository;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_CHAT_ID = "extra_chat_id";
    public static final String EXTRA_OTHER_UID = "extra_other_uid";
    public static final String EXTRA_OTHER_NAME = "extra_other_name";

    private ActivityChatBinding binding;
    private ChatViewModel chatViewModel;
    private ChatMessagesAdapter messagesAdapter;
    private String currentUid;
    private String chatId;
    private String otherUid;
    private String otherName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currentUid = AuthRepository.getCurrentUserId();

        chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        otherUid = getIntent().getStringExtra(EXTRA_OTHER_UID);
        otherName = getIntent().getStringExtra(EXTRA_OTHER_NAME);

        chatViewModel = new ViewModelProvider(this, new ChatViewModel.Factory(chatId)).get(ChatViewModel.class);

        messagesAdapter = new ChatMessagesAdapter(currentUid);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMessages.setAdapter(messagesAdapter);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (otherName != null) {
                getSupportActionBar().setTitle(otherName);
            }
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        if (chatId != null && !chatId.isEmpty()) {
            // Переход из ChatsFragment - чат уже существует
            chatViewModel.listenToMessages();
        } else if (otherUid != null && currentUid != null) {
            // Переход из TrainersFragment - необходимо создать/открыть чат
            chatViewModel.openChat(currentUid, otherUid);
            chatViewModel.getChatId().observe(this, id -> {
                if (id != null) {
                    chatId = id;
                }
            });
        }

        chatViewModel.getMessages().observe(this, messages -> {
            if (messages != null) {
                messagesAdapter.setMessages(messages, () -> binding.rvMessages.scrollToPosition(messages.size() - 1));
                if (!messages.isEmpty()) {
                    binding.rvMessages.scrollToPosition(messages.size() - 1);
                }
            }
        });

        binding.btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String content = binding.etMessage.getText().toString().trim();
        if (content.isEmpty()) return;
        if (chatId == null || chatId.isEmpty()) return;

        chatViewModel.sendMessage(currentUid, content);
        binding.etMessage.setText("");
    }
}
