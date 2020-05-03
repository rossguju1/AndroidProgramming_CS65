package edu.dartmouth.cs.myruns2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.text.format.DateUtils;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Comment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
//import android.icu.util.Calendar;
import edu.dartmouth.cs.myruns2.ExerciseEntryDbHelper;
import edu.dartmouth.cs.myruns2.models.Exercise;
import android.text.format.DateUtils;

class ExerciseEntry {

        private static final String TAG = "DB";
        // Database fields
        private SQLiteDatabase database;
        private ExerciseEntryDbHelper dbHelper;
        private String[] ALL_COLUMNS =
                {
                        ExerciseEntryDbHelper.COLUMN_ID, // cursor index 0
                        ExerciseEntryDbHelper.COLUMN_INPUT, // cursor index 1
                        ExerciseEntryDbHelper.COLUMN_ACTIVITY, // cursor index 2
                        ExerciseEntryDbHelper.COLUMN_DATE, // cursor index 3
                        ExerciseEntryDbHelper.COLUMN_DURATION, // cursor index 4
                        ExerciseEntryDbHelper.COLUMN_DISTANCE, // cursor index 5
                        ExerciseEntryDbHelper.COLUMN_PACE, // cursor index 6
                        ExerciseEntryDbHelper.COLUMN_SPEED, // cursor index 7
                        ExerciseEntryDbHelper.COLUMN_CALORIES, // cursor index 8
                        ExerciseEntryDbHelper.COLUMN_CLIMB, // cursor index 9
                        ExerciseEntryDbHelper.COLUMN_HEARTRATE, // cursor index 10
                        ExerciseEntryDbHelper.COLUMN_COMMENT, // cursor index 11
                        ExerciseEntryDbHelper.COLUMN_PRIVACY, // cursor index 12
                        ExerciseEntryDbHelper.COLUMN_GPS // cursor index 13
                };

        ExerciseEntry(Context context) {
                dbHelper = new ExerciseEntryDbHelper(context);
        }

        public void open() throws SQLException {
                database = dbHelper.getWritableDatabase();
        }

        public void close() {

                dbHelper.close();
        }


        public long insertEntry(Exercise entry) {



                Log.w(ExerciseEntryDbHelper.class.getName(),
                        "INSERTED");


                ContentValues values = new ContentValues();
                // insert values
                //values.put( ExerciseEntryDbHelper.COLUMN_ID, entry.getId());
                values.put( ExerciseEntryDbHelper.COLUMN_INPUT, entry.getmInputType());
                values.put( ExerciseEntryDbHelper.COLUMN_ACTIVITY, entry.getmActivityType());
                values.put( ExerciseEntryDbHelper.COLUMN_DATE, entry.getmDateTime());
                values.put( ExerciseEntryDbHelper.COLUMN_DURATION, entry.getmDuration());
                values.put( ExerciseEntryDbHelper.COLUMN_DISTANCE, entry.getmDistance());
                values.put( ExerciseEntryDbHelper.COLUMN_PACE, entry.getmAvgPace());
                values.put( ExerciseEntryDbHelper.COLUMN_SPEED, entry.getmAvgSpeed());
                values.put( ExerciseEntryDbHelper.COLUMN_CALORIES, entry.getmCalories());
                values.put( ExerciseEntryDbHelper.COLUMN_CLIMB, entry.getmClimb());
                values.put( ExerciseEntryDbHelper.COLUMN_HEARTRATE, entry.getmHeartRate());
                values.put( ExerciseEntryDbHelper.COLUMN_COMMENT, entry.getmComment());
                values.put( ExerciseEntryDbHelper.COLUMN_PRIVACY, entry.getmPrivacy());
                values.put( ExerciseEntryDbHelper.COLUMN_GPS, entry.getmLocationList());

                long insertId = database.insert( ExerciseEntryDbHelper.TABLE_EXERCISES, null,
                        values);
                entry.setId(insertId);

                Cursor cursor = database.query(ExerciseEntryDbHelper.TABLE_EXERCISES,
                        ALL_COLUMNS,
                        ExerciseEntryDbHelper.COLUMN_ID + " = " + insertId, null,
                        null,
                        null,
                        null);
                cursor.moveToFirst();
                //Exercise newExercise = cursorToExercise(cursor);

                // Log the comment stored
                Log.d(TAG, "Exercise Inserted = " + " insert ID = " + insertId);

                cursor.close();
               // return newExercise;



                return insertId;
        }

        public void removeEntry(long id) {
               // long id = comment.getId();

                database.delete(ExerciseEntryDbHelper.TABLE_EXERCISES, ExerciseEntryDbHelper.COLUMN_ID
                        + " = " + id, null);
                Log.d(TAG, "delete comment = " + id);
        }

        public void deleteAllExercises() {
                System.out.println("Comment deleted all");

                database.delete(ExerciseEntryDbHelper.TABLE_EXERCISES, null, null);
                Log.d(TAG, "delete all ");
        }


        // Query a specific entry by its index.
        public Exercise fetchEntryByIndex(long rowId) {

                Cursor cursor = database.query(
                        ExerciseEntryDbHelper.TABLE_EXERCISES,
                        ALL_COLUMNS,
                        ExerciseEntryDbHelper.COLUMN_ID + " = " + rowId,
                        null,
                        null,
                        null,
                        null);
                cursor.moveToFirst();
                Exercise newExercise = cursorToExercise(cursor);

                return newExercise;

        }



        public ArrayList<Exercise> getAllExercises() {
                ArrayList<Exercise> mExercises = new ArrayList<>();

                Cursor cursor = database.query(ExerciseEntryDbHelper.TABLE_EXERCISES,
                        ALL_COLUMNS, null, null, null, null, null);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                        Exercise current_exercise = cursorToExercise(cursor);
                        Long exercise_id = current_exercise.getId();
                        Log.d(TAG, "got Exercise = " + exercise_id);
                        mExercises.add(current_exercise);
                        cursor.moveToNext();
                }
                // Make sure to close the cursor
                cursor.close();
                return mExercises;
        }

        private Exercise cursorToExercise(Cursor cursor) {
//
//                String s = cursor.getString(3);
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                Calendar cal = Calendar.getInstance();
//                try {
//                        cal.setTime(dateFormat.parse(s));
//                } catch (ParseException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                }
                Exercise mExercise = new Exercise();

                mExercise.setId(cursor.getLong(0));
                mExercise.setmInputType(cursor.getInt(1));
                mExercise.setmInputType(cursor.getInt(2));
                mExercise.setmDateTime(cursor.getString(3));
                mExercise.setmDuration(cursor.getInt(4));
                mExercise.setmDistance(cursor.getDouble(5));
                mExercise.setmAvgPace(cursor.getDouble(6));
                mExercise.setmAvgSpeed(cursor.getDouble(7));
                mExercise.setmCalories(cursor.getInt(8));
                mExercise.setmClimb(cursor.getDouble(9));
                mExercise.setmHeartRate(cursor.getInt(10));
                mExercise.setmComment(cursor.getString(11));
                mExercise.setmPrivacy(cursor.getInt(12));
                mExercise.setmLocationList(cursor.getString(13));

                return mExercise;
        }





}


