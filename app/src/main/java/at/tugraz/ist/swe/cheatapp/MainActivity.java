package at.tugraz.ist.swe.cheatapp;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BluetoothProvider bluetoothProvider;
    private ConnectFragment connectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothProvider = new DummyBluetoothProvider();
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        connectFragment = new ConnectFragment();
        transaction.replace(R.id.placeholder_frame, connectFragment);
        transaction.commit();
    }

    public void setBluetoothProvider(final BluetoothProvider bluetoothProvider) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.bluetoothProvider = bluetoothProvider;
                connectFragment.updateValues();
            }
        });
    }

    public BluetoothProvider getBluetoothProvider() {
        return this.bluetoothProvider;
    }
}
