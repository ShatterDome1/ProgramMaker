package com.mpascal.programmaker.core;

import androidx.constraintlayout.solver.widgets.ConstraintAnchor;

public class IntensityTemplateProvider {

    private String[][] intensityPerBlocks;

    public IntensityTemplateProvider(String goal) {
        calcIntensityPerBlocks(goal);
    }

    private void calcIntensityPerBlocks(String goal) {
        intensityPerBlocks = new String[4][4];

        switch (goal) {
            case "Fat Loss":
                // Fat loss routine
                intensityPerBlocks[0] = getFatLossBlock();
                intensityPerBlocks[1] = getHypertrophyBlock(goal,1);
                intensityPerBlocks[2] = getStrengthBlock(goal, 3);
                break;

            case "Hypertrophy":
                // Hypertrophy routine
                intensityPerBlocks[0] = getHypertrophyBlock(goal, 1);
                intensityPerBlocks[1] = getHypertrophyBlock(goal,2);
                intensityPerBlocks[2] = getStrengthBlock(goal, 3);
                break;

            case "Strength":
                // Strength routine
                intensityPerBlocks[0] = getHypertrophyBlock(goal, 1);
                intensityPerBlocks[1] = getStrengthBlock(goal,2);
                intensityPerBlocks[2] = getStrengthBlock(goal, 3);
                break;
        }
    }

    // Hypertrophy blocks can be only the first 2 blocks
    private String[] getHypertrophyBlock(String goal, int currentBlock) {
        String[] intensityPerWeek = new String[4];

        int cardioTime = 0;
        // Ramp up the cardio to 30 if it's the first training block
        if (currentBlock == 1) {
            cardioTime = 25;
        } else {
            cardioTime = 30;
        }

        if (currentBlock == 1 || (goal.equals("Fat Loss") && currentBlock == 2)) { // Higher Rep Range

            intensityPerWeek[0] =  "Main-8 reps @ RPE 6,7,8|" +
                                   "Secondary-10 reps @ RPE 7,8,9|" +
                                   "Accessory-12 reps x 4 sets|" +
                                   "Cardio-" + cardioTime + " minutes";

            intensityPerWeek[1] =  "Main-8 reps @ RPE 6,7,8x2|" +
                                   "Secondary-10 reps @ RPE 7,8,9|" +
                                   "Accessory-12 reps x 4 sets|" +
                                   "Cardio-" + cardioTime + " minutes";

            intensityPerWeek[2] =  "Main-8 reps @ RPE 6,7,8x3|" +
                                   "Secondary-10 reps @ RPE 7,8,9x2|" +
                                   "Accessory-12 reps x 4 sets|" +
                                   "Cardio-30 minutes";

            intensityPerWeek[3] =  "Main-8 reps @ RPE 7,8,9|" +
                                   "Secondary-10 reps @ RPE 7,8,9x2|" +
                                   "Accessory-12 reps x 4 sets|" +
                                   "Cardio-30 minutes";



        } else if (currentBlock == 2 && goal.equals("Hypertrophy")) { // Lower Rep Range

            intensityPerWeek[0] =  "Main-6 reps @ RPE 6,7,8|" +
                                   "Secondary-8 reps @ RPE 7,8,9|" +
                                   "Accessory-10 reps x 5 sets|" +
                                   "Cardio-30 minutes";

            intensityPerWeek[1] =  "Main-6 reps @ RPE 6,7,8x2|" +
                                   "Secondary-8 reps @ RPE 7,8,9|" +
                                   "Accessory-10 reps x 5 sets|" +
                                   "Cardio-30 minutes";

            intensityPerWeek[2] =  "Main-6 reps @ RPE 6,7,8x3|" +
                                   "Secondary-8 reps @ RPE 7,8,9x2|" +
                                   "Accessory-10 reps x 5 sets|" +
                                   "Cardio-30 minutes";

            intensityPerWeek[3] =  "Main-6 reps @ RPE 7,8,9|" +
                                   "Secondary-8 reps @ RPE 7,8,9x2|" +
                                   "Accessory-10 reps x 5 sets|" +
                                   "Cardio-30 minutes";

        }

        return intensityPerWeek;
    }

