package cder.connect4;

public class Driver 
{
    public static void main(String[] args) {
        Connect4 game = new Connect4();
        System.out.println("Starting a new game of Connect 4!");
        
        // Example moves
        game.move(0); 
        game.move(1); 
        game.move(0); 
        game.move(2); 
        game.move(0);
        game.move(3);
        //game.move(0);
        
        // Print the current board state
        System.out.println(game);

        System.out.println("Current player: " + game.getCurrentPlayer());
        System.out.println("Is game over? " + game.isGameOver());
        
        
    }
    
}
