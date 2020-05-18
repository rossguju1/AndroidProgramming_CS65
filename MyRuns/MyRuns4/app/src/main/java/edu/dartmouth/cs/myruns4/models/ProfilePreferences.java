package edu.dartmouth.cs.myruns4.models;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ProfilePreferences {

    private SharedPreferences sharedPreferences;

    public ProfilePreferences(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    //Profile Name Getter/Setter
    public String getProfileName(){
        return sharedPreferences.getString("name", "nan");
    }

    public void setProfileName(String name){
        sharedPreferences.edit().putString("name", name).apply();
    }

    //Profile Email Getter/Setter
    public String getProfileEmail(){
        return sharedPreferences.getString("email", "nan");
    }

    public void setProfileEmail(String email){
        sharedPreferences.edit().putString("email", email).apply();
    }

    //Profile Password Getter/Setter
    public String getProfilePassword(){
        return sharedPreferences.getString("password", "nan");
    }

    public void setProfilePassword(String password){
        sharedPreferences.edit().putString("password", password).apply();
    }

    //Profile Gender Getter/Setter
    public int getProfileGender(){
        return sharedPreferences.getInt("gender", -1);
    }

    public void setProfileGender(int gender){
        sharedPreferences.edit().putInt("gender", gender).apply();
    }

    //Profile Phone Getter/Setter
    public String getProfilePhone(){
        return sharedPreferences.getString("phone", "nan");
    }

    public void setProfilePhone(String phone){
        sharedPreferences.edit().putString("phone", phone).apply();
    }

    //Profile Major Getter/Setter
    public String getProfileMajor(){
        return sharedPreferences.getString("major", "nan");
    }

    public void setProfileMajor(String major){
        sharedPreferences.edit().putString("major", major).apply();
    }

    //Profile Class Year Getter/Setter
    public String getProfileClassYear(){
        return sharedPreferences.getString("class_year", "nan");
    }

    public void setProfileClassYear(String year){
        sharedPreferences.edit().putString("class_year", year).apply();
    }


    //Check if profile is missing any data
    public boolean isProfileEmpty(){
        if(getProfileName().equals("nan")
                || getProfileEmail().equals("nan")
                || getProfilePassword().equals("nan")
                || (getProfileGender() == -1)
                || getProfilePhone().equals("nan")
                || getProfileMajor().equals("nan")
                || getProfileClassYear().equals("nan")){
            return true;
        }
        return false;
    }

    public void ProfilePictureCommit(){
        //sharedPreferences.edit().commit(); //applies immediately but we want it in the background
        sharedPreferences.edit().apply();
    }

    //Remove previous profile preferences
    public void clearProfilePreferences(){
        sharedPreferences.edit().remove("name").apply();
        sharedPreferences.edit().remove("email").apply();
        sharedPreferences.edit().remove("password").apply();
        sharedPreferences.edit().remove("phone").apply();
        sharedPreferences.edit().remove("gender").apply();
        sharedPreferences.edit().remove("major").apply();
        sharedPreferences.edit().remove("class_year").apply();
        sharedPreferences.edit().remove("picture").apply();
    }
}