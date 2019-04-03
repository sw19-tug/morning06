package at.tugraz.ist.swe.cheatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button sendButton;
    private EditText textEntry;
    private Device device;
    private TextView receivedMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        device = new DummyDevice("1", this);

        receivedMessageTextView = findViewById(R.id.receivedMessage);

        textEntry = findViewById(R.id.textEntry);

        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendButtonClicked();
            }
        });
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    private void onSendButtonClicked() {
        String textToSend = textEntry.getText().toString();
        device.sendMessage(textToSend);
        textEntry.getText().clear();
    }

    public void onMessageReceived(String messageText) {
        receivedMessageTextView.setText(messageText);
    }
}
