package com.mpascal.programmaker.core;

public class IntensityTemplateProvider {

    private String[][] intensityPerBlocks;

    public IntensityTemplateProvider(String goal, Double bmi) {
        calcIntensityPerBlocks(goal, bmi);
    }

    private void calcIntensityPerBlocks(String goal, Double bmi) {
        intensityPerBlocks = new String[4][4];

        switch (goal) {
            case "Fat Loss":
                if (bmi > 30) {
                    // First Block -> Strength block
                    intensityPerBlocks[0][0] = "Main-6 reps @ RPE 6,7,8x2|" +
                            "Secondary-8 reps @ RPE 6,7,8|" +
                            "Accessory-10 reps x 3 sets @ RPE 6|" +
                            "Cardio-20 minutes";
                    intensityPerBlocks[0][1] = "Main-6 reps @ RPE 6,7,8x3|" +
                            "Secondary-8 reps @ RPE 6,6,7,7|" +
                            "Accessory-10 reps x 4 sets @ RPE 6|" +
                            "Cardio-20 minutes";
                    intensityPerBlocks[0][2] = "Main-6 reps @ RPE 6,7,8x4|" +
                            "Secondary-8 reps @ RPE 6,7,8,8|" +
                            "Accessory-10 reps x 3 sets @ RPE 7|" +
                            "Cardio-20 minutes";
                    intensityPerBlocks[0][3] = "Main-6 reps @ RPE 6,7,8|" +
                            "Secondary-8 reps @ RPE 6,7,8|" +
                            "Accessory-10 reps x 4 sets @ RPE 7|" +
                            "Cardio-20 minutes";

                    // Second Block -> Fat Loss Block
                    intensityPerBlocks[1][0] = "Main-10 reps @ RPE 5,6,7|" +
                            "Secondary-12 reps @ RPE 5,6,7|" +
                            "Accessory-15 reps x 3 sets @ RPE 6|" +
                            "Cardio-25 minutes";
                    intensityPerBlocks[1][1] = "Main-10 reps @ RPE 5,6,7x2|" +
                            "Secondary-12 reps @ RPE 6x2,7x2|" +
                            "Accessory-15 reps x 4 sets @ RPE 6|" +
                            "Cardio-25 minutes";
                    intensityPerBlocks[1][2] = "Main-10 reps @ RPE 5,6,7x3|" +
                            "Secondary-12 reps @ RPE 6,7x3|" +
                            "Accessory-15 reps x 3 sets @ RPE 7|" +
                            "Cardio-25 minutes";
                    intensityPerBlocks[1][3] = "Main-10 reps @ RPE 5,6,7|" +
                            "Secondary-12 reps @ RPE 5,6,7|" +
                            "Accessory-15 reps x 4 sets @ RPE 7|" +
                            "Cardio-25 minutes";

                    // Third Block -> Fat Loss Block
                    intensityPerBlocks[2][0] = "Main-12 reps @ RPE 5,6,7|" +
                            "Secondary-15 reps @ RPE 5,6,7|" +
                            "Accessory-18 reps x 3 sets @ RPE 6|" +
                            "Cardio-30 minutes";
                    intensityPerBlocks[2][1] = "Main-12 reps @ RPE 5,6,7x2|" +
                            "Secondary-15 reps @ RPE 6x2,7x2|" +
                            "Accessory-18 reps x 4 sets @ RPE 6|" +
                            "Cardio-30 minutes";
                    intensityPerBlocks[2][2] = "Main-12 reps @ RPE 5,6,7x3|" +
                            "Secondary-15 reps @ RPE 6,7x3|" +
                            "Accessory-18 reps x 3 sets @ RPE 7|" +
                            "Cardio-30 minutes";
                    intensityPerBlocks[2][3] = "Main-12 reps @ RPE 5,6,7|" +
                            "Secondary-15 reps @ RPE 5,6,7|" +
                            "Accessory-18 reps x 4 sets @ RPE 7|" +
                            "Cardio-30 minutes";

                } else {
                    // Create the templates for each routine
                    // First Block -> Strength block
                    intensityPerBlocks[0][0] = "Main-6 reps @ RPE 6,7,8x2|" +
                            "Secondary-8 reps @ RPE 6,7,8|" +
                            "Accessory-10 reps x 3 sets @ RPE 6|" +
                            "Cardio-20 minutes";
                    intensityPerBlocks[0][1] = "Main-6 reps @ RPE 6,7,8x3|" +
                            "Secondary-8 reps @ RPE 6x2,7x2|" +
                            "Accessory-10 reps x 4 sets @ RPE 6|" +
                            "Cardio-20 minutes";
                    intensityPerBlocks[0][2] = "Main-6 reps @ RPE 6,7,8x4|" +
                            "Secondary-8 reps @ RPE 6,7x3|" +
                            "Accessory-10 reps x 3 sets @ RPE 7|" +
                            "Cardio-20 minutes";
                    intensityPerBlocks[0][3] = "Main-6 reps @ RPE 6,7,8|" +
                            "Secondary-8 reps @ RPE 6,7,8|" +
                            "Accessory-10 reps x 4 sets @ RPE 7|" +
                            "Cardio-20 minutes";

                    // Second Block -> Hypertrophy Block
                    intensityPerBlocks[1][0] = "Main-8 reps @ RPE 5,6,7|" +
                            "Secondary-10 reps @ RPE 5,6,7|" +
                            "Accessory-12 reps x 3 sets @ RPE 6|" +
                            "Cardio-25 minutes";
                    intensityPerBlocks[1][1] = "Main-8 reps @ RPE 5,6,7x2|" +
                            "Secondary-10 reps @ RPE 6x2,7x2|" +
                            "Accessory-12 reps x 4 sets @ RPE 6|" +
                            "Cardio-25 minutes";
                    intensityPerBlocks[1][2] = "Main-8 reps @ RPE 5,6,7x3|" +
                            "Secondary-10 reps @ RPE 6,7x3|" +
                            "Accessory-12 reps x 3 sets @ RPE 7|" +
                            "Cardio-25 minutes";
                    intensityPerBlocks[1][3] = "Main-8 reps @ RPE 5,6,7|" +
                            "Secondary-10 reps @ RPE 5,6,7|" +
                            "Accessory-12 reps x 4 sets @ RPE 7|" +
                            "Cardio-25 minutes";

                    // Third Block -> Fat Loss Block
                    intensityPerBlocks[2][0] = "Main-10 reps @ RPE 5,6,7|" +
                            "Secondary-12 reps @ RPE 5,6,7|" +
                            "Accessory-15 reps x 3 sets @ RPE 6|" +
                            "Cardio-30 minutes";
                    intensityPerBlocks[2][1] = "Main-10 reps @ RPE 5,6,7x2|" +
                            "Secondary-12 reps @ RPE 6x2,7x2|" +
                            "Accessory-15 reps x 4 sets @ RPE 6|" +
                            "Cardio-30 minutes";
                    intensityPerBlocks[2][2] = "Main-10 reps @ RPE 5,6,7x3|" +
                            "Secondary-12 reps @ RPE 6,7x3|" +
                            "Accessory-15 reps x 3 sets @ RPE 7|" +
                            "Cardio-30 minutes";
                    intensityPerBlocks[2][3] = "Main-10 reps @ RPE 5,6,7|" +
                            "Secondary-12 reps @ RPE 5,6,7|" +
                            "Accessory-15 reps x 4 sets @ RPE 7|" +
                            "Cardio-30 minutes";
                }
                break;

            case "Hypertrophy":
                // First Block -> Strength block
                intensityPerBlocks[0][0] = "Main-6 reps @ RPE 6,7,8x2|" +
                        "Secondary-8 reps @ RPE 6,7,8|" +
                        "Accessory-10 reps x 3 sets @ RPE 6|" +
                        "Cardio-30 minutes";
                intensityPerBlocks[0][1] = "Main-6 reps @ RPE 6,7,8x3|" +
                        "Secondary-8 reps @ RPE 6x2,7x2|" +
                        "Accessory-10 reps x 4 sets @ RPE 6|" +
                        "Cardio-30 minutes";
                intensityPerBlocks[0][2] = "Main-6 reps @ RPE 6,7,8x4|" +
                        "Secondary-8 reps @ RPE 6,7x3|" +
                        "Accessory-10 reps x 3 sets @ RPE 7|" +
                        "Cardio-30 minutes";
                intensityPerBlocks[0][3] = "Main-6 reps @ RPE 6,7,8|" +
                        "Secondary-8 reps @ RPE 6,7,8|" +
                        "Accessory-10 reps x 4 sets @ RPE 7|" +
                        "Cardio-30 minutes";

                // Second Block -> Hypertrophy Block
                intensityPerBlocks[1][0] = "Main-8 reps @ RPE 5,6,7|" +
                        "Secondary-10 reps @ RPE 5,6,7|" +
                        "Accessory-12 reps x 3 sets @ RPE 6|" +
                        "Cardio-25 minutes";
                intensityPerBlocks[1][1] = "Main-8 reps @ RPE 5,6,7x2|" +
                        "Secondary-10 reps @ RPE 6x2,7x2|" +
                        "Accessory-12 reps x 4 sets @ RPE 6|" +
                        "Cardio-25 minutes";
                intensityPerBlocks[1][2] = "Main-8 reps @ RPE 5,6,7x3|" +
                        "Secondary-10 reps @ RPE 6,7x3|" +
                        "Accessory-12 reps x 3 sets @ RPE 7|" +
                        "Cardio-25 minutes";
                intensityPerBlocks[1][3] = "Main-8 reps @ RPE 5,6,7|" +
                        "Secondary-10 reps @ RPE 5,6,7|" +
                        "Accessory-12 reps x 4 sets @ RPE 7|" +
                        "Cardio-25 minutes";

                // Third Block -> Hypertrophy Block
                intensityPerBlocks[2][0] = "Main-10 reps @ RPE 5,6,7|" +
                        "Secondary-12 reps @ RPE 5,6,7|" +
                        "Accessory-15 reps x 3 sets @ RPE 6|" +
                        "Cardio-20 minutes";
                intensityPerBlocks[2][1] = "Main-10 reps @ RPE 5,6,7x2|" +
                        "Secondary-12 reps @ RPE 6x2,7x2|" +
                        "Accessory-15 reps x 4 sets @ RPE 6|" +
                        "Cardio-20 minutes";
                intensityPerBlocks[2][2] = "Main-10 reps @ RPE 5,6,7x3|" +
                        "Secondary-12 reps @ RPE 6,7x3|" +
                        "Accessory-15 reps x 3 sets @ RPE 7|" +
                        "Cardio-20 minutes";
                intensityPerBlocks[2][3] = "Main-10 reps @ RPE 5,6,7|" +
                        "Secondary-12 reps @ RPE 5,6,7|" +
                        "Accessory-15 reps x 4 sets @ RPE 7|" +
                        "Cardio-20 minutes";

                break;

            case "Strength":
                // First Block -> Strength block
                intensityPerBlocks[0][0] = "Main-4 reps @ RPE 6,7,8x2|" +
                        "Secondary-6 reps @ RPE 6,7,8|" +
                        "Accessory-8 reps x 4 sets @ RPE 6|" +
                        "Cardio-20 minutes";
                intensityPerBlocks[0][1] = "Main-4 reps @ RPE 6,7,8x3|" +
                        "Secondary-6 reps @ RPE 6,7,8x2|" +
                        "Accessory-8 reps x 5 sets @ RPE 6|" +
                        "Cardio-20 minutes";
                intensityPerBlocks[0][2] = "Main-4 reps @ RPE 6,7,8x4|" +
                        "Secondary-6 reps @ RPE 6,7,8x3|" +
                        "Accessory-8 reps x 4 sets @ RPE 7|" +
                        "Cardio-20 minutes";
                intensityPerBlocks[0][3] = "Main-4 reps @ RPE 6,7,8|" +
                        "Secondary-6 reps @ RPE 6,7,8|" +
                        "Accessory-8 reps x 5 sets @ RPE 7|" +
                        "Cardio-20 minutes";

                // Second Block -> Strength Block
                intensityPerBlocks[1][0] = "Main-6 reps @ RPE 6,7,8x2|" +
                        "Secondary-8 reps @ RPE 6,7,8|" +
                        "Accessory-10 reps x 3 sets @ RPE 6|" +
                        "Cardio-25 minutes";
                intensityPerBlocks[1][1] = "Main-6 reps @ RPE 6,7,8x3|" +
                        "Secondary-8 reps @ RPE 6x2,7x2|" +
                        "Accessory-10 reps x 4 sets @ RPE 6|" +
                        "Cardio-25 minutes";
                intensityPerBlocks[1][2] = "Main-6 reps @ RPE 6,7,8x4|" +
                        "Secondary-8 reps @ RPE 6,7x3|" +
                        "Accessory-10 reps x 3 sets @ RPE 7|" +
                        "Cardio-25 minutes";
                intensityPerBlocks[1][3] = "Main-6 reps @ RPE 6,7,8|" +
                        "Secondary-8 reps @ RPE 6,7,8|" +
                        "Accessory-10 reps x 4 sets @ RPE 7|" +
                        "Cardio-25 minutes";

                // Third Block -> Hypertrophy Block
                intensityPerBlocks[2][0] = "Main-8 reps @ RPE 5,6,7|" +
                        "Secondary-10 reps @ RPE 6,7,8|" +
                        "Accessory-12 reps x 3 sets @ RPE 6|" +
                        "Cardio-30 minutes";
                intensityPerBlocks[2][1] = "Main-8 reps @ RPE 5,6,7x2|" +
                        "Secondary-10 reps @ RPE 6x2,7x2|" +
                        "Accessory-12 reps x 4 sets @ RPE 6|" +
                        "Cardio-30 minutes";
                intensityPerBlocks[2][2] = "Main-8 reps @ RPE 5,6,7x3|" +
                        "Secondary-10 reps @ RPE 6,7x3|" +
                        "Accessory-12 reps x 3 sets @ RPE 7|" +
                        "Cardio-30 minutes";
                intensityPerBlocks[2][3] = "Main-8 reps @ RPE 5,6,7|" +
                        "Secondary-10 reps @ RPE 6,7,8|" +
                        "Accessory-12 reps x 4 sets @ RPE 7|" +
                        "Cardio-30 minutes";
                break;
        }
    }

    public String[][] getIntensityPerBlocks() {
        return intensityPerBlocks;
    }
}
