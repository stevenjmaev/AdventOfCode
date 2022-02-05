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
 * 
 * 
 * 
 * 
 * 
 * NOTE: My current implementation will perform 40 steps in more than 18,361,796,381 years !!!!
 * 
 * Note: The problem is that I'm iterating over the entire polymer!!
 * At the end of the day, all I care about is the NUMBER of each element that exists.
 * Sooo....
 * I could store the number of pairs at the end of a step (e.g. There are 15 'CH' pairs) (do this in a HashMap)
 * Then, on the next step, I know how many 'B's to add. 
 * 
 * 
 * 
 * 
 */

public class Main{
    public static void main(String[] args) {
        String file = "puzzle_input.txt";
        String[] lines = FileParser.getLines(file);

        // parse the set of insertion rules
        Map<String, Character> rules = new HashMap<>();
        for (int i = 2; i < lines.length; i++){
            Scanner sc = new Scanner(lines[i]).useDelimiter(" -> ");
            rules.put(sc.next(), sc.next().charAt(0));
            sc.close();
        }

        // parse the polymer and do the following:
        // 1. Count how many of each element
        // 2. Log any rule pairs that occur
        Map<Character, Long> element_count = new HashMap<>();
        Map<String, Long> rule_pairs = new HashMap<>();
        for (int i = 0; i < lines[0].length(); i++){                        // increment pair count
            element_count.merge(lines[0].charAt(i), (long)1, Long::sum);    // count the element occurances
            if (i == lines[0].length() - 1) continue;                       // the last element in the polymer
            String pair = String.format("%c%c", lines[0].charAt(i), lines[0].charAt(i + 1));
            if (rules.containsKey(pair)){
                rule_pairs.merge(pair, (long)1, Long::sum);                 // increment the count for that pair
            }
        }


        // Do 10 (or 40 for part 2) insertion steps
        for (int i = 0; i < 40; i++) {         
            long startTime = System.currentTimeMillis();
            do_insertion_step(element_count, rules, rule_pairs); 
            long endTime = System.currentTimeMillis();
            System.out.printf("Step %d took %d milliseconds\n", i + 1, endTime - startTime);
        }

        // (debug print)
        // print_status(element_count, rule_pairs);

        Long min = Collections.min(element_count.values());
        Long max = Collections.max(element_count.values());
        System.out.printf("The most common element has %d occurances.\n", max);
        System.out.printf("The least common element has %d occurances.\n", min);
        System.out.printf("%d - %d = %d\n", max, min, max - min);
        
    }

    /**
     * Function that takes in 3 HashMaps:
     *      element_count:  stores the occurence count of each element
     *      rules:          the record of which element to insert when certain pairs are present
     *      rule_pairs:     pairs that exist in the polymer that will create create a new pair in the next step
     */
    private static void do_insertion_step(  Map<Character, Long> element_count, 
                                            Map<String, Character> rules, 
                                            Map<String, Long> rule_pairs)
    {
        Map<String, Long> new_rule_pairs = new HashMap<>();
        
        // apply the rules to the rule_pairs and add any new_rule_pairs
        for (String pair : rule_pairs.keySet()) {
            if(rule_pairs.get(pair) == 0) continue;
            
            long num_occurances = rule_pairs.get(pair);
            char new_element = rules.get(pair);
            element_count.merge(new_element, num_occurances, Long::sum);

            // figure out if the 2 new pairs that are generated should be included in rule_pairs
            // e.g.   for rules  CH -> B, CB -> H, BH -> H
            //        "CH" then becomes "CBH", which then contains "CB" and "BH" 
            //        both of these new pairs are included in the rules.keyset() !!!
            String new_pair1 = String.format("%c%c", pair.charAt(0), new_element);  // find the new pair
            String new_pair2 = String.format("%c%c", new_element, pair.charAt(1));  // find the new pair
            if (rules.keySet().contains(new_pair1)) new_rule_pairs.merge(new_pair1, num_occurances, Long::sum);  // add to the list of important new pairs
            if (rules.keySet().contains(new_pair2)) new_rule_pairs.merge(new_pair2, num_occurances, Long::sum);  // add to the list of important new pairs
            rule_pairs.merge(pair, -num_occurances, Long::sum);                     // take those occurances away from rule_pairs
        }

        // after we take care of the existing rule pairs, add the new ones for the next step...
        for (String pair : new_rule_pairs.keySet()){
            rule_pairs.merge(pair, new_rule_pairs.get(pair), Long::sum);
        }
    }

    private static void print_status(Map<Character, Long> element_count, Map<String, Long> rule_pairs){
        System.out.println("rule_pairs:\n===========");
        for (String pair : rule_pairs.keySet()) {
            System.out.printf("%s : %d\n", pair, rule_pairs.get(pair));
        }
        System.out.println("element count:\n===========");
        for (Character c : element_count.keySet()) {
            System.out.printf("%c : %d\n", c, element_count.get(c));
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
