package game;

import java.util.Stack;

public class boardChecker {
    private int boardRadius;
    private int playerTurn;
    private Board board;

    //public boardChecker(int boardRadius){
    //    this.boardRadius = boardRadius;
    //}

    private boolean checkWinRows(int lastMoveQ, int lastMoveR, int player){

        //int player = tiles[lastMoveQ][lastMoveR].state;
        int numInRow = 0;
        //Check q row
        for(int q = -4; q <= 4; q++){
            int checkQ = lastMoveQ + q;
            int checkR = lastMoveR ;
            if(!(checkQ + checkR < boardRadius || checkR + checkQ > 3*boardRadius || checkQ < 0 || checkR < 0
                    || checkQ > boardRadius*2 || checkR > boardRadius*2)){
                //Not off the board
                if(q == 0){
                    //Own location, this is thus a player
                    numInRow++;
                }
                else if(board.getTileState(checkQ,checkR) == player){
                    numInRow++;
                }
                else{
                    numInRow = 0;
                }
                if(numInRow == 5){
                    //Win condition met
                    return true;
                }
            }
        }

        //Reset counter
        numInRow = 0;

        //Check r row
        for(int r = -4; r <= 4; r++){
            int checkQ = lastMoveQ;
            int checkR = lastMoveR + r;
            if(!(checkQ + checkR < boardRadius || checkR + checkQ > 3*boardRadius || checkQ < 0 || checkR < 0
                    || checkQ > boardRadius*2 || checkR > boardRadius*2)){
                //Not off the board
                if(r == 0){
                    //Own location, this is thus a player
                    numInRow++;
                }
                else if(board.getTileState(checkQ,checkR) == player){
                    numInRow++;
                }
                else{
                    numInRow = 0;
                }
                if(numInRow == 5){
                    //Win condition met
                    return true;
                }
            }
        }

        //Reset counter
        numInRow = 0;

        //Check q + r row
        for(int r = -4; r <= 4; r++){
            int checkQ = lastMoveQ - r;
            int checkR = lastMoveR + r;
            if(!(checkQ + checkR < boardRadius || checkR + checkQ > 3*boardRadius || checkQ < 0 || checkR < 0
                    || checkQ > boardRadius*2 || checkR > boardRadius*2)){
                //Not off the board
                if(r == 0){
                    //Own location, this is thus a player
                    numInRow++;
                }
                else if(board.getTileState(checkQ,checkR) == player){
                    numInRow++;
                }
                else{
                    numInRow = 0;
                }
                if(numInRow == 5){
                    //Win condition met
                    return true;
                }
            }
        }

        //No win condition met
        return false;
    }


