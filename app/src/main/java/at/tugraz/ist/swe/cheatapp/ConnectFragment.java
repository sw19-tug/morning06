package at.tugraz.ist.swe.cheatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ConnectFragment extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;
    private MainActivity activity;
    private View view;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private int selectedListIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_connect, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();

        listView = view.findViewById(R.id.lv_con_devices);

        this.updateValues();

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setSelector(R.color.colorHighlight);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedListIndex = position;
            }
        });

        swipeRefreshLayout = view.findViewById(R.id.swp_pull_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateValues();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        long lastConnectedDeviceID = activity.getLastConnectedDeviceID();
        SharedPreferences.Editor preferencesEditor =
                activity.getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE).edit();
        preferencesEditor.remove("lastConDev");
        preferencesEditor.apply();

        if(lastConnectedDeviceID != 0)
        {
            tryReconnectByDeviceId(lastConnectedDeviceID);
        }
    }

    private void showSnackbar(String message)
    {
        Snackbar snackbar = Snackbar
            .make(this.view, message, Snackbar.LENGTH_LONG);
        TextView mainTextView = (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);

        mainTextView.setTextColor(Color.WHITE);

        View view = snackbar.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        view.setBackgroundColor(Color.GRAY);
        snackbar.show();
    }

    public void tryReconnectByDeviceId(long deviceId)
    {
        showSnackbar("Try to reconnect...");

        Device connectDevice = activity.getBluetoothProvider().getDeviceByID(deviceId);
        if(connectDevice != null)
        {
            activity.clearLastConnectedDevice();
            connectToDevice(connectDevice);
        }
    }

    public void updateValues() {
        if (activity == null) {
            return;
        }

        final List<Device> deviceList = activity.getBluetoothProvider().getPairedDevices();
        List<String> deviceIDs = getDeviceIDStringList(deviceList);

        adapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, deviceIDs);
        listView.setAdapter(adapter);
    }

    private List<String> getDeviceIDStringList(List<Device> deviceList) {
        List<String> idList = new ArrayList<>();
        for (Device device : deviceList) {
            idList.add(device.getDeviceName());
        }

        return idList;
    }

    public void onConnectClicked() {
        if (selectedListIndex < 0) {
            activity.showToast(activity.getString(R.string.no_device_selected));
        } else {
            Device connectDevice = activity.getBluetoothProvider().getPairedDevices().get(selectedListIndex);
            selectedListIndex = -1;
            connectToDevice(connectDevice);
        }
    }

    public ListView getListView() {
        return listView;
    }

    private void connectToDevice(Device device)
    {
        activity.getBluetoothProvider().connectToDevice(device);
    }
}
