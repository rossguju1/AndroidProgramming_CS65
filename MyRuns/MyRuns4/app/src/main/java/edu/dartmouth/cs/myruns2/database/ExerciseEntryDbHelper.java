package edu.dartmouth.cs.myruns2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExerciseEntryDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "exercises.db";
    private static final int DATABASE_VERSION = 201;

    //Name of TABLE
    // SQL: CREATE TABLE EXERCISES(....)
    public static final String TABLE_EXERCISES = "EXERCISES";

    // SQL Columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_INPUT = "input_type";
    public static final String COLUMN_ACTIVITY = "activity_type";
    public static final String COLUMN_DATE = "date_time";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_PACE = "avg_pace";
    public static final String COLUMN_AVGSPEED = "avg_speed";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_CLIMB = "climb";
    public static final String COLUMN_HEARTRATE = "heartrate";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_PRIVACY = "privacy";
    public static final String COLUMN_GPS = "gps_data";
    public static final String COLUMN_SPEED = "speed";

    private static final String CREATE_TABLE_ENTRIES = "create table "
            + TABLE_EXERCISES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_INPUT + " integer not null, "
            + COLUMN_ACTIVITY + " integer not null, "
            + COLUMN_DATE + " text not null, "
            + COLUMN_DURATION + " integer not null, "
            + COLUMN_DISTANCE + " float, "
            + COLUMN_PACE + " float, "
            + COLUMN_AVGSPEED + " float, "
            + COLUMN_CALORIES + " integer, "
            + COLUMN_CLIMB + " float, "
            + COLUMN_HEARTRATE + " integer, "
            + COLUMN_COMMENT + " text, "
            + COLUMN_PRIVACY + " integer, "
            + COLUMN_GPS + " text, "
            + COLUMN_SPEED + " text);";
    // Constructor
    public ExerciseEntryDbHelper(Context context) {
        // DATABASE_NAME is, of course the name of the database, which is defined as a string constant
        // DATABASE_VERSION is the version of database, which is defined as an integer constant
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table schema if not exists
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ExerciseEntryDbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        onCreate(db);
    }
}
