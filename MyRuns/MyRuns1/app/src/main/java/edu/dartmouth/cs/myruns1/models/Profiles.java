package edu.dartmouth.cs.myruns1.models;

public class Profiles {
    private String mName;
    private int mGender;
    private String mEmail;
    private String mPassword;
    private String mPhone;
    private String mMajor;
    private int mClassYear;

    public Profiles(String mName, int mGender, String mEmail, String mPassword, String mPhone, String mMajor, int mClassYear) {
        this.mName = mName;
        this.mGender = mGender;
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mPhone = mPhone;
        this.mMajor = mMajor;
        this.mClassYear = mClassYear;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmGender() {
        return mGender;
    }

    public void setmGender(int mGender) {
        this.mGender = mGender;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmMajor() {
        return mMajor;
    }

    public void setmMajor(String mMajor) {
        this.mMajor = mMajor;
    }

    public int getmClassYear() {
        return mClassYear;
    }

    public void setmClassYear(int mClassYear) {
        this.mClassYear = mClassYear;
    }

}
