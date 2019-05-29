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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BluetoothProvider bluetoothProvider;
    private ConnectFragment connectFragment;
    private ChatFragment chatFragment;
    private BluetoothEventHandler bluetoothEventHandler;
    private boolean connectFragmentVisible;
    private Toolbar toolbar;
    private Button connectDisconnectButton;
    private Toast currentToast;
    private long lastConnectedDeviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentToast = Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT);

        try {
            // TODO: Make this nicer.
            if (Utils.isTesting()) {
                bluetoothProvider = new DummyBluetoothProvider();
            } else {
                bluetoothProvider = new RealBluetoothProvider();
            }

            if (!bluetoothProvider.isBluetoothEnabled())
            {
                Toast.makeText(this, R.string.bluetooth_disabled, Toast.LENGTH_LONG).show();
            }
        } catch (BluetoothException e) {
            showToast(e.getMessage());
            bluetoothProvider = new DummyBluetoothProvider();
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
                } else {
                    MainActivity.this.bluetoothProvider.disconnect();
                }
            }
        });

        bluetoothEventHandler = new BluetoothEventHandler() {
            @Override
            public void onMessageReceived(final ChatMessage message) {
                chatFragment.onMessageReceived(message);
            }

            @Override
            public void onConnected() {
                try {
                    showChatFragment();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(getString(R.string.connected));
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(getString(R.string.disconnected));
                    }
                });

            }

            @Override
            public void onError(final String errorMsg) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        showToast(errorMsg);
                    }
                });
            }
        };

        bluetoothProvider.registerHandler(bluetoothEventHandler);

        // Attaching the layout to the toolbar object
        toolbar = findViewById(R.id.toolbar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences =
                this.getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE);
        lastConnectedDeviceID = sharedPreferences.getLong("lastConDev", 0);

        showConnectFragment();
    }

    public BluetoothProvider getBluetoothProvider() {
        return this.bluetoothProvider;
    }

    public void showConnectFragment() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setFragment(connectFragment);
                connectFragmentVisible = true;
                connectDisconnectButton.setText(getString(R.string.connect));
                connectFragment.updateValues();
            }
        });
    }

    public void showChatFragment() throws InterruptedException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setFragment(chatFragment);
                connectFragmentVisible = false;
                connectDisconnectButton.setText(getString(R.string.disconnect));
            }
        });
        chatFragment.waitForFragmentReady();
    }

    public ChatFragment getChatFragment() {
        return chatFragment;
    }

    public ConnectFragment getConnectFragment() {
        return connectFragment;
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.placeholder_frame, fragment);
        transaction.commitNowAllowingStateLoss();
    }

    public ListView getListView() {
        return connectFragment.getListView();
    }


    public void showToast(String text) {
        currentToast.setText(text);
        currentToast.show();
    }

    public long getLastConnectedDeviceID() {
        return lastConnectedDeviceID;
    }

    public void clearLastConnectedDevice() {
        this.lastConnectedDeviceID = 0;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_set_nickname: {
                Log.d("MainActivity", "Selected Menu Item menu_set_nickname");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                SharedPreferences sharedPreferences =
                        this.getSharedPreferences("CheatAppSharedPreferences",
                                Context.MODE_PRIVATE);
                String currentNickname = sharedPreferences.getString("nickname", null);

                builder.setTitle(R.string.title_set_nickname_dialog);
                final EditText userInput = new EditText(this);

                if (currentNickname != null) {
                    userInput.setSelection(currentNickname.length());
                }
                builder.setIcon(android.R.drawable.ic_menu_edit);
                builder.setView(userInput);

                builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor preferencesEditor =
                                getSharedPreferences("CheatAppSharedPreferences",
                                        Context.MODE_PRIVATE).edit();
                        preferencesEditor.putString("nickname", userInput.getText().toString().replaceAll("\n", ""));
                        preferencesEditor.apply();
                        Log.d("MainActivity",
                                "Set Nickname Dialog: User clicked Save button");
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("MainActivity",
                                "Set Nickname Dialog: User clicked Cancel button");
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
            case R.id.menu_about_page: {
                Log.d("MainActivity", "Selected Menu Item menu_set_nickname");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                ImageView image = new ImageView(this);
                image.setImageResource(R.drawable.cheat_app_logo_big);

                LinearLayout layout = new LinearLayout(this);
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(parms);

                layout.setGravity(Gravity.CLIP_VERTICAL);
                layout.setPadding(2, 2, 2, 2);




                builder.setTitle(R.string.title_about_page);
                TextView textViewAboutPage = new TextView(this);
                textViewAboutPage.setText(R.string.text_about_page);
                textViewAboutPage.setPadding(40, 40, 40, 40);
                textViewAboutPage.setGravity(Gravity.CENTER);
                textViewAboutPage.setTextSize(20);

                LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tv1Params.bottomMargin = 5;
                layout.addView(textViewAboutPage,tv1Params);
                layout.addView(image, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));



                builder.setPositiveButton(R.string.close_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                //builder.setView(textViewAboutPage);
                builder.setView(layout);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void restartApp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recreate();
            }
        });
    }
}
