package com.mpascal.programmaker.db;

import android.os.Parcel;
import android.os.Parcelable;

public class ExerciseDB implements Parcelable {

    // Possible values:
    // Main
    // Secondary
    // Accessory
    private String category;

    // Unique identifier
    private String name;

    // Recommended repetition ranges, possible values:
    // 1-5 = Strength
    // 6-12 = Hypertrophy
    // 12+ = Endurance
    private String repRange;

    // Is suitable for elderly
    private boolean isSuitableForElders;

    // Is compound exercise
    private boolean isPersistent;

    // Target body muscles
    private String primaryMover;

    public ExerciseDB(String category,
                      String name,
                      String repRange,
                      boolean isSuitableForElders,
                      boolean isPersistent,
                      String primaryMover) {
        this.category = category;
        this.name = name;
        this.repRange = repRange;
        this.isSuitableForElders = isSuitableForElders;
        this.isPersistent = isPersistent;
        this.primaryMover = primaryMover;
    }

    protected ExerciseDB(Parcel in) {
        category = in.readString();
        name = in.readString();
        repRange = in.readString();
        isSuitableForElders = in.readByte() != 0;
        isPersistent = in.readByte() != 0;
        primaryMover = in.readString();
    }

    public static final Creator<ExerciseDB> CREATOR = new Creator<ExerciseDB>() {
        @Override
        public ExerciseDB createFromParcel(Parcel in) {
            return new ExerciseDB(in);
        }

        @Override
        public ExerciseDB[] newArray(int size) {
            return new ExerciseDB[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(category);
        dest.writeString(name);
        dest.writeString(repRange);
        dest.writeByte((byte) (isSuitableForElders ? 1 : 0));
        dest.writeByte((byte) (isPersistent ? 1 : 0));
        dest.writeString(primaryMover);
    }

    public ExerciseDB() {}

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepRange() {
        return repRange;
    }

    public void setRepRange(String repRange) {
        this.repRange = repRange;
    }

    public boolean getIsSuitableForElders() {
        return isSuitableForElders;
    }

    public void setIsSuitableForElders(boolean suitableForElders) {
        isSuitableForElders = suitableForElders;
    }

    public boolean getIsPersistent() {
        return isPersistent;
    }

    public void getIsPersistent(boolean compoundExercise) {
        isPersistent = compoundExercise;
    }

    public String getPrimaryMover() {
        return primaryMover;
    }

    public void setPrimaryMover(String primaryMover) {
        this.primaryMover = primaryMover;
    }
}
