package com.example.energygymapp.data.constants;

/**
 * Класс с основными строковыми константами, использующимися в БД
 */
public class DbConstants {

    // SQLite
    public static final String APP_AI_CHAT_DB_NAME = "ai_chat_db";

    // Корневые узлы Firebase Realtime Database
    public static final String NODE_CLIENTS = "Clients";
    public static final String NODE_TRAINERS = "Trainers";
    public static final String NODE_GYM_SCHEDULE = "Gym_schedule";
    public static final String NODE_CHATS = "Chats";
    public static final String NODE_MESSAGES = "messages";

    // Поля клиента
    public static final String FIELD_NAME = "name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PHONE_NUMBER = "phone_number";
    public static final String FIELD_PROFILE_IMAGE_URL = "profile_image_url";
    public static final String FIELD_SUBSCRIPTION_ACTIVE_UNTIL = "subscription_active_until";
    public static final String FIELD_CHATS_IDS = "chats_ids";

    // Поля тренера
    public static final String FIELD_LESSON_PRICE = "lesson_price";
    public static final String FIELD_WORKOUT_TYPE = "workout_type";
    public static final String FIELD_BIO = "bio";

    // Поля элемента расписания
    public static final String FIELD_SCHEDULE_ITEM_NAME = "schedule_item_name";
    public static final String FIELD_START_TIME = "start_time";
    public static final String FIELD_END_TIME = "end_time";
    public static final String FIELD_LOCATION = "location";

    // Поля чата
    public static final String FIELD_UID1 = "uid1";
    public static final String FIELD_UID2 = "uid2";
    public static final String FIELD_LAST_MESSAGE = "last_message";

    // Поля сообщения
    public static final String FIELD_SENT_BY = "sent_by";
    public static final String FIELD_SENT_AT = "sent_at";
    public static final String FIELD_CONTENT = "content";

    // Пути Firebase Storage
    public static final String STORAGE_PROFILE_IMAGES = "profile_images";
}
