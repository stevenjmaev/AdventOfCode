import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
--- Day 3: Binary Diagnostic ---
The submarine has been making some odd creaking noises, so you ask it to produce a diagnostic report just in case.

The diagnostic report (your puzzle input) consists of a list of binary numbers which, when decoded properly, 
can tell you many useful things about the conditions of the submarine. The first parameter to check is the power consumption.

You need to use the binary numbers in the diagnostic report to generate two new binary numbers 
(called the gamma rate and the epsilon rate). The power consumption can then be found by multiplying the gamma rate by the epsilon rate.

Each bit in the gamma rate can be determined by finding the most common bit in the corresponding position 
of all numbers in the diagnostic report. For example, given the following diagnostic report:

00100
11110
10110
10111
10101
01111
00111
11100
10000
11001
00010
01010
Considering only the first bit of each number, there are five 0 bits and seven 1 bits. Since the most common bit is 1, the first bit of the gamma rate is 1.

The most common second bit of the numbers in the diagnostic report is 0, so the second bit of the gamma rate is 0.

The most common value of the third, fourth, and fifth bits are 1, 1, and 0, respectively, and so the final three bits of the gamma rate are 110.

So, the gamma rate is the binary number 10110, or 22 in decimal.

The epsilon rate is calculated in a similar way; rather than use the most common bit, the least common bit from each position is used. 
So, the epsilon rate is 01001, or 9 in decimal. Multiplying the gamma rate (22) by the epsilon rate (9) produces the power consumption, 198.

Use the binary numbers in your diagnostic report to calculate the gamma rate and epsilon rate, then multiply them together. 
What is the power consumption of the submarine? (Be sure to represent your answer in decimal, not binary.)

Your puzzle answer was 845186.

The first half of this puzzle is complete! It provides one gold star: *

--- Part Two ---
Next, you should verify the life support rating, which can be determined by multiplying the oxygen generator rating by the CO2 scrubber rating.

Both the oxygen generator rating and the CO2 scrubber rating are values that can be found in your diagnostic report - 
finding them is the tricky part. Both values are located using a similar process that involves filtering out values 
until only one remains. Before searching for either rating value, start with the full list of binary numbers from your 
diagnostic report and consider just the first bit of those numbers. Then:

Keep only numbers selected by the bit criteria for the type of rating value for which you are searching. Discard numbers which do not match the bit criteria.
If you only have one number left, stop; this is the rating value for which you are searching.
Otherwise, repeat the process, considering the next bit to the right.
The bit criteria depends on which type of rating value you want to find:

To find oxygen generator rating, determine the most common value (0 or 1) in the current bit position, and keep only numbers 
with that bit in that position. If 0 and 1 are equally common, keep values with a 1 in the position being considered.
To find CO2 scrubber rating, determine the least common value (0 or 1) in the current bit position, and keep only numbers with 
that bit in that position. If 0 and 1 are equally common, keep values with a 0 in the position being considered.
For example, to determine the oxygen generator rating value using the same example diagnostic report from above:

Start with all 12 numbers and consider only the first bit of each number. There are more 1 bits (7) than 0 bits (5), 
so keep only the 7 numbers with a 1 in the first position: 11110, 10110, 10111, 10101, 11100, 10000, and 11001.
Then, consider the second bit of the 7 remaining numbers: there are more 0 bits (4) than 1 bits (3), so keep only the 
4 numbers with a 0 in the second position: 10110, 10111, 10101, and 10000.
In the third position, three of the four numbers have a 1, so keep those three: 10110, 10111, and 10101.
In the fourth position, two of the three numbers have a 1, so keep those two: 10110 and 10111.
In the fifth position, there are an equal number of 0 bits and 1 bits (one each). So, to find the oxygen generator rating, 
keep the number with a 1 in that position: 10111.
As there is only one number left, stop; the oxygen generator rating is 10111, or 23 in decimal.
Then, to determine the CO2 scrubber rating value from the same example above:

Start again with all 12 numbers and consider only the first bit of each number. There are fewer 0 bits (5) than 1 bits (7), 
so keep only the 5 numbers with a 0 in the first position: 00100, 01111, 00111, 00010, and 01010.
Then, consider the second bit of the 5 remaining numbers: there are fewer 1 bits (2) than 0 bits (3), so keep only the 
2 numbers with a 1 in the second position: 01111 and 01010.
In the third position, there are an equal number of 0 bits and 1 bits (one each). So, to find the CO2 scrubber rating, 
keep the number with a 0 in that position: 01010.
As there is only one number left, stop; the CO2 scrubber rating is 01010, or 10 in decimal.
Finally, to find the life support rating, multiply the oxygen generator rating (23) by the CO2 scrubber rating (10) to get 230.

