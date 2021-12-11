import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
        String file = "puzzle_input.txt";
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
        System.out.println("Part One\n==========");
        System.out.printf("Digits 1, 4, 7, and/or 8 appear: %d times.\n\n", unique_dgt_count);
    
        // part two
        long sum = 0;
        for (int i = 0; i < inputs.length; i++) {
            SSegDecoder decoder = new SSegDecoder(inputs[i]);
            sum += decoder.DecodeOutput(outputs[i]);
        }
        
        System.out.println("Part Two\n===========");
        System.out.printf("The sum of all the four-digit output values is: %d", sum);
    
    }
}


class SSegDecoder{
    Map<String, Character> map_position_letter = new HashMap<String, Character>();
    Map<Set<Character>, Integer> map_segs_num = new HashMap<Set<Character>, Integer>();
    String[] inputs;
    String[] positions = {  // order of this matters!!!
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

        // generate the map for decoding a single digit's segments
        for (int i = 0; i < 10; i++) {
            map_segs_num.put(find_segs_for_num(i), i);
        }
    }

    private char find_letter_for_pos(String pos){
        // the most amount of sets we need to decode is 4...
        Set<Character> set1 = new HashSet<Character>();
        Set<Character> set2 = new HashSet<Character>();
        Set<Character> set3 = new HashSet<Character>();
        Set<Character> set4 = new HashSet<Character>();
        List<Set<Character>> sets = new ArrayList<Set<Character>>();
        sets.add(set1);
        sets.add(set2);
        sets.add(set3);
        sets.add(set4);
        short set_counter;

        switch(pos){
            case "top": // ==================================== GET TOP SEGMENT
                for (String digit_segs : inputs) {
                    if (digit_segs.length() == 2)   // get 1's segs
                        for (char c : digit_segs.toCharArray()) 
                            sets.get(0).add(c);

                    else if (digit_segs.length() == 3)  //get 7's segs
                        for (char c : digit_segs.toCharArray()) 
                            sets.get(1).add(c);
                }
                sets.get(1).removeAll(sets.get(0));  // subtract the sets
                return (char) sets.get(1).toArray()[0];

            case "center": // ==================================== GET CENTER SEGMENT
                set_counter = 0;
                for (String digit_segs : inputs){
                    if (digit_segs.length() == 5){   // save the segments of 2,3,5 in their own sets
                        for (char c : digit_segs.toCharArray())
                            sets.get(set_counter).add(c);
                        set_counter++;
                    }
                    else if (digit_segs.length() == 4){  // save 4's segments
                        for (char c : digit_segs.toCharArray())
                            sets.get(3).add(c);
                    }
                }
                // intersect all the sets
                for (int i = 1; i < 4; i++) {
                    sets.get(0).retainAll(sets.get(i));
                }
                return (char) sets.get(0).toArray()[0];

            case "bottom": // ==================================== GET BOTTOM SEGMENT
                set_counter = 0;
                for (String digit_segs : inputs){
                    if (digit_segs.length() == 5){   // save the segments of 2,3,5 in their own sets
                        for (char c : digit_segs.toCharArray())
                            sets.get(set_counter).add(c);
                        set_counter++;
                    }
                }
                // intersect those sets
                for (int i = 1; i < 3; i++) {
                    sets.get(0).retainAll(sets.get(i));
                }
                // subtract the ones we found already
                for (char c : this.map_position_letter.values()) 
                    sets.get(0).remove(c);
                return (char) sets.get(0).toArray()[0];

            case "bot_right": // ==================================== GET BOT_RIGHT SEGMENT
                set_counter = 0;
                for (String digit_segs : inputs){
                    if (digit_segs.length() == 6){   // save the segments of 0,6,9 in their own sets
                        for (char c : digit_segs.toCharArray())
                            sets.get(set_counter).add(c);
                        set_counter++;
                    }
                    else if (digit_segs.length() == 2){  // save 1's segments
                        for (char c : digit_segs.toCharArray())
                            sets.get(3).add(c);
                    }
                }
                // intersect those sets
                for (int i = 1; i < 4; i++) {
                    sets.get(0).retainAll(sets.get(i));
                }
                // subtract the ones we found already
                for (char c : this.map_position_letter.values()) 
                    sets.get(0).remove(c);
                return (char) sets.get(0).toArray()[0];

            case "top_left": // ==================================== GET TOP_LEFT SEGMENT
                set_counter = 0;
                for (String digit_segs : inputs){
                    if (digit_segs.length() == 6){   // save the segments of 0,6,9 in their own sets
                        for (char c : digit_segs.toCharArray())
                            sets.get(set_counter).add(c);
                        set_counter++;
                    }
                }
                // intersect those sets
                for (int i = 1; i < 3; i++) {
                    sets.get(0).retainAll(sets.get(i));
                }
                // subtract the ones we found already
                for (char c : this.map_position_letter.values()) 
                    sets.get(0).remove(c);
                return (char) sets.get(0).toArray()[0];

            case "top_right":// ==================================== GET TOP_RIGHT SEGMENT
                for (String digit_segs : inputs){
                    if (digit_segs.length() == 2){   // get 1's segs
                        for (char c : digit_segs.toCharArray())
                            sets.get(0).add(c);
                    }
                }
                // subtract the ones we found already
                for (char c : this.map_position_letter.values()) 
                    sets.get(0).remove(c);
                return (char) sets.get(0).toArray()[0];

            case "bot_left": // ==================================== GET BOT_LEFT SEGMENT
                for (String digit_segs : inputs){
                    if (digit_segs.length() == 7){   // get 8's segs
                        for (char c : digit_segs.toCharArray())
                            sets.get(0).add(c);
                    }
                }
                // subtract the ones we found already
                for (char c : this.map_position_letter.values()) 
                    sets.get(0).remove(c);
                return (char) sets.get(0).toArray()[0];

            default:
                return '?';
        }
    }

