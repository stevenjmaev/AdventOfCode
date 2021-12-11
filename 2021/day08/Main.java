import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Notes:
 * Segment count for each number:
 *   0: 6
 *   1: 2
 *   2: 5
 *   3: 5
 *   4: 4
 *   5: 5
 *   6: 6
 *   7: 3
 *   8: 7
 *   9: 6
 * 
 *   ordered by length: 1, 7, 4,   2, 3, 5,   0, 6, 9,   8
 * 
 * abfg
 *   - numbers that have unique number of segments: 
 *      1 (2 segs), 7 (3 segs), 4 (4 segs), 8 (7 segs)
 * 
 *      top         &&&&&&
 *                  &    &
 *      top_left    &    &  top_right
 *                  &    &
 *      center      &&&&&&
 *                  &    &
 *      bot_left    &    &  bot_right
 *                  &    &
 *      bottom      &&&&&&
 * 
 * DECODING METHOD
 * To find top -> 7's segs set_subtract 1's segs
 *   
 *   using 2, 3, 5 (all have 5 segments)
 * To find center -> (2 ∩ 3 ∩ 5 ∩ 4)
 * To find bottom -> (2 ∩ 3 ∩ 5 - top - center)
 * 
 *   using 0, 6, 9 (all have 6 segments)
 * To find bot_right -> (0 ∩ 6 ∩ 9 ∩ 1)
 * To find top_left -> (0 ∩ 6 ∩ 9 - top - bottom - bot_right)
 * 
 *   process of elimination
 * To find top_right -> 1's segs - bot_right
 * To find bot_left -> 8's segs - (all others)
 * 
 * 
 * 
 * I want to make a hashmap with descriptors as keys (e.g "top") and seg-letters as values, and add new key-val pairs as I find them
 * Once I complete this hashmap, I can make a new one with numbers as keys and seg-letters as values
 *      This second hashmap will help with the final decoding..
 * 
 */

public class Main{
    public static void main(String[] args) {
        String file = "test.txt";
        String[][] outputs = FileParser.getOutputs(file);
        String[][] inputs = FileParser.getInputs(file);
        
        int unique_dgt_count = 0;
        for (String[] output_line : outputs) {
            for (String output : output_line) {
                if (output.length() == 2 ||
                    output.length() == 3 ||
                    output.length() == 4 ||
                    output.length() == 7){
                        unique_dgt_count++;
                    }
            }            
        }

        // part one
        System.out.printf("Digits 1, 4, 7, and/or 8 appear: %d times.\n", unique_dgt_count);
    
        // part two
        
        for (String[] input_line : inputs) {
            
            SSegDecoder decoder = new SSegDecoder(input_line);
        }
    
    
    }
}


class SSegDecoder{
    Map<String, Character> map_position_letter = new HashMap<String, Character>();
    Map<Integer, String> map_num_segs = new HashMap<Integer, String>();
    String[] inputs;
    String[] positions = {
        "top",
        "center",
        "bottom",
        "bot_right",
        "top_left",
        "top_right",
        "bot_left"
    };

    public SSegDecoder(String[] inputs){
        this.inputs = inputs;
        Arrays.sort(this.inputs, Comparator.comparing(String::length));

        // find the correct letters and fill in the hashmap
        for (String pos : positions) {
            map_position_letter.put(pos, find_letter_for_pos(pos));
        }
    }

    private char find_letter_for_pos(String pos){
        Set<Character> set1 = new HashSet<Character>();
        Set<Character> set2 = new HashSet<Character>();

        switch(pos){
            case "top":
                for (String digit_segs : inputs) {
                    if (digit_segs.length() == 2)   // get 1's segs
                        for (char c : digit_segs.toCharArray()) 
                            set1.add(c);

                    else if (digit_segs.length() == 3)  //get 7's segs
                        for (char c : digit_segs.toCharArray()) 
                            set2.add(c);
                }
                set2.removeAll(set1);  // subtract the sets
                return (char) set2.toArray()[0];

            case "center":
                break;
            case "bottom":
                break;
            case "bot_right":
                break;
            case "top_left":
                break;
            case "top_right":
                break;
            case "bot_left":
                break;
            default:
                break;
        }
            

        Set segs = new HashSet<>(Arrays.asList(pos));

        Arrays.asList(pos);

        return 'a';
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

    public static String[][] getOutputs(String filename){
        String[] lines = getLines(filename);
        String[][] outputs = new String[lines.length][4];
        for (int i = 0; i < lines.length; i++) {
            outputs[i] = lines[i].split(" \\| ")[1].split(" ");
        }
        return outputs;
    }

    public static String[][] getInputs(String filename){
        String[] lines = getLines(filename);
        String[][] inputs = new String[lines.length][4];
        for (int i = 0; i < lines.length; i++) {
            inputs[i] = lines[i].split(" \\| ")[0].split(" ");
        }
        return inputs;
    }

}
