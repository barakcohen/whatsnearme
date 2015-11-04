package one.tribe.whatsnearme.network;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class NetworkManager {


    public NetworkChanges getChanges(List<Discoverable> newList, List<Discoverable> currentList) {
        List<Discoverable> toRemove = new ArrayList<>(currentList);
        toRemove.removeAll(newList);

        List<Discoverable> toAdd = new ArrayList<>(newList);
        toAdd.removeAll(currentList);

        return new NetworkChanges(toAdd, toRemove);
    }

}
