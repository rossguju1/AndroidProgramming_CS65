package edu.dartmouth.cs.myruns4.models;

public class Exercise {

    private Long id;
    private int mInputType;        // Manual, GPS or automatic
    private int mActivityType;     // Running, cycling etc.
    private String mDateTime;
    private int mDuration;         // Exercise duration in seconds
    private double mDistance;      // Distance traveled. Either in meters or feet.
    private double mAvgPace;       // Average pace
    private double mAvgSpeed;      // Average speed
    private double mSpeed;      // Average speed
    private int mCalories;          // Calories burnt
    private double mClimb;         // Climb. Either in meters or feet.
    private int mHeartRate;        // Heart rate
    private String mComment;       // Comments
    private int mPrivacy;
//    private ArrayList<LatLng> mLocationList; // Location list
    private String mLocationList;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public int getmInputType() {
        return mInputType;
    }

    public void setmInputType(int mInputType) {
        this.mInputType = mInputType;
    }

    public int getmActivityType() {
        return mActivityType;
    }

    public void setmActivityType(int mActivityType) {
        this.mActivityType = mActivityType;
    }

    public String getmDateTime() {
        return mDateTime;
    }

    public void setmDateTime(String mDateTime) {
        this.mDateTime = mDateTime;
    }

    public int getmDuration() {
        return mDuration;
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public double getmDistance() {
        return mDistance;
    }

    public void setmDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    public double getmAvgPace() {
        return mAvgPace;
    }

    public void setmAvgPace(double mAvgPace) {
        this.mAvgPace = mAvgPace;
    }

    public double getmAvgSpeed() {
        return mAvgSpeed;
    }

    public void setmAvgSpeed(double mAvgSpeed) {
        this.mAvgSpeed = mAvgSpeed;
    }

    public double getmSpeed() {
        return mSpeed;
    }

    public void setmSpeed(double mAvgSpeed) {
        this.mSpeed = mSpeed;
    }

    public int getmCalories() {
        return mCalories;
    }

    public void setmCalories(int mCalorie) {
        this.mCalories = mCalorie;
    }

    public double getmClimb() {
        return mClimb;
    }

    public void setmClimb(double mClimb) {
        this.mClimb = mClimb;
    }

    public int getmHeartRate() {
        return mHeartRate;
    }

    public void setmHeartRate(int mHeartRate) {
        this.mHeartRate = mHeartRate;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public int getmPrivacy() {
        return mPrivacy;
    }

    public void setmPrivacy(int mPrivacy) {
        this.mPrivacy = mPrivacy;
    }

    public String getmLocationList() {
        return mLocationList;
    }

    public void setmLocationList(String mLocationList) {
        this.mLocationList = mLocationList;
    }

//    public ArrayList<LatLng> getmLocationList() {
//        return mLocationList;
//    }
//
//    public void setmLocationList(ArrayList<LatLng> mLocationList) {
//        this.mLocationList = mLocationList;
//    }
}
