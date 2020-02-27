package com.mpascal.programmaker.db;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {

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
    private boolean isCompoundExercise;

    // Target body muscles
    private String primaryMover;

    public Exercise(String category,
                    String name,
                    String repRange,
                    boolean isSuitableForElders,
                    boolean isCompoundExercise,
                    String primaryMover) {
        this.category = category;
        this.name = name;
        this.repRange = repRange;
        this.isSuitableForElders = isSuitableForElders;
        this.isCompoundExercise = isCompoundExercise;
        this.primaryMover = primaryMover;
    }

    protected Exercise(Parcel in) {
        category = in.readString();
        name = in.readString();
        repRange = in.readString();
        isSuitableForElders = in.readByte() != 0;
        isCompoundExercise = in.readByte() != 0;
        primaryMover = in.readString();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
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
        dest.writeByte((byte) (isCompoundExercise ? 1 : 0));
        dest.writeString(primaryMover);
    }

    public Exercise() {}

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

    public boolean getIsCompoundExercise() {
        return isCompoundExercise;
    }

    public void setIsCompoundExercise(boolean compoundExercise) {
        isCompoundExercise = compoundExercise;
    }

    public String getPrimaryMover() {
        return primaryMover;
    }

    public void setPrimaryMover(String primaryMover) {
        this.primaryMover = primaryMover;
    }
}
