package at.tugraz.ist.swe.cheatapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BluetoothProvider bluetoothProvider;
    private ConnectFragment connectFragment;
    private ChatFragment chatFragment;
    private BluetoothEventHandler bluetoothEventHandler;
    private boolean connectFragmentVisible;
    private Toolbar toolbar;
    private Button connectDisconnectButton;

    // TODO: Refactor
    private Device device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            bluetoothProvider = new RealBluetoothProvider();
        } catch (BluetoothException e) {
            // TODO Refactor
            bluetoothProvider = new DummyBluetoothProvider();
            ((DummyBluetoothProvider) bluetoothProvider).enableDummyDevices(1);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        setContentView(R.layout.activity_main);

        connectFragment = new ConnectFragment();
        chatFragment = new ChatFragment();
        device = new DummyDevice("1", chatFragment);
        connectDisconnectButton = findViewById(R.id.btn_connect_disconnect);

        connectDisconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectFragmentVisible) {
                    connectFragment.onConnectClicked();
                }
                else {
                    MainActivity.this.bluetoothProvider.disconnect();
                }
            }
        });

        bluetoothEventHandler = new BluetoothEventHandler() {
            @Override
            public void onMessageReceived(String message) {
                chatFragment.onMessageReceived(message);
            }

            @Override
            public void onConnected() {
                try {
                    showChatFragment();
                } catch (InterruptedException e) {
                    onError(e.getMessage());
                    onDisconnected();
                }
            }

            @Override
            public void onDisconnected() {
                MainActivity.this.showConnectFragment();
            }

            @Override
            public void onError(String errorMsg) {

            }
        };

        bluetoothProvider.registerHandler(bluetoothEventHandler);

        // Attaching the layout to the toolbar object
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        showConnectFragment();
    }

    public BluetoothProvider getBluetoothProvider() {
        return this.bluetoothProvider;
    }

    public void setBluetoothProvider(final BluetoothProvider bluetoothProvider) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.bluetoothProvider.unregisterHandler(bluetoothEventHandler);
                MainActivity.this.bluetoothProvider = bluetoothProvider;
                MainActivity.this.bluetoothProvider.registerHandler(bluetoothEventHandler);
                connectFragment.updateValues();
            }
        });
    }

    public Device getDevice() {
        return this.device;
    }

    // TODO: Remove this method once ChatFragment uses BluetoothProvider instead of Device
    public void setDevice(Device device) {
        this.device = device;
    }

    public void showConnectFragment() {
        setFragment(connectFragment);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectFragmentVisible = true;
                connectDisconnectButton.setText(getString(R.string.connect));
            }
        });
    }

    public void showChatFragment() throws InterruptedException {
        setFragment(chatFragment);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectFragmentVisible = false;
                connectDisconnectButton.setText(getString(R.string.disconnect));
            }
        });
        chatFragment.waitForFragmentReady();
    }

    public ChatFragment getChatFragment() {
        return chatFragment;
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.placeholder_frame, fragment);
        transaction.commitAllowingStateLoss();
    }
}
