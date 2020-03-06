package com.mpascal.programmaker.core;

public class ExerciseTemplateProvider {

    private String[] daysTemplates;

    public ExerciseTemplateProvider(String routineSplit, int daysAvailable) {
        setDayTemplate(routineSplit, daysAvailable);
    }

    private void setDayTemplate(String routineSplit, int daysAvailable) {
        daysTemplates = new String[daysAvailable];
        switch (routineSplit) {
            case "FB":
                if (daysAvailable == 1) {
                    /**
                     * FB (Full Body) exercise selection:
                     *
                     *   Day1
                     * Main Legs
                     * Main Chest
                     * Main Back
                     * Main Shoulders
                     * Accessory AbsL
                     * Accessory AbsU
                     * Accessory AbsS
                     *
                     */
                    daysTemplates[0] = "Main Legs|Main Chest|Main Back|Main Shoulders|Accessory AbsL|Accessory AbsU|Accessory AbsS";
                }

                if (daysAvailable == 2) {
                    /**
                     * FB (Full Body) exercise selection:
                     *
                     *      Day1               Day2
                     * Main Legs      |     Main Back
                     * Main Shoulders |     Main Chest
                     * Secondary Back |     Secondary Legs
                     * Accessory AbsU |     Accessory AbsL
                     * Accessory AbsS
                     *
                     */
                    daysTemplates[0] = "Main Legs|Main Shoulders|Secondary Back|Accessory AbsU|Accessory AbsS";
                    daysTemplates[1] = "Main Back|Main Chest|Secondary Legs|Accessory AbsL";
                }

                if (daysAvailable == 3) {
                        /**
                         * FB (Full Body) exercise selection:
                         *
                         *      Day1                Day2                   Day3
                         * Main Legs      |     Secondary Legs      |   Main Back
                         * Main Chest     |     Main Shoulders      |   Secondary Chest
                         * Secondary Back |     Secondary Back      |   Secondary Legs
                         * Accessory AbsL |     Accessory AbsU      |   Accessory AbsS
                         *
                         */
                        daysTemplates[0] = "Main Legs|Main Chest|Secondary Back|Accessory AbsL";
                        daysTemplates[1] = "Secondary Legs|Main Shoulders|Secondary Back|Accessory AbsU";
                        daysTemplates[2] = "Main Back|Secondary Chest|Secondary Legs|Accessory AbsS";
                }
                break;

            case "FB+GPP":
                    /**
                     * FB+GPP (Full Body + General Physical Preparedness) exercise selection:
                     *
                     *      Day1                Day2               Day3                 Day4
                     * Main Legs      |     Secondary Legs |    Main Back       |   Accessory UBack
                     * Main Chest     |     Main Shoulders |    Secondary Chest |   Accessory Triceps
                     * Secondary Back |     Secondary Back |    Secondary Legs  |   Accessory Biceps
                     * Accessory AbsL |     Accessory AbsU |    Accessory AbsS  |   Cardio LISS
                     *
                     */
                    daysTemplates[0] = "Main Legs|Main Chest|Secondary Back|Accessory AbsL";
                    daysTemplates[1] = "Secondary Legs|Main Shoulders|Secondary Back|Accessory AbsU";
                    daysTemplates[2] = "Main Back|Secondary Chest|Secondary Legs|Accessory AbsS";
                    daysTemplates[3] = "Accessory UBack|Accessory Triceps|Accessory Biceps|Cardio LISS";
                break;

            case "UL":
                /**
                 * UL (Upper Lower) exercise selection:
                 *
                 *      Day1                    Day2                 Day3                  Day4
                 * Main Back          |     Secondary Legs   |    Secondary Back    |   Main Legs
                 * Main Chest         |     Secondary Back   |    Main Shoulders    |   Secondary Back
                 * Secondary Shoulder |     Accessory Quads  |    Secondary Chest   |   Accessory Hamstrings
                 * Accessory UBack    |     Accessory LBack  |    Accessory UBack   |   Accessory LBack
                 * Accessory Biceps   |     Accessory Calves |    Accessory Biceps  |   Accessory Calves
                 * Accessory Triceps  |     Accessory AbsL   |    Accessory Triceps |   Accessory AbsU
                 *                                                                      Accessory AbsS
                 *
                 */
                daysTemplates[0] = "Main Back|Main Chest|Secondary Shoulder|Accessory UBack|Accessory Biceps|Accessory Triceps";
                daysTemplates[1] = "Secondary Legs|Secondary Back|Accessory Quads|Accessory LBack|Accessory Calves|Accessory AbsL";
                daysTemplates[2] = "Secondary Back|Main Shoulders|Secondary Chest|Accessory UBack|Accessory Triceps|Accessory Biceps";
                daysTemplates[3] = "Main Legs|Secondary Back|Accessory Hamstrings|Accessory LBack|Accessory Calves|Accessory AbsU|Accessory AbsS";
                break;

            case "UL+GPP":
                /**
                 * UL+GPP (Upper Lower + General Physical Preparedness) exercise selection:
                 *
                 *      Day1                    Day2                 Day3                  Day4                   Day5
                 * Main Back          |     Secondary Legs   |    Secondary Back    |   Main Legs            |  Accessory AbsL
                 * Main Chest         |     Secondary Back   |    Main Shoulders    |   Secondary Back       |  Accessory AbsU
                 * Secondary Shoulder |     Accessory Quads  |    Secondary Chest   |   Accessory Hamstrings |  Accessory AbsS
                 * Accessory UBack    |     Accessory LBack  |    Accessory UBack   |   Accessory LBack      |  Cardio LISS
                 * Accessory Biceps   |     Accessory Calves |    Accessory Biceps  |   Accessory Calves     |
                 * Accessory Triceps  |                      |    Accessory Triceps |
                 *
                 *
                 */
                daysTemplates[0] = "Main Back|Main Chest|Secondary Shoulder|Accessory UBack|Accessory Biceps|Accessory Triceps";
                daysTemplates[1] = "Secondary Legs|Secondary Back|Accessory Quads|Accessory LBack|Accessory Calves";
                daysTemplates[2] = "Secondary Back|Main Shoulders|Secondary Chest|Accessory UBack|Accessory Triceps|Accessory Biceps";
                daysTemplates[3] = "Main Legs|Secondary Back|Accessory Hamstrings|Accessory LBack|Accessory Calves";
                daysTemplates[4] = "Accessory AbsL|Accessory AbsU|Accessory AbsS|Cardio LIIS";
                break;

            case "PPL":
                /**
                 * PPL (Push Pull Legs) exercise selection:
                 *
                 *      Day1                    Day2                    Day3
                 * Main Chest          |    Secondary Back      |   Main Legs
                 * Secondary Shoulders |    Accessory UBack     |   Accessory Quads
                 * Accessory Chest     |    Accessory Biceps    |   Accessory Hamstrings
                 * Accessory Triceps   |    Accessory RearDelts |   Accessory Calves
                 * Accessory AbsL      |    Accessory AbsU      |   Accessory AbsS
                 *
                 *      Day4                    Day5                    Day6
                 * Main Shoulders      |    Main Back           |   Secondary Legs
                 * Secondary Chest     |    Accessory LBack     |   Accessory Quads
                 * Accessory MidDelts  |    Accessory Biceps    |   Accessory Hamstrings
                 * Accessory Triceps   |    Accessory RearDelts |   Accessory Calves
                 * Accessory AbsL      |    Accessory AbsU      |   Accessory AbsS
                 *
                 */
                daysTemplates[0] = "Main Chest|Secondary Shoulders|Accessory Chest|Accessory Triceps|Accessory AbsL";
                daysTemplates[1] = "Secondary Back|Accessory UBack|Accessory Biceps|Accessory RearDelts|Accessory AbsU";
                daysTemplates[2] = "Main Legs|Accessory Quads|Accessory Hamstrings|Accessory Calves|Accessory AbsS";
                daysTemplates[3] = "Main Shoulders|Secondary Chest|Accessory MidDelts|Accessory Triceps|Accessory AbsL";
                daysTemplates[4] = "Main Back|Accessory LBack|Accessory Biceps|Accessory RearDelts|Accessory AbsU";
                daysTemplates[5] = "Secondary Legs|Accessory Quads|Accessory Hamstrings|Accessory Calves|Accessory AbsS";
                break;
        }
    }

    public String[] getDaysTemplates() {
        return daysTemplates;
    }
}
