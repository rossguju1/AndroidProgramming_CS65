package edu.dartmouth.cs.myorganizer.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class MyPicture {
    private long id;
    private int mLabel;
    private byte[] mImage;
    private String mText;

    public long getId(){

        return id;
    }
    public void setId(Long id){

        this.id = id;
    }

    public int getmLabel(){

        return mLabel;
    }
    public void setmLabel(int Label){

        this.mLabel = Label;
    }

    public byte[] getmImage(){

        return mImage;
    }

    public void setmImage(Bitmap bmp){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedImageString = Base64.encodeToString(b, Base64.DEFAULT);

        byte[] bytarray = Base64.decode(encodedImageString, Base64.DEFAULT);
        Bitmap bmimage = BitmapFactory.decodeByteArray(bytarray, 0,
                bytarray.length);

        this.mImage = bytarray;
    }

    public String getmText(){

        return mText;
    }
    public void setmText(String text){


        this.mText = text;
    }

}
