package at.tugraz.ist.swe.cheatapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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


    }

    public void updateValues() {
        final List<Device> deviceList = activity.getBluetoothProvider().getPairedDevices();
        List<String> deviceIDs = getDeviceIDStringList(deviceList);

        if (this.adapter == null) {
            adapter = new ArrayAdapter<>(view.getContext(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, deviceIDs);
            listView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(deviceIDs);
            adapter.notifyDataSetChanged();
        }

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
            Toast.makeText(view.getContext(), "No device selected.", Toast.LENGTH_LONG).show();
        } else {
            activity.getBluetoothProvider().connectToDevice(activity.getBluetoothProvider().getPairedDevices().get(selectedListIndex));
        }
    }

    public ListView getListView() {
        return listView;
    }
}
