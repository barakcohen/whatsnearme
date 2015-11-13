package one.tribe.whatsnearme.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Abstraction of a network change between two scanning/discovery operations.
 */
public class NetworkChanges {

    private ArrayList<NetworkEvent> networkEvents;
    private Set<Discoverable> newItems;
    private Set<Discoverable> goneItems;

    public NetworkChanges(Set<Discoverable> newItems, Set<Discoverable> goneItems) {
        networkEvents = new ArrayList<>();

        addEvents(newItems, NetworkEventType.NEW);
        addEvents(goneItems, NetworkEventType.GONE);
        this.goneItems = goneItems;
        this.newItems = newItems;
    }

    private void addEvents(Set<Discoverable> list, NetworkEventType eventType) {
        for (Discoverable discoverable : list) {
            networkEvents.add(new NetworkEvent(discoverable, eventType));
        }
    }

    /**
     * Returns true if there is any changes in the discoverable devices/networks
     */
    public boolean hasChanges() {
        return !networkEvents.isEmpty();
    }


    /**
     * Returns the events (new or gone discoverable networks/devices)
     */
    public ArrayList<NetworkEvent> getNetworkEvents() {
      return networkEvents;
    }

    public Set<Discoverable> getNewItems() {
        return this.newItems;
    }

    public Set<Discoverable> getGoneItems() {
        return this.goneItems;
    }

}
