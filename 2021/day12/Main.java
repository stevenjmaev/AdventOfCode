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


public class Main{
    static Map<String, List<String>> CaveMap = new HashMap<>();
    static List<String> caves = new ArrayList<>();
    static long numPaths;
    static boolean start_visited = false;

    public static void main(String[] args) {
        String file = "puzzle_input.txt";
        String[] lines = FileParser.getLines(file);

        // get a list of all the caves that exist
        for (String line : lines) {
            String first = line.split("-")[0];
            String second = line.split("-")[1];
            if (!caves.contains(first))
                caves.add(first);
            if (!caves.contains(second))
                caves.add(second);
        }

        // initialize hashmap
        for (String cave : caves)
            CaveMap.put(cave, new ArrayList<String>());
        
        // update hashmap with neighbors
        for (String line : lines){
            String first = line.split("-")[0];
            String second = line.split("-")[1];

            CaveMap.get(first).add(second);
            CaveMap.get(second).add(first);
        }
        
        // ============================ PART ONE
        numPaths = 0;
        for (String connecting_cave : CaveMap.get("start")){
            List<String> path = new ArrayList<>();
            Map<String, Integer> smalls_visited = new HashMap<String, Integer>();
            for (String cave : CaveMap.keySet()) {  // init smalls_visited map
                if (!Character.isUpperCase(cave.charAt(0)))
                    smalls_visited.put(cave, 0);
            }
            path.add("start");              // add the start to the paths of this branch by default
            traverse_path(connecting_cave, path, smalls_visited, 1);
        }
        System.out.printf("Number of paths for part one: %d\n\n", numPaths);


        // ============================ PART TWO
        numPaths = 0;
        for (String connecting_cave : CaveMap.get("start")){
            List<String> path = new ArrayList<>();
            Map<String, Integer> smalls_visited = new HashMap<String, Integer>();
            for (String cave : CaveMap.keySet()) {  // init smalls_visited map
                if (!Character.isUpperCase(cave.charAt(0)))
                    smalls_visited.put(cave, 0);
            }
            path.add("start");              // add the start to the paths of this branch by default
            traverse_path(connecting_cave, path, smalls_visited, 2);
        }
        System.out.printf("Number of paths for part two: %d\n\n", numPaths);
        
    }


    /**
     * Recursive function that traverses the path from the 'startCave' all the way to the "end" cave
     */
    static private void traverse_path(String startCave, List<String> current_path, Map<String, Integer> small_caves_visited, int part){
        if (!Character.isUpperCase(startCave.charAt(0))) // if it's a small cave
            small_caves_visited.replace(startCave, small_caves_visited.get(startCave).intValue() + 1); // add it to our list of visited small caves
        current_path.add(startCave);    // add this cave to the path    

        if (startCave.equals("end")) {  // when we get to the end, print the path and "back up" so we can find other paths
            numPaths++;
            // System.out.println(current_path); // for debugging
            current_path.remove(startCave);
            small_caves_visited.replace("end", 0);
            return;
        }  

        // iterate through each of the neighbors and traverse down those respective paths
        for(String connecting_cave : CaveMap.get(startCave)){
            // if it's a small cave and we already visited it, don't go there again
            if (!Character.isUpperCase(connecting_cave.charAt(0))) {
                if (connecting_cave.equals("start"))
                    continue;
                
                // for Part One
                if (part == 1) {
                    if (small_caves_visited.get(connecting_cave).intValue() == 1) 
                        continue;   
                }

                // for Part Two
                else {
                    if (small_caves_visited.values().contains(2)){
                        int times_visited = small_caves_visited.get(connecting_cave).intValue();
                        if (times_visited == 1 || times_visited == 2) {
                            continue;
                        }                            
                    }
                }
            }

            // traverse the entire path for that connecting cave (until you reach the end)
            traverse_path(connecting_cave, current_path, small_caves_visited, part);
            
        }
        // once we traverse through that path, exhaustively, then 
        // "back up" so we can traverse the other paths that the previous node connects to
        current_path.remove(current_path.size() - 1);
        if (!Character.isUpperCase(startCave.charAt(0)))
            small_caves_visited.replace(startCave, small_caves_visited.get(startCave).intValue() - 1);  // decrement the times visited for that small cave
        return;
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
