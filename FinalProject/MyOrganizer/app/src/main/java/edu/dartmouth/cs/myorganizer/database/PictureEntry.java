package edu.dartmouth.cs.myorganizer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

public class PictureEntry {
    private static final String TAG = "DB";
    // Database fields
    private SQLiteDatabase database;
    private PictureEntryDbHelper dbHelper;
    private String[] ALL_COLUMNS =
            {
                    PictureEntryDbHelper.COLUMN_ID, // cursor index 0
                    PictureEntryDbHelper.COLUMN_IMAGE, // cursor index 1
                    PictureEntryDbHelper.COLUMN_TEXT, // cursor index 2
                    PictureEntryDbHelper.COLUMN_LABEL
            };

    public PictureEntry(Context context) {
        dbHelper = new PictureEntryDbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }

    public long insertEntry(MyPicture entry) {
        Log.w(PictureEntryDbHelper.class.getName(),
                "INSERTED");
        ContentValues values = new ContentValues();
        // insert values
        Log.d("Inside Insert image Uri: ", entry.getmImage());
        //values.put( ExerciseEntryDbHelper.COLUMN_ID, entry.getId());
        values.put( PictureEntryDbHelper.COLUMN_IMAGE, entry.getmImage());
        values.put( PictureEntryDbHelper.COLUMN_TEXT, entry.getmText());
        values.put( PictureEntryDbHelper.COLUMN_LABEL, entry.getmLabel());


        long insertId = database.insert( PictureEntryDbHelper.TABLE_PICTURES, null,
                values);
        entry.setId(insertId);

        Cursor cursor = database.query(PictureEntryDbHelper.TABLE_PICTURES, ALL_COLUMNS,
                PictureEntryDbHelper.COLUMN_ID + " = " + insertId, null,
                null,
                null,
                null);
        cursor.moveToFirst();
        // Log the comment stored
        Log.d(TAG, "Picture Inserted = " + " insert ID = " + insertId);

        cursor.close();
        return insertId;
    }


    public void removeEntry(long id) {
        // long id = comment.getId();

        database.delete(PictureEntryDbHelper.TABLE_PICTURES, PictureEntryDbHelper.COLUMN_ID
                + " = " + id, null);
        Log.d(TAG, "delete comment = " + id);
    }

    public void deleteAllExercises() {
        System.out.println("Comment deleted all");

        database.delete(PictureEntryDbHelper.TABLE_PICTURES, null, null);
        Log.d(TAG, "delete all ");
    }



    // Query a specific entry by its index.
    public MyPicture fetchEntryByIndex(long rowId) {

        Cursor cursor = database.query(
                PictureEntryDbHelper.TABLE_PICTURES,
                ALL_COLUMNS,
                PictureEntryDbHelper.COLUMN_ID + " = " + rowId,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        MyPicture newMyPicture = cursorToPicture(cursor);

        return newMyPicture;

    }


    public ArrayList<MyPicture> getAllPictures() {
        ArrayList<MyPicture> mExercises = new ArrayList<>();

        Cursor cursor = database.query(PictureEntryDbHelper.TABLE_PICTURES,
                ALL_COLUMNS, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MyPicture current_exercise = cursorToPicture(cursor);
            long exercise_id = current_exercise.getId();
            Log.d(TAG, "got Exercise = " + exercise_id);
            mExercises.add(current_exercise);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return mExercises;
    }



    private MyPicture cursorToPicture(Cursor cursor) {

        MyPicture mMyPicture = new MyPicture();

        mMyPicture.setId(cursor.getLong(0));
        mMyPicture.setmImage(Uri.parse(cursor.getString(1)));
//        byte[] byteArray = cursor.getBlob(1);
//
//        Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
//        mMyPicture.setmImage(bm);
        mMyPicture.setmText(cursor.getString(2));
        mMyPicture.setmLabel(cursor.getInt(3));


        return mMyPicture;
    }


    public void updatePicture(MyPicture entry){

        long _id = entry.getId();

        ContentValues values = new ContentValues();

        values.put(PictureEntryDbHelper.COLUMN_IMAGE, entry.getmImage());
        values.put( PictureEntryDbHelper.COLUMN_TEXT, entry.getmText());
        values.put( PictureEntryDbHelper.COLUMN_LABEL, entry.getmLabel());


        database.update(PictureEntryDbHelper.TABLE_PICTURES, values, "_id="+ _id, null);
    }

//    public void printExercise(Picture ex){
//
//        Log.d("EXERCISE", "ID: " + ex.getId()
//                + "  Input: " + ex.getmInputType()
//                + " Activity:    " + ex.getmActivityType()
//                + " Date:   " + ex.getmDateTime()
//                + "  mDuration:  " + ex.getmDuration()
//                + "  mDistance  " + ex.getmDistance()
//                + "  mAvgPace:  " + ex.getmAvgPace()
//                + "  mAvgSpeed:  " + ex.getmAvgSpeed()
//                + "  mCalorie:   " + ex.getmCalories()
//                + "  mClimb   " + ex.getmClimb()
//                + "  mHeartRate  " + ex.getmHeartRate()
//                + "  mComment  " + ex.getmComment()
//                + "  mPrivacy   " + ex.getmPrivacy()
//                + "  mLocationList:  " + ex.getmLocationList()
//                + "  mSpeed:  " + ex.getmSpeed()
//        );
//    }

    public int deletePicture(long id){
        if (database.delete(PictureEntryDbHelper.TABLE_PICTURES,PictureEntryDbHelper.COLUMN_ID
                + " = " + id, null) > 0){
            return 1;
        } else{
            return -1;
        }
    }







}
