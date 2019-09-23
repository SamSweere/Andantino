package game;

//States: 0: empty, 1: player 1, -1: player two, -2:player one loses here: 2: player two loses here

public class Tile {
    public int q;
    public int r;
    public int state;

    public Tile(int q, int r, int state){
        this.q  = q;
        this.r = r;
        this.state = state;
    }
}
