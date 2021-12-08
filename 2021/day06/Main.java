import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.awt.*;

import org.javatuples.*;


/**
--- Day 6: Lanternfish ---
The sea floor is getting steeper. Maybe the sleigh keys got carried this way?

A massive school of glowing lanternfish swims past. They must spawn quickly to reach such large numbers - maybe exponentially quickly? You should model their growth rate to be sure.

Although you know nothing about this specific species of lanternfish, you make some guesses about their attributes. Surely, each lanternfish creates a new lanternfish once every 7 days.

However, this process isn't necessarily synchronized between every lanternfish - one lanternfish might 
have 2 days left until it creates another lanternfish, while another might have 4. So, you can model each 
fish as a single number that represents the number of days until it creates a new lanternfish.

Furthermore, you reason, a new lanternfish would surely need slightly longer before it's capable of producing more lanternfish: two more days for its first cycle.

So, suppose you have a lanternfish with an internal timer value of 3:

After one day, its internal timer would become 2.
After another day, its internal timer would become 1.
After another day, its internal timer would become 0.
After another day, its internal timer would reset to 6, and it would 
create a new lanternfish with an internal timer of 8.
After another day, the first lanternfish would have an internal timer 
of 5, and the second lanternfish would have an internal timer of 7.
A lanternfish that creates a new fish resets its timer to 6, not 7 
(because 0 is included as a valid timer value). The new lanternfish starts with 
an internal timer of 8 and does not start counting down until the next day.

Realizing what you're trying to do, the submarine automatically produces 
a list of the ages of several hundred nearby lanternfish (your puzzle input). 
For example, suppose you were given the following list:

3,4,3,1,2
This list means that the first fish has an internal timer of 3, the second 
fish has an internal timer of 4, and so on until the fifth fish, which has an 
internal timer of 2. Simulating these fish over several days would proceed as follows:

Initial state: 3,4,3,1,2
After  1 day:  2,3,2,0,1
After  2 days: 1,2,1,6,0,8
After  3 days: 0,1,0,5,6,7,8
After  4 days: 6,0,6,4,5,6,7,8,8
After  5 days: 5,6,5,3,4,5,6,7,7,8
After  6 days: 4,5,4,2,3,4,5,6,6,7
After  7 days: 3,4,3,1,2,3,4,5,5,6
After  8 days: 2,3,2,0,1,2,3,4,4,5
After  9 days: 1,2,1,6,0,1,2,3,3,4,8
After 10 days: 0,1,0,5,6,0,1,2,2,3,7,8
After 11 days: 6,0,6,4,5,6,0,1,1,2,6,7,8,8,8
After 12 days: 5,6,5,3,4,5,6,0,0,1,45,6,7,7,7,8,8
After 13 days: 4,5,4,2,3,4,5,6,6,0,4,5,6,6,6,7,7,8,8
After 14 days: 3,4,3,1,2,3,4,5,5,6,3,4,5,5,5,6,6,7,7,8
After 15 days: 2,3,2,0,1,2,3,4,4,5,2,3,4,4,4,5,5,6,6,7
After 16 days: 1,2,1,6,0,1,2,3,3,4,1,2,3,3,3,4,4,5,5,6,8
After 17 days: 0,1,0,5,6,0,1,2,2,3,0,1,2,2,2,3,3,4,4,5,7,8
After 18 days: 6,0,6,4,5,6,0,1,1,2,6,0,1,1,1,2,2,3,3,4,6,7,8,8,8,8
Each day, a 0 becomes a 6 and adds a new 8 to the end of the list, while each other 
number decreases by 1 if it was present at the start of the day.

In this example, after 18 days, there are a total of 26 fish. After 80 days, there would be a total of 5934.

Find a way to simulate lanternfish. How many lanternfish would there be after 80 days?

Your puzzle answer was 394994.

The first half of this puzzle is complete! It provides one gold star: *

--- Part Two ---
Suppose the lanternfish live forever and have unlimited food and space. Would they take over the entire ocean?

After 256 days in the example above, there would be a total of 26984457539 lanternfish!

How many lanternfish would there be after 256 days?
 */

public class Main{
    public static void main(String[] args) {
        String file = "puzzle_input.txt";
        long[] clk_bins = FileParser.getBins(file);

        for (int day = 0; day <= 256; day++){

            if (day == 80 || day == 256){
                long count = 0;
                // print_bins(clk_bins, day);
                for (long i : clk_bins)
                    count+=i;
                System.out.printf("day: %d   fish count = %d\n", day, count);
            }

            // how many fish have their timers expired? (in bins[0])
            long expired_timer_fish = 0;
            if (clk_bins[0] != 0){
                expired_timer_fish = clk_bins[0];
            }

            // move the fish from one bin to another, depending on where they're starting from
            for (int i = 0; i <= 8; i++) { 
                if (i == 6)
                    clk_bins[i] = expired_timer_fish + clk_bins[7];   // move the fish that were in 7 to here as well as the ones whose timers are up
                else if (i == 7)
                    clk_bins[i] = clk_bins[8];
                else if (i == 8)
                    clk_bins[i] = expired_timer_fish;
                else
                    clk_bins[i] = clk_bins[i + 1];  // transfer the normal fishes to the next bin down
            }
        }
    }

    private static void print_bins(long[] bins, int day){
        System.out.printf("\nday %d:  \n", day);
        for (int i = 0; i < bins.length; i++)
            System.out.printf("%d  ", i);
        System.out.println("\n-------------------------");

        for (long i : bins) {
            System.out.printf("%d  ", i);
        }
        System.out.println("");

    }
}


/**
 * Contains helper functions for importing data from the 
 * puzzle input text file.
 */
class FileParser{

    /**
     * Takes in the name of a text file that is in the root directory
     * and returns the number of lines it contains. Returns -1 on error.
     */
    public static int countLines(String filename){
        Path path = Paths.get(filename);
        int arr_size = 0;

        try {
            arr_size = (int)Files.lines(path).count();
        } catch (IOException e1) {
            e1.printStackTrace();
            return -1;
        }

        return arr_size;
    }

    /**
     * Takes in the name of a text file that is in the root directory 
     * and returns the lines within the file as an array of strings.
     */
    public static String[] getLines(String filename){       
         // Figure out how many lines are in the file
        int arr_size = countLines(filename);

        // get an array of all the numbers
        File input = new File(filename);
        String[] arr = new String[arr_size];
        int arr_idx = 0;

        try {
            Scanner sc = new Scanner(input);
            while (sc.hasNextLine()){
                arr[arr_idx] = sc.nextLine();
                arr_idx++;
            }
            sc.close();
        } catch (FileNotFoundException e) {}

        return arr;
    }

    /**
     * Takes in the name of a text file that has a single line in it with
     * numbers separated by commas and returns an array representing bins.
     * Each bin holds a number of how many instances of fish have a given number of days
     * in their internal clock left. 
     * 
     * e.g. given an input 1,1,3,5,2,4,4,4
     * it will return the following array: {0, 2, 1, 1, 3, 1}
     */
    public static long[] getBins(String filename){
        long[] bins = new long[9]; // bin labels are 0~5 (inclusive)
        Arrays.fill(bins, 0);
        File input = new File(filename);
        try {
            Scanner sc = new Scanner(input).useDelimiter(",");
            while (sc.hasNext()){
                int num = Integer.parseInt(sc.next());
                bins[num]++;
            }
            sc.close();
        } catch (FileNotFoundException e) {}

        return bins;
    }
}
