package at.tugraz.ist.swe.cheatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText textEntry = findViewById(R.id.textEntry);
        String entryString = textEntry.getText().toString();
    }
}
