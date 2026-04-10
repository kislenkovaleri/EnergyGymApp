package com.example.energygymapp.presentation.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.energygymapp.databinding.ItemScheduleBinding;
import com.example.energygymapp.domain.model.ScheduleItem;

import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<ScheduleItem> items = new ArrayList<>();

    public void setItems(List<ScheduleItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemScheduleBinding binding = ItemScheduleBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemScheduleBinding binding;

        public ViewHolder(ItemScheduleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ScheduleItem item) {
            binding.tvScheduleDay.setText(item.getDay() != null ? item.getDay() : "");
            binding.tvScheduleName.setText(item.getScheduleItemName() != null ? item.getScheduleItemName() : "");
            String timeRange = "";
            if (item.getStartTime() != null && item.getEndTime() != null) {
                timeRange = item.getStartTime() + " - " + item.getEndTime();
            } else if (item.getStartTime() != null) {
                timeRange = item.getStartTime();
            }
            binding.tvScheduleTime.setText(timeRange);
            binding.tvScheduleLocation.setText(item.getLocation() != null ? item.getLocation() : "");
        }
    }
}
