package one.tribe.whatsnearme.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import one.tribe.whatsnearme.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private static final int WIFI = 0;
    private static final int BLUETOOTH = 1;
    private static final int PHONE = 2;

    private List<List<String>> groups;

    private LayoutInflater inflater;

    private Map<Integer, String> groupMap;
    private Map<Integer, Integer> onIconsMap;
    private Map<Integer, Integer> offIconsMap;
    private Map<Integer, Boolean> stateMap;

    public ExpandableListAdapter() {
        groups = new ArrayList<>();
        groups.add(new ArrayList<String>());
        groups.add(new ArrayList<String>());
        groups.add(new ArrayList<String>());

        groupMap = new HashMap<>();
        groupMap.put(WIFI, "Wi-fi Networks");
        groupMap.put(BLUETOOTH, "Bluetooth Devices");
        groupMap.put(PHONE, "Phones that run the app");

        onIconsMap = new HashMap<>();
        onIconsMap.put(WIFI, R.drawable.ic_wifi_white_48dp);
        onIconsMap.put(BLUETOOTH, R.drawable.ic_bluetooth_white_48dp);
        onIconsMap.put(PHONE, R.drawable.ic_phonelink_ring_white_48dp);

        offIconsMap = new HashMap<>();
        offIconsMap.put(WIFI, R.drawable.ic_wifi_white_48dp);
        offIconsMap.put(BLUETOOTH, R.drawable.ic_bluetooth_disabled_white_48dp);
        offIconsMap.put(PHONE, R.drawable.ic_phonelink_erase_white_48dp);

        stateMap = new HashMap<>();
        stateMap.put(WIFI, Boolean.TRUE);
        stateMap.put(BLUETOOTH, Boolean.TRUE);
        stateMap.put(PHONE, Boolean.TRUE);

    }

    public void updateWifiNetworks(List<String> wifiNetworks) {
        if(wifiNetworks.isEmpty()) {
            groups.set(WIFI, Collections.singletonList("No wi-fi networks in range"));
        } else {
            groups.set(WIFI, wifiNetworks);
        }
        notifyDataSetChanged();
    }

    public void updateBlueToothDevices(List<String> bluetoothDevices) {
        if(bluetoothDevices.isEmpty()) {
            groups.set(BLUETOOTH, Collections.singletonList("No bluetooth devices in range"));
        } else {
            groups.set(BLUETOOTH, bluetoothDevices);
        }
        notifyDataSetChanged();
    }

    public void updatePhoneWithApp(List<String> phoneWithApp) {
        if(phoneWithApp.isEmpty()) {
            groups.set(PHONE, Collections.singletonList("No phones with app in range"));
        } else {
            groups.set(PHONE, phoneWithApp);
        }
        notifyDataSetChanged();
    }

    public void enableBluetoothIcon(boolean enable) {
        stateMap.put(BLUETOOTH, enable);
        stateMap.put(PHONE, enable);

        if(!enable) {
            groups.set(BLUETOOTH, Collections.singletonList("Bluetooth is off!"));
            groups.set(PHONE, Collections.singletonList("Bluetooth is off!"));
        } else if(groups.isEmpty()) {
            updateBlueToothDevices(Collections.EMPTY_LIST);
            updatePhoneWithApp(Collections.EMPTY_LIST);
        }
    }

    public void setLayoutInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public int getGroupCount() {
        return 3;
    }

    @Override
    public int getChildrenCount(int groupIndex) {
        return groups.get(groupIndex).size();
    }

    @Override
    public Object getGroup(int groupIndex) {
        return groups.get(groupIndex);
    }

    @Override
    public Object getChild(int groupIndex, int itemIndex) {
        return groups.get(groupIndex).get(itemIndex);
    }

    @Override
    public long getGroupId(int groupIndex) {
        return groupIndex;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_group, null);
        }

        TextView groupHeader = (TextView) view.findViewById(R.id.groupHeaderTxt);
        groupHeader.setText(groupMap.get(i));

        ImageView groupIcon = (ImageView) view.findViewById(R.id.groupIcon);

        if(stateMap.get(i)) {
            groupIcon.setImageResource(onIconsMap.get(i));
        } else {
            groupIcon.setImageResource(offIconsMap.get(i));
        }
        return view;
    }

    @Override
    public View getChildView(int groupIndex, int itemIndex, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_item, null);
        }

        if(groups.get(groupIndex).size() > itemIndex) {
            TextView groupListItem = (TextView) view.findViewById(R.id.groupListItem);
            groupListItem.setText(groups.get(groupIndex).get(itemIndex));
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
