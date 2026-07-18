import java.util.ArrayList;
import java.util.List;

public class chess_engine {
    private int[][] board_state;
    private int elo;

    public chess_engine(int elo){
        this.board_state = new int[8][8];
        this.elo = elo;
    }
    
    public move_gen.Move move(int[][] board_state, boolean whites_move){
        System.out.println("Move");
        this.board_state = board_state;
        int depth;
        move_gen.Move best_move = null;
        if(this.elo == 1000){
            depth = 4;

            if(whites_move){
                List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move);
                int best_score = Integer.MIN_VALUE;
                for(move_gen.Move move_ : legal_moves){
                    int[][] next_board = make_move(board_state, move_);
                    // find the score of the next board, which is the move that appears with this current 'move_'
                    // the parameter is false here because this function is called after a player plays a move, 
                    // meaning that the whites_move boolean is actuall representative of the player who just 
                    // played rather than the player who is about to play.
                    int score = minimax(next_board, false, depth - 1);
                    if(score >  best_score){
                        best_score = score; 
                        best_move = move_;
                    }
                }
            }
            else{
                List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move);
                int best_score = Integer.MAX_VALUE;
                for(move_gen.Move move_ : legal_moves){
                    int[][] next_board = make_move(board_state, move_);
                    int score = minimax(next_board, true, depth - 1); // find the score of the next board, which is the move that appears with this current 'move_'
                    if(score < best_score){
                        best_score = score; 
                        best_move = move_;
                    }
                }

            }
            System.out.println("done");
            return best_move;
        }
        
        return null;
        
    }

    public int minimax(int[][] board_state, boolean whites_move, int depth){
        if(depth == 0){
            return evaluate(board_state);
        }
        else{
            if(whites_move){
                List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move);
                int best_score = Integer.MIN_VALUE;
                for(move_gen.Move move_ : legal_moves){
                    int[][] next_board = make_move(board_state, move_);
                    // find the score of the next board, which is the move that appears with this current 'move_'
                    // the parameter is false here because this function is called after a player plays a move, 
                    // meaning that the whites_move boolean is actuall representative of the player who just 
                    // played rather than the player who is about to play.
                    int score = minimax(next_board, false, depth - 1);
                    if(score >  best_score){
                        best_score = score; 
                    }
                }
                return best_score;
            }
            else{
                List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move);
                int best_score = Integer.MAX_VALUE;
                for(move_gen.Move move_ : legal_moves){
                    int[][] next_board = make_move(board_state, move_);
                    int score = minimax(next_board, true, depth - 1); // find the score of the next board, which is the move that appears with this current 'move_'
                    if(score < best_score){
                        best_score = score; 
                    }
                }
                return best_score;
            }
        }
    }
    
    public int evaluate(int[][] board_state){
        int score = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                score += board_state[i][j];
            }
        }   
        return score;
    }

    public void compute_values(int row, int col){
        // at this point we have a piece to evaluate the value of each move 
        // strategy : keep track of current best move, as well as current best move of other side 
        int max = 0;
        int min = 0;

        int piece = board_state[row][col];

        if(piece == -2){
            for(int i = 0; i < 3; i++){
                // TODO: evaluate_knight isn't implemented yet
                // evaluate_knight(row, col);
            }
        }
        
    }

    // make_move invariant is that the move_ is expected to be valid
    public int[][] make_move( int[][] board_state, move_gen.Move move_){
        
        int[][] new_state = new int[8][];
        for(int i = 0; i < 8; i++){
            new_state[i] = board_state[i].clone();
        }
        new_state[move_.toR][move_.toC] = new_state[move_.fromR][move_.fromC];
        new_state[move_.fromR][move_.fromC] = 0;
        return new_state;
    }

 
}
