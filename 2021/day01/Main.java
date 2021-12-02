import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
--- Day 1: Sonar Sweep ---
You're minding your own business on a ship at sea when the overboard alarm goes off! 
You rush to see if you can help. Apparently, one of the Elves tripped and accidentally sent the sleigh keys flying into the ocean!

Before you know it, you're inside a submarine the Elves keep ready for situations like this. 
It's covered in Christmas lights (because of course it is), and it even has an experimental 
antenna that should be able to track the keys if you can boost its signal strength high enough; 
there's a little meter that indicates the antenna's signal strength by displaying 0-50 stars.

Your instincts tell you that in order to save Christmas, you'll need to get all fifty stars by December 25th.

Collect stars by solving puzzles. Two puzzles will be made available on each day in the Advent calendar;
the second puzzle is unlocked when you complete the first. Each puzzle grants one star. Good luck!

As the submarine drops below the surface of the ocean, it automatically performs a sonar sweep of the nearby sea floor. 
On a small screen, the sonar sweep report (your puzzle input) appears: each line is a measurement of the sea 
floor depth as the sweep looks further and further away from the submarine.

For example, suppose you had the following report:

199
200
208
210
200
207
240
269
260
263
This report indicates that, scanning outward from the submarine, the sonar sweep found depths of 199, 200, 208, 210, and so on.

The first order of business is to figure out how quickly the depth increases, just so you know what you're dealing with - 
you never know if the keys will get carried into deeper water by an ocean current or a fish or something.

To do this, count the number of times a depth measurement increases from the previous measurement. 
(There is no measurement before the first measurement.) In the example above, the changes are as follows:

199 (N/A - no previous measurement)
200 (increased)
208 (increased)
210 (increased)
200 (decreased)
207 (increased)
240 (increased)
269 (increased)
260 (decreased)
263 (increased)
In this example, there are 7 measurements that are larger than the previous measurement.

How many measurements are larger than the previous measurement?
Your puzzle answer was 1715.

The first half of this puzzle is complete! It provides one gold star: *

--- Part Two ---
Considering every single measurement isn't as useful as you expected: there's just too much noise in the data.

Instead, consider sums of a three-measurement sliding window. Again considering the above example:

199  A      
200  A B    
208  A B C  
210    B C D
200  E   C D
207  E F   D
240  E F G  
269    F G H
260      G H
263        H
Start by comparing the first and second three-measurement windows. The measurements in the first window are marked A (199, 200, 208);
their sum is 199 + 200 + 208 = 607. The second window is marked B (200, 208, 210); its sum is 618. 
The sum of measurements in the second window is larger than the sum of the first, so this first comparison increased.

Your goal now is to count the number of times the sum of measurements in this sliding window increases from the previous sum. 
So, compare A with B, then compare B with C, then C with D, and so on. Stop when there aren't enough measurements left to create a new three-measurement sum.

In the above example, the sum of each three-measurement window is as follows:

A: 607 (N/A - no previous sum)
B: 618 (increased)
C: 618 (no change)
D: 617 (decreased)
E: 647 (increased)
F: 716 (increased)
G: 769 (increased)
H: 792 (increased)
In this example, there are 5 sums that are larger than the previous sum.

Consider sums of a three-measurement sliding window. How many sums are larger than the previous sum?
Answer: 1739
 */


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

}


class MovingWindow{
    int windowSize;
    int sum;    // moving sum
    int avg;    // moving avg
    int[] window;

    /**
     * Creates a new moving window with the initial 'size' elements
     */
    public MovingWindow(int size, int[] first_elements){
        windowSize = size;
        window = new int[size];
        for (int i = 0; i < size; i++){
            window[i] = first_elements[i];
            sum += first_elements[i];
        }
        avg = sum / size;
    }

    /**
     * Moves the window by one value
     */
    public void next(int val){
        sum = sum - window[0] + val;
        avg = sum / windowSize;
        
        for (int i = 0; i < windowSize; i++){
            if (i == windowSize - 1){
                window[i] = val;
                continue;
            }
            window[i] = window[i + 1];
        }
    }
}

public class Main{
    public static void main(String[] args) {

        String file = "puzzle_input.txt";
        
        String[] lines = FileParser.getLines(file);

        int last_depth = 0;
        int qty_larger = -1; // -1 so that we don't meaningfully count the first one 

        // ==================================== PART ONE ==========

        for (String line : lines) {
            if (Integer.parseInt(line) > last_depth){
                qty_larger++;
            }
            last_depth = Integer.parseInt(line);
        }
        System.out.printf("Part One:\nNumber of increases in depth: %d\n\n", qty_larger);


        // ==================================== PART TWO ==========

        last_depth = 0;
        qty_larger = -1;
        
        // create a moving window and initialize it with the first `window_size` elements
        // (to act as the _first_ window)
        int window_size = 3;
        int[] tmp = new int[window_size];   // get the first numbers for the first window
        for (int i = 0; i < window_size; i++){
            tmp[i] = Integer.parseInt(lines[i]);
        }
        MovingWindow moving_window = new MovingWindow(window_size, tmp);
        
        
        // advance the window through the array
        for (int nextIdx = window_size; nextIdx <= lines.length; nextIdx++) {
            // check if the current depth is greater than the last depth
            if (moving_window.sum > last_depth){
                qty_larger++;
            }
            // log the sum of the window as the last depth
            last_depth = moving_window.sum;

            // get the next window if there are more numbers in the list
            if (nextIdx < lines.length){
                moving_window.next(Integer.parseInt(lines[nextIdx]));
            }
        }

        
        System.out.printf("Part Two:\nNumber of increases in depth (moving window): %d", qty_larger);

    }

}

