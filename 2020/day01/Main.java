import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
--- Day 1: Report Repair ---
After saving Christmas five years in a row, you've decided to take a vacation at a nice resort on a tropical island. Surely, Christmas will go on without you.

The tropical island has its own currency and is entirely cash-only. The gold coins used there have a little picture of a starfish; the locals just call them stars. None of the currency exchanges seem to have heard of them, but somehow, you'll need to find fifty of these coins by the time you arrive so you can pay the deposit on your room.

To save your vacation, you need to get all fifty stars by December 25th.

Collect stars by solving puzzles. Two puzzles will be made available on each day in the Advent calendar; the second puzzle is unlocked when you complete the first. Each puzzle grants one star. Good luck!

Before you leave, the Elves in accounting just need you to fix your expense report (your puzzle input); apparently, something isn't quite adding up.

Specifically, they need you to find the two entries that sum to 2020 and then multiply those two numbers together.

For example, suppose your expense report contained the following:

1721
979
366
299
675
1456
In this list, the two entries that sum to 2020 are 1721 and 299. Multiplying them together produces 1721 * 299 = 514579, so the correct answer is 514579.

Of course, your expense report is much larger. Find the two entries that sum to 2020; what do you get if you multiply them together?

Your puzzle answer was 381699.

The first half of this puzzle is complete! It provides one gold star: *

--- Part Two ---
The Elves in accounting are thankful for your help; one of them even offers you a starfish coin they had left over from a past vacation. 
They offer you a second one if you can find three numbers in your expense report that meet the same criteria.

Using the above example again, the three entries that sum to 2020 are 979, 366, and 675. Multiplying them together produces the answer, 241861950.

In your expense report, what is the product of the three entries that sum to 2020?

Answer: 
 

Although it hasn't changed, you can still get your puzzle input.
 */



public class Main{
    public static void main(String[] args) {
        Path path = Paths.get("puzzle_input.txt");
        int arr_size = 0;

        // apparently we need to handle all exceptions in Java, otherwise
        // it won't compile ???
        try {
            arr_size = (int)Files.lines(path).count();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // get an array of all the numbers
        File input = new File("puzzle_input.txt");
        int[] arr = new int[arr_size];
        int arr_idx = 0;

        try {
            Scanner sc = new Scanner(input);
            while (sc.hasNextLine()){
                arr[arr_idx] = Integer.parseInt(sc.nextLine());
                arr_idx++;
            }
            sc.close();
        } catch (FileNotFoundException e) {}


        // start searching
        int complement = 0;
        Arrays.sort(arr);
        for (int i = 0; i < arr_size; i++){
            complement = 2020 - arr[i];
            if (Arrays.binarySearch(arr, complement) >= 0){
                // The complement was found!
                System.out.print("The two numbers that add to 2020 are: ");
                System.out.println(arr[i] + " and " + complement);
                System.out.println("Their product is: " + (arr[i] * complement));
                break;
            } 
        }

        //================== Beginning of Part 2 ====================

        int complement2 = 0;
        search: {   // labels are used for breaking out of (or continuing through) nested loops
            for (int i = 0; i < arr_size - 1; i++){   
                complement = 2020 - arr[i];
                for (int j = i + 1; j < arr_size; j++){
                    complement2 = complement - arr[j];
                    if (complement2 <= 0) continue; // we don't have any negative numbers in our pool, so don't bother searching

                    if (Arrays.binarySearch(arr, complement2) >= 0){
                        // The complement was found!
                        System.out.print("The three numbers that add to 2020 are: ");
                        System.out.println(arr[i] + " , " + complement2 + " and " + arr[j]);
                        System.out.println("Their product is: " + (arr[i] * complement2 * arr[j]));
                        break search;
                    } 
                }
            }
            
        } // end search
    }
}