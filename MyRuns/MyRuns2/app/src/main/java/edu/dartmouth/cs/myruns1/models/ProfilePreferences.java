package edu.dartmouth.cs.myruns1.models;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.content.SharedPreferences;

public class ProfilePreferences {

    private SharedPreferences sharedPreferences;

    public ProfilePreferences(Context context){

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }


    public String getProfileName(){

        return sharedPreferences.getString("name", "nan");
    }

    public void setProfileName(String name){

        sharedPreferences.edit().putString("name", name).apply();

    }


    public String getProfileEmail(){

        return sharedPreferences.getString("email", "nan");
    }

    public void setProfileEmail(String email){

        sharedPreferences.edit().putString("email", email).apply();

    }


    public String getProfilePassword(){

        return sharedPreferences.getString("password", "nan");
    }

    public void setProfilePassword(String password){

        sharedPreferences.edit().putString("password", password).apply();

    }


    public int getProfileGender(){

        return sharedPreferences.getInt("gender", -1);
    }

    public void setProfileGender(int gender){

        sharedPreferences.edit().putInt("gender", gender).apply();

    }


    public String getProfilePhone(){

        return sharedPreferences.getString("phone", "nan");
    }

    public void setProfilePhone(String phone){

        sharedPreferences.edit().putString("phone", phone).apply();

    }


    public String getProfileMajor(){

        return sharedPreferences.getString("major", "nan");
    }

    public void setProfileMajor(String major){

        sharedPreferences.edit().putString("major", major).apply();

    }


    public String getProfileClassYear(){

        return sharedPreferences.getString("class_year", "nan");
    }

    public void setProfileClassYear(String year){

        sharedPreferences.edit().putString("class_year", year).apply();

    }


    public String getProfilePicture(){

        return sharedPreferences.getString("picture", "nan");
    }

    public void setProfilePicture(String path){

        sharedPreferences.edit().putString("picture", path).apply();

    }


    public void ProfilePictureCommit(){

        sharedPreferences.edit().commit();

    }




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
