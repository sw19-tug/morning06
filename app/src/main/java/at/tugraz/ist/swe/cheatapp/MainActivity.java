package at.tugraz.ist.swe.cheatapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BluetoothProvider bluetoothProvider;
    private ConnectFragment connectFragment;
    private ChatFragment chatFragment;
    private BluetoothEventHandler bluetoothEventHandler;
    private boolean connectFragmentVisible;
    private Toolbar toolbar;
    private Button connectDisconnectButton;

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
            public void onMessageReceived(final Message message) {
                chatFragment.onMessageReceived(message);
            }

            @Override
            public void onConnected() {
                try {
                    showChatFragment();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, getString(R.string.connected), Toast.LENGTH_LONG).show();
                        }
                    });

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
            public void onError(final String errorMsg) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_set_nickname:
                Log.d("MainActivity", "Selected Menu Item menu_set_nickname");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(R.string.set_nickname_dialog_title);
                final EditText userInput = new EditText(this);
                SharedPreferences sharedPreferences = this.getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE);
                String currentNickname = sharedPreferences.getString("nickname", null);

                userInput.setText(currentNickname);
                if (currentNickname != null) {
                    userInput.setSelection(currentNickname.length());
                }

                /*LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_label_editor, null);
                dialogBuilder.setView(dialogView);

                EditText editText = (EditText) dialogView.findViewById(R.id.label_field);
                editText.setText("test label");
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();*/

                builder.setView(userInput);

                builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor preferencesEditor = getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE).edit();
                        preferencesEditor.putString("nickname", userInput.getText().toString());
                        preferencesEditor.apply();
                        Log.d("MainActivity", "User clicked Save button");
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("MainActivity", "User clicked Cancel button");
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