Use the binary numbers in your diagnostic report to calculate the oxygen generator rating and CO2 scrubber rating, 
then multiply them together. What is the life support rating of the submarine? (Be sure to represent your answer in decimal, not binary.)

answer: 
oxygen generator rating: 1459
CO2 generator rating: 3178
product: 4636702
 */

public class Main{
    public static void main(String[] args) {

        String file = "puzzle_input.txt";
        String[] lines = FileParser.getLines(file);

        PartOne.find_power_consumption(lines);
        
        int oxygenRating = PartTwo.find_generator_rating(lines, "oxygen");
        int co2Rating = PartTwo.find_generator_rating(lines, "CO2");
        System.out.printf("oxygen generator rating: %d\n", oxygenRating);
        System.out.printf("CO2 generator rating: %d\n", co2Rating);
        System.out.printf("product: %d\n", oxygenRating * co2Rating);
    }
}



class PartOne{
    static void find_power_consumption(String[] lines){
        int gamma = 0, epsilon = 0;

        // iterate through each bit
        for (int digitIdx = 0; digitIdx < lines[0].length(); digitIdx++){
            int num_of_ones = 0;

            // iterate through each line
            for (int lineIdx = 0; lineIdx < lines.length; lineIdx++){
                int bin_num = Integer.parseInt(lines[lineIdx], 2);

                // check the bit that corresponds to `digitIdx` to see if it is a `1`
                if ((bin_num & (int)Math.pow(2, digitIdx)) != 0){
                    num_of_ones++;
                }
            }

            // check which bit is most common
            // (lines.length - num_of_ones) is the number of zeros
            if (num_of_ones > (lines.length - num_of_ones)){
                gamma += Math.pow(2, digitIdx);
            }
        }
        // epsilon is one's comp of gamma
        epsilon = gamma ^ ((int)Math.pow(2, lines[0].length()) - 1);
        System.out.printf("gamma = %d, epsilon = %d\n", gamma, epsilon);
        System.out.printf("power consumption = %d\n", gamma * epsilon);
    }
}


class PartTwo{
    static int find_generator_rating(String[] lines, String generator_type){

        List<Integer> nums = FileParser.toIntList(lines);

        // iterate through each bit (going from MSB to LSB)
        for (int digitIdx = lines[0].length() - 1; digitIdx >= 0; digitIdx--){
            int num_of_ones = 0;

            // iterate through each remaining number
            // (needs to go through the list of remaining numbers)
            for (int lineIdx = 0; lineIdx < nums.size(); lineIdx++){
                int bin_num = nums.get(lineIdx);

                // check the bit that corresponds to `digitIdx` to see if it is a `1`
                if ((bin_num & (int)Math.pow(2, digitIdx)) != 0){
                    num_of_ones++;
                }
            }

            // looking for oxygen generator rating 
            if (generator_type == "oxygen"){
                // equally common
                if (num_of_ones == (nums.size() - num_of_ones)){
                    nums = getMatches(nums, digitIdx, 1);
                }
                // 1's more common
                else if (num_of_ones > (nums.size() - num_of_ones)){
                    nums = getMatches(nums, digitIdx, 1);
                }
                // 0's more common
                else{
                    nums = getMatches(nums, digitIdx, 0);
                }
            }
            // looking for CO2 generator rating 
            else if (generator_type == "CO2"){
                // equally common
                if (num_of_ones == (nums.size() - num_of_ones)){
                    nums = getMatches(nums, digitIdx, 0);
                }
                // 1's more common
                else if (num_of_ones > (nums.size() - num_of_ones)){
                    nums = getMatches(nums, digitIdx, 0);
                }
                // 0's more common
                else{
                    nums = getMatches(nums, digitIdx, 1);
                }
            }
            if (nums.size() == 1) // if there's one left, stop (per instructions)
                break;
            else if (nums.size() == 0)
                return -1;
        }
        return nums.get(0);
    }

    static List<Integer> getMatches(List<Integer> nums, int bitIdx, int value){
        List<Integer> ret_list = new ArrayList<>(nums); // copy nums into the list to return
        
        // go backwards through the list
        for(int i = nums.size() - 1; i >= 0; i--){
            // get the bit at location 'bitIdx'
            int bit = (ret_list.get(i) & (int)Math.pow(2, bitIdx)) >> bitIdx;

            // if it doesn't match the search criteria, remove it
            if (bit != value){
                ret_list.remove(i); // since we're going backwards through the list, we can remove by idx
            }
        }
        
        return ret_list;

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

    public static List<Integer> toIntList(String[] bin_nums){
        List<Integer> intList = new ArrayList<Integer>();

        for (String str_bin_num : bin_nums) {
            int bin_num = Integer.parseInt(str_bin_num, 2);
            intList.add(bin_num);
        }
        return intList;
    }

}