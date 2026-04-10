package com.example.energygymapp.data.repository;

import com.example.energygymapp.data.api.DeepSeekApiService;
import com.example.energygymapp.data.dto.DeepSeekMessage;
import com.example.energygymapp.data.dto.DeepSeekRequest;
import com.example.energygymapp.domain.util.OnResultListener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AiRepository {

    private static final String BASE_URL = "https://api.deepseek.com/";
    private static final String MODEL = "deepseek-chat";
    private static final String SYSTEM_PROMPT =
            "Ты профессиональный фитнес тренер и нутрициолог. Ответь на вопрос на русском. Ответ должен быть не слишком длинным, не нужно отвечать очень подробно." +
                    "Постарайся уложиться в несколько предложений, но если нужно - напиши больше.";

    private final String apiKey;
    private final DeepSeekApiService apiService;

    public AiRepository(String apiKey) {
        this.apiKey = apiKey;

        // конфигурация Http клиента, настройка таймаутов
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // здесь ставим 60 секунд, чтобы AI успел снегерировать ответ
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        // создание retrofit объекта и сервиса, который будет отправлять запросы на AI
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.apiService = retrofit.create(DeepSeekApiService.class);
    }

    public void sendMessage(String userMessage, OnResultListener<String> listener) {
        // передаем модели два сообщения: первое - системный промпт, который конфигурирует задачу, говоря, что надо будет сделать и второе - сам запрос пользователя
        List<DeepSeekMessage> messages = Arrays.asList(
                new DeepSeekMessage("system", SYSTEM_PROMPT),
                new DeepSeekMessage("user", userMessage)
        );

        DeepSeekRequest request = new DeepSeekRequest(MODEL, messages);

        apiService.sendMessage(
                "Bearer " + apiKey, request /* API ключ посылается в Authentication header-е */
                ).thenAccept(response -> {
                    if (response != null
                            && response.getChoices() != null
                            && !response.getChoices().isEmpty()) {
                        String content = response.getChoices().get(0).getMessage().getContent();
                        listener.onSuccess(content);
                    } else {
                        listener.onFailure();
                    }
                })
                .exceptionally(throwable -> {
                    listener.onFailure();
                    return null;
                });
    }
}
