import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
--- Day 2: Dive! ---
Now, you need to figure out how to pilot this thing.

It seems like the submarine can take a series of commands like forward 1, down 2, or up 3:

forward X increases the horizontal position by X units.
down X increases the depth by X units.
up X decreases the depth by X units.
Note that since you're on a submarine, down and up affect your depth, and so they have the opposite result of what you might expect.

The submarine seems to already have a planned course (your puzzle input). You should probably figure out where it's going. For example:

forward 5
down 5
forward 8
up 3
down 8
forward 2
Your horizontal position and depth both start at 0. The steps above would then modify them as follows:

forward 5 adds 5 to your horizontal position, a total of 5.
down 5 adds 5 to your depth, resulting in a value of 5.
forward 8 adds 8 to your horizontal position, a total of 13.
up 3 decreases your depth by 3, resulting in a value of 2.
down 8 adds 8 to your depth, resulting in a value of 10.
forward 2 adds 2 to your horizontal position, a total of 15.
After following these instructions, you would have a horizontal position of 15 and 
a depth of 10. (Multiplying these together produces 150.)

Calculate the horizontal position and depth you would have after following the planned course. 
What do you get if you multiply your final horizontal position by your final depth?

Your puzzle answer was 2027977.

The first half of this puzzle is complete! It provides one gold star: *

--- Part Two ---
Based on your calculations, the planned course doesn't seem to make any sense. 
You find the submarine manual and discover that the process is actually slightly more complicated.

In addition to horizontal position and depth, you'll also need to track a third value, 
aim, which also starts at 0. The commands also mean something entirely different than you first thought:

down X increases your aim by X units.
up X decreases your aim by X units.
forward X does two things:
It increases your horizontal position by X units.
It increases your depth by your aim multiplied by X.
Again note that since you're on a submarine, down and up do the opposite 
of what you might expect: "down" means aiming in the positive direction.

Now, the above example does something different:

forward 5 adds 5 to your horizontal position, a total of 5. Because your aim is 0, your depth does not change.
down 5 adds 5 to your aim, resulting in a value of 5.
forward 8 adds 8 to your horizontal position, a total of 13. Because your aim is 5, your depth increases by 8*5=40.
up 3 decreases your aim by 3, resulting in a value of 2.
down 8 adds 8 to your aim, resulting in a value of 10.
forward 2 adds 2 to your horizontal position, a total of 15. Because your aim is 10, 
your depth increases by 2*10=20 to a total of 60.
After following these new instructions, you would have a horizontal 
position of 15 and a depth of 60. (Multiplying these produces 900.)

Using this new interpretation of the commands, calculate the horizontal position and depth you 
would have after following the planned course. What do you get if you multiply your final 
horizontal position by your final depth?

answer: 1903644897
 */

public class Main{

    static int horiz = 0, depth = 0, aim = 0;

    public static void main(String[] args) {

        String file = "puzzle_input.txt";
        String[] lines = FileParser.getLines(file);


        // =========================================== PART ONE

        for (String command : lines) {
            process_command(command);
        }
        System.out.printf("\n=========\n" + 
                            "PART ONE" + 
                            "\n=========\n" + 
                            "horiz = %d, depth = %d\n" +
                            "product = %d\n", 
                            horiz, depth, horiz * depth);

        // ============================================ PART TWO

        horiz = 0;
        depth = 0;
        aim = 0;
        for (String command : lines){
            process_command_part2(command);
        }
        System.out.printf("\n=========\n" + 
                            "PART TWO" + 
                            "\n=========\n" + 
                            "horiz = %d, depth = %d\n" +
                            "product = %d\n", 
                            horiz, depth, horiz * depth);

    }


    private static void process_command(String command){
        // split the command into the word and the amount
        String instruction = command.split(" ")[0];
        int amount = Integer.parseInt(command.split(" ")[1]);
        
        switch (instruction){
            case "forward": 
                horiz += amount;
                break;
            case "down": 
                depth += amount;
                break;
            case "up":
                depth -= amount;
                break;
            default:
                break;
        }
    }


    /**
     * 
        down X increases your aim by X units.
        up X decreases your aim by X units.
        forward X does two things:
        It increases your horizontal position by X units.
        It increases your depth by your aim multiplied by X.
        Again note that since you're on a submarine, down and up do the opposite 
        of what you might expect: "down" means aiming in the positive direction.
     *
     */
    private static void process_command_part2(String command){
        // split the command into the word and the amount
        String instruction = command.split(" ")[0];
        int amount = Integer.parseInt(command.split(" ")[1]);
        
        switch (instruction){
            case "forward": 
                horiz += amount;
                depth += aim * amount;
                break;
            case "down": 
                aim += amount;
                break;
            case "up":
                aim -= amount;
                break;
            default:
                break;
        }
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

}