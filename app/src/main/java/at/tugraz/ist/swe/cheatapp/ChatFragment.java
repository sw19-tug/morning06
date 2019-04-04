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

import java.io.File;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_chat, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();

        textEntry = view.findViewById(R.id.textEntry);
        sendButton = view.findViewById(R.id.sendButton);

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
                for(Message msg : messages) {
                    System.out.println("-----------------------");
                    System.out.println(msg.getUserId());
                    System.out.println(msg.getMessageText());
                    System.out.println(msg.getMessageSent());
                    messageList.add(msg);
                }
            }
        });

        messageRecycler = view.findViewById(R.id.rvChat);
        messageAdapter = new MessageAdapter(messageList);
        messageRecycler.setAdapter(messageAdapter);
        messageRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void onSendButtonClicked() {
        String textToSend = textEntry.getText().toString();
        activity.getDevice().sendMessage(textToSend);
        textEntry.getText().clear();
    }

}
