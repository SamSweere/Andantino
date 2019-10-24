package game;

public class Move {
    public final int q;
    public final int r;

    public Move(int q, int r){
        this.q = q;
        this.r = r;
    }

    public boolean equals(Move move){
        if(this.q == move.q && this.r == move.r){
            return true;
        }
        return false;
    }
}
