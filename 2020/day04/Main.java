import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
--- Day 4: Passport Processing ---
You arrive at the airport only to realize that you grabbed your North Pole Credentials instead of your passport. 
While these documents are extremely similar, North Pole Credentials aren't issued by a country and therefore aren't 
actually valid documentation for travel in most of the world.

It seems like you're not the only one having problems, though; a very long line has formed for the automatic
passport scanners, and the delay could upset your travel itinerary.

Due to some questionable network security, you realize you might be able to solve both of these problems at the same time.

The automatic passport scanners are slow because they're having trouble detecting which passports have all required fields. 
The expected fields are as follows:

byr (Birth Year)
iyr (Issue Year)
eyr (Expiration Year)
hgt (Height)
hcl (Hair Color)
ecl (Eye Color)
pid (Passport ID)
cid (Country ID)
Passport data is validated in batch files (your puzzle input). Each passport is represented as a sequence 
of key:value pairs separated by spaces or newlines. Passports are separated by blank lines.

Here is an example batch file containing four passports:

ecl:gry pid:860033327 eyr:2020 hcl:#fffffd
byr:1937 iyr:2017 cid:147 hgt:183cm

iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884
hcl:#cfa07d byr:1929

hcl:#ae17e1 iyr:2013
eyr:2024
ecl:brn pid:760753108 byr:1931
hgt:179cm

hcl:#cfa07d eyr:2025 pid:166559648
iyr:2011 ecl:brn hgt:59in
The first passport is valid - all eight fields are present. The second passport is invalid - it is missing hgt (the Height field).

The third passport is interesting; the only missing field is cid, so it looks like data from North Pole Credentials, 
not a passport at all! Surely, nobody would mind if you made the system temporarily ignore missing cid fields. 
Treat this "passport" as valid.

The fourth passport is missing two fields, cid and byr. Missing cid is fine, but
missing any other field is not, so this passport is invalid.

According to the above rules, your improved system would report 2 valid passports.

Count the number of valid passports - those that have all required fields. 
Treat cid as optional. In your batch file, how many passports are valid?
 */


/**
 * Contains helper functions for importing data from the 
 * puzzle input text file.
 */
class FileParser{

    String filename = "";
    int currentPosition = 0;
    int numOfLines = 0;
    Scanner sc;
    boolean nextEntryExists = true;


    public FileParser(String filename){
        this.filename = filename;
    }

    public FileParser(String filename, String delimiter){
        this.filename = filename;
        initParser(delimiter);
    }


    /**
     * Initialize (or reset) the scanner for the file with a given delimiter
     */
    public void initParser(String delimiter){
        File input = new File(filename);
        try {
            sc = new Scanner(input).useDelimiter(delimiter);
        } catch (Exception e) {
            sc = null;
        }
    }

    /**
     * Returns the next set of characters as a string, using the delimiter
     * given in `initScanner()` as the end of an entry
     */
    public String getNextEntry(){
        String entry = null;
        if (this.nextEntryExists){
            entry = sc.next();
            if (entry == "" || entry == "\n" || entry == "\r"){ // skip the empty "entries"
                System.out.println("this was an empty one..");
                // entry = getNextEntry();
            }
            System.out.printf("=%s=\n", entry);

            this.nextEntryExists = sc.hasNext();
        }

        return entry;
    }


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

    public int countLines(){
        if (this.filename == "") return -1;
        Path path = Paths.get(this.filename);
        this.numOfLines = 0;

        try {
            this.numOfLines = (int)Files.lines(path).count();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return this.numOfLines;
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

    public String[] getLines(){       
        // Figure out how many lines are in the file
       int arr_size = countLines(this.filename);

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


public class Main{
    public static void main(String[] args) {

        String file = "test.txt";
        FileParser fp = new FileParser(file, "\n");
        
        int count = 1;
        String result = "";
        while (result != null){
            result = fp.getNextEntry();
            // System.out.printf("%d, %s\n", count, fp.getNextEntry());
            count++;
        }
        
    }
}