import java.util.ArrayList;
import java.util.List;

public class chess_engine {
    private int[][] board_state;
    private int elo;

    public chess_engine(int elo){
        this.board_state = new int[8][8];
        this.elo = elo;
    }
    
    public int[] move(int[][] board_state){
        this.board_state = board_state;
        List<Integer> values = new ArrayList<>();
        if(this.elo == 1000){
            for(int i = 0; i < 8; i++){
                for(int j = 0; j < 8; j++){
                    if(board_state[i][j] < 0){
                        compute_value(i,j);
                    }
                }
            }
        }







        //
            int[] fake = new int[1];
            fake[0] = 0;
            return fake;
        //
    }

    public void compute_value(int row, int col){

    }
    

    // public int[] search(){}

}
