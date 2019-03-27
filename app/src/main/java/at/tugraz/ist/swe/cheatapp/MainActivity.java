package at.tugraz.ist.swe.cheatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Button sendButton;
    private EditText textEntry;
    private DummyDevice dummy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dummy = new DummyDevice("1");

        textEntry = findViewById(R.id.textEntry);

        sendButton=(Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendButtonclicked();
            }
        });


    }

    private void onSendButtonclicked()
    {
        String textToSend = textEntry.getText().toString();
        dummy.setMessage(textToSend);
        textEntry.getText().clear();
    }
}
