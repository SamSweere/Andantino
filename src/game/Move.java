package game;

public class Move {
    public final byte q;
    public final byte r;

    public Move(int q, int r){
        this.q = (byte)q;
        this.r = (byte)r;
    }

    public Move(Move move){
        this.q = move.q;
        this.r = move.r;
    }

    public boolean equals(Move move){
        if(this.q == move.q && this.r == move.r){
            return true;
        }
        return false;
    }
}
