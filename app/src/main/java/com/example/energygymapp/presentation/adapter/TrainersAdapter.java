package com.example.energygymapp.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.energygymapp.R;
import com.example.energygymapp.data.repository.AuthRepository;
import com.example.energygymapp.databinding.ItemTrainerBinding;
import com.example.energygymapp.domain.model.Trainer;
import com.example.energygymapp.presentation.activity.ChatActivity;
import com.example.energygymapp.presentation.bottomsheet.TrainerBioBottomSheet;

import java.util.ArrayList;
import java.util.List;

public class TrainersAdapter extends RecyclerView.Adapter<TrainersAdapter.ViewHolder> {

    private List<Trainer> trainers = new ArrayList<>();
    public void setTrainers(List<Trainer> trainers) {
        this.trainers = trainers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrainerBinding binding = ItemTrainerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trainer trainer = trainers.get(position);
        holder.bind(trainer);
    }

    @Override
    public int getItemCount() {
        return trainers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTrainerBinding binding;

        public ViewHolder(ItemTrainerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Trainer trainer) {
            binding.tvTrainerName.setText(trainer.getName() != null ? trainer.getName() : "");
            binding.tvTrainerWorkouts.setText(trainer.getWorkoutType() != null ? trainer.getWorkoutType() : "");
            binding.tvPrice.setText(trainer.getLessonPrice() != null ? trainer.getLessonPrice() : "");

            if (trainer.getProfileImageUrl() != null && !trainer.getProfileImageUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(trainer.getProfileImageUrl())
                        .placeholder(R.drawable.baseline_person_outline_24)
                        .circleCrop()
                        .into(binding.ivTrainer);
            } else {
                binding.ivTrainer.setImageResource(R.drawable.baseline_person_outline_24);
            }

            // если данный пользователь залогинен как тренер, то у него не должно быть возможности написать самому себе через список тренеров
            if (AuthRepository.getCurrentUserId().equals(trainer.getUid())) {
                binding.btnChat.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> {
                Context context = v.getContext();
                if (context instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    FragmentManager fm = activity.getSupportFragmentManager();
                    TrainerBioBottomSheet bottomSheet = new TrainerBioBottomSheet(trainer);
                    bottomSheet.show(fm, TrainerBioBottomSheet.class.getSimpleName());
                }
            });

            binding.btnChat.setOnClickListener(v -> {
                // переходим в чат с тренером, передаем через intent extras параметры: id тренера и имя тренера
                Context context = v.getContext();
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_OTHER_UID, trainer.getUid());
                intent.putExtra(ChatActivity.EXTRA_OTHER_NAME, trainer.getName());
                context.startActivity(intent);
            });
        }
    }
}
