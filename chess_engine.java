import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class chess_engine {
    private int[][] board_state;
    private int elo;
    private double[] piece_enum;
    private double[][] center_bias;
    private double[][] pawn_bias;
    private int search_depth = 6;
    // the last few moves the engine itself played, oldest overwritten first.
    // used to keep it from playing the same move a third time and repeating the position.
    private static final int HISTORY_SIZE = 6;
    private move_gen.Move[] recent_moves = new move_gen.Move[HISTORY_SIZE];
    private int recent_index = 0; // next slot to overwrite, wraps around

    public chess_engine(int elo){
        this.board_state = new int[8][8];
        this.pawn_bias = new double[][]{
            {0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00},
            {0.10,0.10,0.10,0.10,0.10,0.10,0.10,0.10},
            {0.20,0.20,0.20,0.20,0.20,0.20,0.20,0.20},
            {0.30,0.30,0.30,0.30,0.30,0.30,0.30,0.30},
            {0.40,0.40,0.40,0.40,0.40,0.40,0.40,0.40},
            {0.50,0.50,0.50,0.50,0.50,0.50,0.50,0.50},
            {0.60,0.60,0.60,0.60,0.60,0.60,0.60,0.60},
            {0.70,0.70,0.70,0.70,0.70,0.70,0.70,0.70}
        };
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
    
    // no en passant available (the opponent's last move was not a double pawn push)
    public move_gen.Move move(int[][] board_state, boolean whites_move){
        return move(board_state, whites_move, -1);
    }

    // ep_col is the file the opponent just double-pushed a pawn on, or -1 for none
    public move_gen.Move move(int[][] board_state, boolean whites_move, int ep_col){

        System.out.println("Move");
        
        long startTime = System.nanoTime();
        this.board_state = board_state;
        move_gen.Move best_move = null;
        double alpha = Integer.MIN_VALUE;
        double beta = Integer.MAX_VALUE;

        if(this.elo == 1000){
            // search_depth = 6;

            List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move, ep_col);
            // throw out anything already played twice in the last HISTORY_SIZE moves, that
            // third occurence is what completes a threefold repetition
            List<move_gen.Move> candidates = filter_repetitions(legal_moves);
            // the root loop has no cutoff break, but alpha/beta are still threaded into every
            // child minimax call, so a strong first move here raises alpha and prunes the siblings
            candidates = sort_moves_intuitively(board_state, candidates, whites_move);

            if(whites_move){
                double best_score = Integer.MIN_VALUE;

                for(move_gen.Move move_ : candidates){
                    int[][] next_board = make_move(board_state, move_);
                    // find the score of the next board, which is the move that appears with this current 'move_'
                    // the parameter is false here because this function is called after a player plays a move, 
                    // meaning that the whites_move boolean is actuall representative of the player who just 
                    // played rather than the player who is about to play.
                    double score = minimax(next_board, false, search_depth - 1, alpha, beta, ep_after(board_state, move_));


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
                double best_score = Integer.MAX_VALUE;
                for(move_gen.Move move_ : candidates){
                    int[][] next_board = make_move(board_state, move_);
                    double score = minimax(next_board, true, search_depth - 1,  alpha, beta, ep_after(board_state, move_)); // find the score of the next board, which is the move that appears with this current 'move_'
                    
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
            // best_move is null when there were no legal moves at all (checkmate/stalemate)
            if(best_move != null){
                record_move(best_move);
            }
            return best_move;
        }

        return null;

    }

    // move_gen.Move has no equals(), so the from/to squares get compared directly
    private boolean same_move(move_gen.Move a, move_gen.Move b){
        return a.fromR == b.fromR && a.fromC == b.fromC
            && a.toR   == b.toR   && a.toC   == b.toC;
    }

    // how many of the last HISTORY_SIZE engine moves were this same move
    private int repetition_count(move_gen.Move move_){
        int count = 0;
        for(move_gen.Move past : recent_moves){
            if(past != null && same_move(past, move_)){
                count++;
            }
        }
        return count;
    }

    private void record_move(move_gen.Move move_){
        recent_moves[recent_index] = move_;
        recent_index = (recent_index + 1) % HISTORY_SIZE;
    }

    private List<move_gen.Move> filter_repetitions(List<move_gen.Move> legal_moves){
        List<move_gen.Move> candidates = new ArrayList<>();
        for(move_gen.Move move_ : legal_moves){
            if(repetition_count(move_) < 2){
                candidates.add(move_);
            }
        }
        return candidates.isEmpty() ? legal_moves : candidates;
    }

    // a double pawn push is the only thing that grants the opponent an en passant reply.
    // reads the pre-move board on purpose, the from square is empty once the move is applied
    private int ep_after(int[][] board_state, move_gen.Move move_){
        if(Math.abs(board_state[move_.fromR][move_.fromC]) == 1
           && Math.abs(move_.toR - move_.fromR) == 2){
            return move_.toC;
        }
        return -1;
    }

    // no en passant available at this node
    public double minimax(int[][] board_state, boolean whites_move, int depth, double alpha, double beta){
        return minimax(board_state, whites_move, depth, alpha, beta, -1);
    }

    public double minimax(int[][] board_state, boolean whites_move, int depth, double alpha, double beta, int ep_col){
        if(depth == 0){
            return evaluate(board_state);
        }
        List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move, ep_col);
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
            // search the promising moves first so the alpha >= beta cutoff below fires early and
            // the rest of the list never gets expanded. only worth it when the subtrees underneath
            // are expensive, at depth 1 every child is just an evaluate() call and the sort would
            // cost about as much as it saves.
            if(depth >= 2){
                legal_moves = sort_moves_intuitively(board_state, legal_moves, whites_move);
            }
            if(whites_move){

                // List<move_gen.Move> legal_moves = move_gen.generateLegalMoves(board_state, whites_move);
                double best_score = Integer.MIN_VALUE;


                for(move_gen.Move move_ : legal_moves){
                    int[][] next_board = make_move(board_state, move_);
                    // find the score of the next board, which is the move that appears with this current 'move_'
                    // the parameter is false here because this function is called after a player plays a move, 
                    // meaning that the whites_move boolean is actuall representative of the player who just 
                    // played rather than the player who is about to play.

                    double score = minimax(next_board, false, depth - 1, alpha, beta, ep_after(board_state, move_));

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
                    double score = minimax(next_board, true, depth - 1, alpha, beta, ep_after(board_state, move_)); // find the score of the next board, which is the move that appears with this current 'move_'
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

    // pairs a move with its ordering score so the score is computed once per move
    // rather than once per comparison
    private static class scored_move {
        final move_gen.Move move_;
        final double score;
        scored_move(move_gen.Move move_, double score){
            this.move_ = move_;
            this.score = score;
        }
    }

    // orders legal_moves so the moves most likely to be good get searched first, which is what
    // makes the alpha-beta cutoff in minimax actually pay off. the tiers, best first:
    //   captures    - cheapest attacker taking the most valuable victim (MVV-LVA)
    //   promotions  - make_move auto queens, so this is roughly an 8 point swing
    //   checks      - forcing, so the reply list is small
    //   everything else, broken up by center_bias
    // the scores are absolute (every piece read goes through Math.abs), so they say "how good is
    // this for whoever is moving" and descending order is right for both the white and black
    // branches. whites_move is only used to tell gives_check who is doing the attacking.
    public List<move_gen.Move> sort_moves_intuitively(int[][] board_state, List<move_gen.Move> legal_moves, boolean whites_move){
        // our move can never move their king, so this square is the same for every move in the list
        int[] enemy_king = move_gen.findKing(board_state, !whites_move);

        List<scored_move> scored = new ArrayList<>();
        for(move_gen.Move move_ : legal_moves){
            int mover  = board_state[move_.fromR][move_.fromC];
            int victim = board_state[move_.toR][move_.toC];

            // an en passant capture leaves the destination square empty, so it would otherwise
            // score as a quiet move. the victim is always a pawn
            boolean is_ep        = Math.abs(mover) == 1 && move_.fromC != move_.toC && victim == 0;
            boolean is_capture   = victim != 0 || is_ep;
            boolean is_promotion = Math.abs(mover) == 1 && (move_.toR == 0 || move_.toR == 7);

            double score = 0;
            if(is_capture){
                double victim_value = is_ep ? piece_enum[1] : piece_enum[Math.abs(victim)];
                // victim * 10 outweighs any attacker, so a pawn taking a queen (1000 + 90 - 1)
                // sorts ahead of a queen taking a pawn (1000 + 10 - 9)
                score += 1000 + victim_value * 10 - piece_enum[Math.abs(mover)];
            }
            if(is_promotion){
                score += 500 + (piece_enum[5] - piece_enum[1]);
            }
            // gives_check copies the board, so only pay for it on the quiet moves. captures and
            // promotions already sort above every check anyway
            if(!is_capture && !is_promotion && gives_check(board_state, move_, whites_move, enemy_king)){
                score += 100;
            }
            if(score == 0){
                score = this.center_bias[move_.toR][move_.toC]; // free tiebreak among quiet moves
            }
            scored.add(new scored_move(move_, score));
        }

        // stable sort, so moves that tie keep generation order and the engine stays deterministic
        scored.sort((a, b) -> Double.compare(b.score, a.score));

        List<move_gen.Move> ordered = new ArrayList<>();
        for(scored_move s : scored){
            ordered.add(s.move_);
        }
        return ordered;
    }

    // does this move leave the opposing king attacked? make_move is reused rather than a hand
    // rolled copy so the castling rook hop and the auto queen are both accounted for, and it
    // copies instead of mutating so board_state is left alone
    private boolean gives_check(int[][] board_state, move_gen.Move move_, boolean whites_move, int[] enemy_king){
        if(enemy_king == null){ return false; } // no king on board (test positions)
        int[][] next_board = make_move(board_state, move_);
        return move_gen.isAttacked(next_board, enemy_king[0], enemy_king[1], whites_move);
    }

    public double evaluate(int[][] board_state){
        double score = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                double piece = board_state[i][j];
                if(piece < 0){
                    double piecevalue = 1 * piece_enum[(int) Math.abs(piece)];
                    if(piecevalue == 1){
                        piecevalue += this.pawn_bias[i][j];
                    }
                    if(piecevalue != 20){ // 20 = KING
                        piecevalue += this.center_bias[i][j];

                    }
                    else{
                        // piecevalue -= this.center_bias[i][j];

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
        // en passant: a pawn moving diagonally onto an empty square can only be en passant,
        // and the captured pawn is beside the destination rather than on it. must run before
        // the destination is overwritten
        if(Math.abs(new_state[move_.fromR][move_.fromC]) == 1
           && move_.fromC != move_.toC
           && new_state[move_.toR][move_.toC] == 0){
            new_state[move_.fromR][move_.toC] = 0;
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
