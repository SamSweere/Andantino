package game;

import java.util.ArrayList;
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

    //NegaMax implementation
    private int AlphaBeta(Board board, int depth, int alpha, int beta, int moveQ, int moveR){
        int winState = boardCheck.checkWin(moveQ, moveR, board);
        //winState = 0 means no win state reached
        if(winState != 0){
            //TODO: Put the evaluation function here in the future
            //Make more shallow wins more important
            //if (winState == aiColor) {
            return -1 * (depth+1);
            //} else {
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

        int score = Integer.MIN_VALUE;

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
                //break;
            }
        }

        return score;
    }

    private Tile startAlphaBeta(Board board, int depth){
        //Since my alpha beta implementation needs a move we need a function to start that
        if(depth == 0){
            System.out.println("ERROR: initial depth is zero");
        }

        int score = Integer.MIN_VALUE;

        //TODO: windowing can be done here
        int alpha = Integer.MIN_VALUE;
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
                //break;
            }
        }
        return bestMove;
    }

    public Tile makeMove(Board board){
        this.board = board;

        //return randomMove();
        int depth = 6;
        return startAlphaBeta(board,depth);
    }
}
