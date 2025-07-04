package cder.tictactoe;

import java.util.Scanner;

public class Driver 
{
    public static void main(String[] args)
    {
        String user = "test";

        Board board = new Board();
        Scanner scan = new Scanner(System.in);
        while (true)
        {
            System.out.println(board);
            System.out.print("Move :");
            int row = scan.nextInt();
            int col = scan.nextInt();
            board.move(row, col);
            if (board.isGameOver()) break;
        }
        System.out.println(board);
        System.out.println(board.getWinner());
        
        scan.close();
    }

}
