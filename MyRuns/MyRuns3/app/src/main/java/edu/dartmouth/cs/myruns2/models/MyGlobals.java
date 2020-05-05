package edu.dartmouth.cs.myruns2.models;



import java.util.*;

public class MyGlobals {

    public static int CURRENT_UNITS = 0;
    public static final String ACT = "ACTIVITY_TYPES";
    public static final String IN = "INPUT_TYPES";
    public static final String UNIT_TABLE = "UNITS";

    Hashtable<String, Integer> ACTIVITY_TYPES = new Hashtable<>();
    Hashtable<String, Integer> INPUT_TYPES = new Hashtable<>();
    Hashtable<String, Integer> UNITS = new Hashtable<>();

    Hashtable<Integer, String> _ACTIVITY_TYPES = new Hashtable<>();
    Hashtable<Integer, String> _INPUT_TYPES = new Hashtable<>();
    Hashtable<Integer, String> _UNITS = new Hashtable<>();

    public MyGlobals() {
        ACTIVITY_TYPES.put("Running",0);
        ACTIVITY_TYPES.put("Walking", 1);
        ACTIVITY_TYPES.put("Standing", 2);
        ACTIVITY_TYPES.put("Cycling", 3);
        ACTIVITY_TYPES.put("Hiking", 4);
        ACTIVITY_TYPES.put("Downhill Skiing", 5);
        ACTIVITY_TYPES.put("Cross-Country Skiing", 6);
        ACTIVITY_TYPES.put("Snowboarding", 7);
        ACTIVITY_TYPES.put("Skating", 8);
        ACTIVITY_TYPES.put("Swimming", 9);
        ACTIVITY_TYPES.put("Mountain Biking", 10);
        ACTIVITY_TYPES.put("Wheelchair", 11);
        ACTIVITY_TYPES.put("Elliptical", 12);
        ACTIVITY_TYPES.put("Other", 13);

        INPUT_TYPES.put("Manual", 0);
        INPUT_TYPES.put("GPS", 1);
        INPUT_TYPES.put("Automatic", 2);

        _ACTIVITY_TYPES.put(0, "Running");
        _ACTIVITY_TYPES.put(1, "Walking");
        _ACTIVITY_TYPES.put(2, "Standing");
        _ACTIVITY_TYPES.put(3, "Cycling");
        _ACTIVITY_TYPES.put(4, "Hiking");
        _ACTIVITY_TYPES.put(5, "Downhill Skiing");
        _ACTIVITY_TYPES.put(6, "Cross-Country Skiing");
        _ACTIVITY_TYPES.put(7, "Snowboarding");
        _ACTIVITY_TYPES.put(8, "Skating");
        _ACTIVITY_TYPES.put(9, "Swimming");
        _ACTIVITY_TYPES.put(10, "Mountain Biking");
        _ACTIVITY_TYPES.put(11, "Wheelchair");
        _ACTIVITY_TYPES.put(12, "Elliptical");
        _ACTIVITY_TYPES.put(13, "Other");

        _INPUT_TYPES.put(0, "Manual");
        _INPUT_TYPES.put(1, "GPS");
        _INPUT_TYPES.put(2, "Automatic");

        UNITS.put("kms", 0);
        UNITS.put("mi", 1);

        _UNITS.put(0, "kms");
        _UNITS.put(1, "mi");
    }

    public int getValue_int(String table, String key){

        if (table.equals(ACT)) {
            return ACTIVITY_TYPES.get(key);
        } else if(table.equals(IN)){
            return INPUT_TYPES.get(key);
        } else if(table.equals(UNIT_TABLE)){
            return UNITS.get(key);
        }
        return -1;
    }

    public String getValue_str(String table, int key){

        if (table.equals(ACT)) {
            return _ACTIVITY_TYPES.get(key);
        } else if(table.equals(IN)){
            return _INPUT_TYPES.get(key);
        } else if(table.equals(UNIT_TABLE)){
            return _UNITS.get(key);
        }
    return "NaN";
    }
}
