package edu.dartmouth.cs.myorganizer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
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
                    PictureEntryDbHelper.COLUMN_LABEL, //curor 3
                    PictureEntryDbHelper.COLUMN_DATE, //cursor 4
                    PictureEntryDbHelper.COLUMN_SYNC //cursor 5
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
        values.put(PictureEntryDbHelper.COLUMN_IMAGE, entry.getmImage());
        values.put(PictureEntryDbHelper.COLUMN_TEXT, entry.getmText());
        values.put(PictureEntryDbHelper.COLUMN_LABEL, entry.getmLabel());
        values.put(PictureEntryDbHelper.COLUMN_DATE, entry.getmDate());
        values.put(PictureEntryDbHelper.COLUMN_SYNC, entry.getmSynced());


        long insertId = database.insert(PictureEntryDbHelper.TABLE_PICTURES, null,
                values);
        entry.setId(insertId);

        Cursor cursor = database.query(PictureEntryDbHelper.TABLE_PICTURES, ALL_COLUMNS,
                PictureEntryDbHelper.COLUMN_ID + " = " + insertId, null,
                null,
                null,
                null);
        cursor.moveToFirst();
        cursor.close();
        return insertId;
    }


    public void removeEntry(long id) {
        database.delete(PictureEntryDbHelper.TABLE_PICTURES, PictureEntryDbHelper.COLUMN_ID
                + " = " + id, null);
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
        mMyPicture.setmText(cursor.getString(2));
        mMyPicture.setmLabel(cursor.getInt(3));
        mMyPicture.setmDate(cursor.getString(4));
        mMyPicture.setmSynced(cursor.getInt(5));
        return mMyPicture;
    }


    public void updatePicture(MyPicture entry) {

        long _id = entry.getId();

        ContentValues values = new ContentValues();

        values.put(PictureEntryDbHelper.COLUMN_IMAGE, entry.getmImage());
        values.put(PictureEntryDbHelper.COLUMN_TEXT, entry.getmText());
        values.put(PictureEntryDbHelper.COLUMN_LABEL, entry.getmLabel());
        values.put(PictureEntryDbHelper.COLUMN_DATE, entry.getmDate());
        values.put(PictureEntryDbHelper.COLUMN_SYNC, entry.getmSynced());
        database.update(PictureEntryDbHelper.TABLE_PICTURES, values, "_id=" + _id, null);
    }

    public int deletePicture(long id) {
        if (database.delete(PictureEntryDbHelper.TABLE_PICTURES, PictureEntryDbHelper.COLUMN_ID
                + " = " + id, null) > 0) {
            return 1;
        } else {
            return -1;
        }
    }


    public void printPicture(MyPicture ex) {

        Log.d("Picuture DB Entry", "ID: "
                + ex.getId()
                + "\n Image URI:  " + ex.getmImage()
                + "\n Text:     " + ex.getmText()
                + "\n Label   " + ex.getmLabel()
                + "\n  mDuration:  " + ex.getmDate()
                + "\n synced? : " + ex.getmSynced());
        return;
    }


}
