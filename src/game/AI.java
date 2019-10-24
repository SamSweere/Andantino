package game;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Integer.*;

public class AI {
    private String aiName = "pruning_randomEval_withTT_T8";
    private Board board;
    private final int aiColor;
    private final boardChecker boardCheck = new boardChecker();
    private int nodesVisited;
    private fileWriter fw;
    private final int totalTime = 60*10;
    private final int maxTestTime = 60;
    private int totalTestTime = 0;
    private TT tt;

    public AI(int aiColor, int boardRadius){
        this.aiColor = aiColor;
        if(aiColor == 1){
            aiName = aiName + "_white";
        }else{
            aiName = aiName + "_black";
        }
        this.fw = new fileWriter(aiName);

        this.tt = new TT(boardRadius);
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
        int randomNum = ThreadLocalRandom.current().nextInt(99, 99 + 1);

        return randomNum;
    }

    private SearchReturn alphaBeta(Board board, int depth, int alpha, int beta, Move move, long hash){
        //Save the old alpha in case of tt hit
        int olda = alpha;
        int oldb = beta;
        if(move.q == Integer.MAX_VALUE){
            //this is the start, no need to check winstates
        }else{
            nodesVisited += 1;

            //Check if this board is in the tt
            TT.TTElement ttElem = tt.checkTT(hash);
            if(ttElem != null){
                //Board is present in TT
                if(ttElem.depth >= depth){
                    if(ttElem.flag == 1){
                        //Exact value
                        SearchReturn sr = new SearchReturn(move);
                        sr.setValue(ttElem.value);
                        return sr;
                    }
                    else if(ttElem.flag == 1){
                        //LowerBound
                        alpha = max(alpha, ttElem.value);
                    }
                    else if(ttElem.flag == 2){
                        //UpperBound
                        beta = min(beta, ttElem.value);
                    }
                    if(alpha >= beta){
                        SearchReturn sr = new SearchReturn(move);
                        sr.setValue(ttElem.value);
                        return sr;
                    }
                }

            }

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
            int playerTurn = board.getPlayerTurn();
            board.setTileState(move.q, move.r, playerTurn);

            //Update the hash
            hash = tt.updateHash(hash, move, playerTurn);
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

                SearchReturn sr = alphaBeta(new Board(board), depth -1, alpha, beta, childMove, hash);

                //Set the pv initially on the first search return
                if(sr.value > score){
                    pv = sr;
                    score = sr.value;

                    pv.moveUp(childMove);
                    //System.out.println("New best pv! : " + pv.getLastMove().q + " " + pv.getLastMove().r + " with " + pv.value);
                }
                if(sr.value > alpha){
                    alpha = sr.value;
                }
                if(alpha >= beta){
                    //prune
                    break;
                }
            }

            int flag;
            //Add to the tt
            if(score <= olda){
                //Flag upperbound
                flag = 2;
            }
            else if(score >= oldb){
                //Flag lowerbound
                flag = 1;
            }
            else{
                //Flag is exact
                flag = 0;
            }
            //Store it in the tt
            tt.storeTT(hash,flag,score,depth);
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

                SearchReturn sr = alphaBeta(new Board(board), depth - 1, alpha, beta, childMove, hash);

                //Set the pv initially on the first search return
                if (sr.value < score) {
                    pv = sr;
                    score = sr.value;

                    pv.moveUp(childMove);
                }
                if (sr.value < beta) {
                    beta = sr.value;
                }
                if (alpha >= beta) {
                    //prune
                    break;
                }
            }

            int flag;
            //Add to the tt
            if(score <= olda){
                //Flag upperbound
                flag = 2;
            }
            else if(score >= oldb){
                //Flag lowerbound
                flag = 1;
            }
            else{
                //Flag is exact
                flag = 0;
            }
            //Store it in the tt
            tt.storeTT(hash,flag,score,depth);
        }


        return pv;
    }

    private void printInformation(SearchReturn pv, float timeLapsed){
        String aiColorString;
        String oponentColorString;
        if(aiColor == 1){
            aiColorString = "White";
            oponentColorString = "Black";
        }else{
            aiColorString = "Black";
            oponentColorString = "White";
        }
        if(pv.value == 100){
            //We won for sure
            System.out.println("AI " + aiColorString + ": we will win in " + (pv.depth - 1) + " moves!!!");
        }
        if(pv.value == -100){
            //We lost for sure
            System.out.println("AI " + aiColorString + ": we will lose in at least " + (pv.depth-1) + " moves!!!");
        }
        String nodesTime = "Nodes visited: " + nodesVisited + " in " + timeLapsed;
        System.out.println(nodesTime);
        System.out.println("TT collisions: " + tt.collisionCounter);
        //Reset the counter
        tt.collisionCounter = 0;
    }



    public Move makeMove(Board board){
        this.board = board;

        nodesVisited = 0;

        Date date = new Date();
        long start = System.currentTimeMillis();

        //return randomMove();
        int depth = 8;

        int alpha = -1*Integer.MAX_VALUE;
        int beta = Integer.MAX_VALUE;

        //Get the hash of the current board
        long hash = tt.getHash(board);

        //For the intial move make a move with max values (impossible to do)
        SearchReturn pv = alphaBeta(new Board(board),depth,alpha,beta, new Move(Integer.MAX_VALUE, Integer.MAX_VALUE), hash);
        Move move = pv.getLastMove();

        long finish = System.currentTimeMillis();
        float timeElapsed = ((float)(finish - start))/1000;
        totalTestTime += (int)timeElapsed;

        if(totalTestTime > maxTestTime){
            System.out.println("Test complete, you can stop");
        }else{
            fw.writeLine(depth,timeElapsed,nodesVisited);
        }

        printInformation(pv, timeElapsed);

        return move;
    }
}
