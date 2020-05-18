package edu.dartmouth.cs.myruns4.models;

public class Constants {
    public static final int MSG_START = 0;
    public static final int MSG_PAUSE = 1;
    public static final int MSG_AUTO = 2;
    public static final int MSG_GPS = 3;
    public static final int MSG_START_FRAGMENT = 4;
    public static final int MSG_HISTORYFRAGMENT = 5;
    public static final int MSG_RESUME = 6;
    public static final int MSG_DELETE = 7;

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_SET_INT_VALUE = 3;
    public static final int MSG_SET_STRING_VALUE = 4;




    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";
    public static final String BROADCAST_DETECTED_LOCATION = "location_intent";
    // the desired time between activity detections. Larger values will result in fewer activity
    // detections while improving battery life. A value of 0 will result in activity detections
    // at the fastest possible rate.
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 1000; // every N seconds
    public static final String BROADCAST_DETECTED_LOCATION_LIST = "location_intent_list";
    public static final String BROADCAST_DETECTED_LOCATION_STRING = "location_intent_string";
    public static final String BROADCAST_DETECTED_ACTIVITY_LIST = "activity_list";
    public static final String BROADCAST_DETECTED_ACTIVITY_STRING = "activity_intent_string";

}
