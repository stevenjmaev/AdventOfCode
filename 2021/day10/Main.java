import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Notes to self: 
 * I want a function called `getComplement(char c)` that returns the complementary character (e.g if you pass 
 * in '{' it'll return '}' and visa versa)
 * 
 * I probably want to use a LIFO for which you PUT the 'open' characters when you come across them, and if you 
 * come across a 'close' character, you GET the most recent 'open' character from the LIFO and check if it is the
 * complement of the 'close' character you just came across
 */


public class Main{
    static Map<Character, Integer> illegal_char_count = new HashMap<Character, Integer>();
    public static void main(String[] args) {
        String file = "puzzle_input.txt";
        String[] lines = FileParser.getLines(file);
        illegal_char_count.put(')', 0);
        illegal_char_count.put(']', 0);
        illegal_char_count.put('}', 0);
        illegal_char_count.put('>', 0);
        
        // check syntax of all lines
        for (String line : lines) {
            SyntaxChecker checker = new SyntaxChecker(line);
            char illegal_char = checker.Check();
            if (illegal_char != '.'){   // if there was an error in the line..
                int prev_count = illegal_char_count.get(illegal_char);
                illegal_char_count.replace(illegal_char, prev_count + 1);
            }
        }

        // calculate error score
        long error_score = 0;
        for (char c : illegal_char_count.keySet()){
            int multiplier = 1;
            switch (c){
                case ')': 
                    multiplier = 3;
                    break;
                case ']': 
                    multiplier = 57;
                    break;
                case '}': 
                    multiplier = 1197;
                    break;
                case '>': 
                    multiplier = 25137;
                    break;
                default: multiplier = 1;
            }
            error_score += multiplier * illegal_char_count.get(c);
        }

        System.out.println("Part One\n============");
        System.out.printf("The total syntax error score for this file is: %d\n\n", error_score);
        
        List<Long> completion_scores = new ArrayList<>();
        for (String line : lines) {
            long score = 0;
            SyntaxChecker checker = new SyntaxChecker(line);
            char[] closingChars = checker.CompleteBlocks();
            if (closingChars != null){
                for (char c : closingChars){
                    score *= 5;
                    switch (c){
                        case ')': 
                            score += 1;
                            break;
                        case ']': 
                            score += 2;
                            break;
                        case '}': 
                            score += 3;
                            break;
                        case '>': 
                            score += 4;
                            break;
                    }
                }

                completion_scores.add(score);  
            }
        }

        completion_scores.sort(null);
        System.out.println("Part Two\n============");
        System.out.printf("The middle score is: %d\n\n", completion_scores.get(completion_scores.size() / 2));
        // note: 43704420 is too low
    }

}

class SyntaxChecker{
    String line;
    char[] oChars = {'[', '{', '(', '<'};
    char[] cChars = {']', '}', ')', '>'};
    List<Character> openChars = new ArrayList<>();
    List<Character> closeChars = new ArrayList<>();

    public SyntaxChecker(String line){
        this.line = line;
        for (char c : cChars) 
            closeChars.add(c);
        for (char c : oChars) 
            openChars.add(c);
    }
    
    /**
     * Checks the line's syntax and returns the illegal character if there is one,
     * returns '.' if the line is of good syntax
     */
    public char Check(){
        Stack<Character> lifo = new Stack<Character>();
        char[] chars = line.toCharArray();
        for (int i = 0; i < chars.length; i++){
            if (openChars.contains(chars[i]))
                lifo.push(chars[i]);
            else if ((char)lifo.peek() != get_complement(chars[i]))
                return chars[i];
            else // if the corresponding close char was found, remove the open char from the fifo
                lifo.pop();
        }
        return '.';
    }

    /**
     * Checks the line's syntax, and if it's good syntax (but just incomplete) it returns 
     * an array of characters needed to complete the blocks correctly (in the correct order).
     * Returns null if the line was bad.
     */
    public char[] CompleteBlocks(){
        if (Check() != '.') return null;
        Stack<Character> lifo = new Stack<Character>();
        char[] chars = line.toCharArray();
        for (int i = 0; i < chars.length; i++){
            if (openChars.contains(chars[i]))
                lifo.push(chars[i]);
            else // if the corresponding close char was found, remove the open char from the fifo
                lifo.pop();
        }

        char[] ret = new char[lifo.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = get_complement(lifo.pop());
        }

        return ret;
    }

    private char get_complement(char c){
        for (int i = 0; i < oChars.length; i++) {
            if (oChars[i] == c)
                return cChars[i];
            else if (cChars[i] == c)
                return oChars[i];
        }
        return '?'; // default return, if the character wasn't in either of the arrays
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
