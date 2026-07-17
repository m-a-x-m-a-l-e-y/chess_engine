import java.util.ArrayList;
import java.util.List;

public class chess_engine {
    private int[][] board_state;
    private int elo;

    public chess_engine(int elo){
        this.board_state = new int[8][8];
        this.elo = elo;
    }
    
    public int[] move(int[][] board_state, boolean whites_move){
        this.board_state = board_state;
        if(this.elo == 1000){
            if(whites_move){
                List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move);
                minimax
            }
            else{
                List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move);
            }
            

            



            for(move_gen.Move mv : legal_moves){
                int score = 0;
                score += -1* (board_state[mv.toR][mv.toC]);
            }











        }

        // TEMP
            int[] fake = new int[4];
            fake[0] = 3;
            fake[3] = 3;
            return fake;
        //
    }

    public int minimax(int[][] board_state, int depth){
        //
        return 0;
    }

    public void compute_values(int row, int col){
        // at this point we have a piece to evaluate the value of each move 
        // strategy : keep track of current best move, as well as current best move of other side 
        int max = 0;
        int min = 0;

        int piece = board_state[row][col];

        if(piece == -2){
            for(int i = 0; i < 3; i++){
                evaluate_knight(row, col);
            }
        }
        
    }

    public void evaluate_knight(int row, int col){
        
    }

    // public record Move(int from_row, int from_col, int to_row, int to_col){}
    

    // The first stage of the engine analysis
    // public List<Move> generate_legal_moves(int[][]board_state){

    // }
}
