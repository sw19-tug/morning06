package at.tugraz.ist.swe.cheatapp;

import android.arch.lifecycle.Observer;
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
    private Button editButton;
    private EditText textEntry;
    private RecyclerView messageRecycler;
    private MessageAdapter messageAdapter;
    private MessageRepository messageRepository;
    private boolean chatFragmentReady = false;


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

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendButtonClicked();
            }
        });

        messageRepository = new MessageRepository(this.getContext());

        final List<Message> messageList = new ArrayList<>();
        messageRepository.getMessagesByUserId(1).observe(this, new Observer<List<Message>>() { // TODO: change user id to the id of the chat partner
            @Override
            public void onChanged(@Nullable List<Message> messages) {
                messageList.clear();
                for (Message msg : messages) {
                    // System.out.println("-----------------------");
                    // System.out.println(msg.getUserId());
                    // System.out.println(msg.getMessageText());
                    // System.out.println(msg.getMessageSent());
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
        String textToSend = textEntry.getText().toString();
        activity.getBluetoothProvider().sendMessage(textToSend);
        messageRepository.insertMessage(new Message(1, textToSend, true));
        textEntry.getText().clear();
    }

    public void onMessageReceived(String messageText) {
        messageRepository.insertMessage(new Message(1, messageText, false));
    }

    public void onMessageEdit(final Message message)
    {
        textEntry.setText(message.getMessageText());
        sendButton.setVisibility(view.INVISIBLE);
        editButton.setVisibility(view.VISIBLE);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setMessageText(textEntry.getText().toString());
                messageRepository.updateMessage(message);
                textEntry.getText().clear();
                sendButton.setVisibility(view.VISIBLE);
                editButton.setVisibility(view.INVISIBLE);
            }
        });
        // wait for user to enter edit text
        // change messagetext to edited text
        //messageRepository.updateMessage(message);
        //update view

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
}
