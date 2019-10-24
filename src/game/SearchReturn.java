package game;

import java.util.ArrayList;

//This class is used for the pv and other useful data for a search return
public class SearchReturn{
    public int value;
    private ArrayList<Move> pv;
    public int depth;
    public boolean outOfTime = false;

    public SearchReturn(boolean outOfTime){
        this.outOfTime = outOfTime;
    }

    public SearchReturn(Move move, int depth){
        this.depth = depth;
        this.pv = new ArrayList<>();
        pv.add(move);
    }

    public SearchReturn(SearchReturn sr){
        //Copy function
        this.pv = sr.pv;
        this.value = sr.value;
        this.depth = sr.depth;
    }

    public void setValue(int value){
        this.value = value;
    }

    public void setDepth(int depth){
        this.depth = depth;
    }

    public void moveUp(Move move){
        pv.add(move);
        depth += 1;
    }

    public Move getLastMove(){
        return pv.get(pv.size()-1);
    }
}
