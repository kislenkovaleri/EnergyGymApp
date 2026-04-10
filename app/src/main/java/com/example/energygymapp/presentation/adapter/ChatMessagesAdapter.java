package com.example.energygymapp.presentation.adapter;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.energygymapp.databinding.ItemDateSeparatorBinding;
import com.example.energygymapp.databinding.ItemMessageIncomingBinding;
import com.example.energygymapp.databinding.ItemMessageOutgoingBinding;
import com.example.energygymapp.domain.model.Message;

public class ChatMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_OUTGOING = 1;
    private static final int VIEW_TYPE_INCOMING = 2;
    private static final int VIEW_TYPE_DATE_HEADER = 3;

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("d MMMM yyyy", new Locale("ru"));

    private final String currentUid;

    private final AsyncListDiffer<Object> differ = new AsyncListDiffer<>(this,
            new DiffUtil.ItemCallback<Object>() {
                @Override
                public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                    if (oldItem instanceof String && newItem instanceof String) {
                        return oldItem.equals(newItem);
                    }
                    if (oldItem instanceof Message && newItem instanceof Message) {
                        return Objects.equals(((Message) oldItem).getId(), ((Message) newItem).getId());
                    }
                    return false;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                    if (oldItem instanceof String && newItem instanceof String) {
                        return oldItem.equals(newItem);
                    }
                    if (oldItem instanceof Message && newItem instanceof Message) {
                        return Objects.equals(((Message) oldItem).getContent(), ((Message) newItem).getContent());
                    }
                    return false;
                }
            });

    public ChatMessagesAdapter(String currentUid) {
        this.currentUid = currentUid;
    }

    public void setMessages(List<Message> messages, @Nullable Runnable commitCallback) {
        differ.submitList(buildDisplayList(messages), commitCallback);
    }

    private List<Object> buildDisplayList(List<Message> messages) {
        List<Object> display = new ArrayList<>();
        if (messages == null) return display;
        String lastDateLabel = null;
        for (Message message : messages) {
            String dateLabel = getDateLabel(message.getSentAt());
            if (!dateLabel.equals(lastDateLabel)) {
                display.add(dateLabel);
                lastDateLabel = dateLabel;
            }
            display.add(message);
        }
        return display;
    }

    private String getDateLabel(long sentAt) {
        if (sentAt == 0) return "";
        Calendar msgCal = Calendar.getInstance();
        msgCal.setTimeInMillis(sentAt);

        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        if (isSameDay(msgCal, today)) return "Сегодня";
        if (isSameDay(msgCal, yesterday)) return "Вчера";
        return DATE_FORMAT.format(new Date(sentAt));
    }

    private boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    private static String formatTime(long sentAt) {
        if (sentAt == 0) return "";
        return TIME_FORMAT.format(new Date(sentAt));
    }

    @Override
    public int getItemViewType(int position) {
        Object item = differ.getCurrentList().get(position);
        if (item instanceof String) return VIEW_TYPE_DATE_HEADER;
        Message message = (Message) item;
        return (currentUid != null && currentUid.equals(message.getSentBy()))
                ? VIEW_TYPE_OUTGOING : VIEW_TYPE_INCOMING;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_OUTGOING) {
            return new OutgoingViewHolder(ItemMessageOutgoingBinding.inflate(inflater, parent, false));
        } else if (viewType == VIEW_TYPE_INCOMING) {
            return new IncomingViewHolder(ItemMessageIncomingBinding.inflate(inflater, parent, false));
        } else {
            return new DateHeaderViewHolder(ItemDateSeparatorBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = differ.getCurrentList().get(position);
        if (holder instanceof DateHeaderViewHolder) {
            ((DateHeaderViewHolder) holder).bind((String) item);
        } else if (holder instanceof OutgoingViewHolder) {
            ((OutgoingViewHolder) holder).bind((Message) item);
        } else if (holder instanceof IncomingViewHolder) {
            ((IncomingViewHolder) holder).bind((Message) item);
        }
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        private final ItemDateSeparatorBinding binding;

        DateHeaderViewHolder(ItemDateSeparatorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String dateLabel) {
            binding.tvDate.setText(dateLabel);
        }
    }

    static class OutgoingViewHolder extends RecyclerView.ViewHolder {
        private final ItemMessageOutgoingBinding binding;

        OutgoingViewHolder(ItemMessageOutgoingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Message message) {
            binding.tvMessageContent.setText(message.getContent());
            binding.tvSentAt.setText(formatTime(message.getSentAt()));
        }
    }

    static class IncomingViewHolder extends RecyclerView.ViewHolder {
        private static final Pattern BOLD_PATTERN = Pattern.compile("\\*\\*(.*?)\\*\\*");
        private final ItemMessageIncomingBinding binding;

        IncomingViewHolder(ItemMessageIncomingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Message message) {
            binding.tvMessageContent.setText(formatBold(message.getContent()));
            binding.tvSentAt.setText(formatTime(message.getSentAt()));
        }

        private static CharSequence formatBold(String text) {
            if (text == null) return "";
            SpannableStringBuilder builder = new SpannableStringBuilder();
            Matcher matcher = BOLD_PATTERN.matcher(text);
            int lastEnd = 0;
            while (matcher.find()) {
                builder.append(text, lastEnd, matcher.start());
                int spanStart = builder.length();
                builder.append(matcher.group(1));
                builder.setSpan(new StyleSpan(Typeface.BOLD), spanStart, builder.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                lastEnd = matcher.end();
            }
            builder.append(text, lastEnd, text.length());
            return builder;
        }
    }
}
