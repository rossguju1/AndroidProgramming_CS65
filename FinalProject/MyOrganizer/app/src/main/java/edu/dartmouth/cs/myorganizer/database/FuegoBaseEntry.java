package edu.dartmouth.cs.myorganizer.database;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FuegoBaseEntry {
    private String email;
    private String id;
    private String imageUri;
    private String imageBase64;
    private String text;
    private String label;
    private String date;
    private String synced;


    public FuegoBaseEntry(String email, String id, String imageUri, String imageBase64, String text, String label, String date, String synced) {

        this.email = email;
        this.id = id;
        this.imageUri = imageUri;
        this.imageBase64 = imageBase64;
        this.text = text;
        this.label = label;
        this.date = date;
        this.synced = synced;
    }

    public FuegoBaseEntry() {

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSynced() {
        return synced;
    }

    public void setSynced(String synced) {
        this.synced = synced;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
