package at.tugraz.ist.swe.cheatapp;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private MainActivity activity;
    private View view;
    private Button sendButton;
    private EditText textEntry;
    private RecyclerView messageRecycler;
    private MessageAdapter messageAdapter;
    private MessageRepository messageRepository;
    private boolean chatFragmentReady = false;
    private long connectedDeviceId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_chat, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();

        textEntry = view.findViewById(R.id.txt_chat_entry);
        sendButton = view.findViewById(R.id.btn_chat_send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendButtonClicked();
            }
        });

        if (!Utils.isTesting()) {
            messageRepository = MessageRepository.createRepository(this.getContext());
        } else {
            messageRepository = MessageRepository.createInMemoryRepository(this.getContext());
        }


        Device connectedDevice = null;
        while(connectedDevice == null)
        {
            connectedDevice = activity.getBluetoothProvider().getConnectedDevice();
            Thread.yield();
        }
        connectedDeviceId = connectedDevice.getDeviceId();

        final List<ChatMessage> messageList = new ArrayList<>();
        messageRepository.getMessagesByUserId(connectedDeviceId).observe(this, new Observer<List<ChatMessage>>() { // TODO: change user id to the id of the chat partner
            @Override
            public void onChanged(@Nullable List<ChatMessage> messages) {
                messageList.clear();
                for (ChatMessage msg : messages) {
                    /*
                    System.out.println("-----------REMOTE------------");
                    System.out.println(msg.getUserId());
                    System.out.println(msg.getMessageText());
                    System.out.println(msg.getMessageSent()); */
                    messageList.add(msg);
                }
                messageAdapter.notifyDataSetChanged();
                if(messageAdapter.getItemCount() > 1)
                    messageRecycler.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });

        messageRecycler = view.findViewById(R.id.rvChat);
        messageAdapter = new MessageAdapter(messageList);
        messageRecycler.setAdapter(messageAdapter);
        messageRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

        SharedPreferences.Editor preferencesEditor =
                 activity.getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE).edit();
        preferencesEditor.putLong("lastConDev",connectedDevice.getDeviceId());
        preferencesEditor.apply();

        synchronized (this) {
            this.chatFragmentReady = true;
            this.notify();
        }
    }

    @Override
    public void onDestroy() {
        synchronized (this ) {
            this.chatFragmentReady = false;
        }
        super.onDestroy();
    }

    private void onSendButtonClicked() {
        String sanitizedMessageText = Utils.sanitizeMessage(textEntry.getText().toString());

        if (sanitizedMessageText != null) {
            final ChatMessage message = new ChatMessage(connectedDeviceId, sanitizedMessageText, true);
            activity.getBluetoothProvider().sendMessage(message);
            messageRepository.insertMessage(message);
            textEntry.getText().clear();
        }
    }

    public void onMessageReceived(final ChatMessage message) {
        message.setUserId(connectedDeviceId);
        messageRepository.insertMessage(message);
        System.out.println("Receive: " + message.getJsonString());
    }

    public synchronized void waitForFragmentReady() throws InterruptedException {
        if(!this.chatFragmentReady)
        {
            this.wait();
        }
    }

    public MessageAdapter getMessageAdapter() {
        return messageAdapter;
    }

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }
}
