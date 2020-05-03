package edu.dartmouth.cs.myruns2.models;



import java.util.*;

public class MyGlobals {

    Hashtable<String, Integer> ACTIVITY_TYPES = new Hashtable<>();
    //  Map<String, Integer> INPUT_TYPES = new Hashtable<>();
    Hashtable<String, Integer> INPUT_TYPES = new Hashtable<>();
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

    }

    public int getValue(String table, String key){

        if (table.equals("ACTIVITY_TYPES")) {
            return ACTIVITY_TYPES.get(key);
        } else if(table.equals("INPUT_TYPES")){
            return INPUT_TYPES.get(key);
        }
        return -1;
    }



}
