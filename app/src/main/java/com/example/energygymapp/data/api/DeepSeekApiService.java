package com.example.energygymapp.data.api;

import com.example.energygymapp.data.dto.DeepSeekRequest;
import com.example.energygymapp.data.dto.DeepSeekResponse;

import java.util.concurrent.CompletableFuture;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Сервис Retrofit, который содержит метод для отправки запроса в DeepSeek
 */
public interface DeepSeekApiService {
    @POST("chat/completions")
    CompletableFuture<DeepSeekResponse> sendMessage(
            @Header("Authorization") String authorization,
            @Body DeepSeekRequest request
    );
}
