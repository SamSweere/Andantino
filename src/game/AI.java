package game;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class AI {
    private Board board;
    private final int aiColor;

    public AI(int aiColor){
        this.aiColor = aiColor;
    }

    public void updateBoard(Board board){
        this.board = board;
    }

    private Tile randomMove(){
        ArrayList<Tile> playableTiles = board.getPlayableTiles();
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int min = 0;
        int max = playableTiles.size() - 1;
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);

        return playableTiles.get(randomNum);
    }

    public Tile makeMove(){
        return randomMove();
    }
}
