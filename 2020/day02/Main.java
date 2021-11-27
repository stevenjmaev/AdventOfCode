import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
--- Day 2: Password Philosophy ---
Your flight departs in a few days from the coastal airport; the easiest way down to the coast from here is via toboggan.

The shopkeeper at the North Pole Toboggan Rental Shop is having a bad day. 
"Something's wrong with our computers; we can't log in!" You ask if you can take a look.

Their password database seems to be a little corrupted: some of the passwords wouldn't have been 
allowed by the Official Toboggan Corporate Policy that was in effect when they were chosen.

To try to debug the problem, they have created a list (your puzzle input) of passwords 
(according to the corrupted database) and the corporate policy when that password was set.

For example, suppose you have the following list:

1-3 a: abcde
1-3 b: cdefg
2-9 c: ccccccccc
Each line gives the password policy and then the password. The password policy indicates the 
lowest and highest number of times a given letter must appear for the password to be valid. 
For example, 1-3 a means that the password must contain a at least 1 time and at most 3 times.

In the above example, 2 passwords are valid. The middle password, cdefg, is not; it contains 
no instances of b, but needs at least 1. The first and third passwords are valid: they contain one a or nine c,
both within the limits of their respective policies.

How many passwords are valid according to their policies?
 */


/**
 * Contains helper functions for importing data from the 
 * puzzle input text file.
 */
class FileReader{

    /**
     * Takes in the name of a text file that is in the root directory
     * and returns the number of lines it contains. 
     */
    public static int countLines(String filename){
        Path path = Paths.get(filename);
        int arr_size = 0;

        try {
            arr_size = (int)Files.lines(path).count();
        } catch (IOException e1) {
            e1.printStackTrace();
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

/**
 * Holds the info for each password given by the puzzle input
 */
class Password{

    String policy;
    String password;
    
    /**
     * Constructor
     */
    public Password(String password_info){
        String[] separated = password_info.split(":");
        policy = separated[0];
        password = separated[1].trim();
    }

    /**
     * Checks the password against the policy and returns whether it
     * is valid according to the policy
     */
    public boolean isValid(){
        // e.g. policy -> '11-17 c'
        //      password -> 'ccccccccccccccccgc'
        int min = Integer.parseInt(policy.split("-")[0]); // gives '11'
        String tmp = policy.split("-")[1];                // gives '17 c'
        int max = Integer.parseInt(tmp.split(" ")[0]);    // gives '17'
        String letter = tmp.split(" ")[1];                // gives 'c'

        // count how many occurances of the letter
        // remove all the letters except the one of interest and count the result
        int count = password.replaceAll("[^"+letter+"]", "").length();    
        if (count >= min && count <= max) 
            return true;
        else 
            return false;
    }
}

public class Main{
    public static void main(String[] args) {
        int valid_passwords_count = 0;
        String[] input_arr = FileReader.getLines("puzzle_input.txt");
        for (String line : input_arr) {
            Password pw = new Password(line);
            if (pw.isValid())
                valid_passwords_count++;
        }
        
        System.out.println(String.format("Valid passwords: %d", valid_passwords_count));
    }
}