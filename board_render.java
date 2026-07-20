import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class board_render extends JPanel {

    // ENUM for pieces : 
    // [positive = white; negative = black]
    // 1: pawn 
    // 2: rook
    // 3: knight
    // 4: bishop
    // 5: queen 
    // 6: king

    private int mode; // not used because mode is only used within the constructor for creating mouselistener
    private int[][] board_state;
    private final int space_size = 100;
    private int selected_row = -1; // -1 :  nothing selected
    private int selected_col = -1;
    private boolean white_to_move = true;
    private boolean in_check = false; // deprecated because of king_safety check
    private chess_engine engine;
    private valid_move_utils validator = new valid_move_utils();

    // Constant references to where the kings are 
    // for evaluation of check and checkmate
        private int b_king_row = 0;
        private int b_king_col = 4;
        private int w_king_row = 7;
        private int w_king_col = 4;
    // ===========================================

    private boolean game_ended = false;
    private final int ELO = 1000; 
    public board_render(int[][] board_state_in, int mode) {
        this.board_state = board_state_in;
        this.mode = mode; // -> assign player versus player or player vs engine
        this.engine = new chess_engine(ELO);
        Dimension dimension = new Dimension(8 * space_size, 8 * space_size);
        this.setPreferredSize(dimension);

        // Listen for clicks on the board and determines what column / row the click was executed
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent clicked){
                int x = clicked.getX();
                int y = clicked.getY();
                if(!(x > 0 && x < 800 && y > 0 && y < 800 )){return;} // Handles out of bounds clicks
                int column = x / space_size;
                int row = y / space_size;
                //
                // Gameplay Loop Logic handling if engine is playing or not 
                // 
                if(!game_ended){
                    if(mode == 2 && selected_col != -1 && !(column == selected_col && row == selected_row)){
                        // note : if mode == 2 white_to_move is essentially always true
                        // this just means that white playing triggers a response from the engine which plays black
                        // the engine then returns the best move it found and then the board is moved accordingly
                        if(white_to_move){
                            if(handle_click(row, column)){ // handle_click returns whether a move actually occurs
                                repaint(); 
                                System.out.println("Generating Engine Move");
                                // Play Engine Move :
                                move_gen.Move engine_move = engine.move(board_state, white_to_move);
                                System.out.println("Generated Move : "  + engine_move.fromR + engine_move.fromC + engine_move.toR + engine_move.toC);
                                board_state[engine_move.toR][engine_move.toC] = board_state[engine_move.fromR][engine_move.fromC];
                                board_state[engine_move.fromR][engine_move.fromC] = 0;
                                white_to_move = true;   
                                repaint(); 
                            }
                        }
                        else{
                            // temporary handling is deprecated but exists for implementation of engine playing as white
                            System.out.println("WATITING");
                            handle_click(row, column);
                        }
                    }
                    else {
                        handle_click(row, column);
                    }
                }
                
                
            }
        });
    }

    private boolean piece_belongs_to_mover(int piece) { 
        return (white_to_move && piece > 0) || (!white_to_move && piece < 0);
    }

    private boolean handle_click(int row, int col) {
        
        int clicked_piece = board_state[row][col];
        boolean move_occurs = false;
        if (selected_row == -1) {
            // Case 1: the player hasn'row selected a piece yet
            // Nothing selected yet: select this square if it holds a piece
            // belonging to the side whose turn it is
            if (clicked_piece != 0 && piece_belongs_to_mover(clicked_piece)) {
                selected_row = row;
                selected_col = col;
            }
        } 
        else {
            // Case 2: the player has selected a piece and this is trying to move that piece to the new row and col
            if (row == selected_row && col == selected_col) {
                selected_row = -1;
                selected_col = -1;
                
            } else if (clicked_piece != 0 && piece_belongs_to_mover(clicked_piece)) {
                selected_row = row;
                selected_col = col;
            } else if (is_valid_move(selected_row, selected_col, row, col)) {
                move_occurs = true;

                int moved_piece = board_state[selected_row][selected_col];
                board_state[row][col] = moved_piece;
                board_state[selected_row][selected_col] = 0;
                if(moved_piece == 1 && row == 0){
                    promote_pawn(row,col, this.white_to_move);
                }


                if(white_to_move){ 
                    if(is_valid_move(row, col, b_king_row, b_king_col)){
                       in_check = true; // -> refactor this logic
                    }
                    else{
                        in_check = false;
                    }
                }
                else{
                    if(is_valid_move(row, col, w_king_row, w_king_col)){
                       in_check = true; 
                    }
                    else{
                        in_check = false;
                    }
                }
                
                
                // reset selected moves and alternate turns
                selected_row = -1;
                selected_col = -1;
                white_to_move = !white_to_move;

                // Check if the opponent has any legal moves * if not checkmate or stalemate, either way ends the game
                // if(move_gen.generateLegalMoves(board_state, white_to_move).size() == 0){
                //     System.out.println("Checkmate status check!");
                //     this.game_ended = true;
                //     return true;
                // }

                System.out.println(white_to_move);
                System.out.println(w_king_row + "w" + w_king_col);
                System.out.println(b_king_row + "b" + b_king_col);
            }
            

        }
        // redraw board for changes
        repaint();
        return move_occurs;
    }
    private void promote_pawn(int row, int col, boolean is_white){
        // create a prompt at the row, col of the promoting pawn
        if(is_white){
            board_state[row][col] = 5;
            
        }
        else{
            board_state[row][col] = -5;
        }

        repaint();
    }

    private boolean is_valid_move(int from_row, int from_col, int to_row, int to_col) {
        // System.out.println( from_row + " " +  from_col +  " " + to_row + " " +  to_col);
        int moving_to = board_state[from_row][from_col];
        int target_to = board_state[to_row][to_col];

        if (target_to != 0 && (moving_to > 0) == (target_to > 0)) {
            return false;
        }
        int selected_piece = board_state[from_row][from_col];
        
        // check for piece and run legal check for that piece
        if(selected_piece == 1){
            return validator.white_pawn(board_state, from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == -1){
            return validator.black_pawn(board_state, from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == 2){
            return validator.white_rook(board_state, from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == -2){
            return validator.black_rook(board_state, from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == 3){
            return validator.white_knight(board_state, from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == -3){
            return validator.black_knight(board_state, from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == 4){
            return validator.white_bishop(board_state, from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == -4){
            return validator.black_bishop(board_state, from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == 5){
            return validator.white_queen(board_state, from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == -5){
            return validator.black_queen(board_state, from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == 6){
            boolean valid = validator.white_king(board_state, from_row, from_col, to_row, to_col);
            // tracking king's position for check related calculations

            if(valid){
                w_king_col = to_col;
                w_king_row = to_row; 
                // Kingside castle
                if((to_col - from_col) == 2){
                    board_state[7][5] = 2;
                    board_state[7][7] = 0;
                }
                // Queenside castle
                else if((to_col - from_col) == -2){
                    board_state[7][3] = 2;
                    board_state[7][0] = 0;
                }
            }
            
            return valid;
        }
        else if (selected_piece == -6){
            boolean valid = validator.black_king(board_state, from_row, from_col, to_row, to_col);
            // tracking king's position for check related calculations
            if(valid){
                b_king_col = to_col;
                b_king_row = to_row; 
            }
            return valid;
        }

        return false;
    }

    private String piece_symbol(int piece) {
        switch (piece) {
            case  1: return "\u2659"; // white pawn
            case  2: return "\u2656"; // white rook
            case  3: return "\u2658"; // white knight
            case  4: return "\u2657"; // white bishop
            case  5: return "\u2655"; // white queen
            case  6: return "\u2654"; // white king
            case -1: return "\u265F"; // black pawn
            case -2: return "\u265C"; // black rook
            case -3: return "\u265E"; // black knight
            case -4: return "\u265D"; // black bishop
            case -5: return "\u265B"; // black queen
            case -6: return "\u265A"; // black king
            default: return "";
        }
    }

    // Render 8x8 board and place pieces according to board_state 
    @Override // Overrides JPanel's default paintComponent() method
    protected void paintComponent(Graphics graphic_in) {
        super.paintComponent(graphic_in);
        Graphics2D board = (Graphics2D) graphic_in;
        board.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
        board.setFont(new Font("SansSerif", Font.PLAIN, 72));
        FontMetrics fm = board.getFontMetrics();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                int piece = this.board_state[row][col];

                if (row == selected_row && col == selected_col) {
                    board.setColor(new Color(130, 180, 120)); // selected square
                } else if ((row + col) % 2 == 0) {
                    // board.setColor(new Color(240, 217, 181)); // light squares
                     board.setColor(new Color(240, 240,240)); 
                } else {
                    // board.setColor(new Color(181, 136, 99));  // dark squares
                    board.setColor(new Color(140, 140, 140));  // dark squares
                }

                board.fillRect(col * space_size, row * space_size, space_size, space_size);

                if (piece != 0) {
                    String symbol = piece_symbol(piece);
                    // Center the glyph on the tile
                    int x = col * space_size + (space_size - fm.stringWidth(symbol)) / 2;
                    int y = row * space_size + (space_size - fm.getHeight()) / 2 + fm.getAscent();
                    board.setColor(new Color(30, 30, 30));
                    board.drawString(symbol, x, y);
                }
            }
        }
    }
}
