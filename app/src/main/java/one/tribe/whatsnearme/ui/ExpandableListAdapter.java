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

    private List<List<String>> groups;

    private LayoutInflater inflater;

    private Map<Integer, String> groupMap;
    private Map<Integer, Integer> iconsMap;

    public ExpandableListAdapter() {
        groups = new ArrayList<>();
        groups.add(new ArrayList<String>());
        groups.add(new ArrayList<String>());
        groups.add(new ArrayList<String>());

        groupMap = new HashMap<>();
        groupMap.put(0, "Wi-fi Networks");
        groupMap.put(1, "Bluetooth Devices");
        groupMap.put(2, "Phones that run the app");

        iconsMap = new HashMap<>();
        iconsMap.put(0, R.drawable.ic_wifi_white_48dp);
        iconsMap.put(1, R.drawable.ic_bluetooth_white_48dp);
        iconsMap.put(2, R.drawable.ic_phonelink_ring_white_48dp);

    }

    public void updateWifiNetworks(List<String> wifiNetworks) {
        if(wifiNetworks.isEmpty()) {
            groups.set(0, Collections.singletonList("No wi-fi networks in range"));
        } else {
            groups.set(0, wifiNetworks);
        }
        notifyDataSetChanged();
    }

    public void updateBlueToothDevices(List<String> bluetoothDevices) {
        if(bluetoothDevices.isEmpty()) {
            groups.set(1, Collections.singletonList("No bluetooth devices in range"));
        } else {
            groups.set(1, bluetoothDevices);
        }
        notifyDataSetChanged();
    }

    public void updatePhoneWithApp(List<String> phoneWithApp) {
        if(phoneWithApp.isEmpty()) {
            groups.set(2, Collections.singletonList("No phones with app in range"));
        } else {
            groups.set(2, phoneWithApp);
        }
        notifyDataSetChanged();
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
        groupIcon.setImageResource(iconsMap.get(i));

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
