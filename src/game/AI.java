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

    private int evaluate(Board board, Move move){
        //Win == 100, lose == -100
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = ThreadLocalRandom.current().nextInt(-99, 99 + 1);

        return randomNum;
    }

    private SearchReturn AlphaBeta(Board board, int depth, int alpha, int beta, Move move){
        if(move.q == Integer.MAX_VALUE){
            //this is the start, no need to check winstates
        }else{
            int winState = boardCheck.checkWin(move.q, move.r, board);
            //winState = 0 means no win state reached
            if(winState != 0 || depth == 0){
                //This is a leaf node, make new searchReturn object
                SearchReturn sr = new SearchReturn(move);
                if(winState != 0){
                    //A win state has been found, this is thus a leaf node.
                    if(winState == aiColor){
                        sr.setValue(100);
                    } else {
                        sr.setValue(-100);
                    }

                }
                else if(depth == 0) {
                    //Max depth reached without win, now evaluate
                    sr.setValue(evaluate(board,move));
                }
                return sr;
            }

            //No endstate is reached, make the actual move
            board.setTileState(move.q, move.r, board.getPlayerTurn());
        }

        ArrayList<Tile> playableTiles = board.getPlayableTiles();

        //Initiate the principle variation class
        SearchReturn pv = new SearchReturn(-1*Integer.MAX_VALUE);

        //Check if min or max player
        if(board.getPlayerTurn() == aiColor){
            //Max player

            //Set the initial pv to the smallest value possible for max
            //pv = new SearchReturn(-1*Integer.MAX_VALUE);
            int score = -1*Integer.MAX_VALUE;
            //Move bestMove;

            for(int childBoardNum = 0; childBoardNum < playableTiles.size(); childBoardNum++) {
                int childMoveQ = playableTiles.get(childBoardNum).q;
                int childMoveR = playableTiles.get(childBoardNum).r;
                Move childMove = new Move(childMoveQ, childMoveR);

                SearchReturn sr = AlphaBeta(new Board(board), depth -1, alpha, beta, childMove);

                //Set the pv initially on the first search return
                if(sr.value > score){
                    pv = sr;
                    score = sr.value;

                    pv.moveUp(childMove);
                    //System.out.println("New best pv! : " + pv.getLastMove().q + " " + pv.getLastMove().r + " with " + pv.value);
                }
                /**else if(sr.value == score){
                 //Choose the one with the lowest depth
                 if(sr.depth < pv.depth){
                 pv = sr;
                 pv.moveUp(childMove);
                 }
                 }**/
                if(sr.value > alpha){
                    alpha = sr.value;
                }
                if(alpha >= beta){
                    //prune
                    break;
                }
            }
        }
        else {
            //Min player

            //Set the initial pv to the biggest value possible for max
            //pv = new SearchReturn(Integer.MAX_VALUE);
            int score = Integer.MAX_VALUE;

            for (int childBoardNum = 0; childBoardNum < playableTiles.size(); childBoardNum++) {
                int childMoveQ = playableTiles.get(childBoardNum).q;
                int childMoveR = playableTiles.get(childBoardNum).r;
                Move childMove = new Move(childMoveQ, childMoveR);

                SearchReturn sr = AlphaBeta(new Board(board), depth - 1, alpha, beta, childMove);

                //Set the pv initially on the first search return
                if (sr.value < score) {
                    pv = sr;
                    score = sr.value;

                    pv.moveUp(childMove);
                }
                /**else if(sr.value == score){
                    //Choose the one with the lowest depth
                    if(sr.depth < pv.depth){
                        pv = sr;
                        pv.moveUp(childMove);
                    }
                }**/
                if (sr.value < beta) {
                    beta = sr.value;
                }
                if (alpha >= beta) {
                    //prune
                    break;
                }
            }
        }
        return pv;
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
        int depth = 3;

        int alpha = -1*Integer.MAX_VALUE;
        int beta = Integer.MAX_VALUE;

        //For the intial move make a move with max values (impossible to do)
        SearchReturn pv = AlphaBeta(new Board(board),depth,alpha,beta, new Move(Integer.MAX_VALUE, Integer.MAX_VALUE));
        Move move = pv.getLastMove();

        System.out.println(pv.value);
        if(pv.value == 100){
            //We won for sure
            System.out.println("We will win in " + (pv.depth - 1) + " moves!!!");
        }
        if(pv.value == -100){
            //We lost for sure
            System.out.println("We will lose in at least " + (pv.depth-1) + " moves!!!");
        }

        return move;
    }
}
