package one.tribe.whatsnearme.notification;

import one.tribe.whatsnearme.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class NotificationManager {

    private List<NetworkEvent> networkEventList;

    public NotificationManager() {
        networkEventList = new ArrayList<>();
    }

    public void addNetworkEvents(List<NetworkEvent> networkEvents) {
        networkEventList.addAll(networkEvents);
    }


}
