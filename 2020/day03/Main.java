import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
--- Day 3: Toboggan Trajectory ---
With the toboggan login problems resolved, you set off toward the airport. 
While travel by toboggan might be easy, it's certainly not safe: there's very 
minimal steering and the area is covered in trees. You'll need to see which angles
will take you near the fewest trees.

Due to the local geology, trees in this area only grow on exact integer coordinates 
in a grid. You make a map (your puzzle input) of the open squares (.) and trees (#) you can see. For example:

..##.......
#...#...#..
.#....#..#.
..#.#...#.#
.#...##..#.
..#.##.....
.#.#.#....#
.#........#
#.##...#...
#...##....#
.#..#...#.#
These aren't the only trees, though; due to something you read about once involving 
arboreal genetics and biome stability, the same pattern repeats to the right many times:

..##.........##.........##.........##.........##.........##.......  --->
#...#...#..#...#...#..#...#...#..#...#...#..#...#...#..#...#...#..
.#....#..#..#....#..#..#....#..#..#....#..#..#....#..#..#....#..#.
..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#
.#...##..#..#...##..#..#...##..#..#...##..#..#...##..#..#...##..#.
..#.##.......#.##.......#.##.......#.##.......#.##.......#.##.....  --->
.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#
.#........#.#........#.#........#.#........#.#........#.#........#
#.##...#...#.##...#...#.##...#...#.##...#...#.##...#...#.##...#...
#...##....##...##....##...##....##...##....##...##....##...##....#
.#..#...#.#.#..#...#.#.#..#...#.#.#..#...#.#.#..#...#.#.#..#...#.#  --->
You start on the open square (.) in the top-left corner and need to reach the bottom (below the bottom-most row on your map).

The toboggan can only follow a few specific slopes (you opted for a cheaper model
that prefers rational numbers); start by counting all the trees you would encounter for the slope right 3, down 1:

From your starting position at the top-left, check the position that is 
right 3 and down 1. Then, check the position that is right 3 and down 1 from there,
and so on until you go past the bottom of the map.

The locations you'd check in the above example are marked here with O where 
there was an open square and X where there was a tree:

..##.........##.........##.........##.........##.........##.......  --->
#..O#...#..#...#...#..#...#...#..#...#...#..#...#...#..#...#...#..
.#....X..#..#....#..#..#....#..#..#....#..#..#....#..#..#....#..#.
..#.#...#O#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#..#.#...#.#
.#...##..#..X...##..#..#...##..#..#...##..#..#...##..#..#...##..#.
..#.##.......#.X#.......#.##.......#.##.......#.##.......#.##.....  --->
.#.#.#....#.#.#.#.O..#.#.#.#....#.#.#.#....#.#.#.#....#.#.#.#....#
.#........#.#........X.#........#.#........#.#........#.#........#
#.##...#...#.##...#...#.X#...#...#.##...#...#.##...#...#.##...#...
#...##....##...##....##...#X....##...##....##...##....##...##....#
.#..#...#.#.#..#...#.#.#..#...X.#.#..#...#.#.#..#...#.#.#..#...#.#  --->
In this example, traversing the map using this slope would cause you to encounter 7 trees.

Starting at the top-left corner of your map and following a slope of right 3 and down 1, how many trees would you encounter?
Your puzzle answer was 145.

The first half of this puzzle is complete! It provides one gold star: *

--- Part Two ---
Time to check the rest of the slopes - you need to minimize the probability of a sudden arboreal stop, after all.

Determine the number of trees you would encounter if, for each of the following slopes, 
you start at the top-left corner and traverse the map all the way to the bottom:

Right 1, down 1.
Right 3, down 1. (This is the slope you already checked.)
Right 5, down 1.
Right 7, down 1.
Right 1, down 2.
In the above example, these slopes would find 2, 7, 3, 4, and 2 tree(s) respectively; 
multiplied together, these produce the answer 336.

What do you get if you multiply together the number of trees encountered on each of the listed slopes?
answer: 3424528800
 */


/**
 * Contains helper functions for importing data from the 
 * puzzle input text file.
 */
class FileReader{

    /**
     * Takes in the name of a text file that is in the root directory
     * and returns the number of lines it contains. 
     */
    public static int countLines(String filename){
        Path path = Paths.get(filename);
        int arr_size = 0;

        try {
            arr_size = (int)Files.lines(path).count();
        } catch (IOException e1) {
            e1.printStackTrace();
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


public class Main{
    public static void main(String[] args) {

        String[] input_arr = FileReader.getLines("puzzle_input.txt");
        int width = input_arr[0].length();
        int height = Array.getLength(input_arr);
        

        int[][] slopes = {  // in {slope_x, slope_y} format
            {1, 1},
            {3, 1},
            {5, 1},
            {7, 1},
            {1, 2}
        };

        int[] trees_per_slope = new int[Array.getLength(slopes)];


        int idx = 0;
        for (int[] slope : slopes){
            trees_per_slope[idx] = 0;
            int slope_x = slope[0];
            int slope_y = slope[1];

            // keep track of where I am in width and height
            int posX = 1; 
            int posY = 1; 

            do {
                 // go to next position
                 posX += slope_x;
                 posY += slope_y;
 
                 // reset width location (due to repeating pattern)
                 if (posX > width){
                     posX = posX - width;
                 }
 
                 // log the character found at that position
                 char encountered = input_arr[posY - 1].charAt(posX - 1);
                 if (encountered == '#') trees_per_slope[idx]++;
 
                //  if (slope_y == 2) System.out.printf("encountered = %c\n", encountered);
            } while (posY < height);
            
            System.out.printf("number of trees encountered for slope {%d, %d}: %d\n", slope_x, slope_y, trees_per_slope[idx]);
            idx++;
        }


        // find product of numbers
        long product = 1;
        for (int trees : trees_per_slope) {
            product *= trees;
        }

        System.out.printf("product of trees encontered = %d", product);
    }
}