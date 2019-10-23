package game;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class AI {
    private Board board;
    private final int aiColor;
    private final boardChecker boardCheck = new boardChecker();

    public AI(int aiColor){
        this.aiColor = aiColor;
    }

    //public void updateBoard(Board board){
    //    this.board = board;
    //}

    private Tile randomMove(){
        ArrayList<Tile> playableTiles = board.getPlayableTiles();
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int min = 0;
        int max = playableTiles.size() - 1;
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);

        return playableTiles.get(randomNum);
    }

    //This class is used for the pv and other useful data for a search return
    private class SearchReturn{
        public int value;
        private Stack pv;
        public int depth;

        public SearchReturn(Move move){
            this.pv = new Stack();
            pv.add(move);
            this.depth = 0;
        }

        public void setValue(int value){
            this.value = value;
        }

        public void moveUp(Move move){
            pv.add(move);
            depth += 1;
        }

        public Move getLastMove(){
            return (Move)pv.get(pv.size()-1);
        }
    }

    private SearchReturn AlphaBeta(Board board, int depth, int alpha, int beta, Move move){
        int winState = boardCheck.checkWin(move.q, move.r, board);
        //winState = 0 means no win state reached
        if(winState != 0 || depth == 0){
            //This is a leaf node, make new searchReturn object
            SearchReturn sr = new SearchReturn(move);
            if(winState != 0){
                //TODO: Put the evaluation function here in the future
                //A win state has been found, this is thus a leafe node.
                if(winState == aiColor){
                    sr.setValue(1);
                } else {
                    sr.setValue(-1);
                }

            }
            else if(depth == 0){
                //Max depth reached without win
                sr.setValue(0);
            }
            return sr;
        }
    }

    //NegaMax implementation
    /**private int AlphaBeta(Board board, int depth, int alpha, int beta, int moveQ, int moveR){
        int winState = boardCheck.checkWin(moveQ, moveR, board);
        //winState = 0 means no win state reached
        if(winState != 0){
            //TODO: Put the evaluation function here in the future
            //Make more shallow wins more important
            //In NegaMax the win values are returned with respect to the root player
            if(board.getPlayerTurn() == aiColor){
                if(winState == aiColor){
                    return 1;
                } else {
                    return -1;
                }
            }else{
                if(winState != aiColor){
                    return 1;
                } else {
                    return -1;
                }
            }
            //    return -1 * (depth+1);
            //}
                //System.out.println("a winstate has been seen: " + winState );
           // if(winState == 0){
                //Depth has been reached, do not return 0 since it will rate it better than losing
            //    return Integer.MIN_VALUE;
        }
        else if(depth == 0){
            //Max depth reached without win
            return 0;
        }

        //No endstate is reached, make the actual move
        board.setTileState(moveQ,moveR,board.getPlayerTurn());

        //Have to take max value
        int score = -1*Integer.MAX_VALUE;

        ArrayList<Tile> playableTiles = board.getPlayableTiles();

        for(int childBoardNum = 0; childBoardNum < playableTiles.size(); childBoardNum++){
            int childMoveQ = playableTiles.get(childBoardNum).q;
            int childMoveR = playableTiles.get(childBoardNum).r;

            int value = -1 * AlphaBeta(new Board(board), depth -1, -1*beta, -1*alpha, childMoveQ, childMoveR);
            if(value > score){
                score = value;
            }
            if(score > alpha){
                alpha = score;
            }
            if(score >= beta){
                //TODO: reimplement pruning
                break;
            }
        }

        return score;
    }

    private Tile startAlphaBeta(Board board, int depth){
        //Since my alpha beta implementation needs a move we need a function to start that
        if(depth == 0){
            System.out.println("ERROR: initial depth is zero");
        }

        int score = -1*Integer.MAX_VALUE;

        //TODO: windowing can be done here
        int alpha = -1*Integer.MAX_VALUE;
        int beta = Integer.MAX_VALUE;

        ArrayList<Tile> playableTiles = board.getPlayableTiles();

        //Initialize bestMove with the first option
        Tile bestMove = playableTiles.get(0);

        for(int childBoardNum = 0; childBoardNum < playableTiles.size(); childBoardNum++){

            int childMoveQ = playableTiles.get(childBoardNum).q;
            int childMoveR = playableTiles.get(childBoardNum).r;
            int value = -1 * AlphaBeta(new Board(board), depth-1, -1*beta, -1*alpha, childMoveQ, childMoveR);
            //System.out.println("childNum " + childBoardNum + " gives: " + value);
            if(value > score){
                score = value;
                //New highest
                bestMove = playableTiles.get(childBoardNum);
                System.out.println("new best move: " + childBoardNum + " with score: " + score );
            }
            if(score > alpha){
                alpha = score;
            }
            if(score >= beta){
                //TODO: reimplement pruning, it breaks it now
                break;
            }
        }
        return bestMove;
    }**/

    public Move makeMove(Board board){
        this.board = board;

        //return randomMove();
        int depth = 2;
        SearchReturn pv = AlphaBeta(board,depth);
        return pv.getLastMove();
    }
}
