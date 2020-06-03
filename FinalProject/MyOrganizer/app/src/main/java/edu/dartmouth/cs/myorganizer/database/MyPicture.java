package edu.dartmouth.cs.myorganizer.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class MyPicture {
    private long id;
    private int mLabel;
    private Uri mImage;
    private String mText;
    private String mDate;
    private int mSynced;
    public long getId(){

        return id;
    }
    public void setId(long id){

        this.id = id;
    }

    public int getmLabel(){

        return mLabel;
    }
    public void setmLabel(int Label){

        this.mLabel = Label;
    }

    public String getmImage(){
        String res =  mImage.toString();
        return res;
    }

    public void setmImage(Uri imageUri){
        this.mImage = imageUri;
    }

    public String getmText(){
        return mText;
    }
    public void setmText(String text){
        this.mText = text;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public int getmSynced() {
        return mSynced;
    }

    public void setmSynced(int mSynced) {
        this.mSynced = mSynced;
    }
}
