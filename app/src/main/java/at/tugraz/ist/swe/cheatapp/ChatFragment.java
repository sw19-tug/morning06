package at.tugraz.ist.swe.cheatapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChatFragment extends Fragment {
    private MainActivity activity;
    private View view;
    private Button sendButton;
    private EditText textEntry;
    private TextView receivedMessageTextView;

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
        receivedMessageTextView = view.findViewById(R.id.receivedMessage);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendButtonClicked();
            }
        });
    }

    private void onSendButtonClicked() {
        String textToSend = textEntry.getText().toString();
        activity.getDevice().sendMessage(textToSend);
        textEntry.getText().clear();
    }

    public void onMessageReceived(String messageText) {
       if(receivedMessageTextView != null)
        receivedMessageTextView.setText(messageText);
    }
}