    private String[] getStrengthBlock(String goal, int currentBlock) {
        String[] intensityPerWeek = new String[4];

        if (currentBlock == 2 || !goal.equals("Strength")) { // Higher Rep Range

            intensityPerWeek[0] =  "Main-5 reps @ RPE 6,7,8x2|" +
                                   "Secondary-6 reps @ RPE 7,8,9|" +
                                   "Accessory-10 reps x 5 sets|" +
                                   "Cardio-30 minutes";

            intensityPerWeek[1] =  "Main-5 reps @ RPE 6,7,8x3|" +
                                   "Secondary-6 reps @ RPE 7,8,9|" +
                                   "Accessory-10 reps x 5 sets|" +
                                   "Cardio-30 minutes";

            intensityPerWeek[2] =  "Main-5 reps @ RPE 6,7,8x4|" +
                                   "Secondary-6 reps @ RPE 7,8,9x2|" +
                                   "Accessory-10 reps x 5 sets|" +
                                   "Cardio-30 minutes";

            intensityPerWeek[3] =  "Main-5 reps @ RPE 7,8,9|" +
                                   "Secondary-6 reps @ RPE 7,8,9x2|" +
                                   "Accessory-10 reps x 5 sets|" +
                                   "Cardio-30 minutes";

        } else if (currentBlock == 3) { // Lower Rep Range

            intensityPerWeek[0] =  "Main-1 rep @ RPE 8, 3 reps @ RPE 8x2|" +
                                   "Secondary-4 reps @ RPE 7,8,9|" +
                                   "Accessory-12 reps x 5 sets|" +
                                   "Cardio-30 minutes";

            intensityPerWeek[1] =  "Main-1 rep @ RPE 8, 3 reps @ RPE 8x3|" +
                                   "Secondary-4 reps @ RPE 7,8,9|" +
                                   "Accessory-12 reps x 5 sets|" +
                                   "Cardio-30 minutes";

            intensityPerWeek[2] =  "Main-1 rep @ RPE 8, 3 reps @ RPE 8x4|" +
                                   "Secondary-4 reps @ RPE 7,8,9x2|" +
                                   "Accessory-12 reps x 5 sets|" +
                                   "Cardio-30 minutes";

            intensityPerWeek[3] =  "Main-1 rep @ RPE 8, 3 reps @ RPE 8x2|" +
                                   "Secondary-4 reps @ RPE 7,8,9x2|" +
                                   "Accessory-12 reps x 5 sets|" +
                                   "Cardio-30 minutes";
        }

        return intensityPerWeek;
    }

    private String[] getFatLossBlock() { // Only available when goal is Fat Loss
        String[] intensityPerWeek = new String[4];

        intensityPerWeek[0] =  "Main-10 reps @ RPE 6,7,8|" +
                               "Secondary-12 reps @ RPE 7,8,9|" +
                               "Accessory-12 reps x 4 sets|" +
                               "Cardio-25 minutes";

        intensityPerWeek[1] =  "Main-10 reps @ RPE 6,7,8x2|" +
                               "Secondary-12 reps @ RPE 7,8,9|" +
                               "Accessory-12 reps x 4 sets|" +
                               "Cardio-25 minutes";

        intensityPerWeek[2] =  "Main-10 reps @ RPE 6,7,8x3|" +
                               "Secondary-12 reps @ RPE 7,8,9x2|" +
                               "Accessory-12 reps x 4 sets|" +
                               "Cardio-30 minutes";

        intensityPerWeek[3] =  "Main-10 reps @ RPE 7,8,9|" +
                               "Secondary-12 reps @ RPE 7,8,9x2|" +
                               "Accessory-12 reps x 4 sets|" +
                               "Cardio-30 minutes";

        return intensityPerWeek;
    }

    public String[][] getIntensityPerBlocks() {
        return intensityPerBlocks;
    }
}
