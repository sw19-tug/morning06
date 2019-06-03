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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChatFragment extends Fragment {
    private MainActivity activity;
    private View view;
    private Button sendButton;
    private Button editButton;
    private Button abortEditButton;
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
        editButton = view.findViewById(R.id.btn_edit_send);
        abortEditButton = view.findViewById(R.id.btn_abort_edit);

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
                    messageList.add(msg);
                }
                messageAdapter.notifyDataSetChanged();
                if(messageAdapter.getItemCount() > 1)
                    messageRecycler.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });

        messageRecycler = view.findViewById(R.id.rvChat);
        messageAdapter = new MessageAdapter(messageList, this);
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
            final ChatMessage message = new ChatMessage(connectedDeviceId, sanitizedMessageText, true, false);
            activity.getBluetoothProvider().sendMessage(message);
            messageRepository.insertMessage(message);
            textEntry.getText().clear();
        }
    }

    public void onMessageReceived(final ChatMessage message) {
        message.setUserId(connectedDeviceId);
        if(message.getMessageEdited()){
            ChatMessage updatedMessage = messageRepository.getMessageByMessageUUID(message.getMessageUUID());
            updatedMessage.setMessageText(message.getMessageText());
            updatedMessage.setMessageEdited(true);

            messageRepository.updateMessage(updatedMessage);
            //textEditedIndicator.setText("edited");
            System.out.println("Update message: " + updatedMessage.getJsonString());
        }
        else{
            messageRepository.insertMessage(message);
        }
        System.out.println("Receive: " + message.getJsonString());
    }

    public void onMessageEdit(final ChatMessage message)
    {
        textEntry.setText(message.getMessageText());
        message.setMessageEdited(true);
        sendButton.setVisibility(view.INVISIBLE);
        editButton.setVisibility(view.VISIBLE);
        abortEditButton.setVisibility(view.VISIBLE);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setMessageText(textEntry.getText().toString());
                message.setMessageEdited(true);
                messageRepository.updateMessage(message);
                //textEditedIndicator.setText((CharSequence)"edited");

                activity.getBluetoothProvider().sendMessage(message);
                textEntry.getText().clear();
                sendButton.setVisibility(view.VISIBLE);
                editButton.setVisibility(view.INVISIBLE);
                abortEditButton.setVisibility(view.INVISIBLE);
            }
        });

        abortEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textEntry.getText().clear();
                sendButton.setVisibility(view.VISIBLE);
                editButton.setVisibility(view.INVISIBLE);
                abortEditButton.setVisibility(view.INVISIBLE);
            }
        });
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

    public EditText getTextEntry() {return textEntry;}

    public void clearTextEntry() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textEntry.setText("");
            }
        });
    }
}
