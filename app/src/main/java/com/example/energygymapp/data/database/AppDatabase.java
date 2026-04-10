package com.example.energygymapp.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.energygymapp.data.constants.DbConstants;
import com.example.energygymapp.domain.dao.AiMessageDao;
import com.example.energygymapp.domain.entity.AiMessageEntity;

/**
 * Класс управления SQLite БД
 */
@Database(entities = {AiMessageEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract AiMessageDao aiMessageDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DbConstants.APP_AI_CHAT_DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
