package com.example.energygymapp.domain.util;


/**
 * Базовая конструкция обработчика результата асинхронной операции.
 * Все операции по получению данных из Realtime Database - асинхронные,
 * поэтому нужен некий callback, который сработает, когда данные будут получены
 * @param <T> - Класс, объект которого будет представлен как результат в onSuccess
 */
public interface OnResultListener<T> {
    void onSuccess(T result);
    default void onFailure() {}
}
