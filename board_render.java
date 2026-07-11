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


    private int[][] board_state;
    private final int space_size = 100;
    private int selected_row = -1; // -1 :  nothing selected
    private int selected_col = -1;
    private boolean white_to_move = true;
    private boolean in_check = false;

    // Constant reference to where the kings are 
    // for evaluation of check and checkmate
    private int b_king_row = 0;
    private int b_king_col = 4;
    private int w_king_row = 7;
    private int w_king_col = 4;
    // private Set<int[]> whiteValid= new HashSet<>();

    public board_render(int[][] board_state_in) {
        this.board_state = board_state_in;
        Dimension dimension = new Dimension(8 * space_size, 8 * space_size);
        this.setPreferredSize(dimension);

        // Listen for clicks on the board
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent clicked){
                // 
                int column = clicked.getX() / space_size;
                int row = clicked.getY() / space_size;

                handle_click(row, column);

            }
        });
    }

    private boolean piece_belongs_to_mover(int piece) { 
        return (white_to_move && piece > 0) || (!white_to_move && piece < 0);
    }

    private void handle_click(int row, int col) {
         
        int clicked_piece = board_state[row][col];
        
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
            if(in_check && clicked_piece != Math.abs(6)){
                selected_col = -1;
                selected_row = -1;
                return;
            }    
            // Case 2: the player has selected a piece and this is trying to move that piece to the new row and col
            if (row == selected_row && col == selected_col) {
                selected_row = -1;
                selected_col = -1;
            } else if (clicked_piece != 0 && piece_belongs_to_mover(clicked_piece)) {
                selected_row = row;
                selected_col = col;
            } else if (is_valid_move(selected_row, selected_col, row, col)) {
                int moved_piece = board_state[selected_row][selected_col];
                board_state[row][col] = moved_piece;
                board_state[selected_row][selected_col] = 0;

                // keep king position tracking in sync since the validity checks are now pure
                if (moved_piece == 6) {
                    w_king_row = row;
                    w_king_col = col;
                } else if (moved_piece == -6) {
                    b_king_row = row;
                    b_king_col = col;
                }

                // reset selected moves and alternate turns
                selected_row = -1;
                selected_col = -1;
                white_to_move = !white_to_move;
            }
        }
        // redraw board for changes
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
            return valid_move_utils.white_pawn(board_state, from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == -1){
            return valid_move_utils.black_pawn(board_state, from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == 2){
            return valid_move_utils.white_rook(board_state, from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == -2){
            return valid_move_utils.black_rook(board_state, from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == 3){
            return valid_move_utils.white_knight(board_state, from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == -3){
            return valid_move_utils.black_knight(board_state, from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == 4){
            return valid_move_utils.white_bishop(board_state, from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == -4){
            return valid_move_utils.black_bishop(board_state, from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == 5){
            return valid_move_utils.white_queen(board_state, from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == -5){
            return valid_move_utils.black_queen(board_state, from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == 6){
            return valid_move_utils.white_king(board_state, from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == -6){
            return valid_move_utils.black_king(board_state, from_row, from_col, to_row, to_col);
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
    @Override
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
                    board.setColor(new Color(240, 217, 181)); // light squares
                } else {
                    board.setColor(new Color(181, 136, 99));  // dark squares
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
