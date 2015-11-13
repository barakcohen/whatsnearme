package one.tribe.whatsnearme.network;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class NetworkManager {


    public NetworkChanges getChanges(Set<Discoverable> newList, Set<Discoverable> currentList) {
        Set<Discoverable> toRemove = new HashSet<>(currentList);
        toRemove.removeAll(newList);

        Set<Discoverable> toAdd = new HashSet<>(newList);
        toAdd.removeAll(currentList);

        return new NetworkChanges(toAdd, toRemove);
    }

}
