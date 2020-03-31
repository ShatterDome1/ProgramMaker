package com.mpascal.programmaker.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class UserDB implements Parcelable {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dateOfBirth;
    private String key;

    public UserDB(String firstName,
                  String lastName,
                  String email,
                  String password,
                  String dateOfBirth,
                  String key) {

        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.key = key;
    }

    public UserDB() {}

    protected UserDB(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        password = in.readString();
        dateOfBirth = in.readString();
        key = in.readString();
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
        dest.writeString(password);
        dest.writeString(dateOfBirth);
        dest.writeString(key);
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

    public String getPassword() {
        return password;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getKey() {
        return key;
    }

}
