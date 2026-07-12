// Static, board_render-independent chess move-legality rules.
// [positive = white; negative = black]
// 1: pawn  2: rook  3: knight  4: bishop  5: queen  6: king
public class valid_move_utils {

    private valid_move_utils() {}

    // ==========================================================================
    // PAWN
    // ==========================================================================

    public static boolean white_pawn(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
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

    public static boolean black_pawn(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
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

    // ==========================================================================
    // ROOK
    // ==========================================================================

    public static boolean white_rook(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
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

    public static boolean black_rook(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
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

    // ==========================================================================
    // KNIGHT
    // ==========================================================================

    public static boolean white_knight(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
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

    public static boolean black_knight(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
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

    // ==========================================================================
    // BISHOP
    // ==========================================================================

    public static boolean white_bishop(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
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

    public static boolean black_bishop(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
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

    // ==========================================================================
    // QUEEN
    // ==========================================================================

    public static boolean white_queen(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        return white_rook(board_state, from_row, from_col, to_row, to_col) || white_bishop(board_state, from_row, from_col, to_row, to_col);
    }

    public static boolean black_queen(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        return black_rook(board_state, from_row, from_col, to_row, to_col) || black_bishop(board_state, from_row, from_col, to_row, to_col);
    }

    // ==========================================================================
    // KING
    // ==========================================================================

    public static boolean white_king(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        if(board_state[to_row][to_col] > 0 ){ return false; }
        if(Math.abs(to_row - from_row) > 1 || Math.abs(to_col - from_col) > 1 ){ return false; }

        // check if the target is seen by an opposing piece
        if(checked_by_black(board_state, to_row, to_col)){
            return false;
        }

        return true;
    }

    public static boolean black_king(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        if(board_state[to_row][to_col] < 0 ){ return false; }
        if(Math.abs(to_row - from_row) > 1 || Math.abs(to_col - from_col) > 1 ){ return false; }

        // check if the target is seen by an opposing piece
        if(checked_by_white(board_state, to_row, to_col)){
            return false;
        }

        return true;
    }

    // ==========================================================================
    // CHECK DETECTION
    // ==========================================================================

    public static boolean checked_by_black(int[][] board_state, int to_row, int to_col){
        //
        // VERTICALS AND HORIZONTALS
        //
        int piece;
        int temp;
        int temp_x;
        int temp_y;

                // temp is a temporary view of selected squares on the board
                temp = to_row;
                temp--;

                //System.out.println("1");
                // check vertically upwards
                while(temp > -1){
                    if(board_state[temp][to_col] == -6 && (to_row - temp) == 1){return true;} // opposite king
                    if(board_state[temp][to_col] == -2 || board_state[temp][to_col] == -5){
                        //System.out.println(board_state[temp][to_col]);
                        return true;
                    }
                    if(board_state[temp][to_col] == -3
                        || board_state[temp][to_col] == -4
                        || board_state[temp][to_col] == -1
                        || board_state[temp][to_col] > 0){
                        break; // hit white piece, or a black: bishop, pawn or king -> TODO handle kings later
                    }
                    temp--;
                }
                // check vertically downwards
                temp = to_row;
                temp++;
                //System.out.println("1");
                while(temp < 8){
                    if(board_state[temp][to_col] == -6 && (temp - to_row) == 1){return true;}
                    if(board_state[temp][to_col] == -2 || board_state[temp][to_col] == -5){
                        //System.out.println(board_state[temp][to_col]);
                        return true;
                    }
                    if(board_state[temp][to_col] == -3
                        || board_state[temp][to_col] == -4
                        || board_state[temp][to_col] == -1
                        || board_state[temp][to_col] > 0){
                        break; // hit white piece, or a black: bishop, pawn or king -> TODO handle kings later
                    }
                    temp++;
                }
                // check horizontally right
                temp = to_col;
                temp--;
                //System.out.println("1");
                while(temp > -1){
                    if(board_state[to_row][temp] == -6 && (to_col - temp) == 1){return true;}
                    if(board_state[to_row][temp] == -2 || board_state[to_row][temp] == -5){
                        //System.out.println(board_state[to_row][temp]);
                        return true;
                    }
                    if(board_state[to_row][temp] == -3
                        || board_state[to_row][temp] == -4
                        || board_state[to_row][temp] == -1
                        || board_state[to_row][temp] > 0){
                        break; // hit white piece, or a black: bishop, pawn or king -> TODO handle kings later
                    }
                    temp--;
                }
                // check horizontally left
                temp = to_col;
                temp++;
                temp = to_col;
                //System.out.println("1");

                while(temp < 8){
                    if(board_state[to_row][temp] == -6 && (temp - to_col) == 1){return true;}
                    if(board_state[to_row][temp] == -2 || board_state[to_row][temp] == -5){
                        //System.out.println(board_state[to_row][temp]);
                        return true;
                    }
                    if(board_state[to_row][temp] == -3
                        || board_state[to_row][temp] == -4
                        || board_state[to_row][temp] == -1
                        || board_state[to_row][temp] > 0){
                        break; // hit white piece, or a black: bishop, pawn or king -> TODO handle kings later
                    }
                    temp++;
                }
                //System.out.println("1");
        //
        // END OF VERTICAL AND HORIZONTAL CHECKING
        //

        //
        // CHECKING DIAGONALS :
        //
                                    //System.out.println("diag");

            // Quadrant 1

                temp_x = to_col;
                temp_y = to_row;
                temp_y--;
                temp_x++;
                if((temp_x > -1 && temp_x < 8 && temp_y > -1 && temp_y < 8)){
                    piece = board_state[temp_y][temp_x];
                    if(piece == -1){return true;} // Quadrants 2 and 1 for white king, 3 and 4 for black king because pawns only capture one way

                    while(temp_x < 8 && temp_y > -1){
                        piece = board_state[temp_y][temp_x];
                        
                        if(piece == -6 && (to_row - temp_y) == 1){return true;}
                        if(piece == -4 || piece == -5){
                            return true;
                        }
                        if(piece > 0 || piece == -3 || piece == -2 || piece == -1 || piece == -6){
                            break;
                        }
                        temp_y--;
                        temp_x++;
                    }
                }
                                                 //System.out.println("diag");

            // Quadrant 2

                temp_x = to_col;
                temp_y = to_row;
                temp_y--;
                temp_x--;

                if((temp_x > -1 && temp_x < 8 && temp_y > -1 && temp_y < 8)){
                    piece = board_state[temp_y][temp_x];
                    if(piece == -1){return true;}

                    while(temp_x > -1 && temp_y > -1){
                            piece = board_state[temp_y][temp_x];

                            if(piece == -6 && (to_row - temp_y) == 1){return true;} // check for kings two spaces away
                            if(piece == -4 || piece == -5){
                                System.out.println(board_state[temp_y][temp_x]);
                                return true;
                            }
                            if(piece > 0 || piece == -3 || piece == -2 || piece == -1 || piece == -6){ // its okay to check -1 here because we have an initial check
                                break;
                            }
                            temp_y--;
                            temp_x--;
                    }
                }
                        //System.out.println("2");


            // Quadrant 3

                temp_x = to_col;
                temp_y = to_row;
                temp_y++;
                temp_x--;

                if((temp_x > -1 && temp_x < 8 && temp_y > -1 && temp_y < 8)){
                    piece = board_state[temp_y][temp_x];

                    while(temp_x > -1 && temp_y < 8){
                            piece = board_state[temp_y][temp_x];

                            if(piece == -6 && (temp_y - to_row) == 1){return true;}
                            if(piece == -4 || piece == -5){
                                return true;
                            }
                            if(piece > 0 || piece == -3 || piece == -2 || piece == -1 || piece == -6){
                                break;
                            }
                            temp_y++;
                            temp_x--;
                    }
                }
                        //System.out.println("3");
            // Quadrant 4

                temp_x = to_col;
                temp_y = to_row;
                temp_y++;
                temp_x++;

                if((temp_x > -1 && temp_x < 8 && temp_y > -1 && temp_y < 8)){
                    piece = board_state[temp_y][temp_x];

                    while(temp_x < 8 && temp_y < 8){
                            piece = board_state[temp_y][temp_x];

                            if(piece == -6 && (temp_y - to_row) == 1){return true;}
                            if(piece == -4 || piece == -5){
                                return true;
                            }
                            if(piece > 0 || piece == -3 || piece == -2 || piece == -1 || piece == -6){
                                break;
                            }
                            temp_y++;
                            temp_x++;
                    }
                }

            //

        //
        // Done with diagonal checks
        //


        return false;
    }

    public static boolean checked_by_white(int[][] board_state, int to_row, int to_col){
        //
        // VERTICALS AND HORIZONTALS
        //
        int piece;
        int temp;
        int temp_x;
        int temp_y;

                // temp is a temporary view of selected squares on the board
                temp = to_row;
                temp--;

                //System.out.println("1");
                // check vertically upwards
                while(temp > -1){
                    if(board_state[temp][to_col] == 6 && (to_row - temp) == 1){return true;} // opposite king
                    if(board_state[temp][to_col] == 2 || board_state[temp][to_col] == 5){
                        //System.out.println(board_state[temp][to_col]);
                        return true;
                    }
                    if(board_state[temp][to_col] == 3
                        || board_state[temp][to_col] == 4
                        || board_state[temp][to_col] == 1
                        || board_state[temp][to_col] < 0){
                        break; // hit black piece, or a white: bishop, pawn or king -> TODO handle kings later
                    }
                    temp--;
                }
                // check vertically downwards
                temp = to_row;
                temp++;
                //System.out.println("1");
                while(temp < 8){
                    if(board_state[temp][to_col] == 6 && (temp - to_row) == 1){return true;}
                    if(board_state[temp][to_col] == 2 || board_state[temp][to_col] == 5){
                        //System.out.println(board_state[temp][to_col]);
                        return true;
                    }
                    if(board_state[temp][to_col] == 3
                        || board_state[temp][to_col] == 4
                        || board_state[temp][to_col] == 1
                        || board_state[temp][to_col] < 0){
                        break; // hit black piece, or a white: bishop, pawn or king -> TODO handle kings later
                    }
                    temp++;
                }
                // check horizontally right
                temp = to_col;
                temp--;
                //System.out.println("1");
                while(temp > -1){
                    if(board_state[to_row][temp] == 6 && (to_col - temp) == 1){return true;}
                    if(board_state[to_row][temp] == 2 || board_state[to_row][temp] == 5){
                        //System.out.println(board_state[to_row][temp]);
                        return true;
                    }
                    if(board_state[to_row][temp] == 3
                        || board_state[to_row][temp] == 4
                        || board_state[to_row][temp] == 1
                        || board_state[to_row][temp] < 0){
                        break; // hit black piece, or a white: bishop, pawn or king -> TODO handle kings later
                    }
                    temp--;
                }
                // check horizontally left
                temp = to_col;
                temp++;
                temp = to_col;
                //System.out.println("1");

                while(temp < 8){
                    if(board_state[to_row][temp] == 6 && (temp - to_col) == 1){return true;}
                    if(board_state[to_row][temp] == 2 || board_state[to_row][temp] == 5){
                        //System.out.println(board_state[to_row][temp]);
                        return true;
                    }
                    if(board_state[to_row][temp] == 3
                        || board_state[to_row][temp] == 4
                        || board_state[to_row][temp] == 1
                        || board_state[to_row][temp] < 0){
                        break; // hit black piece, or a white: bishop, pawn or king -> TODO handle kings later
                    }
                    temp++;
                }
                //System.out.println("1");
        //
        // END OF VERTICAL AND HORIZONTAL CHECKING
        //

        //
        // CHECKING DIAGONALS :
        //
                                    //System.out.println("diag");

            // Quadrant 1

                temp_x = to_col;
                temp_y = to_row;
                temp_y--;
                temp_x++;
                if((temp_x > -1 && temp_x < 8 && temp_y > -1 && temp_y < 8)){
                    piece = board_state[temp_y][temp_x];

                    while(temp_x < 8 && temp_y > -1){
                        piece = board_state[temp_y][temp_x];
                        if(piece == 6 && (to_row - temp_y) == 1){return true;}
                        if(piece == 4 || piece == 5){
                            return true;
                        }
                        if(piece < 0 || piece == 3 || piece == 2 || piece == 1){
                            break;
                        }
                        temp_y--;
                        temp_x++;
                    }
                }
                                                 //System.out.println("diag");

            // Quadrant 2

                temp_x = to_col;
                temp_y = to_row;
                temp_y--;
                temp_x--;

                if((temp_x > -1 && temp_x < 8 && temp_y > -1 && temp_y < 8)){

                    piece = board_state[temp_y][temp_x];

                    while(temp_x > -1 && temp_y > -1){
                            piece = board_state[temp_y][temp_x];
                            if(piece == 6 && (to_row - temp_y) == 1){return true;}
                            if(piece == 4 || piece == 5){
                                //System.out.println(board_state[temp_y][temp_x]);
                                return true;
                            }
                            if(piece < 0 || piece == 3 || piece == 2 || piece == 1){ // its okay to check 1 here because we have an initial check
                                break;
                            }
                            temp_y--;
                            temp_x--;
                    }
                }
                        //System.out.println("2");


            // Quadrant 3

                temp_x = to_col;
                temp_y = to_row;
                temp_y++;
                temp_x--;

                if((temp_x > -1 && temp_x < 8 && temp_y > -1 && temp_y < 8)){
                    piece = board_state[temp_y][temp_x];
                    if(piece == 1){return true;} // Quadrants 2 and 1 for white king, 3 and 4 for black king because pawns only capture one way

                    while(temp_x > -1 && temp_y < 8){
                            piece = board_state[temp_y][temp_x];
                            if(piece == 6 && (temp_y - to_row) == 1){return true;}
                            if(piece == 4 || piece == 5){
                                return true;
                            }
                            if(piece < 0 || piece == 3 || piece == 2 || piece == 1){
                                break;
                            }
                            temp_y++;
                            temp_x--;
                    }
                }
                        //System.out.println("3");
            // Quadrant 4

                temp_x = to_col;
                temp_y = to_row;
                temp_y++;
                temp_x++;

                if((temp_x > -1 && temp_x < 8 && temp_y > -1 && temp_y < 8)){
                    piece = board_state[temp_y][temp_x];
                    if(piece == 1){return true;} // Quadrants 2 and 1 for white king, 3 and 4 for black king because pawns only capture one way

                    while(temp_x < 8 && temp_y < 8){
                            piece = board_state[temp_y][temp_x];
                            if(piece == 6 && (temp_y - to_row) == 1){return true;}
                            if(piece == 4 || piece == 5){
                                return true;
                            }
                            if(piece < 0 || piece == 3 || piece == 2 || piece == 1){
                                break;
                            }
                            temp_y++;
                            temp_x++;
                    }
                }

            //

        //
        // Done with diagonal checks
        //


        return false;
    }
}
