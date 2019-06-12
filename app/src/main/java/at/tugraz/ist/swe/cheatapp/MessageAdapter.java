package at.tugraz.ist.swe.cheatapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<ChatMessage> messageList;
    Format dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private ChatFragment chatFragment;

    public MessageAdapter(List<ChatMessage> messageList, ChatFragment chatFragment) {
        this.messageList = messageList;
        this.chatFragment = chatFragment;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);
        if (message.getMessageSent()) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sent_message_layout, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_message_layout, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    public List<ChatMessage> getMessageList() {
        return messageList;
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        TextView textEditedIndicator;


        SentMessageHolder(final View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.txt_message_messageBody);
            timeText = itemView.findViewById(R.id.txt_message_messageTime);
            textEditedIndicator = itemView.findViewById(R.id.txt_message_messageEdited);

        }

        void bind(final ChatMessage message) {
            messageText.setText(message.getMessageText());
            timeText.setText(dateFormat.format(message.getTimestamp()));
            if(message.getMessageEdited()){
                textEditedIndicator.setText("edited");
            }
            else{
                textEditedIndicator.setText("");
            }
            messageText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    chatFragment.onMessageEdit(message);
                    return true;
                }
            });
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        TextView textEditedIndicator;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.txt_message_messageBody);
            timeText = itemView.findViewById(R.id.txt_message_messageTime);
            textEditedIndicator = itemView.findViewById(R.id.txt_message_messageEdited);
        }

        void bind(ChatMessage message) {
            timeText.setText(dateFormat.format(message.getTimestamp()));
            messageText.setText(message.getMessageText());
            if(message.getMessageEdited()){
                textEditedIndicator.setText("edited");
            }
            else{
                textEditedIndicator.setText("");
            }
        }
    }
}