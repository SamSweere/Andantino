package game;
import java.util.concurrent.ThreadLocalRandom;

public class TT {
    private final long[] zobristQ;
    private final long[] zobristR;
    private final long[] zobristS;
    private long lastKey;

    public TT(Board board){
        int boardRadius = board.getBoardRadius();
        //Create the random values for every parameter
        zobristQ = new long[boardRadius*2+1];
        zobristR = new long[boardRadius*2+1];
        zobristS = new long[3];

        //Fill them with random long values
        for(int i = 0; i < zobristQ.length; i++){
            zobristQ[i] = ThreadLocalRandom.current().nextLong();
            zobristR[i] = ThreadLocalRandom.current().nextLong();
        }

        for(int i = 0; i < zobristS.length; i++){
            zobristS[i] = ThreadLocalRandom.current().nextLong();
        }

        lastKey = calculateInitKey(board);
    }

    private long updateKey(long hashKey, Move move, int player) {
        hashKey ^= zobristQ[move.q];
        hashKey ^= zobristR[move.r];
        //Make sure the playernumber is converted to the array acces value
        int playerNum = 0;
        if(player == 0){
            playerNum = 1;
        }
        else if(player == -1){
            playerNum = 2;
        }
        hashKey ^= zobristS[playerNum];
        return hashKey;
    }

    private long calculateInitKey(Board board) {
        long hashKey = 0;
        int boardRadius = board.getBoardRadius();
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius) {
                    hashKey ^= zobristQ[q];
                    hashKey ^= zobristR[r];

                    if(board.getTile(q,r).state == 1){
                        hashKey ^= zobristS[1];
                    }
                    else if(board.getTile(q,r).state == -1){
                        hashKey ^= zobristS[2];
                    }
                    else{
                        hashKey ^= zobristS[0];
                    }
                }
            }
        }
        return hashKey;
    }

}
