package at.tugraz.ist.swe.cheatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendButton=(Button) findViewById(R.id.sendButton);



        EditText textEntry = findViewById(R.id.textEntry);
        String entryString = textEntry.getText().toString();
    }
}
