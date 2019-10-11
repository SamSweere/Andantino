package game;

//States: 0: empty, 1: player 1, -1: player two, -2:player one loses here, 2: player two loses here,
// 3:local visited, 4; global visited

public class Tile {
    public final int q;
    public final int r;
    public int state;
    public Boolean playable;

    public Tile(int q, int r, int state, boolean playable){
        this.q  = q;
        this.r = r;
        this.state = state;
        this.playable = playable;
    }
}
