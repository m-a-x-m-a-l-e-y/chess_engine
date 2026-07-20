import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class chess_engine {
    private int[][] board_state;
    private int elo;
    private Map<Integer, Integer> piece_enum;

    public chess_engine(int elo){
        this.board_state = new int[8][8];
        this.elo = elo;
        this.piece_enum = new HashMap<>();
        piece_enum.put(0,0); 
        piece_enum.put(1,1); 
        piece_enum.put(2,5); 
        piece_enum.put(3,3); 
        piece_enum.put(4,3); 
        piece_enum.put(5,9); 
        piece_enum.put(6,10); 
    }
    
    public move_gen.Move move(int[][] board_state, boolean whites_move){
        System.out.println("Move");
        long startTime = System.nanoTime();
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
            long elapsed = System.nanoTime() - startTime;
            System.out.println("Engine move generated in " + (elapsed / 1_000_000.0) + " ms");
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
                int piece = board_state[i][j];
                if(piece < 0){
                    score -= piece_enum.get(Math.abs(piece));
                }
                else{
                    score += piece_enum.get(Math.abs(piece));
                }
            }
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
        return new_state;
    }

 
}
