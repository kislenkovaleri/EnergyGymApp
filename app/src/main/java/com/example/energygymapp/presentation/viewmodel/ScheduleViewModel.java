package com.example.energygymapp.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.energygymapp.data.repository.ScheduleRepository;
import com.example.energygymapp.domain.model.ScheduleItem;

import java.util.List;

public class ScheduleViewModel extends ViewModel {

    private final ScheduleRepository scheduleRepository;
    private final MutableLiveData<List<ScheduleItem>> scheduleLiveData = new MutableLiveData<>();

    public ScheduleViewModel() {
        this.scheduleRepository = new ScheduleRepository();
    }

    public void loadSchedule() {
        scheduleRepository.loadSchedule(scheduleLiveData::postValue);
    }

    public LiveData<List<ScheduleItem>> getSchedule() {
        return scheduleLiveData;
    }
}
