import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.text.Position;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
--- Day 4: Giant Squid ---
You're already almost 1.5km (almost a mile) below the surface of the ocean, already so deep that you can't see any sunlight. 
What you can see, however, is a giant squid that has attached itself to the outside of your submarine.

Maybe it wants to play bingo?

Bingo is played on a set of boards each consisting of a 5x5 grid of numbers. Numbers are chosen at random, and the 
chosen number is marked on all boards on which it appears. (Numbers may not appear on all boards.) 
If all numbers in any row or any column of a board are marked, that board wins. (Diagonals don't count.)

The submarine has a bingo subsystem to help passengers (currently, you and the giant squid) pass the time. It automatically 
generates a random order in which to draw numbers and a random set of boards (your puzzle input). For example:

7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

22 13 17 11  0
 8  2 23  4 24
21  9 14 16  7
 6 10  3 18  5
 1 12 20 15 19

 3 15  0  2 22
 9 18 13 17  5
19  8  7 25 23
20 11 10 24  4
14 21 16 12  6

14 21 17 24  4
10 16 15  9 19
18  8 23 26 20
22 11 13  6  5
 2  0 12  3  7
After the first five numbers are drawn (7, 4, 9, 5, and 11), there are no winners, but the boards are marked as follows 
(shown here adjacent to each other to save space):

22 13 17 11  0         3 15  0  2 22        14 21 17 24  4
 8  2 23  4 24         9 18 13 17  5        10 16 15  9 19
21  9 14 16  7        19  8  7 25 23        18  8 23 26 20
 6 10  3 18  5        20 11 10 24  4        22 11 13  6  5
 1 12 20 15 19        14 21 16 12  6         2  0 12  3  7
After the next six numbers are drawn (17, 23, 2, 0, 14, and 21), there are still no winners:

22 13 17 11  0         3 15  0  2 22        14 21 17 24  4
 8  2 23  4 24         9 18 13 17  5        10 16 15  9 19
21  9 14 16  7        19  8  7 25 23        18  8 23 26 20
 6 10  3 18  5        20 11 10 24  4        22 11 13  6  5
 1 12 20 15 19        14 21 16 12  6         2  0 12  3  7
Finally, 24 is drawn:

22 13 17 11  0         3 15  0  2 22        14 21 17 24  4
 8  2 23  4 24         9 18 13 17  5        10 16 15  9 19
21  9 14 16  7        19  8  7 25 23        18  8 23 26 20
 6 10  3 18  5        20 11 10 24  4        22 11 13  6  5
 1 12 20 15 19        14 21 16 12  6         2  0 12  3  7
At this point, the third board wins because it has at least one complete row or column of marked numbers 
(in this case, the entire top row is marked: 14 21 17 24 4).

The score of the winning board can now be calculated. Start by finding the sum of all unmarked numbers on that board; 
in this case, the sum is 188. Then, multiply that sum by the number that was just called when the board won, 24, to get the final score, 188 * 24 = 4512.

To guarantee victory against the giant squid, figure out which board will win first. What will your final score be if you choose that board?
 */

public class Main{
    public static void main(String[] args) {
        String file = "puzzle_input.txt";
        String[] lines = FileParser.getLines(file);
        String[] tmp = Arrays.copyOfRange(lines, 2, lines.length);
        List<BingoBoard> boards = FileParser.to_BingoBoards(tmp, 5, 5);
        List<Integer> winning_board_idxs = new ArrayList<>();  

        Scanner sc = new Scanner(lines[0]).useDelimiter(",");

        // iterate through all the guesses (in the first line of the puzzle input)
        int guess = 0, board_idx = 0;
        guess_loop:
        while (sc.hasNext()){
            guess = Integer.parseInt(sc.next());
            board_idx = 0;
            for (BingoBoard bb : boards) {
                if (!bb.elements.keySet().contains(guess)){
                    board_idx++;
                    continue;
                }
                // System.out.printf("marking number (%d) on board idx (%d)\n", guess, board_idx);
                bb.MarkNumber(guess);
                if (bb.winner)
                    winning_board_idxs.add(board_idx);
                board_idx++;
            }
        }
        // add up all the unmarked numbers for the winning board
        int sum = 0;
        BingoBoard winningBoard = boards.get(winning_board_idxs.get(0));
        for (int num : winningBoard.elements.keySet()){
            if (!winningBoard.elements.get(num).marked)
                sum += num;
        }

        System.out.printf("winning board was: boardidx %d\n", winning_board_idxs.get(0));
        System.out.printf("last number called was: %d\n", winningBoard.winning_number);
        System.out.printf("sum of non-marked numbers for that board: %d\n", sum);
        System.out.printf("product = %d", winningBoard.winning_number * sum);
        
    }
}

/**winning board was: boardidx 87
 * last number called was: 21
sum of non-marked numbers for that board: 796
product = 16716
 */





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

    public static List<BingoBoard> to_BingoBoards(String[] lines, int board_width, int board_height){
        List<BingoBoard> boardList = new ArrayList<>();
        int[][] boardNums = new int[board_height][board_width];
        int i = 0;
        for (String line : lines) {
            if (line.isEmpty()){    // empty lines indicate new boards, so reset that index
                i = 0;
                continue;
            }

            int j = 0;
            // scan across the line and get the 5 numbers
            Scanner sc = new Scanner(line).useDelimiter("\s\s|\s");
            while (sc.hasNext()){ 
                String tmp = sc.next();
                boardNums[i][j] = Integer.parseInt(tmp);
                j++;
            }
            
            i++;
            if (line.isEmpty())
                i = 0;
            if (i == board_height){    // we got 5 rows, so let's create a new board
                boardList.add(new BingoBoard(boardNums));
            }
        }
        return boardList;
    }

}


class BingoBoard{
    Map<Integer, BingoBoardElement> elements = new HashMap<>();
    boolean winner = false;
    int winning_number = -1;
    private int width, height;
    
    
    public BingoBoard(int[][] numbers){
        width = numbers[0].length;
        height = numbers.length;

        for(int row = 0; row < height; row++){
            for(int column = 0; column < width; column++)
            {
                // fill in the map with the board elements
                BingoBoardElement ele = new BingoBoardElement(column, row);
                elements.put(numbers[row][column], ele);
            }
        }
    }

    public void MarkNumber(int number){
        elements.get(number).marked = true; // mark that number
        check_winner();        
        if (winner && winning_number == -1)
            winning_number = number;
    }

    private void check_winner(){
        // count how many marks are in each row
        int[] rowMarks = new int[height];
        int[] colMarks = new int[width];

        // iterate over all the elements in the board to check if it is a winner
        for (Integer i : elements.keySet()) {
            if (!elements.get(i).marked)    // don't do the counting for any of the unmarked elements
                continue;
            // check if any of the marked elements are in a row
            for (int row = 0; row < height; row++){
                if (elements.get(i).y == row){
                    rowMarks[row]++;
                }
            }
            // check if any of the marked elements are in a column
            for (int col = 0; col < width; col++){
                if (elements.get(i).x == col){
                    colMarks[col]++;
                }
            }
        }

        // if any column or row is filled, it is a winner
        for (int row_mark_count : rowMarks){
            if (row_mark_count == height){
                winner = true;
                break;
            }
        }
        for (int col_mark_count : colMarks){
            if (col_mark_count == width){
                winner = true;
                break;
            }
        }
    }
}

class BingoBoardElement{
    int x, y;
    boolean marked = false;


    public BingoBoardElement(int x, int y){
        this.x = x;
        this.y = y;
    }
}