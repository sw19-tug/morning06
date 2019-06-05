package at.tugraz.ist.swe.cheatapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private BluetoothProvider bluetoothProvider;
    private ConnectFragment connectFragment;
    private ChatFragment chatFragment;
    private BluetoothEventHandler bluetoothEventHandler;
    private boolean connectFragmentVisible;
    private Toolbar toolbar;
    private ImageButton connectDisconnectButton;
    private Toast currentToast;
    private long lastConnectedDeviceID;
    private int numberOfLogoClicks = 0;
    private static int RESULT_LOAD_IMAGE = 1;

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

            if (!bluetoothProvider.isBluetoothEnabled()) {
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

        String profile_picture_path = this.getApplicationContext().getFilesDir().toString() + "/" + getString(R.string.profile_picture_name);
        Bitmap bmp = BitmapFactory.decodeFile(profile_picture_path);
        if(bmp != null) {
            Drawable drawable = new BitmapDrawable(getResources(), bmp);
            toolbar.setNavigationIcon(drawable);
        } else {
            toolbar.setNavigationIcon(R.mipmap.cheat_app_logo);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GaleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(GaleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        SharedPreferences sharedPreferences =
                this.getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE);
        lastConnectedDeviceID = sharedPreferences.getLong("lastConDev", 0);
        String nickname = sharedPreferences.getString("nickname", "");
        getBluetoothProvider().setOwnNickname(nickname);

        showConnectFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri SelectedImage = data.getData();
            String[] FilePathColumn = {MediaStore.Images.Media.DATA };

            Cursor SelectedCursor = getContentResolver().query(SelectedImage, FilePathColumn, null, null, null);
            SelectedCursor.moveToFirst();

            int columnIndex = SelectedCursor.getColumnIndex(FilePathColumn[0]);
            String picturePath = SelectedCursor.getString(columnIndex);
            SelectedCursor.close();

            BitmapFactory.Options options;
            Bitmap bmp = null;
            try {
                bmp = BitmapFactory.decodeFile(picturePath);
            } catch (OutOfMemoryError e) {
                try {
                    options = new BitmapFactory.Options();
                    options.inSampleSize = 4;
                    bmp = BitmapFactory.decodeFile(picturePath, options);
                } catch(Exception excepetion) {
                    Log.e("MainActivity", excepetion.getMessage());
                }
            }

            try {
                System.out.println("Bitmap");
                System.out.println(bmp);
                File file = new File(this.getApplicationContext().getFilesDir(), getString(R.string.profile_picture_name));
                FileOutputStream outStream = new FileOutputStream(file);
                bmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);
                bmp = Utils.getRoundedCornerBitmap(bmp, 70);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();

                Drawable drawable = new BitmapDrawable(getResources(), bmp);
                toolbar.setNavigationIcon(drawable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
                connectDisconnectButton.setImageResource(R.drawable.connect_icon);
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
                connectDisconnectButton.setImageResource(R.drawable.disconnect_icon);
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
                userInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
                userInput.setSingleLine(true);
                userInput.setText(currentNickname);

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
                        String newNickname = userInput.getText().toString().replaceAll("\n", "");
                        preferencesEditor.putString("nickname", newNickname);
                        preferencesEditor.apply();
                        getBluetoothProvider().setOwnNickname(newNickname);
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

                numberOfLogoClicks = 0;

                final ImageView image = new ImageView(this);
                image.setImageResource(R.drawable.cheat_app_logo_big_round);

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.equals(image)) {
                            if (numberOfLogoClicks == Constants.NUMBER_UNTIL_EGG) {
                                image.setImageResource(R.drawable.cheat_app_logo_big_round_x);
                            }
                            numberOfLogoClicks++;
                        }
                    }
                });

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

                LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textViewParams.bottomMargin = 5;
                layout.addView(textViewAboutPage, textViewParams);
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
