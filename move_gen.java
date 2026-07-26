import java.util.ArrayList;
import java.util.List;
// This file is vibecoded unlike other files


// Legal move generation for one side.
// Board encoding (matches chess_board.java):
//   row 0 = black back rank (top), row 7 = white back rank (bottom)
//   positive = white, negative = black, 0 = empty
//   1 pawn  2 rook  3 knight  4 bishop  5 queen  6 king
// White pawns move toward row 0 (row decreasing); black pawns toward row 7.
//
// Design: generate pseudo-legal moves by geometry, then filter out any move
// that leaves the mover's own king attacked. King safety lives in exactly one
// place (leavesKingInCheck / isAttacked).
//
// TODO (not yet handled): en passant, under-promotion. A pawn
// reaching the last rank is emitted as an ordinary move for now (auto-queen
// can be applied when the move is played).
public class move_gen {

    // ----- direction / offset tables -------------------------------------
    static final int[][] ROOK_DIRS   = {{1,0},{-1,0},{0,1},{0,-1}};
    static final int[][] BISHOP_DIRS = {{1,1},{1,-1},{-1,1},{-1,-1}};
    static final int[][] ALL_DIRS    = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
    static final int[][] KNIGHT_OFFS = {{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1},{-2,1},{-1,2}};

    // A move: origin square -> destination square.
    public static class Move {
        public final int fromR, fromC, toR, toC;
        public Move(int fromR, int fromC, int toR, int toC){
            this.fromR = fromR; this.fromC = fromC; this.toR = toR; this.toC = toC;
        }
        // algebraic-ish, e.g. e2e4 (col -> file a..h, row 0 -> rank 8)
        public String toString(){
            return "" + (char)('a' + fromC) + (8 - fromR)
                      + (char)('a' + toC)   + (8 - toR);
        }
    }

    // =====================================================================
    // PUBLIC ENTRY POINT
    // =====================================================================

    public static List<Move> generateLegalMoves(int[][] board, boolean forWhite){
        List<Move> pseudo = new ArrayList<>();

        for(int r = 0; r < 8; r++){
            for(int c = 0; c < 8; c++){
                int p = board[r][c];
                if(p == 0 || (p > 0) != forWhite){ continue; } // empty or not our piece
                switch(Math.abs(p)){
                    case 1: pawnMoves(board, r, c, forWhite, pseudo); break;
                    case 2: slide(board, r, c, ROOK_DIRS,   forWhite, pseudo); break;
                    case 3: hop(board, r, c, KNIGHT_OFFS,   forWhite, pseudo); break;
                    case 4: slide(board, r, c, BISHOP_DIRS, forWhite, pseudo); break;
                    case 5: slide(board, r, c, ALL_DIRS,    forWhite, pseudo); break;
                    case 6: hop(board, r, c, ALL_DIRS,      forWhite, pseudo);
                            castle(board, r, c,             forWhite, pseudo); break;
                    default: break;
                }
            }
        }

        List<Move> legal = new ArrayList<>();
        for(Move m : pseudo){
            if(!leavesKingInCheck(board, m, forWhite)){ legal.add(m); }
        }
        return legal;
    }

    // =====================================================================
    // PSEUDO-LEGAL GENERATORS (geometry only, ignore own-king safety)
    // =====================================================================

    // Castling, emitted as a two-square king move (e1g1 / e1c1). Whoever plays
    // the move is responsible for hopping the rook over as well (see
    // chess_engine.make_move).
    //
    // There is no castling-rights state to consult -- a position here is a bare
    // int[8][8] with no move history -- so rights are inferred from placement:
    // king home, rook home, gap empty. Same approximation valid_move_utils
    // makes: a king that walks off e1 and back could still castle.
    static void castle(int[][] b, int r, int c, boolean white, List<Move> out){
        int homeRow = white ? 7 : 0;
        int king    = white ? 6 : -6;
        int rook    = white ? 2 : -2;

        if(r != homeRow || c != 4 || b[r][c] != king){ return; }
        if(isAttacked(b, homeRow, 4, !white)){ return; }   // may not castle out of check

        // kingside: f and g empty, king crosses f
        if(b[homeRow][7] == rook
           && b[homeRow][5] == 0 && b[homeRow][6] == 0
           && !isAttacked(b, homeRow, 5, !white)){
            out.add(new Move(homeRow, 4, homeRow, 6));
        }

        // queenside: b, c and d empty, king crosses d (b is only the rook's
        // path, so it may be attacked)
        if(b[homeRow][0] == rook
           && b[homeRow][1] == 0 && b[homeRow][2] == 0 && b[homeRow][3] == 0
           && !isAttacked(b, homeRow, 3, !white)){
            out.add(new Move(homeRow, 4, homeRow, 2));
        }
        // the destination square itself is checked by the leavesKingInCheck
        // filter in generateLegalMoves, like every other move
    }

    // Non-sliding pieces (knight, king): fixed offset list, one step each.
    static void hop(int[][] b, int r, int c, int[][] offs, boolean white, List<Move> out){
        for(int[] o : offs){
            int nr = r + o[0], nc = c + o[1];
            if(nr < 0 || nr > 7 || nc < 0 || nc > 7){ continue; }
            int t = b[nr][nc];
            if(t == 0 || (t > 0) != white){ out.add(new Move(r, c, nr, nc)); } // empty or enemy
        }
    }

