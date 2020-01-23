package com.mpascal.programmaker.db;

public class Exercise {

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

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getRepRange() {
        return repRange;
    }

    public boolean isSuitableForElders() {
        return isSuitableForElders;
    }

    public boolean isCompoundExercise() {
        return isCompoundExercise;
    }

    public String getPrimaryMover() {
        return primaryMover;
    }
}
