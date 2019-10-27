package game;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Integer.*;

public class AITest {
    private String aiName = "finalVersion_groupsizeEval_vsNeighboursEval";
    private Board board;
    private final int aiColor;
    private int nodesVisited;
    private fileWriter fw;

    private String aiColorString;
    private String oponentColorString;

    public static final int WIN = 100;

    //TODO: make sure this is set correctly
    private final int totalTime = 60*10;
    private double timeLeft = totalTime;

    private int expectedAmountOfMoves = 25;
    private double takeYourTimePerc = 0.8;
    private double panicPercentage = 0.08;
    private int movesTaken = 0;
    //TODO: change this for the final version back to nothing
    private double maxMoveTime = 20;

    //Max depth
    private final int maxDepth = 20;
    private final int boardRadius;

    private String log = "";

    private TT tt;
    private long startTime;

    public AITest(int aiColor, int boardRadius){
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

        int relativeDiference = (int)((((double)scoreWhite)/((double)(scoreWhite+scoreBlack))
                - ((double)scoreBlack)/((double)(scoreWhite+scoreBlack)))*99);
        //System.out.println(relativeDiference);

        return relativeDiference;
    }

    private int evaluateNeighbours(Board board, Move move){
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

        int relativeDiference = (int)((((double)scoreWhite)/((double)(scoreWhite+scoreBlack))
                - ((double)scoreBlack)/((double)(scoreWhite+scoreBlack)))*99);
        //System.out.println(relativeDiference);

        return relativeDiference;
    }

    private int evaluate(Board board, Move move){
        //Score is always in favour of white and should be reversible (-1*score)
        int evalScore = evaluateGroupsize(new Board(board),move);
        return evalScore;
    }

    private SearchReturn alphaBeta(Board board, int depth, int targetDepth, int alpha, int beta, Move move, long hash){
        //For log builder
        boolean pruning = false;
        int layerDepth = targetDepth - depth;
        //TODO: the search tree wil now not display
        //logBuilder(-1,alpha,beta,layerDepth,false);

        //Save the old alpha in case of tt hit
        int olda = alpha;

        //Create the move order
        Queue<Move> moveOrder = new LinkedList<>();

        //For found tt Move
        Move ttMove = null;

        //Get the time
        double timeElapsed = ((double)(System.currentTimeMillis() - startTime))/1000;

        if(move.q == Byte.MAX_VALUE){
            //this is the start of the game, thus first move, no need to check for winstates
            //The move is valid, return it.
        }else if(timeElapsed > maxMoveTime){
            //Out of time, return the out of time search result
            return new SearchReturn(true);
        }
        else{
            //Update notes visited counter
            nodesVisited += 1;

            //Check if this board is in the tt
            TTElement ttElem = tt.checkTT(hash);
            if(ttElem != null){
                //Board is present in TT
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
                    //Add the tt move first to the que
                    ttMove = ttElem.move;
                }

            }

            //Check for winstate
            int winState = board.checkWin(move.q, move.r);
            //winState = 0 means no win state reached
            if(winState != 0 || depth == 0){
                //This is a leaf node, make new searchReturn object
                SearchReturn sr = new SearchReturn(move, 1);
                if(winState != 0){
                    //A win state has been found, this is thus a leaf node.
                    //Remember, this is negamax
                    //We have not made the move yet, thus take inverse
                    if(winState == -1*board.getPlayerTurn()){
                        //Win
                        sr.setValue(WIN);
                    } else {
                        //Lose
                        sr.setValue(-1*(WIN));
                    }

                }
                else if(depth == 0) {
                    //Max depth reached without win, now evaluate
                    //The evaluation function is always positive to white
                    //Negamax thus invert
                    //We have not made the move yet, thus take inverse
                    if(-1*board.getPlayerTurn() == 1){
                        sr.setValue(evaluate(board,move));
                    }else{
                        sr.setValue(-1*evaluate(board,move));
                    }
                }

                //TODO: the search tree wil now not display
                //logBuilder(sr.value,alpha,beta,layerDepth,false);
                return sr;
            }

            //Do the move
            int playerTurn = board.getPlayerTurn();
            board.setTileState(move.q, move.r, playerTurn);

            //Update the hash
            hash = tt.updateHash(hash, move, playerTurn);
        }

