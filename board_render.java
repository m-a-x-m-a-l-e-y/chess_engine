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
                board_state[row][col] = board_state[selected_row][selected_col];
                board_state[selected_row][selected_col] = 0;
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
            return white_pawn(from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == -1){
            return black_pawn(from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == 2){
            return white_rook(from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == -2){
            return black_rook(from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == 3){
            return white_knight(from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == -3){
            return black_knight(from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == 4){
            return white_bishop(from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == -4){
            return black_bishop(from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == 5){
            return white_queen(from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == -5){
            return black_queen(from_row, from_col, to_row, to_col);
        }
        else if(selected_piece == 6){
            return white_king(from_row, from_col, to_row, to_col);
        }
        else if (selected_piece == -6){
            return black_king(from_row, from_col, to_row, to_col);
        }

        return false;
        
    }

    private boolean white_pawn(int from_row, int from_col, int to_row, int to_col){
        // should consolidate functions of both pieces to one function but for now this is fine just trying to make it possible to demonstrate the chess engine
        if(to_row == 7 || to_row == 6){
            return false;
        }
        if(from_col == to_col){
            if(board_state[to_row][to_col] == 0){
                if(from_row == (6)){
                    if(board_state[5][from_col] == 0 && from_col == to_col && (from_row - to_row) < 3){
                        return true;
                    }
                }
                else{
                    if(from_row > to_row && (from_row - to_row) < 2){
                        return true;
                    }
                }
            }
        }
        else if( Math.abs(to_col - from_col) == 1 && (from_row- to_row) == 1){
            if(board_state[to_row][to_col] < 0){
                return true;
            } 
        }
        return false;
    }

    private boolean black_pawn(int from_row, int from_col, int to_row, int to_col){
        if(to_row == 0 || to_row == 1){
            return false;
        }

        if(from_col == to_col){
            if(board_state[to_row][to_col] == 0){
                if(from_row == (1)){
                    if(board_state[2][from_col] == 0 && (to_row - from_row) < 3){
                        return true;
                    }
                }
                else if(to_col == from_col){
                    if(from_row < to_row && (to_row - from_row) < 2){
                        return true;
                    }
                }
            }
        }
        else if( Math.abs(to_col - from_col) == 1 && ( to_row - from_row) == 1){
            if(board_state[to_row][to_col] > 0){
                return true;
            } 
        }
        return false;
    }

    private boolean white_rook(int from_row, int from_col, int to_row, int to_col){
        int col = from_col;
        int row = from_row;

        if(board_state[to_row][to_col] > 0){ return false;}

        // check all four directions at once
        if(from_row != to_row && from_col != to_col){return false;}

        // finding bounds horizontally
        if(col < to_col && board_state[from_row][col + 1] <= 0){
            while(col < to_col && board_state[from_row][col + 1] <= 0){
                col++;  
            }
            
        }
        else if (col > to_col && board_state[from_row][col - 1] <= 0){
            while(col > to_col && board_state[from_row][col - 1] <= 0){
                col--;  
            }
        }

        // finding bounds of vertically
        if(row < to_row && board_state[row + 1][from_col] <= 0){
            while(row < to_row && board_state[row + 1][from_col] <= 0){
                row++; 
            }
        }
        else if (row > to_row && board_state[row - 1][from_col] <= 0){
            while(row > to_row && board_state[row - 1][from_col] <= 0){
                row--;
            }
                
        }   
            
        // at this point col and row hvae moved towards the to_col or to_row as far as possible, we need to check if the value is 1 less than the target row/column, and determine if the piece is capturable
        if(from_col != to_col){ return (Math.abs(col - to_col) == 0);}
        if(from_row != to_row){ return (Math.abs(row - to_row) == 0);}
        
        return false;
    }
    
    private boolean black_rook(int from_row, int from_col, int to_row, int to_col){
                int col = from_col;
        int row = from_row;

        if(board_state[to_row][to_col] < 0){ return false;}

        // check all four directions at once
        if(from_row != to_row && from_col != to_col){return false;}

        // finding bounds horizontally
        if(col < to_col && board_state[from_row][col + 1] >= 0){
            while(col < to_col && board_state[from_row][col + 1] >= 0){
                col++;  
            }
            
        }
        else if (col > to_col && board_state[from_row][col - 1] >= 0){
            while(col > to_col && board_state[from_row][col - 1] >= 0){
                col--;  
            }
        }

        // finding bounds of vertically
        if(row < to_row && board_state[row + 1][from_col] >= 0){
            while(row < to_row && board_state[row + 1][from_col] >= 0){
                row++; 
            }
        }
        else if (row > to_row && board_state[row - 1][from_col] >= 0){
            while(row > to_row && board_state[row - 1][from_col] >= 0){
                row--;
            }
                
        }   
            
        // at this point col and row hvae moved towards the to_col or to_row as far as possible, we need to check if the value is 1 less than the target row/column, and determine if the piece is capturable
        if(from_col != to_col){ return (Math.abs(col - to_col) == 0);}
        if(from_row != to_row){ return (Math.abs(row - to_row) == 0);}
        
        return false;
    }
    
    private boolean white_knight(int from_row, int from_col, int to_row, int to_col){
        if(board_state[to_row][to_col] > 0){ return false; }

        if(to_col - from_col == 2 || to_col - from_col == -2){
            if(to_row - from_row == 1 || to_row - from_row == -1){
                return true;
            }
        } 
        else if(to_row - from_row == 2 || to_row - from_row == -2){
            if(to_col - from_col == 1 || to_col - from_col == -1){
                return true;
            }
        } 
        return false;
    }
    
    private boolean black_knight(int from_row, int from_col, int to_row, int to_col){
        if(board_state[to_row][to_col] < 0){ return false; }

        if(to_col - from_col == 2 || to_col - from_col == -2){
            if(to_row - from_row == 1 || to_row - from_row == -1){
                return true;
            }
        } 
        else if(to_row - from_row == 2 || to_row - from_row == -2){
            if(to_col - from_col == 1 || to_col - from_col == -1){
                return true;
            }
        } 
        return false;

    }

    private boolean white_bishop(int from_row, int from_col, int to_row, int to_col){
        // don't arrive at a square where there is a white space
        if(board_state[to_row][to_col] > 0){ return false;}

        // the method that the bishop uses to evaluate whether its move is legal
        // is to determine which quadrant the bishop is moving to, then check the 'up to the right' or equivalent square 
        // until reaching the target square. If any square is not empty, return false, finally check if the coordinates of the moving checker (the 'from' values)
        // are equal to the target values ('to')

        if(to_col > from_col){
            if(to_row > from_row){
                while(to_row > from_row ){
                    from_row++;
                    from_col++;
                    if(board_state[from_row][from_col] > 0){
                        return false;
                    }
                }
                if(from_col == to_col && from_row == to_row){return true;}
                else {return false;} 
            } 
            else if (to_row < from_row){
                while(to_row < from_row){
                    from_row--;
                    from_col++;
                    if(board_state[from_row][from_col] > 0){
                        return false;
                    } 
                }
                if(from_col == to_col && from_row == to_row){return true;}
                else {return false;}
            }
        }
        else if(to_col < from_col){
            if(to_row > from_row){
                while(to_row > from_row ){
                    from_row++;
                    from_col--;
                    if(board_state[from_row][from_col] > 0){
                        return false;
                    }
                }
                if(from_col == to_col && from_row == to_row){return true;}
                else {return false;}
            } 
            else if (to_row < from_row){
                while(to_row < from_row){
                    from_row--;
                    from_col--;
                    if(board_state[from_row][from_col] > 0){
                        return false;
                    }
                }
                if(from_col == to_col && from_row == to_row){return true;}
                else {return false;}
            }
        }
        return false;
    }
    
    private boolean black_bishop(int from_row, int from_col, int to_row, int to_col){
        // don't arrive at a square where there is a white space
        if(board_state[to_row][to_col] < 0){ return false;}

        // the method that the bishop uses to evaluate whether its move is legal
        // is to determine which quadrant the bishop is moving to, then check the 'up to the right' or equivalent square 
        // until reaching the target square. If any square is not empty, return false, finally check if the coordinates of the moving checker (the 'from' values)
        // are equal to the target values ('to')

        if(to_col > from_col){
            if(to_row > from_row){
                while(to_row > from_row ){
                    from_row++;
                    from_col++;
                    if(board_state[from_row][from_col] < 0){
                        return false;
                    }
                }
                if(from_col == to_col && from_row == to_row){return true;}
                else {return false;} 
            } 
            else if (to_row < from_row){
                while(to_row < from_row){
                    from_row--;
                    from_col++;
                    if(board_state[from_row][from_col] < 0){
                        return false;
                    } 
                }
                if(from_col == to_col && from_row == to_row){return true;}
                else {return false;}
            }
        }
        else if(to_col < from_col){
            if(to_row > from_row){
                while(to_row > from_row ){
                    from_row++;
                    from_col--;
                    if(board_state[from_row][from_col] < 0){
                        return false;
                    }
                }
                if(from_col == to_col && from_row == to_row){return true;}
                else {return false;}
            } 
            else if (to_row < from_row){
                while(to_row < from_row){
                    from_row--;
                    from_col--;
                    if(board_state[from_row][from_col] < 0){
                        return false;
                    }
                }
                if(from_col == to_col && from_row == to_row){return true;}
                else {return false;}
            }
        }
        return false;
    }

    private boolean white_queen(int from_row, int from_col, int to_row, int to_col){
        return white_rook(from_row, from_col, to_row, to_col) || white_bishop(from_row, from_col, to_row, to_col);
    }
    
    private boolean black_queen(int from_row, int from_col, int to_row, int to_col){
        return black_rook(from_row, from_col, to_row, to_col) || black_bishop(from_row, from_col, to_row, to_col);
    }

    private boolean white_king(int from_row, int from_col, int to_row, int to_col){
        if(board_state[to_row][to_col] > 0 ){ return false; }
        if(Math.abs(to_row - from_row) > 1 || Math.abs(to_col - from_col) > 1 ){ return false; }
        
        // check if the target is seen by an opposing piece
        if(checked_by_white(to_row, to_col)){

        }

        // Update king position : 
        w_king_col = to_col;
        w_king_row = to_row;
        System.out.println(w_king_col + "  wkc : wkr" + w_king_row);
        return true;
    }
    
    private boolean black_king(int from_row, int from_col, int to_row, int to_col){
        if(board_state[to_row][to_col] < 0 ){ return false; }
        if(Math.abs(to_row - from_row) > 1 || Math.abs(to_col - from_col) > 1 ){ return false; }
        
        
        // check if the target is seen by an opposing piece

        // Update king position : 
        b_king_col = to_col;
        b_king_row = to_row;
        System.out.println(b_king_col + "  wkc : wkr" + b_king_row);

        return true;
    }

    private boolean checked_by_white(int to_row, int to_col){
        
        // temp is a temporary view of selected squares on the board 
        int temp = to_row;
        temp--;
        

        // check vertically upwards
        while(temp > -1){
            if(board_state[to_row][to_col] == -2 || board_state[to_row][to_col] == -5){
                return true;
            }
            if(board_state[to_row][to_col] == -3 
                || board_state[to_row][to_col] == -4 
                || board_state[to_row][to_col] == -1
                || board_state[to_row][to_col] > 0){
                break; // hit white piece, or a black: bishop, pawn or king -> TODO handle kings later
            }
            temp--;
        }
        // check vertically downwards
        temp = to_row;
        temp++;
        while(temp < 8){
            if(board_state[to_row][to_col] == -2 || board_state[to_row][to_col] == -5){
                return true;
            }
            if(board_state[to_row][to_col] == -3 
                || board_state[to_row][to_col] == -4 
                || board_state[to_row][to_col] == -1
                || board_state[to_row][to_col] > 0){
                break; // hit white piece, or a black: bishop, pawn or king -> TODO handle kings later
            }
            temp++;
        }
        // check horizontally right
        while(temp > -1){
            if(board_state[to_row][to_col] == -2 || board_state[to_row][to_col] == -5){
                return true;
            }
            if(board_state[to_row][to_col] == -3 
                || board_state[to_row][to_col] == -4 
                || board_state[to_row][to_col] == -1
                || board_state[to_row][to_col] > 0){
                break; // hit white piece, or a black: bishop, pawn or king -> TODO handle kings later
            }
            temp++;
        }
        // check horizontally left
        while(temp < 8){
            if(board_state[to_row][to_col] == -2 || board_state[to_row][to_col] == -5){
                return true;
            }
            if(board_state[to_row][to_col] == -3 
                || board_state[to_row][to_col] == -4 
                || board_state[to_row][to_col] == -1
                || board_state[to_row][to_col] > 0){
                break; // hit white piece, or a black: bishop, pawn or king -> TODO handle kings later
            }
            temp++;
        }



        // Check diagonals : 
            // Quadrant 1

            // Quadrant 2 
            
            // Quadrant 3 
            
            // Quadrant 4 



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
