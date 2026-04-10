package com.example.energygymapp.domain.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.energygymapp.domain.entity.AiMessageEntity;

import java.util.List;

/**
 * AiMessageDao - Room Dao (database access object) - интерфейс (который реализуется с помощью Room), который предоставляет функционал для взаимодействия с локальной базой данных SQLite
 */
@Dao
public interface AiMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AiMessageEntity entity);

    @Query("SELECT * FROM ai_messages WHERE userId = :userId ORDER BY sentAt ASC")
    List<AiMessageEntity> getByUserId(String userId);
}
