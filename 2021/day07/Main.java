import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import StdStats.StdStats;


public class Main{
    public static void main(String[] args) {
        String file = "test.txt";

        List<Double> positions = FileParser.getVals(file);     
        double[] arr = list_to_array(positions);

        double std_dev = StdStats.stddev(arr);
        double mean = StdStats.mean(arr);
        System.out.printf("mean: %f, stddev = %f\n", mean, std_dev);

        // clean the list of positions, removing those outside of 1 stddev
        Iterator<Double> iter = positions.iterator();
        while (iter.hasNext()) {
            Double num = iter.next();
            if (num > mean + (2*std_dev) ||
                num < mean - (2*std_dev)){
                    iter.remove();
                }
        }

        // find the new mean... that will be the place to go
        arr = list_to_array(positions);

        std_dev = StdStats.stddev(arr);
        mean = StdStats.mean(arr);
        int dest = (int)Math.round(mean);
        
        // get all the positions again
        positions = FileParser.getVals(file);

        // now we have to "move" the submarines to that location
        long fuel_cost = 0;
        for (int i = 0; i < positions.size(); i++){
            if (positions.get(i)== dest)
                continue;    // don't move the ones that are at the destination
            fuel_cost += ((int)Math.abs(positions.get(i) - dest));
            positions.remove(i);
            positions.add(i, (double)dest);
        }

        System.out.println(fuel_cost);
        // note: 328659 is too high
    }


    private static double[] list_to_array(List<Double> list){ 
        double[] arr = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);  // StdStats needs an array, not a List
        }
        return arr;
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
     * numbers separated by commas and returns a List of Doubles.
     */
    public static List<Double> getVals(String filename){
        List<Double> dbl_list = new ArrayList<>();
        File input = new File(filename);
        try {
            Scanner sc = new Scanner(input).useDelimiter(",");
            while (sc.hasNext()){
                Double num = Double.parseDouble(sc.next());
                dbl_list.add(num);
            }
            sc.close();
        } catch (FileNotFoundException e) {}

        return dbl_list;
    }
}
