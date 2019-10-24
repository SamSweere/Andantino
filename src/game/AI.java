package game;

import java.util.*;
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
    private final int maxMoveTime = 15;
    //Max depth
    private final int maxDepth = 12;
    private final int boardRadius;

    private String log = "";


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
        this.boardRadius = boardRadius;
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

    private int floodFill(Tile startingPoint, Board board){
        Board boardFF = new Board(board);

        Stack st = new Stack();
        st.add(startingPoint);

        int player = boardFF.getTileState(startingPoint.q, startingPoint.r);

        int connectedTiles = 0;

        while(!st.empty()){
            Tile tile = (Tile)st.pop();

            //Set tile as localy visited
            boardFF.setTileStateFF(tile.q, tile.r, 4);

            //For every neighbor
            for(int q = -1; q <= 1; q++){
                for(int r = -1; r <= 1; r++){
                    if(q != r){
                        int checkQ = tile.q + q;
                        int checkR = tile.r + r;
                        if(checkQ + checkR < boardRadius || checkR + checkQ > 3*boardRadius || checkQ < 0 || checkR < 0
                                || checkQ > boardRadius*2 || checkR > boardRadius*2){
                            //Search out of bounds
                        }
                        else if(boardFF.getTileState(checkQ,checkR) == player){
                            //Tile of player
                            connectedTiles += 1;
                            continue;
                        }
                        else if(boardFF.getTileState(checkQ,checkR) == 4){
                            //Tile already visited
                            continue;
                        }
                        else{
                            //add tile to the stack
                            st.add(boardFF.getTile(checkQ,checkR));
                        }
                    }
                }
            }
        }

        return connectedTiles;
    }

    private int evaluateGroupsize(Board board, Move move){
        //Make the move on this board
        int playerTurn = board.getPlayerTurn();
        board.setTileState(move.q, move.r, playerTurn);

        int scoreWhite = 0;
        int scoreBlack = 0;

        //Based of floodfill
        //For every playable tile of the board do:
        for(int q = 0; q < boardRadius*2+1; q++) {
            for (int r = 0; r < boardRadius * 2 + 1; r++) {
                if (r + q >= boardRadius && r + q <= 3 * boardRadius) {
                    int tileOfPlayer = board.getTileState(q,r);
                    if(tileOfPlayer == 1){
                        scoreWhite += floodFill(new Tile(q,r,tileOfPlayer,false), board);
                    }else if(tileOfPlayer == -1){
                        scoreBlack += floodFill(new Tile(q,r,tileOfPlayer,false), board);
                    }
                }
            }
        }


        //Win == 100, lose == -100
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        //int randomNum = ThreadLocalRandom.current().nextInt(99, 99 + 1);

        //TODO: turned of random eval
        //return randomNum;

        int relativeDiference = (int)((((float)scoreWhite)/((float)(scoreWhite+scoreBlack))
                - ((float)scoreBlack)/((float)(scoreWhite+scoreBlack)))*99);
        //System.out.println(relativeDiference);

        return relativeDiference;
    }

    private int evaluateConnections(Board board, Move move){
        //Make the move on this board
        int playerTurn = board.getPlayerTurn();
        board.setTileState(move.q, move.r, playerTurn);

        int scoreWhite = 0;
        int scoreBlack = 0;

        //Only have to traverse the board once
        //For every playable tile of the board do:
        for(int q = 0; q < boardRadius*2+1; q++) {
            for (int r = 0; r < boardRadius * 2 + 1; r++) {
                if (r + q >= boardRadius && r + q <= 3 * boardRadius) {
                    int player = board.getTileState(q,r);

                    //Give a point for the tile itself
                    int playerScore = 1;

                    if(player == -1 || player ==1){

                        //For every neighbor
                        for(int qN = -1; qN <= 1; qN++){
                            for(int rN = -1; rN <= 1; rN++){
                                if(qN != rN){
                                    int checkQ = q + qN;
                                    int checkR = r + rN;
                                    if(checkQ + checkR < boardRadius || checkR + checkQ > 3*boardRadius || checkQ < 0 || checkR < 0
                                            || checkQ > boardRadius*2 || checkR > boardRadius*2){
                                        //Search out of bounds
                                    }
                                    else if(board.getTileState(checkQ,checkR) == player){
                                        //Tile of player
                                        playerScore += 1;
                                        continue;
                                    }
                                }
                            }
                        }
                    }

                    if(player == 1){
                        scoreWhite += playerScore;
                    }else if(player == -1){
                        scoreBlack += playerScore;
                    }
                }
            }
        }

        int relativeDiference = (int)((((float)scoreWhite)/((float)(scoreWhite+scoreBlack))
                - ((float)scoreBlack)/((float)(scoreWhite+scoreBlack)))*99);
        //System.out.println(relativeDiference);

        return relativeDiference;
    }

    private int evaluate(Board board, Move move){
        //Score is always in favour of white and should be reversible (-1*score)
        return evaluateConnections(new Board(board),move);
        //TODO: put back good eval
        //return 0;
    }

    private SearchReturn alphaBeta(Board board, int depth, int targetDepth, int alpha, int beta, Move move, long hash){
        int layerDepth = targetDepth - depth;

        logBuilder(-1,alpha,beta,layerDepth,false);

        //TODO: for log builder, can be removed
        boolean pruning = false;



        //Save the old alpha in case of tt hit
        int olda = alpha;

        //Create the move order
        Queue<Move> moveOrder = new LinkedList<>();

        //For found tt Move
        Move ttMove = null;

        //Get the time
        float timeElapsed = ((float)(System.currentTimeMillis() - startTime))/1000;

        if(move.q == Integer.MAX_VALUE){
            //this is the start, no need to check winstates
            //There is no move to be done
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
                //TODO: changed layerDepth here, check if correct
                if(depth <= ttElem.depth){
                    if(ttElem.flag == 0){
                        //Exact value
                        SearchReturn sr = new SearchReturn(move, ttElem.depth);
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
                        SearchReturn sr = new SearchReturn(move, ttElem.depth);
                        sr.setValue(ttElem.value);
                        return sr;
                    }
                }
                else{
                    //TODO: first investigate stored move, move ordering
                    //TODO: move ordering enable here
                    //ttMove = ttElem.move;
                }

            }

            int winState = boardCheck.checkWin(move.q, move.r, board);
            //winState = 0 means no win state reached
            if(winState != 0 || depth == 0){
                //This is a leaf node, make new searchReturn object
                SearchReturn sr = new SearchReturn(move, 1);
                if(winState != 0){
                    //A win state has been found, this is thus a leaf node.
                    //Remember, this is negamax
                    //We ahve not made the move yet, thus take inverse
                    if(winState == -1*board.getPlayerTurn()){
                        sr.setValue(WIN);
                    } else {
                        sr.setValue(-1*WIN);
                    }

                }
                else if(depth == 0) {
                    //Max depth reached without win, now evaluate
                    //The evaluation function is always positive to white
                    //Negamax thus invert
                    //We ahve not made the move yet, thus take inverse
                    if(-1*board.getPlayerTurn() == 1){
                        sr.setValue(evaluate(board,move));
                    }else{
                        sr.setValue(-1*evaluate(board,move));
                    }
                    /**if(board.getPlayerTurn() == aiColor){
                        sr.setValue(-1*evaluate(board,move));
                    }else{
                        sr.setValue(evaluate(board,move));
                    }**/

                }
                //TODO:log builder here
                logBuilder(sr.value,alpha,beta,layerDepth,false);
                return sr;
            }


            int playerTurn = board.getPlayerTurn();
            board.setTileState(move.q, move.r, playerTurn);

            //Update the hash
            hash = tt.updateHash(hash, move, playerTurn);
        }

        //Get all the playable tiles and convert them to moves in a que
        ArrayList<Tile> playableTiles = board.getPlayableTiles();


        //TODO: removed move ordering
        //If a tt move is found add that first
        if(ttMove != null){
            moveOrder.add(ttMove);
        }

        for(int childBoardNum = 0; childBoardNum < playableTiles.size(); childBoardNum++) {
            int childMoveQ = playableTiles.get(childBoardNum).q;
            int childMoveR = playableTiles.get(childBoardNum).r;
            Move childMove = new Move(childMoveQ, childMoveR);

            //Do not add ttmove twice
            if(ttMove != null){
                if(ttMove.equals(childMove)){
                    //skip this one
                    continue;
                }
            }
            moveOrder.add(childMove);
        }


        //Initiate the principle variation class
        SearchReturn pv = null;

        //Minimum score is a loss
        int score = -1* MAX_VALUE;
        //Move bestMove;

        while(!moveOrder.isEmpty()){
            //Get the best next move from the moveOrder que
            Move childMove = moveOrder.remove();

            SearchReturn sr = alphaBeta(new Board(board), depth -1,targetDepth, -1*beta, -1*alpha, childMove, hash);
            //Negamax, -1*value
            sr.value = -1*sr.value;



            //Check for out of time
            if(sr.outOfTime){
                //Return out of time searchresult
                return sr;
            }

            //Check if there is a principle variation, if not put this move in it
            if(pv == null){
                pv = sr;
                pv.moveUp(childMove);
            }

            if(sr.value > score){
                pv = sr;
                score = sr.value;

                pv.moveUp(childMove);
                //System.out.println("New best pv! : " + pv.getLastMove().q + " " + pv.getLastMove().r + " with " + pv.value);
            }
            if(score > alpha){
                alpha = sr.value;
            }
            if(score >= beta){
                //prune
                pruning = true;
                break;
            }
        }

        int flag;
        //Add to the tt
        if(score <= olda){
            //Fail low
            //Flag upperbound
            flag = 2;
        }
        else if(score >= beta){
            //Fail high
            //Flag lowerbound
            flag = 1;
        }
        else{
            //Flag is exact
            flag = 0;
        }
        //Store it in the tt
        //Negamax store the inverse
        if (pv != null) {
            //TODO: check if the score is saved correctly
            tt.storeTT(hash,flag,score,depth, pv.getLastMove());
        }
        //TODO:log builder here
        logBuilder(pv.value,alpha,beta,layerDepth,pruning);
        return pv;
    }

    private void printInformation(Board board, SearchReturn pv, float timeLapsed){
        if(pv.value == WIN){
            //We won for sure
            System.out.println("AI " + aiColorString + ": we will win in " + (pv.depth - 1) + " moves!!!");
        }
        else if(pv.value == -1*WIN){
            //We lost for sure
            System.out.println("AI " + aiColorString + ": we will lose in at least " + (pv.depth-1) + " moves!!!");
        }

        System.out.println("AI " + aiColorString + ": best value " + (pv.value));

        String nodesTime = "AI " + aiColorString + ": " + "Nodes visited: " + nodesVisited + " in " + timeLapsed;
        System.out.println(nodesTime);
        System.out.println("AI " + aiColorString + ": " +"Total time: " + totalTestTime);
        System.out.println("AI " + aiColorString + ": " + "board value " + evaluate(board,pv.getLastMove()));
        System.out.println("AI " + aiColorString + ": " + "TT collisions: " + tt.collisionCounter);
        System.out.println(" ");
        //Reset the counter
        tt.collisionCounter = 0;
    }

    private void logBuilder(int value, int alpha, int beta, int depth, boolean prune){
        String spacing = "";
        for(int i = 0; i < depth; i++){
            spacing = spacing + "   ";
        }

        String pruning = "";
        if(prune){
            pruning = ";Prune";
        }

        int player = 0;
        String playerName = "";
        if(player == 1){
            playerName = "Max player";
        }
        else if(player == -1){
            playerName = "Min player";
        }

        String line = spacing + ";Value: "+ value + ";Alpha: " + alpha + ";Beta: " + beta + pruning;
        //System.out.println(line);
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
        SearchReturn pv = null;

        //Get the hash of the current board
        long hash = tt.getHash(board);

        startTime = System.currentTimeMillis();

        //This is for out of time, forced moves or definite wins
        boolean stopLoop = false;

        //Iterative deepening
        while(depth < maxDepth && !stopLoop){
            depth += 1;
            //System.out.println("AI " + aiColorString + ": " + "Depth: " + depth);
            int targetDepth = maxDepth;
            //For the intial move make a move with max values
            //Save the pv on this depth (for instant win or loss cases)
            SearchReturn pvD = alphaBeta(new Board(board),depth,targetDepth,alpha,beta, new Move(Integer.MAX_VALUE, Integer.MAX_VALUE), hash);

            //Check for out of time
            if(pvD.outOfTime){
                //We are out of time, take the pv of one depth lower, thus do not update the pv
                stopLoop = true;
                System.out.println("AI " + aiColorString + ": " + "Out of time, play depth: " + (depth-1));
            }
            else{
                //This construction could trigger a null in pv when debugging
                //This is not really a problem otherwise since you will have enough time to do a search
                //on depth 1
                pv = pvD;

                /**System.out.println("AI " + aiColorString + ": " + "Pv value in main loop: " + pv.value);

                if(pv.value == WIN){
                    System.out.println("AI " + aiColorString + ": " + "we win!");
                }
                if(pv.value == -1*WIN){
                    System.out.println("AI " + aiColorString + ": " + "we lose!");
                }
                System.out.println("");**/
            }

        /**
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
            }**/
        }



        Move move = pv.getLastMove();

        float timeElapsed = ((float)(System.currentTimeMillis() - startTime))/1000;
        totalTestTime += (int)timeElapsed;

        /**if(totalTestTime > maxTestTime){
            System.out.println("AI " + aiColorString + ": " + "Test complete, you can stop");
        }else{
            fw.writeLine(depth,timeElapsed,nodesVisited);
        }**/

        //For some reason passing only the board will change the board
        //TODO: enable this back
        printInformation(board, pv, timeElapsed);

        return move;
    }
}
