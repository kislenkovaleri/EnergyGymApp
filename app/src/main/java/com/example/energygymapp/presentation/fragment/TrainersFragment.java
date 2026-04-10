package com.example.energygymapp.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.energygymapp.databinding.FragmentTrainersBinding;
import com.example.energygymapp.domain.model.Trainer;
import com.example.energygymapp.presentation.adapter.TrainersAdapter;
import com.example.energygymapp.presentation.viewmodel.TrainersViewModel;
import com.example.energygymapp.data.repository.AuthRepository;

import java.util.List;
import java.util.stream.Collectors;

public class TrainersFragment extends Fragment {

    private FragmentTrainersBinding binding;
    private TrainersViewModel trainersViewModel;
    private TrainersAdapter trainersAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTrainersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        trainersViewModel = new ViewModelProvider(this).get(TrainersViewModel.class);

        trainersAdapter = new TrainersAdapter();
        binding.rvTrainers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTrainers.setAdapter(trainersAdapter);

        trainersViewModel.loadTrainers();
        trainersViewModel.getTrainers().observe(getViewLifecycleOwner(), this::bindTrainers);
    }

    private void bindTrainers(List<Trainer> trainers) {
        if (trainers == null) return;
        trainersAdapter.setTrainers(
                trainers
                        .stream()
                        .filter(trainer -> !trainer.getUid().equals(AuthRepository.getCurrentUserId()))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
