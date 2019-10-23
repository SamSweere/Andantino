package game;

import java.util.ArrayList;
import java.util.Stack;

//This class is used for the pv and other useful data for a search return
public class SearchReturn{
    public int value;
    private ArrayList<Move> pv;
    public int depth = 0;

    public SearchReturn(Move move){
        this.pv = new ArrayList<>();
        pv.add(move);
    }

    public SearchReturn(int value){
        this.value = value;
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

    public void moveUp(Move move){
        pv.add(move);
        depth += 1;
    }

    public Move getLastMove(){
        return pv.get(pv.size()-1);
    }
}