        //Get all the playable tiles and convert them to moves in a que
        ArrayList<Tile> playableTiles = board.getPlayableTiles();

        //If a tt move is found add that first
        if(ttMove != null){
            moveOrder.add(ttMove);
        }

        //Add every move to the move order que
        for(int childBoardNum = 0; childBoardNum < playableTiles.size(); childBoardNum++) {
            int childMoveQ = playableTiles.get(childBoardNum).q;
            int childMoveR = playableTiles.get(childBoardNum).r;

            Move childMove = new Move(childMoveQ, childMoveR);

            //Do not add ttMove twice
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

        while(!moveOrder.isEmpty()){
            //Get the best next move from the moveOrder que
            Move childMove = moveOrder.remove();

            //Do alpha beta search one layer deeper
            SearchReturn sr = alphaBeta(new Board(board), depth -1,targetDepth, -1*beta, -1*alpha, childMove, hash);
            //Negamax, -1*value
            sr.value = -1*sr.value;

            //Check for out of time
            if(sr.outOfTime){
                //Return out of time searchResult
                return sr;
            }

            //Check if there is a principle variation, if not put this move in it
            if(pv == null){
                pv = new SearchReturn(sr);
            }

            if(sr.value > score){
                pv = new SearchReturn(sr);
                score = sr.value;

                pv.moveUp(childMove);
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
        if (pv != null) {
            tt.storeTT(hash,flag,score,depth, pv.getLastMove());
        }
        //TODO: the search tree wil now not display
        //logBuilder(pv.value,alpha,beta,layerDepth,pruning);
        return pv;
    }

    private void logBuilder(int value, int alpha, int beta, int depth, boolean prune){
        //This function can be used to visualise the alpha beta search tree
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

        System.out.println(line);
    }

    private void printInformation(Board board, SearchReturn pv, double timeLapsed){
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
        System.out.println("AI " + aiColorString + ": " + "board value " + evaluate(board,pv.getLastMove()));
        System.out.println("AI " + aiColorString + ": " + "TT collisions: " + tt.collisionCounter);
        System.out.println("AI " + aiColorString + ": " +"Time for this move: " + maxMoveTime);
        System.out.println("AI " + aiColorString + ": " +"Total time left: " + timeLeft);
        System.out.println(" ");
        //Reset the counter
        tt.collisionCounter = 0;
    }

    private Move checkWinInOne(Board board){
        //This function checks for a win in one
        //Get all the playable tiles and convert them to moves in a que
        ArrayList<Tile> playableTiles = board.getPlayableTiles();

        for(int i = 0; i < playableTiles.size(); i++){
            int childMoveQ = playableTiles.get(i).q;
            int childMoveR = playableTiles.get(i).r;
            Move childMove = new Move(childMoveQ, childMoveR);

            //Update the node counter
            nodesVisited += 1;

            if(board.checkWin(childMoveQ, childMoveR) == aiColor){
                //There is a win in one do it
                return childMove;
            }
        }
        return null;
    }

    private Move checkLoseInTwo(Board board){
        //This function checks for a loss in two by just search to depth 2
        //Get all the playable tiles and convert them to moves in a que
        ArrayList<Tile> playableTiles = board.getPlayableTiles();

        for(int i = 0; i < playableTiles.size(); i++){
            int childMoveQ = playableTiles.get(i).q;
            int childMoveR = playableTiles.get(i).r;

            Board boardTmp = new Board(board);
            //Do the move
            int playerTurn = boardTmp.getPlayerTurn();
            boardTmp.setTileState(childMoveQ, childMoveR, playerTurn);

            //Update the node counter
            nodesVisited += 1;


            for(int i2 = 0; i2 < playableTiles.size(); i2++){
                int childMoveQ2 = playableTiles.get(i).q;
                int childMoveR2 = playableTiles.get(i).r;
                Move childMove2 = new Move(childMoveQ2, childMoveR2);

                //Update the node counter
                nodesVisited += 1;

                if(boardTmp.checkWin(childMoveQ2, childMoveR2) == -1*aiColor){
                    //There is a loss in two, play on the location of the loss in two
                    return childMove2;
                }
            }
        }

        return null;
    }

    public Move makeMove(Board board){
        this.board = board;

        //Mark the time as the start of searching
        startTime = System.currentTimeMillis();

        //Reset the node counter
        nodesVisited = 0;

        //Check for win in one
        /**Move winInOneMove = checkWinInOne(board);
         if(winInOneMove != null){
         //There is a win in one, do it
         System.out.println("AI " + aiColorString + ": " +"Go for win in one!");
         System.out.println(" ");
         return winInOneMove;
         }**/

        //Check for loss in two
        Move blockLossInTwo = checkLoseInTwo(board);
        if(blockLossInTwo != null){
            //There is a loss in two, block it
            System.out.println("AI " + aiColorString + ": " +"Block lose in two");
            System.out.println(" ");

            //Update the time left
            double timeElapsed = ((double)(System.currentTimeMillis() - startTime))/1000;
            timeLeft = timeLeft - timeElapsed;

            //Since it is a block in two it only had to go to depth 2
            fw.writeLine(2,timeElapsed,nodesVisited);

            return blockLossInTwo;
        }

        //TODO: enable this back for the final version
        //Set the moveTime
        /**if(movesTaken < expectedAmountOfMoves){
         maxMoveTime = (((double)totalTime)*takeYourTimePerc)/((double)expectedAmountOfMoves);
         }else{
         //Running out of time, take percentage of remaining
         maxMoveTime = ((double)timeLeft)*panicPercentage;
         }**/

        //Apply window of win lose + maxDepth
        int alpha = -1*WIN;
        int beta = WIN;

        //initial depth
        int depth = 0;

        //Save the principal variation, we need to initialize something since the real value will be created in the
        SearchReturn pv = null;

        //Get the hash of the current board
        long hash = tt.getHash(board);

        //This is for out of time, forced moves or definite wins
        boolean stopLoop = false;

        //Iterative deepening
        while(depth < maxDepth && !stopLoop){
            //Increase the depth
            depth += 1;

            int targetDepth = maxDepth;

            //For the initial move make a move with max values
            //Save the pv on this depth (for instant win or loss cases)
            //Java and copying classes is not my strongest point, just in case copy the class
            SearchReturn pvD = new SearchReturn(alphaBeta(new Board(board),depth,targetDepth,alpha,beta, new Move(Byte.MAX_VALUE, Byte.MAX_VALUE), hash));

            //Check for out of time
            if(pvD.outOfTime){
                //We are out of time, take the pv of one depth lower, thus do not update the pv
                stopLoop = true;
                System.out.println("AI " + aiColorString + ": " + "Out of time, play depth: " + (depth-1));
            }else if(pvD.value == WIN){
                pv = new SearchReturn(pvD);
                //The search concluded in a definite win
                //We won no need to search further
                stopLoop = true;
                System.out.println("AI " + aiColorString + ": " + "Stopping loop for win");
            }else if(pvD.value == -1*WIN) {
                //The program thinks that it has lost, go back to the depth where it didn't think it lost and play that move
                //Thus do not update pv, but if the depth < 2, then do update the pv, otherwise it cant block
                if (depth <= 2) {
                    pv = new SearchReturn(pvD);
                }
                //pv = alphaBeta(new Board(board),1,alpha,beta, new Move(Integer.MAX_VALUE, Integer.MAX_VALUE), hash);
                stopLoop = true;
                System.out.println("AI " + aiColorString + ": " + "Detected loss, play on depth where loss was not yet detected");
            }
            else{
                //This construction could trigger a null in pv when debugging
                //This is not really a problem otherwise since you will have enough time to do a search
                //on depth 1
                pv = new SearchReturn(pvD);
            }
        }

        //Get the best move
        Move move = pv.getLastMove();

        //Update the time left
        double timeElapsed = ((double)(System.currentTimeMillis() - startTime))/1000;
        timeLeft = timeLeft - timeElapsed;

        fw.writeLine(depth,timeElapsed,nodesVisited);

        //Print the information about the search
        printInformation(new Board(board), pv, timeElapsed);

        //Update the move counter
        movesTaken += 1;

        return move;
    }
}