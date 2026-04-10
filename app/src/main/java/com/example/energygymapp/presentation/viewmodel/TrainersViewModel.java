package com.example.energygymapp.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.energygymapp.data.repository.TrainersRepository;
import com.example.energygymapp.domain.model.Trainer;

import java.util.List;

public class TrainersViewModel extends ViewModel {

    private final TrainersRepository trainersRepository;
    private final MutableLiveData<List<Trainer>> trainersLiveData = new MutableLiveData<>();

    public TrainersViewModel() {
        this.trainersRepository = new TrainersRepository();
    }

    public void loadTrainers() {
        trainersRepository.loadTrainers(trainersLiveData::postValue);
    }

    public LiveData<List<Trainer>> getTrainers() {
        return trainersLiveData;
    }
}
