package com.example.energygymapp.presentation.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.energygymapp.R;
import com.example.energygymapp.databinding.ItemChatBinding;
import com.example.energygymapp.domain.model.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    public interface OnChatClickListener {
        void onClick(Chat chat);
    }

    private List<Chat> chats = new ArrayList<>();
    private final OnChatClickListener listener;

    public ChatsAdapter(OnChatClickListener listener) {
        this.listener = listener;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatBinding binding = ItemChatBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(chats.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatBinding binding;

        public ViewHolder(ItemChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Chat chat, OnChatClickListener listener) {
            binding.tvChatName.setText(chat.getOtherPersonName() != null ? chat.getOtherPersonName() : "");
            binding.tvLastMessage.setText(chat.getLastMessage() != null ? chat.getLastMessage() : "");

            if (chat.getOtherPersonImageUrl() != null && !chat.getOtherPersonImageUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(chat.getOtherPersonImageUrl())
                        .placeholder(R.drawable.baseline_person_outline_24)
                        .circleCrop()
                        .into(binding.ivChatAvatar);
            } else {
                binding.ivChatAvatar.setImageResource(R.drawable.baseline_person_outline_24);
            }

            binding.getRoot().setOnClickListener(v -> listener.onClick(chat));
        }
    }
}
