package com.mpascal.programmaker.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.firestore.Exclude;
import com.mpascal.programmaker.db.ExerciseDB;
import com.mpascal.programmaker.db.RoutineDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

public class Routine extends RoutineDB implements Parcelable {
    private static final String TAG = "Routine";


    private String[][] intensityPerBlock;
    private String[][] exercisesPerBlock;

    public Routine(String title,
                   String goal,
                   ArrayList<Integer> daysAvailable,
                   Double bmi,
                   String routineSplit,
                   int age,
                   String intensityPerBlockStr,
                   String exercisesPerBlockStr,
                   String email,
                   String weight,
                   String height,
                   ArrayList<ExerciseDB> mainExercises,
                   ArrayList<ExerciseDB> secondaryExercises,
                   ArrayList<ExerciseDB> accessoryExercises,
                   ArrayList<ExerciseDB> cardioExercises) {
        super(title, goal, daysAvailable, bmi, routineSplit, age, intensityPerBlockStr, exercisesPerBlockStr, email);

        if (bmi == 0) {
            setBmi(calcBMI(weight, height));
        }

        if (routineSplit.isEmpty()) {
            setRoutineSplit(calcRoutineSplit(daysAvailable, goal, getBmi()));
        }

        if (intensityPerBlockStr.isEmpty()) {
            this.intensityPerBlock = new IntensityTemplateProvider(goal, getBmi()).getIntensityPerBlocks();
            setIntensityPerBlockStr(convert2dArrayToStr(intensityPerBlock));
        } else {
            this.intensityPerBlock = convertStrTo2dArray(intensityPerBlockStr);
        }

        if (exercisesPerBlockStr.isEmpty()) {
            this.exercisesPerBlock = calcExercisesForTrainingBlocks(getRoutineSplit(),
                    daysAvailable.size(),
                    age,
                    goal,
                    intensityPerBlock,
                    mainExercises,
                    secondaryExercises,
                    accessoryExercises,
                    cardioExercises);
            setExercisesPerBlockStr(convert2dArrayToStr(exercisesPerBlock));
        } else {
            this.exercisesPerBlock = convertStrTo2dArray(exercisesPerBlockStr);
        }

    }

    public Routine(String title,
                   String goal,
                   ArrayList<Integer> daysAvailable,
                   Double bmi,
                   String routineSplit,
                   int age,
                   String intensityPerBlockStr,
                   String exercisesPerBlockStr,
                   String email) {

        super(title, goal, daysAvailable, bmi, routineSplit, age, intensityPerBlockStr, exercisesPerBlockStr, email);
        this.intensityPerBlock = convertStrTo2dArray(intensityPerBlockStr);
        this.exercisesPerBlock = convertStrTo2dArray(exercisesPerBlockStr);

    }

    protected Routine(Parcel in) {
        setTitle(in.readString());
        setGoal(in.readString());
        setDaysAvailable(in.readArrayList(Routine.class.getClassLoader()));
        setBmi(in.readDouble());
        setRoutineSplit(in.readString());
        setAge(in.readInt());
        setEmail(in.readString());
        intensityPerBlock = new String[3][4];
        for (int i = 0; i < 3 ; i++) {
            in.readStringArray(intensityPerBlock[i]);
        }
        exercisesPerBlock = new String[3][getDaysAvailable().size()];
        for (int i = 0; i < 3; i++) {
            in.readStringArray(exercisesPerBlock[i]);
        }
        setIntensityPerBlockStr(in.readString());
        setExercisesPerBlockStr(in.readString());
    }

    public static final Creator<Routine> CREATOR = new Creator<Routine>() {
        @Override
        public Routine createFromParcel(Parcel in) {
            return new Routine(in);
        }

        @Override
        public Routine[] newArray(int size) {
            return new Routine[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getTitle());
        dest.writeString(getGoal());
        dest.writeList(getDaysAvailable());
        dest.writeDouble(getBmi());
        dest.writeString(getRoutineSplit());
        dest.writeInt(getAge());
        dest.writeString(getEmail());
        for (int i = 0; i < 3 ; i++) {
            dest.writeStringArray(intensityPerBlock[i]);
        }
        for (int i = 0; i < 3 ; i++) {
            dest.writeStringArray(exercisesPerBlock[i]);
        }
        dest.writeString(getIntensityPerBlockStr());
        dest.writeString(getExercisesPerBlockStr());
    }

    private String convert2dArrayToStr(String[][] array) {
        String result = "";

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < array[i].length; j++) {
                String suffix = "!";
                if (j == array[i].length - 1) {
                    suffix = "";
                }
                result += array[i][j] + suffix;
            }
            if (i != 2) {
                result += "?";
            }
        }