    //Local visit: state = 4, global visit state = 3
    //The reason for the difference is that is speeds up the calculation
    //If one global visit did not result in a winstate then when visiting a globally visited tile in the
    //next check will not give a winstate, thus it can be skipped at that moment
    private void setLocalVisitToGlobalVisit(int state, Board boardFF){
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius) {
                    if(boardFF.getTileState(q,r) == 4){
                        boardFF.setTileStateFF(q,r, state);
                    }
                }
            }
        }
    }

    private void setGlobalLossStates(int state, Board boardFF){
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius) {
                    //Check in the local tiles field and change is the global
                    //The reason for this is that loss states are permanent to the game
                    if(boardFF.getTileState(q,r) == 4){
                        board.setTileState(q,r,state);
                    }
                }
            }
        }
    }

    private boolean floodFill(Tile startingPoint, int player, Board boardFF){
        Stack st = new Stack();
        st.add(startingPoint);

        boolean encounteredOtherPlayer = false;

        while(!st.empty()){
            Tile tile = (Tile)st.pop();

            //This needs to be done such that we dont encapsulate an empty area
            if(tile.state == player*-1){
                encounteredOtherPlayer = true;
            }

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
                            //Search out of bounds, thus not enclosed
                            //Set all the locally visited tiles to globally visited tiles
                            setLocalVisitToGlobalVisit(3, boardFF);
                            return false;
                        }else if(boardFF.getTileState(checkQ,checkR) == 3){
                            //Already visited in previous runs, thus no encaptulation
                            //Set all the locally visited tiles to globally visited tiles
                            setLocalVisitToGlobalVisit(3, boardFF);
                            return false;
                        }
                        else if(boardFF.getTileState(checkQ,checkR) == player){
                            //Tile of player
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

        if(encounteredOtherPlayer){
            return true;
        }
        //It encapsulates, but there is no tile of the other player inside
        //Set all the locally visited tiles to loss tiles for the other team
        setGlobalLossStates(player*2, boardFF);

        return false;
    }

    private boolean checkWinEnclose(int lastMoveQ, int lastMoveR, int player) {
        //We use a flood-fill to detect this, the flood-fill starts at an emtpy or at an opponent tile
        //surrounding the new placed tile. If the flood-fill hits a wall it is impossible that it is encapsulated
        //thus stop and return false
        //int player = tiles[lastMoveQ][lastMoveR].state;

        //Point[] axialDirections =  {new Point(1,0), new Point(1,-1),new Point(0,-1),
        //         new Point(-1,0),new Point(-1,+1),new Point(0,+1)};

        //Copy the board such that we can mark where we have been
        Board boardFF = new Board(board);


        //Set the state for the dwarn tile in the FF. Since in this case the check already has been done
        //The reason for this ugly contstruction is that in this way we can keep the check win in one function
        boardFF.setTileStateFF(lastMoveQ, lastMoveR, player);

        Stack startingPoints = new Stack();

        //For every neighbor
        for(int q = -1; q <= 1; q++){
            for(int r = -1; r <= 1; r++){
                if(q != r){
                    int checkQ = lastMoveQ + q;
                    int checkR = lastMoveR + r;
                    if(!(checkQ + checkR < boardRadius || checkR + checkQ > 3*boardRadius || checkQ < 0 || checkR < 0
                            || checkQ > boardRadius*2 || checkR > boardRadius*2)) {
                        //This is a valid tile
                        if (board.getTileState(checkQ,checkR) != player) {
                            //startingPoints.add(tiles[checkQ][checkR]);
                            startingPoints.add(board.getTile(checkQ,checkR));
                        }
                    }
                }
            }
        }

        while(!startingPoints.empty()){
            Tile startingPoint = (Tile)startingPoints.pop();
            if(boardFF.getTileState(startingPoint.q,startingPoint.r) == 2){
                //tile already visited, no use checking
            }
            else if(floodFill(startingPoint, player, boardFF)){
                //win condition met
                return true;
            }
        }

        //No win condition met
        return false;
    }


    public int checkWin(int lastMoveQ, int lastMoveR, Board board){
        int player = board.getPlayerTurn();
        this.playerTurn = player;
        this.board = board;
        this.boardRadius = board.getBoardRadius();
        //Check if the player put a move in a define losing position (previously found by the enclosement algorithm
        if(board.getTileState(lastMoveQ,lastMoveR) == playerTurn*-2){
            //The other player wins because they put their own in an enclosed area
            return player*-1;
        }

        //Check win rows
        if(checkWinRows(lastMoveQ,lastMoveR, player)){
            //win condition met, return player number
            return player;
        }

        //Check win enclose
        if(checkWinEnclose(lastMoveQ,lastMoveR, player)){
            //win condition met, return player number
            return player;
        }

        //No win conditions met, thus nobody won
        return 0;
    }


    public boolean checkValidMove(int clickQ, int clickR, Board board, int moveNumber){
        this.board = board;
        this.boardRadius = board.getBoardRadius();
        //Check if the location is on the board
        if(clickQ + clickR < boardRadius || clickQ + clickR > 3*boardRadius || clickQ < 0 || clickR < 0
                || clickQ > boardRadius*2 || clickR > boardRadius*2){
            //Off the board
            return false;
        }

        int playedNeighbors = 0;
        //Check if tile not already taken
        if(board.getTileState(clickQ,clickR) == -1 || board.getTileState(clickQ,clickR) == 1){
            return false;
        }

        //Check all the tiles around the tile
        for(int q = -1; q <= 1; q++){
            for(int r = -1; r <= 1; r++){
                int checkQ = clickQ + q;
                int checkR = clickR + r;
                if(checkQ + checkR < boardRadius || checkR + checkQ > 3*boardRadius || checkQ < 0 || checkR < 0
                        || checkQ > boardRadius*2 || checkR > boardRadius*2){
                    //Off the board, no neighbors
                }
                else if(Math.abs(q + r) > 1){
                    //Not a neighbor
                }
                else{
                    if(board.getTileState(checkQ,checkR) == -1 || board.getTileState(checkQ,checkR) == 1){
                        //Something is played here
                        playedNeighbors++;
                    }
                }
            }
        }

        if(moveNumber == 1){
            //this is the first move, 1 neighbor is allowed
            if(playedNeighbors >= 1){
                return true;
            }
        }
        else if(playedNeighbors >= 2){
            //It is a valid move
            return true;
        }
        //Otherwise it is an invalid move
        return false;

    }

}
