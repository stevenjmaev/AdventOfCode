import java.awt.Point;
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
 * Notes to self: 
 * For part 2, a "basin" is simply just an area that has a low point in it and is 
 * bounded by 9's 
 * 
 * To find all locations in the basin, I probably will start at a low point, then traverse 
 * forward and backward through the row until I find a 9. Then, for each location in the row 
 * that was found, I will start THERE and look through that column. For the ones in that column,
 * I'll then traverse its row and look for '9' boundaries, and so on..
 * 
 *    This might take a bit of recursion :,(
 */


public class Main{
    static List<Point> low_point_locations = new ArrayList<>();
    public static void main(String[] args) {
        String file = "puzzle_input.txt";
        
        int[][] map = FileParser.getMap(file);
        long risk_level_sum = 0;

        for (int row = 1; row < map.length - 1; row++) {
            for (int col = 1; col < map[row].length - 1; col++) {
                if (map[row][col] < map[row - 1][col] &&
                    map[row][col] < map[row + 1][col] &&
                    map[row][col] < map[row][col - 1] &&
                    map[row][col] < map[row][col + 1]){
                        low_point_locations.add(new Point(col, row));
                        risk_level_sum += map[row][col] + 1;
                    }
            }
        }
        System.out.println("Part One\n==============");
        System.out.printf("The sum of all risk levels is: %d\n\n", risk_level_sum);
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


    public static int[][] getMap(String filename){
        String[] lines = getLines(filename);
        // pad with 9's (in first row, last row, first col, last col)
        String[] padded = new String[lines.length + 2];

        // add first and last row of 9's
        padded[0] = "";
        padded[lines.length + 1] = "";
        for (int i = 0; i < lines[0].length() + 2; i++) {
            padded[0] += "9";
            padded[lines.length + 1] += "9";
        }

        // add first and last column of 9's
        for (int i = 1; i < lines.length + 1; i++) {
            padded[i] = "9";
            padded[i] = padded[i].concat(lines[i - 1]);
            padded[i] = padded[i].concat("9");
        }

        int[][] map = new int[lines.length + 2][lines[0].length() + 2];

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = Character.getNumericValue(padded[i].toCharArray()[j]);
            }
        }

        return map;
    }
}
