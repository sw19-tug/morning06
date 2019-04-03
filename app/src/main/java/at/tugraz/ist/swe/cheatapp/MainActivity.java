package at.tugraz.ist.swe.cheatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private Button sendButton;
    private EditText textEntry;
    private Device device;
    private RecyclerView messageRecycler;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        device = new DummyDevice("1");
        textEntry = findViewById(R.id.textEntry);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendButtonClicked();
            }
        });

        List<Message> messageList = new ArrayList<>();
        messageRecycler = findViewById(R.id.rvChat);
        messageAdapter = new MessageAdapter(messageList);
        messageRecycler.setAdapter(messageAdapter);
        messageRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    private void onSendButtonClicked() {
        String textToSend = textEntry.getText().toString();
        device.sendMessage(textToSend);
        textEntry.getText().clear();
    }
}
