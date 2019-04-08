package at.tugraz.ist.swe.cheatapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BluetoothProvider bluetoothProvider;
    private ConnectFragment connectFragment;
    private ChatFragment chatFragment;
    private BluetoothEventHandler mainActivityEventHandler;

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

        mainActivityEventHandler = new BluetoothEventHandler() {
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

            }

            @Override
            public void onError(String errorMsg) {

            }
        };

        bluetoothProvider.registerHandler(mainActivityEventHandler);

        connectFragment = new ConnectFragment();
        chatFragment = new ChatFragment();
        device = new DummyDevice("1", chatFragment);

        showConnectFragment();
    }

    public BluetoothProvider getBluetoothProvider() {
        return this.bluetoothProvider;
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

    public Device getDevice() {
        return this.device;
    }

    // TODO: Remove this method once ChatFragment uses BluetoothProvider instead of Device
    public void setDevice(Device device) {
        this.device = device;
    }

    public void showConnectFragment() {
        setFragment(connectFragment);
    }

    public void showChatFragment() throws InterruptedException {
        setFragment(chatFragment);
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