    // Sliding pieces (rook, bishop, queen): walk each ray until blocked.
    static void slide(int[][] b, int r, int c, int[][] dirs, boolean white, List<Move> out){
        for(int[] d : dirs){
            int nr = r + d[0], nc = c + d[1];
            while(nr >= 0 && nr < 8 && nc >= 0 && nc < 8){
                int t = b[nr][nc];
                if(t == 0){
                    out.add(new Move(r, c, nr, nc));      // empty: move on and keep sliding
                } else {
                    if((t > 0) != white){ out.add(new Move(r, c, nr, nc)); } // enemy: capture
                    break;                                 // blocked either way
                }
                nr += d[0]; nc += d[1];
            }
        }
    }

    // Pawns: single/double push, diagonal captures. (No en passant / promotion choice yet.)
    static void pawnMoves(int[][] b, int r, int c, boolean white, List<Move> out){
        int dir      = white ? -1 : 1;   // white moves toward row 0
        int startRow = white ?  6 : 1;
        int one = r + dir;
        if(one < 0 || one > 7){ return; } // off board (would be a promotion square handled elsewhere)

        // forward push
        if(b[one][c] == 0){
            out.add(new Move(r, c, one, c));
            int two = r + 2 * dir;         // double push from start rank
            if(r == startRow && b[two][c] == 0){
                out.add(new Move(r, c, two, c));
            }
        }
        // diagonal captures
        for(int dc = -1; dc <= 1; dc += 2){
            int nc = c + dc;
            if(nc < 0 || nc > 7){ continue; }
            int t = b[one][nc];
            if(t != 0 && (t > 0) != white){ out.add(new Move(r, c, one, nc)); }
        }
    }

    // =====================================================================
    // LEGALITY FILTER  (the only place king safety is decided)
    // =====================================================================

    static boolean leavesKingInCheck(int[][] board, Move m, boolean white){
        int[][] b = copyBoard(board);
        b[m.toR][m.toC]   = b[m.fromR][m.fromC];
        b[m.fromR][m.fromC] = 0;
        int[] k = findKing(b, white);
        if(k == null){ return false; } // no king on board (test positions); nothing to protect
        return isAttacked(b, k[0], k[1], !white);
    }

    // Is square (r,c) attacked by a piece of color `byWhite`?
    // Self-contained: covers pawn, knight, bishop, rook, queen, king.
    public static boolean isAttacked(int[][] b, int r, int c, boolean byWhite){
        int sign = byWhite ? 1 : -1;

        // pawns: a white pawn attacks the square from row r+1; a black pawn from r-1
        int pr = r - (byWhite ? -1 : 1); // row the attacking pawn would sit on
        if(pr >= 0 && pr < 8){
            for(int dc = -1; dc <= 1; dc += 2){
                int pc = c + dc;
                if(pc >= 0 && pc < 8 && b[pr][pc] == sign * 1){ return true; }
            }
        }

        // knights
        for(int[] o : KNIGHT_OFFS){
            int nr = r + o[0], nc = c + o[1];
            if(nr >= 0 && nr < 8 && nc >= 0 && nc < 8 && b[nr][nc] == sign * 3){ return true; }
        }

        // king (adjacent)
        for(int[] o : ALL_DIRS){
            int nr = r + o[0], nc = c + o[1];
            if(nr >= 0 && nr < 8 && nc >= 0 && nc < 8 && b[nr][nc] == sign * 6){ return true; }
        }

        // sliding: rook/queen along orthogonals
        if(rayHits(b, r, c, ROOK_DIRS, sign * 2, sign * 5)){ return true; }
        // sliding: bishop/queen along diagonals
        if(rayHits(b, r, c, BISHOP_DIRS, sign * 4, sign * 5)){ return true; }

        return false;
    }

    // Walk each direction to the first occupied square; true if it is one of the
    // two given attacker piece codes.
    static boolean rayHits(int[][] b, int r, int c, int[][] dirs, int pieceA, int pieceB){
        for(int[] d : dirs){
            int nr = r + d[0], nc = c + d[1];
            while(nr >= 0 && nr < 8 && nc >= 0 && nc < 8){
                int t = b[nr][nc];
                if(t != 0){
                    if(t == pieceA || t == pieceB){ return true; }
                    break; // blocked by some other piece
                }
                nr += d[0]; nc += d[1];
            }
        }
        return false;
    }

    // =====================================================================
    // SMALL HELPERS
    // =====================================================================

    static int[][] copyBoard(int[][] board){
        int[][] copy = new int[board.length][];
        for(int r = 0; r < board.length; r++){ copy[r] = board[r].clone(); }
        return copy;
    }

    // Locate the king (6 for white, -6 for black); null if absent.
    static int[] findKing(int[][] b, boolean white){
        int target = white ? 6 : -6;
        for(int r = 0; r < 8; r++){
            for(int c = 0; c < 8; c++){
                if(b[r][c] == target){ return new int[]{r, c}; }
            }
        }
        return null;
    }

    // =====================================================================
    // SANITY CHECK: legal moves from the start position should be 20 per side.
    // =====================================================================

    public static void main(String[] args){
        int[][] start = {
            {-2, -3, -4, -5, -6, -4, -3, -2},
            {-1, -1, -1, -1, -1, -1, -1, -1},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 1,  1,  1,  1,  1,  1,  1,  1},
            { 2,  3,  4,  5,  6,  4,  3,  2}
        };

        List<Move> white = generateLegalMoves(start, true);
        List<Move> black = generateLegalMoves(start, false);

        System.out.println("white legal moves: " + white.size() + " (expected 20)");
        System.out.println(white);
        System.out.println("black legal moves: " + black.size() + " (expected 20)");
        System.out.println(black);
    }
}
