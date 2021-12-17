import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Notes to self: 
 * 
 * The puzzle input is small enough (100) that I can create a class called DumboOctopus.
 * 
 * Also, I should create a hashmap for all the octopi (the keys will be the locations and the
 * values will be their values). I will go through the map and apply the 'nextStep()' method 
 * to all the ones that have (> 9) energy. Then I will mark them as 'flashed'. Then I'll go through 
 * the list again and do the same thing until none of them flash. So I need a static method inside
 * the Main class that does this recursion..
 */


public class Main{
    static long flashes = 0; // number of flashes
    static Map<Point, DumboOctopus> octopi = new HashMap<>();

    public static void main(String[] args) {
        String file = "puzzle_input.txt";
        List<DumboOctopus> octo_list = FileParser.getOctopi(file);
        int numSteps = 100;
        for (DumboOctopus dumboOctopus : octo_list) {
            octopi.put(dumboOctopus.location, dumboOctopus);
        }
        for (int i = 1; i <= numSteps; i++) {
            next_step();       
        }
        // print_octopi();
        System.out.printf("After %d steps, there have been a total of %d flashes.\n", numSteps, flashes);

        // Part Two
        // reset the octopi to the start (step 0)
        octopi.clear();
        octo_list = FileParser.getOctopi(file);
        for (DumboOctopus dumboOctopus : octo_list) {
            octopi.put(dumboOctopus.location, dumboOctopus);
        }
        int step = 1;
        while(true){
            if(next_step()){    // look for first time they flash simultaneously
                System.out.printf("All octopi flashed simultaneously on step %d\n", step);  
                break;
            }
            step++;
        }
    }

    /**
     * Iterates over all the octopi and increments their charges. If there are any flashes, it 
     * calls the recursive function `flash(DumboOctopus)`. Returns true if 
     * all octopi flashed at the same time (false otherwise).
     */
    private static boolean next_step(){
        for (DumboOctopus dumboOctopus : octopi.values()) {
            if (dumboOctopus.incrCharge()){
                flash(dumboOctopus);
                flashes++;
            }
        }
        int octopi_flash_count = 0; // how many octopi flashed during this step
        for (DumboOctopus dumboOctopus : octopi.values()){
            if (dumboOctopus.flashed)
                octopi_flash_count++;
            dumboOctopus.flashed = false; // reset the 'flashed' attributes AFTER the step
        }
        if (octopi_flash_count == octopi.values().size())
            return true;
        else
            return false;
    }

    /**
     * Iterates through the neighbors of the octopus that flashed and increments THEIR 
     * charges, and if any of those neighbors flashed, then increment THEIR neighbors (recursion)
     */
    private static void flash(DumboOctopus octopus){
        for (Point p : octopus.neighborLocations){
            if (octopi.get(p).incrCharge()){
                flash(octopi.get(p));
                flashes++;
            }
        }
    }

    /**
     * Prints the charges of the octopi in their correct locations... (for debugging purposes)
     */
    private static void print_octopi(){
        int[][] charges = new int[10][10];
        for (DumboOctopus dumboOctopus : octopi.values()){
            charges[(int)dumboOctopus.location.getY()][(int)dumboOctopus.location.getX()] = dumboOctopus.charge;
        }

        for (int[] charge_row : charges){
            for (int charge : charge_row)
                System.out.printf("%d", charge);
            System.out.printf("\n");
        }
        System.out.printf("\n");
    }

}

class DumboOctopus{
    boolean flashed;
    Point location;
    int charge;
    List<Point> neighborLocations = new ArrayList<>();

    public DumboOctopus(int charge, int x, int y){
        this.charge = charge;
        this.location = new Point(x, y);
        this.flashed = false;
        this.neighborLocations = add_neighbors(x, y);
    }

    private List<Point> add_neighbors(int x, int y){ 
        List<Point> ret_list = new ArrayList<>();       
        // add all possible neighbors (all 8, even if they don't exist)
        ret_list.add(new Point(x - 1, y - 1));
        ret_list.add(new Point(x    , y - 1));
        ret_list.add(new Point(x + 1, y - 1));
        ret_list.add(new Point(x - 1, y    ));
        ret_list.add(new Point(x + 1, y    ));
        ret_list.add(new Point(x - 1, y + 1));
        ret_list.add(new Point(x    , y + 1));
        ret_list.add(new Point(x + 1, y + 1));

        // remove the neighbors that don't exist (the ones in the negative row / column,
        // and the ones in the 11th row / column)
        for (int i = ret_list.size() - 1; i >= 0; i--) {
             if (ret_list.get(i).getX() < 0 ||
                 ret_list.get(i).getY() < 0 ||
                 ret_list.get(i).getX() > 9 ||
                 ret_list.get(i).getY() > 9){
                    ret_list.remove(i);
                 }
        }

        return ret_list;
    }


    /**
     * Increment the octopus's charge (only if <= 9 and not already flashed)
     * Returns true if it flashed, returns false if it didn't flash
     */
    public boolean incrCharge(){
        if (!flashed)
            charge++;
        if (charge > 9 && !flashed){
            charge = 0;
            flashed = true;
            return true;
        }
        return false;
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

    public static List<DumboOctopus> getOctopi(String filename){
        String[] lines = getLines(filename);
        List<DumboOctopus> octopi = new ArrayList<DumboOctopus>();
        for (int row = 0; row < lines.length; row++) {
            char[] nums_in_line = lines[row].toCharArray();
            for (int col = 0; col < nums_in_line.length; col++) {
                DumboOctopus octo = new DumboOctopus(Character.getNumericValue(nums_in_line[col]), col, row);
                octopi.add(octo);
            }
        }
        return octopi;
    }

}
