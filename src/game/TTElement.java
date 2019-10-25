package game;

public class TTElement {
    public final long primaryHash;
    public final int value;
    public final byte depth;
    //flag: 0 exact value, 1: lower bound, 2:upper bound
    public final byte flag;
    public final Move move;

    public TTElement(long primaryHash, int flag, int value, int depth, Move move){
        this.primaryHash = primaryHash;
        this.flag = (byte)flag;
        this.value = value;
        this.depth = (byte)depth;
        this.move = move;
    }
}
