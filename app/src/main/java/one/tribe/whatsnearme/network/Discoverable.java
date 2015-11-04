package one.tribe.whatsnearme.network;

import android.os.Parcelable;

/**
 *
 */
public interface Discoverable extends Parcelable {

    String getName();
    String getAddress();
    NetworkType getNetworkType();
}
