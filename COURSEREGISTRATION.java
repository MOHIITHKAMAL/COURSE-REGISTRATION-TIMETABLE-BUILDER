import java.util.Scanner;

public class DirectTimetableBuilder {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Setup the courses and section capacities
        System.out.print("Enter the number of courses you want to register for: ");
        int totalCourses = scanner.nextInt();
        scanner.nextLine(); // Clear buffer

        String[] courseNames = new String[totalCourses];
        int[] sectionsPerCourse = new int[totalCourses];

        // 3D Arrays to store schedule info: [CourseIndex][SectionIndex][0=Day, 1=StartHour, 2=EndHour]
        // Days are mapped to integers: 1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri
        int[][][] sectionDays = new int[totalCourses][10][2];      // Stores Day info (up to 2 slots per section)
        int[][][] sectionStarts = new int[totalCourses][10][2];    // Stores Start Hours
        int[][][] sectionEnds = new int[totalCourses][10][2];      // Stores End Hours
        String[][] sectionNames = new String[totalCourses][10];    // Section labels (e.g., "Sec A")

        // 2. Gather inputs using Scanners and Loops
        for (int c = 0; c < totalCourses; c++) {
            System.out.print("\nEnter name for Course #" + (c + 1) + " (e.g., CS101): ");
            courseNames[c] = scanner.nextLine();

            System.out.print("How many section options are available for " + courseNames[c] + "? ");
            sectionsPerCourse[c] = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            for (int s = 0; s < sectionsPerCourse[c]; s++) {
                System.out.print("  Enter label for Section #" + (s + 1) + " (e.g., SecA): ");
                sectionNames[c][s] = scanner.next();

                System.out.print("  How many weekly time slots does this section have (1 or 2)? ");
                int slots = scanner.nextInt();

                for (int t = 0; t < 2; t++) {
                    if (t < slots) {
                        System.out.print("    Enter Day (1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri): ");
                        sectionDays[c][s][t] = scanner.nextInt();
                        System.out.print("    Enter Start Hour (24h format, e.g., 9): ");
                        sectionStarts[c][s][t] = scanner.nextInt();
                        System.out.print("    Enter End Hour (24h format, e.g., 11): ");
                        sectionEnds[c][s][t] = scanner.nextInt();
                    } else {
                        // Dummy values for empty slots
                        sectionDays[c][s][t] = -1;
                    }
                }
                scanner.nextLine(); // Clear buffer
            }
        }

        System.out.println("\n=== GENERATING VALID SCHEDULE COMBINATIONS ===");

        // 3. Simple brute force logic using nested loops to find valid schedules
        // This demonstration explicitly handles up to 3 courses. 
        if (totalCourses == 2) {
            int optionsCount = 1;
            for (int s1 = 0; s1 < sectionsPerCourse[0]; s1++) {
                for (int s2 = 0; s2 < sectionsPerCourse[1]; s2++) {
                    
                    // Conflict detection logic
                    boolean conflict = false;
                    for (int t1 = 0; t1 < 2; t1++) {
                        if (sectionDays[0][s1][t1] == -1) continue;
                        
                        for (int t2 = 0; t2 < 2; t2++) {
                            if (sectionDays[1][s2][t2] == -1) continue;

                            // If they share the same day, check for time overlaps
                            if (sectionDays[0][s1][t1] == sectionDays[1][s2][t2]) {
                                if (sectionStarts[0][s1][t1] < sectionEnds[1][s2][t2] && 
                                    sectionStarts[1][s2][t2] < sectionEnds[0][s1][t1]) {
                                    conflict = true;
                                }
                            }
                        }
                    }

                    // Print if no overlaps found
                    if (!conflict) {
                        System.out.println("Option " + optionsCount++ + ":");
                        System.out.println("  " + courseNames[0] + " -> " + sectionNames[0][s1]);
                        System.out.println("  " + courseNames[1] + " -> " + sectionNames[1][s2]);
                        System.out.println();
                    }
                }
            }
        } 
        else if (totalCourses == 3) {
            int optionsCount = 1;
            for (int s1 = 0; s1 < sectionsPerCourse[0]; s1++) {
                for (int s2 = 0; s2 < sectionsPerCourse[1]; s2++) {
                    for (int s3 = 0; s3 < sectionsPerCourse[2]; s3++) {

                        boolean conflict = false;
                        
                        // Check Course 1 vs Course 2, Course 1 vs Course 3, and Course 2 vs Course 3
                        // Loop pair logic for structural checking
                        for (int t1 = 0; t1 < 2; t1++) {
                            for (int t2 = 0; t2 < 2; t2++) {
                                // C1 vs C2
                                if (sectionDays[0][s1][t1] != -1 && sectionDays[1][s2][t2] != -1 &&
                                    sectionDays[0][s1][t1] == sectionDays[1][s2][t2] &&
                                    sectionStarts[0][s1][t1] < sectionEnds[1][s2][t2] && 
                                    sectionStarts[1][s2][t2] < sectionEnds[0][s1][t1]) {
                                    conflict = true;
                                }
                                // C1 vs C3
                                if (sectionDays[0][s1][t1] != -1 && sectionDays[2][s3][t2] != -1 &&
                                    sectionDays[0][s1][t1] == sectionDays[2][s3][t2] &&
                                    sectionStarts[0][s1][t1] < sectionEnds[2][s3][t2] && 
                                    sectionStarts[2][s3][t2] < sectionEnds[0][s1][t1]) {
                                    conflict = true;
                                }
                                // C2 vs C3
                                if (sectionDays[1][s2][t1] != -1 && sectionDays[2][s3][t2] != -1 &&
                                    sectionDays[1][s2][t1] == sectionDays[2][s3][t2] &&
                                    sectionStarts[1][s2][t1] < sectionEnds[2][s3][t2] && 
                                    sectionStarts[2][s3][t2] < sectionEnds[1][s2][t1]) {
                                    conflict = true;
                                }
                            }
                        }

                        if (!conflict) {
                            System.out.println("Option " + optionsCount++ + ":");
                            System.out.println("  " + courseNames[0] + " -> " + sectionNames[0][s1]);
                            System.out.println("  " + courseNames[1] + " -> " + sectionNames[1][s2]);
                            System.out.println("  " + courseNames[2] + " -> " + sectionNames[2][s3]);
                            System.out.println();
                        }
                    }
                }
            }
        } else {
            System.out.println("This basic loop structure is configured to process exactly 2 or 3 courses.");
        }

        scanner.close();
    }
}
