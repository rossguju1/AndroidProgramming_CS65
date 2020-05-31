package edu.dartmouth.cs.myorganizer.database;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FuegoBaseEntry {
    public String email;
    public String id;
    public String imageUri;
    public String text;
    public String label;
    public String date;
    public String synced;

    public FuegoBaseEntry(String email, String id, String imageUri, String text, String label, String date, String synced){

        this.email = email;
        this.id = id;
        this.imageUri = imageUri;
        this.text = text;
        this.label = label;
        this.date = date;
        this.synced = synced;
    }

}

