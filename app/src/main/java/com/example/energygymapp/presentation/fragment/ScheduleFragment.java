package com.example.energygymapp.presentation.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.energygymapp.R;
import com.example.energygymapp.databinding.FragmentScheduleBinding;
import com.example.energygymapp.domain.model.ScheduleItem;
import com.example.energygymapp.domain.model.User;
import com.example.energygymapp.presentation.activity.LoginActivity;
import com.example.energygymapp.presentation.adapter.ScheduleAdapter;
import com.example.energygymapp.presentation.viewmodel.ScheduleViewModel;
import com.example.energygymapp.presentation.viewmodel.UserViewModel;
import com.example.energygymapp.data.repository.AuthRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    private ScheduleViewModel scheduleViewModel;
    private UserViewModel userViewModel;
    private ScheduleAdapter scheduleAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        scheduleAdapter = new ScheduleAdapter();
        binding.rvSchedule.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSchedule.setAdapter(scheduleAdapter);

        String uid = AuthRepository.getCurrentUserId();

        if (uid != null) {
            userViewModel.loadUser(uid);
            userViewModel.getUser().observe(getViewLifecycleOwner(), this::bindUser);
        }

        binding.btnLogout.setOnClickListener(v -> {
            AuthRepository.signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            // Очищаем back stack, чтобы после выхода пользователь не мог вернуться в приложение кнопкой "Назад"
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        scheduleViewModel.loadSchedule();
        scheduleViewModel.getSchedule().observe(getViewLifecycleOwner(), this::bindSchedule);
    }

    private boolean isSubscriptionExpired(String subscriptionActiveUntil) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date date = dateFormat.parse(subscriptionActiveUntil);
            Date today = new Date();
            return today.after(date) && !dateFormat.format(today).equals(subscriptionActiveUntil) /* check that it's not the current day */;
        } catch (ParseException e) {
            return false;
        }
    }

    private void bindUser(User user) {
        if (user == null) return;
        binding.tvName.setText(user.getName() != null ? user.getName() : "");
        binding.tvPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");

        if (user.isTrainer()) {
            binding.profileCard.setBackground(
                    ContextCompat.getDrawable(requireContext(), R.drawable.trainer_card_background));
            binding.tvTrainerBadge.setVisibility(View.VISIBLE);
            binding.tvSubscriptionUntil.setVisibility(View.GONE);
        } else {
            String subUntil = user.getSubscriptionActiveUntil();
            if (subUntil != null && !subUntil.isEmpty() && !isSubscriptionExpired(subUntil)) {
                binding.tvSubscriptionUntil.setText(getString(R.string.subscription_active_until, subUntil));
            } else {
                binding.tvSubscriptionUntil.setText(R.string.subscription_inactive);
            }
        }

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getProfileImageUrl())
                    .circleCrop()
                    .into(binding.ivProfilePicture);
        }
    }

    private void bindSchedule(List<ScheduleItem> items) {
        if (items == null) return;
        scheduleAdapter.setItems(items);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
