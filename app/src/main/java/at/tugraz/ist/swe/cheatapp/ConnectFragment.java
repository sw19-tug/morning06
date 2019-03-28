package at.tugraz.ist.swe.cheatapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ConnectFragment extends Fragment {

    private ListView listView;
    private Button connectButton;
    private ArrayAdapter<String> adapter;
    private BluetoothProvider bluetoothProvider;
    private int selectedListIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_connect, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = getView().findViewById(R.id.lv_con_devices);
        connectButton = getView().findViewById(R.id.bt_con_connect);

        // TODO change this to real bluetooth provider later
        bluetoothProvider = new DummyBluetoothProvider();

        this.updateValues();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedListIndex = position;
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedListIndex < 0) {
                    Toast.makeText(getActivity(), "No device selected.", Toast.LENGTH_LONG).show();
                } else {
                    bluetoothProvider.connectToDevice(bluetoothProvider.getPairedDevices().get(selectedListIndex));
                }
            }
        });
    }


    private void updateValues() {
        final List<Device> deviceList = bluetoothProvider.getPairedDevices();
        List<String> deviceIDs = getDeviceIDStringList(deviceList);


        if (this.adapter == null) {
            adapter = new ArrayAdapter<>(getActivity(),
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
            idList.add(device.getID());
        }

        return idList;
    }

    public void setBluetoothProvider(final BluetoothProvider bluetoothProvider) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConnectFragment.this.bluetoothProvider = bluetoothProvider;
                ConnectFragment.this.updateValues();
            }
        });

    }
}
