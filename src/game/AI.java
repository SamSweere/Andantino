package game;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Integer.*;

public class AI {
    private String aiName = "testing";
    private Board board;
    private final int aiColor;
    private final boardChecker boardCheck = new boardChecker();
    private int nodesVisited;
    private fileWriter fw;

    private String aiColorString;
    private String oponentColorString;

    public static final int WIN = 100;

    private final int totalTime = 60*10;
    private final int maxTestTime = 60;
    private final int maxMoveTime = 2;
    //Max depth
    private final int maxDepth = 10;

    private int totalTestTime = 0;
    private TT tt;
    private long startTime;

    public AI(int aiColor, int boardRadius){
        this.aiColor = aiColor;
        if(aiColor == 1){
            aiName = aiName + "_white";
            aiColorString = "White";
            oponentColorString = "Black";
        }else{
            aiName = aiName + "_black";
            aiColorString = "Black";
            oponentColorString = "White";
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

        //Get the time
        float timeElapsed = ((float)(System.currentTimeMillis() - startTime))/1000;

        if(move.q == Integer.MAX_VALUE){
            //this is the start, no need to check winstates
        }else if(timeElapsed > maxMoveTime){
            //Out of time, return the out of time search result
            return new SearchReturn(true);
        }
        else{
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
                    if(winState == -1*board.getPlayerTurn()){
                        sr.setValue(WIN);
                    } else {
                        sr.setValue(-1*WIN);
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


        //Set the initial pv to the smallest value possible for max
        //pv = new SearchReturn(-1*Integer.MAX_VALUE);
        int score = -1*Integer.MAX_VALUE;
        //Move bestMove;

        for(int childBoardNum = 0; childBoardNum < playableTiles.size(); childBoardNum++) {
            int childMoveQ = playableTiles.get(childBoardNum).q;
            int childMoveR = playableTiles.get(childBoardNum).r;
            Move childMove = new Move(childMoveQ, childMoveR);

            SearchReturn sr = alphaBeta(new Board(board), depth -1, -1*beta, -1*alpha, childMove, hash);
            //Negamax, -1*value
            sr.value = -1*sr.value;

            //Check for out of time
            if(sr.outOfTime){
                //Return out of time searchresult
                return sr;
            }

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
        else if(score >= beta){
            //Flag lowerbound
            flag = 1;
        }
        else{
            //Flag is exact
            flag = 0;
        }
        //Store it in the tt
        tt.storeTT(hash,flag,score,depth);

        return pv;
    }

    private void printInformation(SearchReturn pv, float timeLapsed){
        if(pv.value == WIN){
            //We won for sure
            System.out.println("AI " + aiColorString + ": we will win in " + (pv.depth - 1) + " moves!!!");
        }
        if(pv.value == -1*WIN){
            //We lost for sure
            System.out.println("AI " + aiColorString + ": we will lose in at least " + (pv.depth-1) + " moves!!!");
        }
        String nodesTime = "AI " + aiColorString + ": " + "Nodes visited: " + nodesVisited + " in " + timeLapsed;
        System.out.println(nodesTime);
        System.out.println("AI " + aiColorString + ": " +"Total time: " + totalTestTime);
        System.out.println("AI " + aiColorString + ": " + "TT collisions: " + tt.collisionCounter);
        //Reset the counter
        tt.collisionCounter = 0;
    }



    public Move makeMove(Board board){
        this.board = board;

        nodesVisited = 0;

        //Apply window of win lose
        int alpha = -WIN;
        int beta = WIN;


        //initial depth
        int depth = 0;

        //Save the principal variation, we need to initialize something since the real value will be created in the
        SearchReturn pv = new SearchReturn(0);

        //Get the hash of the current board
        long hash = tt.getHash(board);

        startTime = System.currentTimeMillis();

        //This is for out of time, forced moves or definite wins
        boolean stopLoop = false;

        //Iterative deepening
        while(depth < maxDepth && !stopLoop){
            depth += 1;
            //System.out.println("AI " + aiColorString + ": " + "Depth: " + depth);

            //For the intial move make a move with max values (impossible to do)
            //Save the pv on this depth (for instant win or loss cases)
            SearchReturn pvD = alphaBeta(new Board(board),depth,alpha,beta, new Move(Integer.MAX_VALUE, Integer.MAX_VALUE), hash);

            //Check for out of time
            if(pvD.outOfTime){
                //We are out of time, take the pv of one depth lower, thus do not update the pv
                stopLoop = true;
                System.out.println("AI " + aiColorString + ": " + "Out of time, play depth: " + (depth-1));
            }
            else if(pvD.value == WIN){
                pv = pvD;
                //We won no need to search further
                stopLoop = true;
                System.out.println("AI " + aiColorString + ": " + "Stopping loop for win");
            }
            else if(pvD.value == -1*WIN){
                float timeElapsed = ((float)(System.currentTimeMillis() - startTime))/1000;
                printInformation(pvD, timeElapsed);
                //The program thinks that it has lost, go back to the depth where it didnt think it lost and play that move
                //Thus do not update pv, but if the depth < 2, then do update the pv, otherwise it cant block
                if(depth <= 2){
                    pv = pvD;
                }
                //pv = alphaBeta(new Board(board),1,alpha,beta, new Move(Integer.MAX_VALUE, Integer.MAX_VALUE), hash);
                stopLoop = true;
                System.out.println("AI " + aiColorString + ": " + "Detected loss, play on depth where loss was not yet detected");
            }else{
                //Update the pv
                pv = pvD;
            }
        }


        Move move = pv.getLastMove();

        float timeElapsed = ((float)(System.currentTimeMillis() - startTime))/1000;
        totalTestTime += (int)timeElapsed;

        if(totalTestTime > maxTestTime){
            System.out.println("AI " + aiColorString + ": " + "Test complete, you can stop");
        }else{
            fw.writeLine(depth,timeElapsed,nodesVisited);
        }

        printInformation(pv, timeElapsed);

        return move;
    }
}
