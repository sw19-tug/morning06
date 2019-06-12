package at.tugraz.ist.swe.cheatapp;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private MainActivity activity;
    private View view;
    private Button sendButton;
    private Button editButton;
    private Button abortEditButton;
    private Button emojiKeyboardButton;
    private EmojiEditText textEntry;
    private RecyclerView messageRecycler;
    private MessageAdapter messageAdapter;
    private MessageRepository messageRepository;
    private boolean chatFragmentReady = false;
    private long connectedDeviceId;
    private EmojiPopup emojiPopup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_chat, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textEntry = view.findViewById(R.id.txt_chat_entry);
        sendButton = view.findViewById(R.id.btn_chat_send);
        editButton = view.findViewById(R.id.btn_edit_send);
        abortEditButton = view.findViewById(R.id.btn_abort_edit);
        emojiKeyboardButton = view.findViewById(R.id.btn_emoji_keyboard);

        ViewGroup rootView = view.findViewById(R.id.relativeLayout1);

        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                    @Override
                    public void onEmojiPopupDismiss() {
                        emojiKeyboardButton.setBackground(getResources().getDrawable(R.drawable.emoji_button_layout_inactive));
                    }
                })
                .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                    @Override
                    public void onEmojiPopupShown() {
                        emojiKeyboardButton.setBackground(getResources().getDrawable(R.drawable.emoji_button_layout_active));
                    }
                })
                .build(textEntry);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        emojiPopup.dismiss();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
        BluetoothProvider provider = activity.getBluetoothProvider();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendButtonClicked();
            }
        });
        emojiKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmojiKeyboardButtonClicked();
            }
        });

        if (!Utils.isTesting()) {
            messageRepository = MessageRepository.createRepository(this.getContext());
        } else {
            messageRepository = MessageRepository.createInMemoryRepository(this.getContext());
        }

        try {
            provider.waitForConnectionFinished();
        } catch (InterruptedException e) {
            e.printStackTrace();
            activity.showToast(e.getMessage());
        }

        Device connectedDevice = provider.getConnectedDevice();
        connectedDeviceId = connectedDevice.getDeviceId();

        activity.getSupportActionBar().setTitle(connectedDevice.getNickname());
        FileEncoder fileEncoder = new FileEncoder();
        byte[] profilePicture = fileEncoder.decodeBase64(connectedDevice.getProfilePicture());
        Drawable image = new BitmapDrawable(activity.getResources(), BitmapFactory.decodeByteArray(profilePicture, 0, profilePicture.length));
        activity.getToolbar().setNavigationIcon(image);

        final List<ChatMessage> messageList = new ArrayList<>();
        messageRepository.getMessagesByUserId(connectedDeviceId).observe(this, new Observer<List<ChatMessage>>() { // TODO: change user id to the id of the chat partner
            @Override
            public void onChanged(@Nullable List<ChatMessage> messages) {
                messageList.clear();
                messageList.addAll(messages);

                messageAdapter.notifyDataSetChanged();
                if (messageAdapter.getItemCount() > 1)
                    messageRecycler.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });

        messageRecycler = view.findViewById(R.id.rvChat);
        messageAdapter = new MessageAdapter(messageList, this);
        messageRecycler.setAdapter(messageAdapter);
        messageRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

        SharedPreferences.Editor preferencesEditor =
                activity.getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE).edit();
        preferencesEditor.putLong("lastConDev", connectedDevice.getDeviceId());
        preferencesEditor.apply();

        synchronized (this) {
            this.chatFragmentReady = true;
            this.notify();
        }
    }

    @Override
    public void onDestroy() {
        activity.getSupportActionBar().setTitle(R.string.app_name);
        synchronized (this) {
            this.chatFragmentReady = false;
        }
        super.onDestroy();
    }

    private void onSendButtonClicked() {
        String sanitizedMessageText = Utils.sanitizeMessage(textEntry.getText().toString());

        if (sanitizedMessageText != null) {
            final ChatMessage message = new ChatMessage(connectedDeviceId, sanitizedMessageText, true, false);
            activity.getBluetoothProvider().sendMessage(message);
            messageRepository.insertMessage(message);
            textEntry.getText().clear();
        }
    }

    private void onEmojiKeyboardButtonClicked() {
        emojiPopup.toggle();
    }

    public void onMessageReceived(final ChatMessage message) {
        message.setUserId(connectedDeviceId);
        if (message.getMessageEdited()) {
            List<ChatMessage> updatedMessages = messageRepository.getMessagesByMessageUUID(message.getMessageUUID());

            for (ChatMessage updatedMessage : updatedMessages) {
                if (!updatedMessage.getMessageSent()) {
                    updatedMessage.setMessageText(message.getMessageText());
                    updatedMessage.setMessageEdited(true);
                    updatedMessage.setTimestamp(System.currentTimeMillis());

                    messageRepository.updateMessage(updatedMessage);
                    System.out.println("Update message: " + updatedMessage.getJsonString());
                }
            }
        } else {
            messageRepository.insertMessage(message);
        }
        System.out.println("Receive: " + message.getJsonString());
    }

    public void onMessageEdit(final ChatMessage message) {
        textEntry.setText(message.getMessageText());
        message.setMessageEdited(true);
        sendButton.setVisibility(View.INVISIBLE);
        editButton.setVisibility(View.VISIBLE);
        abortEditButton.setVisibility(View.VISIBLE);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setMessageText(textEntry.getText().toString());
                message.setMessageEdited(true);
                message.setTimestamp(System.currentTimeMillis());
                messageRepository.updateMessage(message);

                activity.getBluetoothProvider().sendMessage(message);
                textEntry.getText().clear();
                sendButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.INVISIBLE);
                abortEditButton.setVisibility(View.INVISIBLE);
            }
        });

        abortEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textEntry.getText().clear();
                sendButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.INVISIBLE);
                abortEditButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    public synchronized void waitForFragmentReady() throws InterruptedException {
        if (!this.chatFragmentReady) {
            this.wait();
        }
    }

    public MessageAdapter getMessageAdapter() {
        return messageAdapter;
    }

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }

    public EditText getTextEntry() {
        return textEntry;
    }

    public boolean isEmojiKeyboardShowing() {
        return emojiPopup.isShowing();
    }

    public void clearTextEntry() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textEntry.setText("");
            }
        });
    }
}
