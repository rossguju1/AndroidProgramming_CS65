package edu.dartmouth.cs.myorganizer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PictureEntryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "exercises.db";
    private static final int DATABASE_VERSION = 201;

    //Name of TABLE
    // SQL: CREATE TABLE EXERCISES(....)
    public static final String TABLE_PICTURES = "PICTURES";

    // SQL Columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_LABEL= "label";


    private static final String CREATE_TABLE_ENTRIES = "create table "
            + TABLE_PICTURES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_IMAGE + " text not null, "
            + COLUMN_TEXT + " text not null, "
            + COLUMN_LABEL + " integer);";

    public PictureEntryDbHelper(Context context) {
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
        Log.w(PictureEntryDbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURES);
        onCreate(db);
    }

}
