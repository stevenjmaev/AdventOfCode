import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.awt.*;

import org.javatuples.*;


/**
--- Day 5: Hydrothermal Venture ---
You come across a field of hydrothermal vents on the ocean floor! 
These vents constantly produce large, opaque clouds, so it would be best to avoid them if possible.

They tend to form in lines; the submarine helpfully produces a list of nearby 
lines of vents (your puzzle input) for you to review. For example:

0,9 -> 5,9
8,0 -> 0,8
9,4 -> 3,4
2,2 -> 2,1
7,0 -> 7,4
6,4 -> 2,0
0,9 -> 2,9
3,4 -> 1,4
0,0 -> 8,8
5,5 -> 8,2
Each line of vents is given as a line segment in the format x1,y1 -> x2,y2 
where x1,y1 are the coordinates of one end the line segment and x2,y2 are the coordinates of the other end. 
These line segments include the points at both ends. In other words:

An entry like 1,1 -> 1,3 covers points 1,1, 1,2, and 1,3.
An entry like 9,7 -> 7,7 covers points 9,7, 8,7, and 7,7.
For now, only consider horizontal and vertical lines: lines where either x1 = x2 or y1 = y2.

So, the horizontal and vertical lines from the above list would produce the following diagram:

.......1..
..1....1..
..1....1..
.......1..
.112111211
..........
..........
..........
..........
222111....
In this diagram, the top left corner is 0,0 and the bottom right corner is 9,9. Each position 
is shown as the number of lines which cover that point or . if no line covers that point. 
The top-left pair of 1s, for example, comes from 2,2 -> 2,1; the very bottom row is formed 
by the overlapping lines 0,9 -> 5,9 and 0,9 -> 2,9.
y
To avoid the most dangerous areas, you need to determine the number of points where at least 
two lines overlap. In the above example, this is anwhere in the diagram with a 2 or larger - a total of 5 points.

Consider only horizontal and vertical lines. At how many points do at least two lines overlap?

Your puzzle answer was 4826.

The first half of this puzzle is complete! It provides one gold star: *

--- Part Two ---
Unfortunately, considering only horizontal and vertical lines doesn't give you the full picture; you need to also consider diagonal lines.

Because of the limits of the hydrothermal vent mapping system, the lines in your list will only ever be horizontal, 
vertical, or a diagonal line at exactly 45 degrees. In other words:

An entry like 1,1 -> 3,3 covers points 1,1, 2,2, and 3,3.
An entry like 9,7 -> 7,9 covers points 9,7, 8,8, and 7,9.
Considering all lines from the above example would now produce the following diagram:

1.1....11.
.111...2..
..2.1.111.
...1.2.2..
.112313211
...1.2....
..1...1...
.1.....1..
1.......1.
222111....
You still need to determine the number of points where at least two lines overlap. In the above example, 
this is still anywhere in the diagram with a 2 or larger - now a total of 12 points.

Consider all of the lines. At how many points do at least two lines overlap?

answer: 16793
 */

public class Main{
    public static void main(String[] args) {
        String file = "puzzle_input.txt";
        String[] lines = FileParser.getLines(file);
        
        VentMap vm = new VentMap(lines, true);
        System.out.printf("number of overlaps: %d", vm.countIntersections(2));
    }
}





class VentLine{
    Point start, end;

    /**
     * Parses a line description of the form
     * x1,y1 -> x2,y2
     * and generates a start and end point
     */
    public VentLine(String line_description){
        // format: x1,y1 -> x2,y2
        Scanner sc = new Scanner(line_description).useDelimiter(",| -> ");
        int x = sc.nextInt();
        int y = sc.nextInt();
        start = new Point(x, y);

        x = sc.nextInt();
        y = sc.nextInt();
        end = new Point(x,y);
    }

}

class VentMap{
    int width, height;
    int[][] map;
    boolean considerDiagonals;

    /**
     * Creates a map using an array of line descriptors of the form:
     * x1,y1 -> x2,y2
     */
    public VentMap(String[] input_lines, boolean diagonals){
        this.considerDiagonals = diagonals;
        List<VentLine> ventLines = new ArrayList<>();
        for (String line_description : input_lines) {
            VentLine vl = new VentLine(line_description);
            ventLines.add(vl);
        }

        // find the max X and Y so that we can set the size of the map
        width = 0;
        height = 0;
        find_map_size(ventLines);
        
        map = new int[height][width];
        for (int[] row : map) {
            Arrays.fill(row, 0);
        }

        for (VentLine ventLine : ventLines) {
            draw_line(ventLine);
        }
    }


    /**
     * Iterates over all the ventLines and looks for the maximum
     * for X and for Y to determine the width and height, respectively
     */
    private void find_map_size(List<VentLine> ventLines){
        for (VentLine ventLine : ventLines) {
            if ((int)ventLine.start.getX() > width)
                width = (int)ventLine.start.getX();
            if ((int)ventLine.end.getX() > width)
                width = (int)ventLine.end.getX();
            
            if ((int)ventLine.start.getY() > height)
                height = (int)ventLine.start.getY();
            if ((int)ventLine.end.getY() > height)
                height = (int)ventLine.end.getY();
        }
        
        // convert from indeces to size
        width += 1;
        height += 1;
    }

    /**
     * Adds the given VentLine to the VentMap, incrementing 
     * any spaces on the map that it falls on.
     */
    private void draw_line(VentLine line){
        int x = (int)line.start.getX();
        int y = (int)line.start.getY();
        if (line.start.getX() == line.end.getX()){
            // Draw a vertical line
            int startY = (int)line.start.getY();
            int endY = (int)line.end.getY();
            if (endY > startY){
                for (y = startY; y <= endY; y++){
                        map[y][x]++;
                    }
            }
            else{
                for (y = startY; y >= endY; y--){
                    map[y][x]++;
                }
            }
            
        }
        else if (line.start.getY() == line.end.getY()){
            // Draw a horizontal line
            int startX = (int)line.start.getX();
            int endX = (int)line.end.getX();
            if (endX > startX){
                for (x = startX; x <= endX; x++){
                        map[y][x]++;
                    }
            }
            else{
                for (x = startX; x >= endX; x--){
                    map[y][x]++;
                }
            }
        }
        else{
            if (!considerDiagonals)
                return;
            // Draw 45deg diagonal line
            int startX = (int)line.start.getX();
            int startY = (int)line.start.getY();
            int endX = (int)line.end.getX();
            int endY = (int)line.end.getY();

            // get the 'slope' for dx and dy (both either -1 or +1)
            int dx = Integer.signum(endX - startX);
            int dy = Integer.signum(endY - startY);
            
            while (x != endX || y != endY){
                map[y][x]++;
                x += dx;
                y += dy;
                if (x == endX && y == endY)
                    map[y][x]++;
            }

        }
    }

    /**
     * Prints the map... Used for debugging purposes only!
     */
    public void print(){
        for (int row = 0; row < height; row++){
            for (int col = 0; col < width; col++){
                System.out.printf("%d\t", map[row][col]);
            }
            System.out.printf("\n");
        }
    }

    /**
     * Iterates over the map and counts how many intersections there are,
     * allowing the user to define a `overlap_threshold` to determine what
     * counts as an "intersection"
     */
    public int countIntersections(int overlap_threshold){
        int count = 0;
        for (int row = 0; row < height; row++){
            for (int col = 0; col < width; col++){
                if (map[row][col] >= overlap_threshold)
                    count++;
            }
        }
        return count;
    }
}


// I should work on my java packaging know-how and put this in an external file / .jar-package
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
