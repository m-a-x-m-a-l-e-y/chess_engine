// Static, board_render-independent chess move-legality rules.
// [positive = white; negative = black]
// 1: pawn  2: rook  3: knight  4: bishop  5: queen  6: king
public class valid_move_utils {

    public valid_move_utils() {}
    private int b_king_row = 0;
    private int b_king_col = 4;
    private int w_king_row = 7;
    private int w_king_col = 4;


    //
    //
    //  TO DO : 
    // - Add check sensitivity to knights, currently the only piece not considered
    // - add checkmate analysis -> a pre-task to this would be to add a way to check for valid moves
    // 
    //


    // ==========================================================================
    // PAWN
    // ==========================================================================

    public boolean white_pawn(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        // should consolidate functions of both pieces to one function but for now this is fine just trying to make it possible to demonstrate the chess engine
        if(to_row == 7 || to_row == 6){
            return false;
        }
        if(from_col == to_col){
            if(board_state[to_row][to_col] == 0){
                if(from_row == (6)){
                    if(board_state[5][from_col] == 0 && from_col == to_col && (from_row - to_row) < 3){
                        return white_move_is_safe(board_state, from_row, from_col, to_row, to_col);
                    }
                }
                else{
                    if(from_row > to_row && (from_row - to_row) < 2){
                        return white_move_is_safe(board_state, from_row, from_col, to_row, to_col);
                    }
                }
            }
        }
        else if( Math.abs(to_col - from_col) == 1 && (from_row- to_row) == 1){
            if(board_state[to_row][to_col] < 0){
                return white_move_is_safe(board_state, from_row, from_col, to_row, to_col);
            }
        }
        return false;
    }

    public boolean black_pawn(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        if(to_row == 0 || to_row == 1){
            return false;
        }

        if(from_col == to_col){
            if(board_state[to_row][to_col] == 0){
                if(from_row == (1)){
                    if(board_state[2][from_col] == 0 && (to_row - from_row) < 3){
                        return black_move_is_safe(board_state, from_row, from_col, to_row, to_col);
                    }
                }
                else if(to_col == from_col){
                    if(from_row < to_row && (to_row - from_row) < 2){
                        return black_move_is_safe(board_state, from_row, from_col, to_row, to_col);
                    }
                }
            }
        }
        else if( Math.abs(to_col - from_col) == 1 && ( to_row - from_row) == 1){
            if(board_state[to_row][to_col] > 0){
                return black_move_is_safe(board_state, from_row, from_col, to_row, to_col);
            }
        }
        return false;
    }

    // ==========================================================================
    // ROOK
    // ==========================================================================

    public boolean white_rook(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
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
        if(from_col != to_col){ return (Math.abs(col - to_col) == 0) && white_move_is_safe(board_state, from_row, from_col, to_row, to_col);}
        if(from_row != to_row){ return (Math.abs(row - to_row) == 0) && white_move_is_safe(board_state, from_row, from_col, to_row, to_col);}

        return false;
    }

    public boolean black_rook(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
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
        if(from_col != to_col){ return (Math.abs(col - to_col) == 0) && black_move_is_safe(board_state, from_row, from_col, to_row, to_col);}
        if(from_row != to_row){ return (Math.abs(row - to_row) == 0) && black_move_is_safe(board_state, from_row, from_col, to_row, to_col);}

        return false;
    }

    // ==========================================================================
    // KNIGHT
    // ==========================================================================

    public boolean white_knight(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        if(board_state[to_row][to_col] > 0){ return false; }

        if(to_col - from_col == 2 || to_col - from_col == -2){
            if(to_row - from_row == 1 || to_row - from_row == -1){
                return white_move_is_safe(board_state, from_row, from_col, to_row, to_col);
            }
        }
        else if(to_row - from_row == 2 || to_row - from_row == -2){
            if(to_col - from_col == 1 || to_col - from_col == -1){
                return white_move_is_safe(board_state, from_row, from_col, to_row, to_col);
            }
        }
        return false;
    }

    public boolean black_knight(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        if(board_state[to_row][to_col] < 0){ return false; }

        if(to_col - from_col == 2 || to_col - from_col == -2){
            if(to_row - from_row == 1 || to_row - from_row == -1){
                return black_move_is_safe(board_state, from_row, from_col, to_row, to_col);
            }
        }
        else if(to_row - from_row == 2 || to_row - from_row == -2){
            if(to_col - from_col == 1 || to_col - from_col == -1){
                return black_move_is_safe(board_state, from_row, from_col, to_row, to_col);
            }
        }
        return false;

    }

    // ==========================================================================
    // BISHOP
    // ==========================================================================

