package com.example.energygymapp.data.repository;

import androidx.annotation.NonNull;

import com.example.energygymapp.data.constants.DbConstants;
import com.example.energygymapp.domain.model.Trainer;
import com.example.energygymapp.domain.util.OnResultListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrainersRepository {

    private final DatabaseReference databaseReference;

    public TrainersRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void loadTrainers(OnResultListener<List<Trainer>> listener) {
        databaseReference.child(DbConstants.NODE_TRAINERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Trainer> trainers = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Trainer trainer = new Trainer();
                            trainer.setUid(child.getKey());
                            if (child.hasChild(DbConstants.FIELD_NAME)) {
                                trainer.setName(child.child(DbConstants.FIELD_NAME).getValue(String.class));
                            }
                            if (child.hasChild(DbConstants.FIELD_EMAIL)) {
                                trainer.setEmail(child.child(DbConstants.FIELD_EMAIL).getValue(String.class));
                            }
                            if (child.hasChild(DbConstants.FIELD_PHONE_NUMBER)) {
                                trainer.setPhoneNumber(child.child(DbConstants.FIELD_PHONE_NUMBER).getValue(String.class));
                            }
                            if (child.hasChild(DbConstants.FIELD_PROFILE_IMAGE_URL)) {
                                trainer.setProfileImageUrl(child.child(DbConstants.FIELD_PROFILE_IMAGE_URL).getValue(String.class));
                            }
                            if (child.hasChild(DbConstants.FIELD_LESSON_PRICE)) {
                                trainer.setLessonPrice(child.child(DbConstants.FIELD_LESSON_PRICE).getValue(String.class));
                            }
                            if (child.hasChild(DbConstants.FIELD_WORKOUT_TYPE)) {
                                trainer.setWorkoutType(child.child(DbConstants.FIELD_WORKOUT_TYPE).getValue(String.class));
                            }
                            if (child.hasChild(DbConstants.FIELD_BIO)) {
                                trainer.setBio(child.child(DbConstants.FIELD_BIO).getValue(String.class));
                            }
                            trainers.add(trainer);
                        }
                        listener.onSuccess(trainers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onFailure();
                    }
                });
    }
}
