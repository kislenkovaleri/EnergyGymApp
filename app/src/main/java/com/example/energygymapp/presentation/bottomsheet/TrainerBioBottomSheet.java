package com.example.energygymapp.presentation.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.energygymapp.R;
import com.example.energygymapp.databinding.BottomSheetTrainerBioBinding;
import com.example.energygymapp.domain.model.Trainer;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class TrainerBioBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetTrainerBioBinding binding;
    private final Trainer trainer;

    public TrainerBioBottomSheet(Trainer trainer) {
        this.trainer = trainer;
    }

    public TrainerBioBottomSheet() {
        this.trainer = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetTrainerBioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (trainer == null) return;

        binding.tvTrainerNameBio.setText(trainer.getName() != null ? trainer.getName() : "");
        binding.tvWorkoutTypeBio.setText(trainer.getWorkoutType() != null ? trainer.getWorkoutType() : "");
        binding.tvPriceBio.setText(trainer.getLessonPrice() != null ? trainer.getLessonPrice() : "");
        binding.tvTrainerBioText.setText(trainer.getBio() != null ? trainer.getBio() : "");

        if (trainer.getProfileImageUrl() != null && !trainer.getProfileImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(trainer.getProfileImageUrl())
                    .placeholder(R.drawable.baseline_person_outline_24)
                    .circleCrop()
                    .into(binding.ivTrainerImageBio);
        } else {
            binding.ivTrainerImageBio.setImageResource(R.drawable.baseline_person_outline_24);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
