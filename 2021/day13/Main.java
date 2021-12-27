import java.awt.Point;
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

// NOTE: I could've kept track of ONLY the dots (rather than both the dots and the blank spots), but 
// This would've saved a lot of memory, and would've been much faster... 

public class Main{
    public static void main(String[] args) {
        String file = "puzzle_input.txt";
        String[] lines = FileParser.getLines(file);

        int instruction_start_idx = 0;

        // look for the max size of the map (so we can initialize the paper's hashmap)
        //   this isn't necessary to keep track of the empty spaces, since I could 
        //   just keep a list of all the points, but it might be helpful to have the ENTIRE map
        int max_x = 0, max_y = 0;
        for (int i = 0; i < lines.length; i++) {    
            if (lines[i].length() == 0){
                instruction_start_idx = i + 1;
                break;
            }
            Scanner sc = new Scanner(lines[i]).useDelimiter(",");
            int x = Integer.parseInt(sc.next());
            int y = Integer.parseInt(sc.next());

            if (x > max_x)
                max_x = x;
            if (y > max_y)
                max_y = y; 

            sc.close();
        }
        
        // initialize the transparent paper
        TransparentPaper paper = new TransparentPaper(max_x, max_y);

        // add the dots to the map
        for (int i = 0; i < instruction_start_idx - 1; i++) {    
            Scanner sc = new Scanner(lines[i]).useDelimiter(",");
            int x = Integer.parseInt(sc.next());
            int y = Integer.parseInt(sc.next());

            paper.add_dot(new Point(x, y));

            sc.close();            
        }

        // execute the folding instructions
        for (int i = instruction_start_idx; i < lines.length; i++){
            Scanner sc = new Scanner(lines[i]).useDelimiter("fold along |=");
            char x_or_y = sc.next().charAt(0);
            int loc = Integer.parseInt(sc.next());

            switch (x_or_y){
                case 'x':
                    paper.foldLeft(loc);
                    break;
                case 'y': 
                    paper.foldUp(loc);
                    break;
            }
            sc.close();

            // only print info for the LAST iteration 
            if (i != lines.length - 1)
                continue;
            System.out.print("\n");
            System.out.printf("After folding along %c = %d, there are %d dots visible and the paper looks like:\n", 
                             x_or_y, loc, paper.countDots());
            paper.print();  // it looks like 'FGKCKBZG'
                        
        }
    }
}


class TransparentPaper{
    Map<Point, Character> map_coord_mark = new HashMap<Point, Character>();
    int max_x, max_y;

    public TransparentPaper(int max_x, int max_y){
        this.max_x = max_x;
        this.max_y = max_y;

        for (int x = 0; x <= max_x; x++){
            for (int y = 0; y <= max_y; y++){
                map_coord_mark.put(new Point(x, y), '.');   // initialize the map with empty spaces
            }
        }
    }

    /**
     * Puts a dot '#' in the given location
     */
    public void add_dot(Point pt){
        map_coord_mark.replace(pt, '#');
    }

    /**
     * For folding along horizontal lines (y = ...)
     */
    public void foldUp(int y){
        for (int i = y; i <= max_y; i++){
            for (int j = 0; j <= max_x; j++){
                if (map_coord_mark.get(new Point(j, i)) == '.') // don't replace anything if there was no dot there
                    continue;
                map_coord_mark.replace(
                    get_overlapped_point(new Point(j, i), 'y', y),
                    '#'
                    );
                map_coord_mark.remove(new Point(j, i));
            }
        }
        max_y = y - 1;
    }

    /**
     * For folding along vertical lines (x = ...)
     */
    public void foldLeft(int x){

        for (int i = x; i <= max_x; i++){
            for (int j = 0; j <= max_y; j++){
                if (map_coord_mark.get(new Point(i, j)) == '.') // don't replace anything if there was no dot there
                    continue;
                map_coord_mark.replace(
                    get_overlapped_point(new Point(i, j), 'x', x),
                    '#'
                    );
                map_coord_mark.remove(new Point(i, j));
            }
        }
        max_x = x - 1;
    }

    /**
     * Takes in the original point the fold direction (either 'x' or 'y')
     * and the location of the fold. Returns the point that it will overlap after the fold.
     * Returns a new Point(-1, -1) on error.
     */
    private Point get_overlapped_point(Point in, char fold_dir, int fold_loc){
        Point out;
        int x = (int) in.getX();
        int y = (int) in.getY();
        int dist_to_fold;
        switch(fold_dir){   // man, I'm glad this worked the first time. I was honestly spitballing this equation...
            case 'x':   // folding left
                dist_to_fold = x - fold_loc;
                x = x - (2 * dist_to_fold);
                break;
            case 'y':   // folding right
                dist_to_fold = y - fold_loc;
                y = y - (2 * dist_to_fold);
                break;
            default:
                return new Point(-1, -1);
        }
        return new Point(x, y);
    }

    public long countDots(){
        long count = 0;
        for (char c : map_coord_mark.values()){
            if (c == '#')
                count++;
        }
        return count;
    }

    /**
     * Prints the paper's current state to the console.
     */
    public void print(){
        for (int y = 0; y <= max_y; y++){
            for (int x = 0; x <= max_x; x++){
                System.out.printf("%c", map_coord_mark.get(new Point(x, y)));
            }
            System.out.print("\n");
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
