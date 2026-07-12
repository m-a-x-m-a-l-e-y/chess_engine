import javax.swing.*;

/*
    ## TODO ## 

    - Make black/white piece legal move checks into single functions with a param black or white with a param
    that passes through do define what the check for piece color should be
    - Add checks and checkmates -> add kings being able to influent whether a square is checked by other side
    - Consolidate repeated code, is seen a lot in logic code
    - build an interface to build a chess engine into this game
    - port the legal move logic over to a seperate file for the engine

    ##########
*/


public class chess_board {
    private int[][] brd = {
        {-2, -3, -4, -5, -6, -4, -3, -2},
        {-1, -1, -1, -1, -1, -1, -1, -1},
        { 0,  0,  0,  0,  0,  0,  0,  0},
        { 0,  0,  0,  0,  0,  0,  0,  0},
        { 0,  0,  0,  0,  0,  0,  0,  0},
        { 0,  0,  0,  0,  0,  0,  0,  0},
        { 1,  1,  1,  1,  1,  1,  1,  1},
        { 2,  3,  4,  5,  6,  4,  3,  2}
    };

    public chess_board(){
        // ENUM for pieces : 
        // [positive = white; negative = black]
        // 1: pawn 
        // 2: rook
        // 3: knight
        // 4: bishop
        // 5: queen 
        // 6: king

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                System.out.print("|"  + brd[i][j] + "|");
            }
            System.out.println();

        }
    }   

    private boolean won(){
        // to implement //// probably evaluating this in  the board_render function now just check if there are any legal moves for the player
        return false;
    }

    private void render_board(int mode){
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chessboard from 2D Array");
            board_render boardPanel = new board_render(this.brd, mode);

            frame.add(boardPanel);
            frame.pack(); // Fits the frame perfectly around our 8x8 panel dimensions
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    

    // Running the game is encompassed in this main function
    public static void main(String[] args) {
        int mode = 1;
        if(mode == 1){
            chess_board board = new chess_board(); 
            board.render_board(mode); // renders board and adds rules
        }
        if(mode == 2){ // Mode 2 is playing against an engine
            chess_board board = new chess_board(); 
            board.render_board(mode);
        }
        
    }
}




