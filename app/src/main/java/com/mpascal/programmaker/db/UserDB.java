package com.mpascal.programmaker.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class UserDB implements Parcelable {

    private String firstName;
    private String lastName;
    private String email;
    private String dateOfBirth;

    public UserDB(String firstName,
                  String lastName,
                  String email,
                  String dateOfBirth) {

        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public UserDB() {}

    protected UserDB(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        dateOfBirth = in.readString();
    }

    public static final Creator<UserDB> CREATOR = new Creator<UserDB>() {
        @Override
        public UserDB createFromParcel(Parcel in) {
            return new UserDB(in);
        }

        @Override
        public UserDB[] newArray(int size) {
            return new UserDB[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(dateOfBirth);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Exclude
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {this.email = email;}

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
