package com.example.energygymapp.presentation.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.energygymapp.data.database.AppDatabase;
import com.example.energygymapp.data.mapper.AiMessageMapper;
import com.example.energygymapp.data.repository.AiRepository;
import com.example.energygymapp.data.repository.AuthRepository;
import com.example.energygymapp.domain.util.OnResultListener;
import com.example.energygymapp.databinding.ActivityAiChatBinding;
import com.example.energygymapp.domain.dao.AiMessageDao;
import com.example.energygymapp.domain.model.Message;
import com.example.energygymapp.presentation.adapter.ChatMessagesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AiChatActivity extends AppCompatActivity {

    private static final String CURRENT_UID = "user";

    private ActivityAiChatBinding binding;
    private ChatMessagesAdapter adapter;
    private final List<Message> messageList = new ArrayList<>();
    private AiRepository aiRepository;
    private AiMessageDao aiMessageDao;
    private ExecutorService executor;
    private String currentUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAiChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        currentUserUid = AuthRepository.getCurrentUserId();
        aiRepository = new AiRepository("sk-07947eb5ea0441538d1434e11a118830");

        executor = Executors.newSingleThreadExecutor();
        aiMessageDao = AppDatabase.getInstance(this).aiMessageDao();

        adapter = new ChatMessagesAdapter(CURRENT_UID);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);

        // получаем историю переписки с AI в фоновом потоке (с помощью executor-а)
        executor.execute(() -> {
            List<Message> history = AiMessageMapper.toMessageList(aiMessageDao.getByUserId(currentUserUid));
            runOnUiThread(() -> {
                messageList.addAll(history);
                if (!messageList.isEmpty()) {
                    int lastPosition = messageList.size() - 1;
                    adapter.setMessages(new ArrayList<>(messageList), () ->
                            binding.recyclerView.scrollToPosition(lastPosition));
                }
            });
        });

        binding.sendButton.setOnClickListener(v -> {
            String text = binding.messageEditText.getText().toString().trim();
            if (text.isEmpty()) return;

            addMessage(text, "user");
            binding.messageEditText.setText("");

            aiRepository.sendMessage(text, new OnResultListener<String>() {
                @Override
                public void onSuccess(String result) {
                    runOnUiThread(() -> addMessage(result, "ai"));
                }

                @Override
                public void onFailure() {
                    runOnUiThread(() ->
                            Toast.makeText(AiChatActivity.this, "Ошибка получения ответа", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    private void addMessage(String content, String sentBy) {
        Message message = new Message(
                UUID.randomUUID().toString(),
                sentBy,
                System.currentTimeMillis(),
                content
        );
        messageList.add(message);
        int targetPosition = messageList.size() - 1;
        adapter.setMessages(new ArrayList<>(messageList), () ->
                binding.recyclerView.scrollToPosition(targetPosition));

        // Добавляем сообщение в локальную БД. Делаем это через executor, который выполнит операцию на фоновом потоке
        executor.execute(() -> aiMessageDao.insert(AiMessageMapper.toEntity(message, currentUserUid)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
