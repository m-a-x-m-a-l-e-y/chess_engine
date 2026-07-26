import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class chess_engine {
    private int[][] board_state;
    private int elo;
    private double[] piece_enum;
    private double[][] center_bias;
    private int search_depth = 6;

    public chess_engine(int elo){
        this.board_state = new int[8][8];
        this.center_bias = new double[][] {
            {0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00},
            {0.00,0.10,0.10,0.10,0.10,0.10,0.10,0.00},
            {0.00,0.10,0.20,0.20,0.20,0.20,0.10,0.00},
            {0.00,0.10,0.20,0.30,0.30,0.20,0.10,0.00},
            {0.00,0.10,0.20,0.30,0.30,0.20,0.10,0.00},
            {0.00,0.10,0.20,0.20,0.20,0.20,0.10,0.00},
            {0.00,0.10,0.10,0.10,0.10,0.10,0.10,0.00},
            {0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00}
        };
        this.elo = elo;
        this.piece_enum = new double[]{0, 1, 5, 3, 3, 9, 20};
        // Below are the values of pieces
        // piece_enum.put(0.0,0.0); 
        // piece_enum.put(1.0,1.0); 
        // piece_enum.put(2.0,5.0); 
        // piece_enum.put(3.0,3.0); 
        // piece_enum.put(4.0,3.0); 
        // piece_enum.put(5.0,9.0); 
        // piece_enum.put(6.0,10.0); 
    }
    
    public move_gen.Move move(int[][] board_state, boolean whites_move){

        System.out.println("Move");
        
        long startTime = System.nanoTime();
        this.board_state = board_state;
        move_gen.Move best_move = null;
        double alpha = Integer.MIN_VALUE;
        double beta = Integer.MAX_VALUE;

        if(this.elo == 1000){
            // search_depth = 6;

            if(whites_move){
                List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move);
                double best_score = Integer.MIN_VALUE;
                
                for(move_gen.Move move_ : legal_moves){
                    int[][] next_board = make_move(board_state, move_);
                    // find the score of the next board, which is the move that appears with this current 'move_'
                    // the parameter is false here because this function is called after a player plays a move, 
                    // meaning that the whites_move boolean is actuall representative of the player who just 
                    // played rather than the player who is about to play.
                    double score = minimax(next_board, false, search_depth - 1, alpha, beta);


                    // The way that alpha and beta work is that if it is black's turn, and we are now checkinga branch rightwards, if we reach a black value 
                    // which is less than the maximum value of the left tree, don't explore the rest of the tree because the top node will be <= this_val,
                    // so white inherently wouldn't play that way

                    alpha = Math.max(alpha, score); 

                    if(score >  best_score){
                        best_score = score; 
                        best_move = move_;
                    }
                }
            }
            else{
                List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move);
                double best_score = Integer.MAX_VALUE;
                for(move_gen.Move move_ : legal_moves){
                    int[][] next_board = make_move(board_state, move_);
                    double score = minimax(next_board, true, search_depth - 1,  alpha, beta); // find the score of the next board, which is the move that appears with this current 'move_'
                    
                    beta = Math.min(beta,score);
                    if(score < best_score){
                        best_score = score; 
                        best_move = move_;
                    }
                }

            }
            System.out.println("done");
            long elapsed = System.nanoTime() - startTime;
            System.out.println("Engine move generated in " + (elapsed / 1_000_000.0) + " ms");
            return best_move;
        }
        
        return null;
        
    }

    public double minimax(int[][] board_state, boolean whites_move, int depth, double alpha, double beta){
        if(depth == 0){
            return evaluate(board_state);
        }
        List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move);
        if(legal_moves.size() == 0){
            // Right now checkmates are valued equally no matter
            // which one is faster, 
            if(whites_move){
                return (- Integer.MAX_VALUE + ( depth));
            }
            else{
                return ( Integer.MAX_VALUE - ( depth ));
            }
        }
        
        else{
            if(whites_move){

                // List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move);
                double best_score = Integer.MIN_VALUE;


                for(move_gen.Move move_ : legal_moves){
                    int[][] next_board = make_move(board_state, move_);
                    // find the score of the next board, which is the move that appears with this current 'move_'
                    // the parameter is false here because this function is called after a player plays a move, 
                    // meaning that the whites_move boolean is actuall representative of the player who just 
                    // played rather than the player who is about to play.

                    double score = minimax(next_board, false, depth - 1, alpha, beta);
                    
                    if(score >  best_score){
                        best_score = score; 
                    }
                    alpha = Math.max(score, alpha);
                    if(alpha >= beta){
                        break; // Exit because this would mean a player took an un optiomal move
                    }

                }
                return best_score;
            }
            else{
                // List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move);
                double best_score = Integer.MAX_VALUE;
                for(move_gen.Move move_ : legal_moves){
                    int[][] next_board = make_move(board_state, move_);
                    double score = minimax(next_board, true, depth - 1, alpha, beta); // find the score of the next board, which is the move that appears with this current 'move_'
                    if(score < best_score){
                        best_score = score; 
                    }
                    beta = Math.min(score, beta);
                    if(alpha >= beta){
                        break; // Exit because this would mean a player took an un optiomal move
                    }
                    
                }
                return best_score;
            }
        }
    }
    
    public double evaluate(int[][] board_state){
        double score = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                double piece = board_state[i][j];
                if(piece < 0){
                    // if(piece == -1 && i == 7){
                    //     piecevalue = 9;
                    // }
                    // else{
                    //     piecevalue = 1 * piece_enum[Math.abs(piece)];
                    // }
                    double piecevalue = 1 * piece_enum[(int) Math.abs(piece)];
                    if(piecevalue != 20){ // 20 = KING
                        piecevalue += this.center_bias[i][j];

                    }
                    else{
                        piecevalue -= this.center_bias[i][j];

                    }
                    score -= piecevalue;
                    // black trim scenario, but basically if the lowest value seen is less than the highest value
                    // if(beta > alpha){j = 8;}
                }
                else{
                    double piecevalue = 1 * piece_enum[(int) Math.abs(piece)];
                    // if(piece == 1 && i == 0){
                    //     piecevalue = 9;
                    // else{
                    //     
                    // }
                    piecevalue += this.center_bias[i][j];
                    score += piecevalue;
                    // if(beta > alpha){j = 8;}

                }
            }
            // where I should break to on a prune
        }   
        return score;
    }

    // make_move invariant is that the move_ is expected to be valid
    public int[][] make_move( int[][] board_state, move_gen.Move move_){
        
        int[][] new_state = new int[8][];
        for(int i = 0; i < 8; i++){
            new_state[i] = board_state[i].clone();
        }
        new_state[move_.toR][move_.toC] = new_state[move_.fromR][move_.fromC];
        new_state[move_.fromR][move_.fromC] = 0;
        // castling arrives as a two-square king move, the rook has to follow
        if(Math.abs(new_state[move_.toR][move_.toC]) == 6 && Math.abs(move_.toC - move_.fromC) == 2){
            int rook_from = (move_.toC == 6) ? 7 : 0;
            int rook_to   = (move_.toC == 6) ? 5 : 3;
            new_state[move_.toR][rook_to]   = new_state[move_.toR][rook_from];
            new_state[move_.toR][rook_from] = 0;
        }
        if(new_state[move_.toR][move_.toC] == -1 && move_.toR == 7){
            new_state[move_.toR][move_.toC] = -5;
        }
        if(new_state[move_.toR][move_.toC] == 1 && move_.toR == 0){
            new_state[move_.toR][move_.toC] = 5;
        }
        return new_state;
    }

 
}
