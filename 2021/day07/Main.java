import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Note to self:
 * Maybe try to calculate the IQR and exclude outliers from that..
 */

public class Main{
    public static void main(String[] args) {
        String file = "puzzle_input.txt";

        List<Integer> positions = FileParser.getVals(file);  

        // find median of numbers
        Collections.sort(positions);
        int dest = positions.get(positions.size() / 2);  
        
        // now we have to "move" the submarines to that location
        long fuel_cost = 0;
        for (int i = 0; i < positions.size(); i++){
            if (positions.get(i)== dest)
                continue;    // don't move the ones that are at the destination
            fuel_cost += (Math.abs(positions.get(i) - dest));
            positions.remove(i);
            positions.add(i, dest);
        }

        System.out.println("Part One\n========");
        System.out.printf("destination: %d\n", dest);
        System.out.printf("fuel cost: %d\n", fuel_cost);


        positions = FileParser.getVals(file);
        // find mean of numbers
        double avg = 0;
        for (Integer num : positions)
            avg += num;
        avg /= positions.size();
        dest = (int) avg;  
        
        // now we have to "move" the submarines to that location
        fuel_cost = 0;
        for (int i = 0; i < positions.size(); i++){
            if (positions.get(i)== dest)
                continue;    // don't move the ones that are at the destination

            // create an array of fuel costs for that move
            //   note: this could also be done by using "triangular numbers" (factorials but for addition)
            int distance = Math.abs(positions.get(i) - dest);
            int[] distance_cost_vals = new int[distance];
            for (int j = 0; j < distance_cost_vals.length; j++)
                distance_cost_vals[j] = j + 1;

            // add the sum of this array to the fuel_cost
            fuel_cost += IntStream.of(distance_cost_vals).sum();
            positions.remove(i);
            positions.add(i, dest);
        }
        
        System.out.println("\nPart Two\n========");
        System.out.printf("destination: %d\n", dest);
        System.out.printf("fuel cost: %d\n", fuel_cost);

        // 343165 is too low


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

    // /**
    //  * Takes in the name of a text file that has a single line in it with
    //  * numbers separated by commas and returns an array of Doubles.
    //  */
    // public static double[] getVals(String filename){
    //     List<Double> dbl_list = new ArrayList<>();
    //     File input = new File(filename);
    //     try {
    //         Scanner sc = new Scanner(input).useDelimiter(",");
    //         while (sc.hasNext()){
    //             Double num = Double.parseDouble(sc.next());
    //             dbl_list.add(num);
    //         }
    //         sc.close();
    //     } catch (FileNotFoundException e) {}
    //     double[] positions = new double[dbl_list.size()];
    //     System.arraycopy(dbl_list.toArray(), 0, positions, 0, dbl_list.size());

    //     return positions;
    // }

    /**
     * Takes in the name of a text file that has a single line in it with
     * numbers separated by commas and returns a List of Integers.
     */
    public static List<Integer> getVals(String filename){
        List<Integer> dbl_list = new ArrayList<>();
        File input = new File(filename);
        try {
            Scanner sc = new Scanner(input).useDelimiter(",");
            while (sc.hasNext()){
                int num = Integer.parseInt(sc.next());
                dbl_list.add(num);
            }
            sc.close();
        } catch (FileNotFoundException e) {}

        return dbl_list;
    }
}
