package at.tugraz.ist.swe.cheatapp;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends ArrayAdapter<String> {
    private List<String> deviceList;

    public DeviceAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        deviceList = new ArrayList<>(objects);
    }

    public List<String> getItemList()
    {
        return deviceList;
    }
}