    private Set<Character> find_segs_for_num(int num){
        Set<Character> retSet = new HashSet<>();
        switch(num){
            case 0: // add all and remove center
                for (char c : map_position_letter.values()) 
                    retSet.add(c);
                retSet.remove(map_position_letter.get("center"));
                return retSet;
                
            case 1: // add right side
                retSet.add(map_position_letter.get("top_right"));
                retSet.add(map_position_letter.get("bot_right"));
                return retSet;
                
            case 2: // add all and remove bot_right and top_left
                for (char c : map_position_letter.values())
                    retSet.add(c);
                retSet.remove(map_position_letter.get("bot_right"));
                retSet.remove(map_position_letter.get("top_left"));
                return retSet;
                
            case 3: // add all and remove left side
                for (char c : map_position_letter.values())
                    retSet.add(c);
                retSet.remove(map_position_letter.get("top_left"));
                retSet.remove(map_position_letter.get("bot_left"));
                return retSet;
                
            case 4: // add just those four
                retSet.add(map_position_letter.get("top_right"));
                retSet.add(map_position_letter.get("top_left"));
                retSet.add(map_position_letter.get("center"));
                retSet.add(map_position_letter.get("bot_right"));
                return retSet;
                
            case 5: // add all and remove bot_left and top_right
                for (char c : map_position_letter.values())
                    retSet.add(c);
                retSet.remove(map_position_letter.get("bot_left"));
                retSet.remove(map_position_letter.get("top_right"));
                return retSet;
                
            case 6: // add all and remove top_right
                for (char c : map_position_letter.values())
                    retSet.add(c);
                retSet.remove(map_position_letter.get("top_right"));
                return retSet;
                
            case 7: // add just those three
                retSet.add(map_position_letter.get("top"));
                retSet.add(map_position_letter.get("top_right"));
                retSet.add(map_position_letter.get("bot_right"));
                return retSet;
                
            case 8: // add all
                for (char c : map_position_letter.values())
                    retSet.add(c);
                return retSet;
                
            case 9: // add all and remove bot_left
                for (char c : map_position_letter.values())
                    retSet.add(c);
                retSet.remove(map_position_letter.get("bot_left"));
                return retSet;
                
            default:
                return retSet;
        }
    }

    /**
     * Takes in an array of segments (the output) representing the different 
     * segments and converts them to the corresponding 4-digit number
     */
    public int DecodeOutput(String[] segs){
        int ret = 0;
        for (int i = 0; i < segs.length; i++) {
            // convert to set
            Set<Character> digit_segs = new HashSet<>();
            for (char c : segs[i].toCharArray())
                digit_segs.add(c);
            
            int num = map_segs_num.get(digit_segs);
            ret += num * Math.pow(10, 3 - i);
        }
        return ret;
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
