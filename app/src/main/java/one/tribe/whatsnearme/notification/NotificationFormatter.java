package one.tribe.whatsnearme.notification;

import one.tribe.whatsnearme.network.NetworkEvent;
import one.tribe.whatsnearme.network.NetworkEventType;
import one.tribe.whatsnearme.network.NetworkType;

/**
 *
 */
public class NotificationFormatter {

    /*
     * Message format is:
     * %type% [in range|out of range] at [datetime]: %name% (%MAC Address%)
     */
    private static final String COMPLETE_MESSAGE_FORMAT = "%s %s at %s: %s (%s)";

    /*
     * Message format is:
     * [New] %type% %name% [gone]
     */
    private static final String SHORT_MESSAGE_FORMAT = "%s %s %s";

    /*
     * Message format is:
     * %type%: N %eventType%
     *
     * Where %type% can be the types defined by NetworkType and %eventType% as
     * define by NetworkEventType
     */
    private static final String NOTIFICATION_SUMMARY_ONE_TYPE = "%s: %d %s";

    /*
     * Message format is:
     * %type%: N new, M gone
     *
     * Where %type% can be the types defined by NetworkType
     */
    private static final String NOTIFICATION_SUMMARY_TWO_TYPES = "%s: %d new, %d gone";



    public static String formatCompleteMessage(NetworkEvent networkEvent){
        return String.format(COMPLETE_MESSAGE_FORMAT,
                networkEvent.getType().getName(),
                networkEvent.getEventType().getDesc(),
                networkEvent.getTimestamp(),
                networkEvent.getNetworkName(),
                networkEvent.getNetworkAddress());
    }

    public static String formatShortMessage(NetworkEvent networkEvent){

        if(NetworkEventType.NEW.equals(networkEvent.getType())) {
            return String.format(SHORT_MESSAGE_FORMAT,
                    networkEvent.getEventType().getShortDesc(),
                    networkEvent.getType().getName(),
                    networkEvent.getNetworkName());
        }

        return String.format(SHORT_MESSAGE_FORMAT,
                networkEvent.getType().getName(),
                networkEvent.getNetworkName(),
                networkEvent.getEventType().getShortDesc());

    }

    public static String formatNotificationSummary(NetworkType networkType,
                                                   int newCount, int goneCount ) {
        if(goneCount == 0) {
            return String.format(NOTIFICATION_SUMMARY_ONE_TYPE,
                    networkType.getName(),
                    newCount,
                    NetworkEventType.NEW.getShortDesc());
        } else if(newCount == 0) {
            return String.format(NOTIFICATION_SUMMARY_ONE_TYPE,
                    networkType.getName(),
                    goneCount,
                    NetworkEventType.GONE.getShortDesc());
        } else {
            return String.format(NOTIFICATION_SUMMARY_TWO_TYPES,
                    networkType.getName(),
                    newCount, goneCount);
        }
    }


}
