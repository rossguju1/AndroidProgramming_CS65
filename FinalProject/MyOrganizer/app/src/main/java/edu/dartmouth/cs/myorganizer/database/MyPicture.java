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
        Log.d("MyPicture", "getmImage():" + res);
        return res;
    }

    public void setmImage(Uri imageUri){



//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] b = baos.toByteArray();
//        String encodedImageString = Base64.encodeToString(b, Base64.DEFAULT);
//
//        byte[] bytarray = Base64.decode(encodedImageString, Base64.DEFAULT);
//        Bitmap bmimage = BitmapFactory.decodeByteArray(bytarray, 0,
//                bytarray.length);

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
}