        return result;
    }

    private String[][] convertStrTo2dArray(String str) {
        String[][] result = new String[3][7];
        
        String[] blocks = str.split(Pattern.quote("?"));
        for (int i = 0; i < blocks.length; i++) {
            String[] dayTemplates = blocks[i].split("!");
            result[i] = dayTemplates;
        }
        
        return result;
    }

    private String[][] calcExercisesForTrainingBlocks(String routineSplit,
                                                     int daysAvailable,
                                                     int age,
                                                     String goal,
                                                     String[][] intensityPerBlock,
                                                     ArrayList<ExerciseDB> mainExercises,
                                                     ArrayList<ExerciseDB> secondaryExercises,
                                                     ArrayList<ExerciseDB> accessoryExercises,
                                                     ArrayList<ExerciseDB> cardioExercises) {

        // Get the exercise templates for the routine
        ExerciseTemplateProvider template = new ExerciseTemplateProvider(routineSplit, daysAvailable, goal);
        String[] dayTemplates = template.getDaysTemplates();

        // Boolean for checking if the routine creator is an elder based on the age
        boolean isElder = age > 60;

        String[][] exercisesPerBlock = new String[3][dayTemplates.length];
        // Initialise all strings of every block with empty strings so that we don't have null
        // when we concatenate the exercise names
        for (int i = 0; i < daysAvailable; i++) {
            exercisesPerBlock[0][i] = "";
            exercisesPerBlock[1][i] = "";
            exercisesPerBlock[2][i] = "";
        }

        // Iterate through every training block and intensity block
        for (int i = 0; i < 3; i++) {

            // Get the number of reps per exercise type. The exercise rep ranges will not change
            // weekly. It is safe to get only the first entry from the intensity 2d array and determine
            // the reps from there
            int mainRepsPerBlock = -1;
            int secondaryRepsPerBlock = -1;
            int accessoryRepsPerBlock = -1;
            int cardioMinsPerBlock = -1;

            // This list will hold all the persistent exercises and add them back to the appropriate
            // lists once the block is completed. Doing this removes the prescription of the same
            // same exercise twice a week
            ArrayList<ExerciseDB> exercisesToAddBack = new ArrayList<>();

            // Example String:
            // "Main-6 reps @ RPE 6,7,8|Secondary-8 reps @ RPE 7,8,9|Accessory-10 reps x 5 sets";
            String[] exerciseRepRanges = intensityPerBlock[i][0].split(Pattern.quote("|"));
            for (String exerciseRepRange : exerciseRepRanges) {
                // Example String:
                // "Main-6 reps @ RPE 6,7,8"
                String[] repRangePerType = exerciseRepRange.split("-");
                if (repRangePerType[0].equals("Main")) {
                    // get the first string after the -
                    mainRepsPerBlock = Integer.parseInt(repRangePerType[1].split(" ")[0]);
                }

                if (repRangePerType[0].equals("Secondary")) {
                    // get the first string after the -
                    secondaryRepsPerBlock = Integer.parseInt(repRangePerType[1].split(" ")[0]);
                }

                if (repRangePerType[0].equals("Accessory")) {
                    // get the first string after the -
                    accessoryRepsPerBlock = Integer.parseInt(repRangePerType[1].split(" ")[0]);
                }

                if (repRangePerType[0].equals("Cardio")) {
                    cardioMinsPerBlock = Integer.parseInt(repRangePerType[1].split(" ")[0]);
                }
            }


            // Iterate through all the day templates
            for (int j = 0; j < dayTemplates.length; j++) {

                // Example String:
                // "Main Legs|Main Chest|Secondary Back|Accessory AbsL"
                String[] exercisesTypes = dayTemplates[j].split(Pattern.quote("|"));

                for (String exerciseType : exercisesTypes) {
                    // Example String:
                    // "Main Legs"

                    // Find the first exercise in the lists given that match the criteria
                    final String[] exerciseProperties = exerciseType.split(" ");

                    ArrayList<ExerciseDB> exerciseList = new ArrayList<>();
                    int repRange = -1;

                    switch (exerciseProperties[0]) {
                        case "Main":
                            exerciseList = mainExercises;
                            repRange = mainRepsPerBlock;
                            break;

                        case "Secondary":
                            exerciseList = secondaryExercises;
                            repRange = secondaryRepsPerBlock;
                            break;

                        case "Accessory":
                            exerciseList = accessoryExercises;
                            repRange = accessoryRepsPerBlock;
                            break;

                        case "Cardio":
                            exerciseList = cardioExercises;
                            repRange = cardioMinsPerBlock;
                            break;
                    }

                    // Exercise List should never be null
                    for (ExerciseDB exercise : exerciseList) {

                        boolean betweenRepRange = false;
                        if (repRange != -1) {
                            String[] repRanges = exercise.getRepRange().split("-");
                            int exerciseLowerReps = Integer.parseInt(repRanges[0]);
                            int exerciseUpperReps = Integer.parseInt(repRanges[1]);
                            betweenRepRange = (repRange >= exerciseLowerReps) && (repRange <= exerciseUpperReps);
                        } else {
                            Log.d(TAG, "calcExercisesForTrainingBlocks: repRange calculation failed for " + exerciseProperties[0]);
                        }

                        // Check if the user is an elder and if so, check if the exercise is suitable
                        if ((!isElder || exercise.getIsSuitableForElders()) && exercise.getPrimaryMover().equals(exerciseProperties[1]) && betweenRepRange) {

                            String suffix = "";
                            // Check if it's the last exercise that's substituted from the day template
                            if (!exerciseType.equals(exercisesTypes[exercisesTypes.length - 1])) {
                                suffix = "|";
                            }

                            exercisesPerBlock[i][j] +=  exercise.getCategory() + "-" + exercise.getName() + suffix;

                            if (exercise.getIsPersistent()) {
                                exercisesToAddBack.add(exercise);
                            }
                            exerciseList.remove(exercise);

                            break;
                        }
                    }
                }
            }

            // Add back the exercises removed to build the current block
            for (ExerciseDB exercise : exercisesToAddBack) {
                switch (exercise.getCategory()) {
                    case "Main":
                        mainExercises.add(0, exercise);
                        break;

                    case "Secondary":
                        secondaryExercises.add(0, exercise);
                        break;

                    case "Accessory":
                        accessoryExercises.add(0, exercise);
                        break;

                    case "Cardio":
                        cardioExercises.add(0, exercise);
                        break;
                }
            }
        }

        return exercisesPerBlock;
    }

    private String calcRoutineSplit(ArrayList<Integer> daysAvailable, String goal, Double bmi) {
        String routineSplit;
        if (daysAvailable.size() > 3) {
            if (daysAvailable.size() == 4) {
                // Decide between Upper/Lower and Full Body
                if (goal.equals("Hypertrophy") && bmi < 30) {
                    routineSplit = "UL";
                } else {
                    Collections.sort(daysAvailable);
                    // Check if routine has 3 odds or evens
                    int ctOdd = 0;
                    int ctEven = 0;
                    for (int i = 0; i <= daysAvailable.size() - 1; i++) {
                        if (daysAvailable.get(i) % 2 == 0) {
                            ctEven++;
                        } else {
                            ctOdd++;
                        }
                    }

                    // Check if it has 2 pairs of consecutive days
                    int ct = 0;
                    for (int i = 0; i < daysAvailable.size() - 1; i++) {
                        if (daysAvailable.get(i).equals(daysAvailable.get(i + 1) - 1)) {
                            ct++;
                        }
                    }

                    if (ct > 1 && ctOdd < 3 && ctEven < 3) {
                        routineSplit = "UL";
                    } else {
                        routineSplit = "FB+GPP";
                    }
                }
            } else if (daysAvailable.size() == 5) {
                routineSplit = "UL+GPP";
            } else {
                routineSplit = "PPL";
            }
        } else {
            routineSplit = "FB";
        }

        return routineSplit;
    }

    private Double calcBMI(String weight, String height) {

        // Weight possible values: eg "42.5kg" "225lbs"
        String weightSuffix = weight.substring(weight.length() - 2);
        Double weightValue;
        if (weightSuffix.equals("Kg")) {
            weightValue = Double.parseDouble(weight.substring(0, weight.length() - 2));
        } else {
            Double pounds = Double.parseDouble(weight.substring(0, weight.length() - 3));

            // Convert to kg
            weightValue = pounds * 0.453592;
        }

        // Height possible values: eg "150cm" "7'5ft"
        String heightSuffix = height.substring(height.length() - 2);
        Double heightValue;
        if (heightSuffix.equals("Cm")) {
            Double cms = Double.parseDouble(height.substring(0, height.length() - 2));

            // Convert to meters
            heightValue = cms / 100;
        } else {
            String[] arrOfStrings = height.split("'");
            // Safe to assume that people are not over 10 feet
            int feet = Integer.parseInt(arrOfStrings[0]);

            int inches;
            if (arrOfStrings[1].charAt(1) == 'F') {
                inches = Character.getNumericValue(arrOfStrings[1].charAt(0));
            } else {
                inches = Integer.parseInt(arrOfStrings[1].substring(0, 2));
            }
            inches += 12 * feet;

            // Convert to meters
            heightValue = inches * 0.0254;
        }

        return weightValue / (heightValue * heightValue);
    }

    @Exclude
    public String[][] getExercisesPerBlock() {
        return exercisesPerBlock;
    }

    public void setExercisesPerBlock(String[][] exercisesPerBlock) {
        this.exercisesPerBlock = exercisesPerBlock;
    }

    @Exclude
    public String[][] getIntensityPerBlock() {
        return intensityPerBlock;
    }

    public void setIntensityPerBlock(String[][] intensityPerBlock) {
        this.intensityPerBlock = intensityPerBlock;
    }
}
