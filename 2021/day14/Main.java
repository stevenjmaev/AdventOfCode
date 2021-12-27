import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
 * Initial thoughts:
 * 
 * Since everything has to happen simultaneously, I need to iterate through
 * the polymer and find the indeces of all the pairs that exist in the 
 * insertion rules.
 * 
 * Then, I will insert the correct character (polymer) according to that rule
 * into the correct index using StringBuilder.insert()
 */

public class Main{
    public static void main(String[] args) {
        String file = "puzzle_input.txt";
        String[] lines = FileParser.getLines(file);

        // parse the polymer template
        String polymer = lines[0];
        Map<Character, Long> element_occurances = new HashMap<>();

        // parse the set of insertion rules
        Map<String, Character> rules = new HashMap<>();
        for (int i = 2; i < lines.length; i++){
            Scanner sc = new Scanner(lines[i]).useDelimiter(" -> ");
            rules.put(sc.next(), sc.next().charAt(0));
            sc.close();
        }

        // Do 10 (or 40 for part 2) insertion steps
        for (int i = 0; i < 40; i++) {         
            long startTime = System.currentTimeMillis();
            polymer = do_insertion_step(polymer, rules); 
            long endTime = System.currentTimeMillis();
            System.out.printf("Step %d took %d milliseconds\n", i + 1, endTime - startTime);
        }

        // count the occurances of each element
        for (int i = 0; i < polymer.length(); i++) {
            char element = polymer.charAt(i);
            if (!element_occurances.containsKey(element)){
                element_occurances.put(
                    element, 
                    count_occurance(polymer, element)
                    );
            }
        }

        // find the elements that have the most and least occurances
        char max_element = '.', min_element = '.';
        long max = 0, min = polymer.length();
        for (char c : element_occurances.keySet()){
            long occurance_count = element_occurances.get(c);
            if (occurance_count > max){
                max = occurance_count;
                max_element = c;
            }
            if (occurance_count < min){
                min = occurance_count;
                min_element = c;
            }
        }

        System.out.printf("The most common element is '%c' with %d occurances.\n", max_element, max);
        System.out.printf("The least common element is '%c' with %d occurances.\n", min_element, min);
        System.out.printf("%d - %d = %d\n", max, min, max - min);
        

        

        
    }

    private static String do_insertion_step(String polymer, Map<String, Character> rules){
        StringBuilder sb = new StringBuilder(polymer);
        // scan the polymer for any matches to the rules and log the indeces that it happens
        List<Integer> pair_matches = new ArrayList<>();
        for (int i = 0; i < polymer.length(); i++){
            if (i == polymer.length() - 1)
                continue;
            if (rules.containsKey(polymer.substring(i, i+2))){
                pair_matches.add(i);
            }
        }

        // sort the list do the insertions "backwards"
        //    note: this will prevent index mutilation
        pair_matches.sort(null);
        for (int i = pair_matches.size() - 1; i >= 0; i--){
            int idx = pair_matches.get(i);
            sb.insert(idx + 1, rules.get(polymer.substring(idx, idx + 2)));
        }

        return sb.toString();
    }

    private static long count_occurance(String polymer, char c){
        long count = 0;

        for (int i = 0; i < polymer.length(); i++){
            if (polymer.charAt(i) == c)
                count++;
        }
        return count;
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