    public boolean white_bishop(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        // don't arrive at a square where there is a white space
        if(board_state[to_row][to_col] > 0){ return false;}

        // the from_row/from_col below are walked toward the target, so remember the
        // real origin square for the king-safety check
        int origin_row = from_row;
        int origin_col = from_col;

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
                if(from_col == to_col && from_row == to_row){return white_move_is_safe(board_state, origin_row, origin_col, to_row, to_col);}
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
                if(from_col == to_col && from_row == to_row){return white_move_is_safe(board_state, origin_row, origin_col, to_row, to_col);}
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
                if(from_col == to_col && from_row == to_row){return white_move_is_safe(board_state, origin_row, origin_col, to_row, to_col);}
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
                if(from_col == to_col && from_row == to_row){return white_move_is_safe(board_state, origin_row, origin_col, to_row, to_col);}
                else {return false;}
            }
        }
        return false;
    }

    public boolean black_bishop(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        // don't arrive at a square where there is a white space
        if(board_state[to_row][to_col] < 0){ return false;}

        // the from_row/from_col below are walked toward the target, so remember the
        // real origin square for the king-safety check
        int origin_row = from_row;
        int origin_col = from_col;

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
                if(from_col == to_col && from_row == to_row){return black_move_is_safe(board_state, origin_row, origin_col, to_row, to_col);}
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
                if(from_col == to_col && from_row == to_row){return black_move_is_safe(board_state, origin_row, origin_col, to_row, to_col);}
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
                if(from_col == to_col && from_row == to_row){return black_move_is_safe(board_state, origin_row, origin_col, to_row, to_col);}
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
                if(from_col == to_col && from_row == to_row){return black_move_is_safe(board_state, origin_row, origin_col, to_row, to_col);}
                else {return false;}
            }
        }
        return false;
    }

    // ==========================================================================
    // QUEEN
    // ==========================================================================

    public boolean white_queen(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        return white_rook(board_state, from_row, from_col, to_row, to_col) || white_bishop(board_state, from_row, from_col, to_row, to_col);
    }

    public boolean black_queen(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        return black_rook(board_state, from_row, from_col, to_row, to_col) || black_bishop(board_state, from_row, from_col, to_row, to_col);
    }

    // ==========================================================================
    // KING
    // ==========================================================================

    public boolean white_king(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        if(board_state[to_row][to_col] > 0 ){ return false; }
        if(Math.abs(to_row - from_row) > 1 || Math.abs(to_col - from_col) > 1 ){ return false; }

        // simulate the king move on a copy (king removed from origin) and check the
        // destination is not seen by an opposing piece
        int[][] b = copy_board(board_state);
        b[to_row][to_col]     = b[from_row][from_col];
        b[from_row][from_col] = 0;
        boolean safe = !checked_by_black(b, to_row, to_col);

        // move is legal, update the tracked white king position
        if(safe){
            w_king_row = to_row;
            w_king_col = to_col;
        }
        return safe;
    }

    public boolean black_king(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        if(board_state[to_row][to_col] < 0 ){ return false; }
        if(Math.abs(to_row - from_row) > 1 || Math.abs(to_col - from_col) > 1 ){ return false; }

        // simulate the king move on a copy (king removed from origin) and check the
        // destination is not seen by an opposing piece
        int[][] b = copy_board(board_state);
        b[to_row][to_col]     = b[from_row][from_col];
        b[from_row][from_col] = 0;
        boolean safe = !checked_by_white(b, to_row, to_col);

        // move is legal, update the tracked black king position
        if(safe){
            b_king_row = to_row;
            b_king_col = to_col;
        }
        return safe;
    }

    // ==========================================================================
    // CHECK DETECTION
    // ==========================================================================

    public boolean checked_by_black(int[][] board_state, int to_row, int to_col){
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
                        break;
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
                        break;
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
                        break;
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
                        break;
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

    public boolean checked_by_white(int[][] board_state, int to_row, int to_col){
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
                        break;
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
                        break;
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
                        break;
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
                        break;
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
                            if(piece < 0 || piece == 3 || piece == 2 || piece == 1){
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


    // ==========================================================================
    // KING-SAFETY HELPERS
    // ==========================================================================

    // Utility function for creating a new board 'b' in order to not mutate the original boardstate when checking if this
    // move puts the king in check
    private int[][] copy_board(int[][] board_state){
        int[][] copy = new int[board_state.length][];
        for(int r = 0; r < board_state.length; r++){
            copy[r] = board_state[r].clone();
        }
        return copy;
    }

    // Returns whether or not the move on the given board doesn't leave the mover's king in check
    private boolean white_move_is_safe(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        int[][] b = copy_board(board_state);
        b[to_row][to_col] = b[from_row][from_col];
        b[from_row][from_col] = 0;
        return !checked_by_black(b, w_king_row, w_king_col);
    }

    // Returns whether or not the move on the given board doesn't leave the mover's king in check
    private boolean black_move_is_safe(int[][] board_state, int from_row, int from_col, int to_row, int to_col){
        int[][] b = copy_board(board_state);
        b[to_row][to_col] = b[from_row][from_col];
        b[from_row][from_col] = 0;
        return !checked_by_white(b, b_king_row, b_king_col);
    }

}
