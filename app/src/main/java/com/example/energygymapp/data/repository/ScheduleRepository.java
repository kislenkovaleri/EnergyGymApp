package com.example.energygymapp.data.repository;

import androidx.annotation.NonNull;

import com.example.energygymapp.data.constants.DbConstants;
import com.example.energygymapp.domain.model.ScheduleItem;
import com.example.energygymapp.domain.util.OnResultListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleRepository {

    private static final Map<String, Integer> DAY_ORDER = new HashMap<String, Integer>() {{
        put("Понедельник", 0);
        put("Вторник", 1);
        put("Среда", 2);
        put("Четверг", 3);
        put("Пятница", 4);
        put("Суббота", 5);
        put("Воскресенье", 6);
    }};

    private final DatabaseReference databaseReference;

    public ScheduleRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void loadSchedule(OnResultListener<List<ScheduleItem>> listener) {
        databaseReference.child(DbConstants.NODE_GYM_SCHEDULE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ScheduleItem> items = new ArrayList<>();
                        for (DataSnapshot daySnapshot : snapshot.getChildren()) {
                            String day = daySnapshot.getKey();
                            for (DataSnapshot itemSnapshot : daySnapshot.getChildren()) {
                                ScheduleItem item = new ScheduleItem();
                                item.setId(itemSnapshot.getKey());
                                item.setDay(day);
                                if (itemSnapshot.hasChild(DbConstants.FIELD_SCHEDULE_ITEM_NAME)) {
                                    item.setScheduleItemName(itemSnapshot.child(DbConstants.FIELD_SCHEDULE_ITEM_NAME).getValue(String.class));
                                }
                                if (itemSnapshot.hasChild(DbConstants.FIELD_START_TIME)) {
                                    item.setStartTime(itemSnapshot.child(DbConstants.FIELD_START_TIME).getValue(String.class));
                                }
                                if (itemSnapshot.hasChild(DbConstants.FIELD_END_TIME)) {
                                    item.setEndTime(itemSnapshot.child(DbConstants.FIELD_END_TIME).getValue(String.class));
                                }
                                if (itemSnapshot.hasChild(DbConstants.FIELD_LOCATION)) {
                                    item.setLocation(itemSnapshot.child(DbConstants.FIELD_LOCATION).getValue(String.class));
                                }
                                items.add(item);
                            }
                        }
                        items.sort(Comparator.comparingInt(
                                item -> DAY_ORDER.getOrDefault(item.getDay(), 7)));
                        listener.onSuccess(items);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure();
                    }
                });
    }
}
