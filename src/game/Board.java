package game;

public class Board {
    private final int boardRadius;
    private Tile[][] tiles;

    public Board(int boardRadius){
        this.boardRadius = boardRadius;
        this.tiles = new Tile[boardRadius*2+1][boardRadius*2+1];
        initialBoard();
    }

    public Board(Board board){
        this.boardRadius = board.getBoardRadius();
        this.tiles = board.getTiles();
    }

    public void initialBoard(){
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius){
                    tiles[q][r] = new Tile(q,r, 0);
                    if(q == boardRadius && r == boardRadius){
                        //starting tile
                        tiles[q][r].state = -1;
                    }
                }
            }
        }

        //Set initial playable tiles
        //Check all the tiles around the starting tile
        for(int surQ = -1; surQ <= 1; surQ++) {
            for (int surR = -1; surR <= 1; surR++) {
                int checkSurQ = surQ + boardRadius;
                int checkSurR = surR + boardRadius;
                if (checkSurQ + checkSurR < boardRadius || checkSurQ + checkSurR > 3 * boardRadius || checkSurQ < 0 || checkSurR < 0
                        || checkSurQ > boardRadius * 2 || checkSurR > boardRadius * 2) {
                    //Off the board, not playable
                } else if (Math.abs(surQ + surR) > 1) {
                    //Not a neighbor
                } else if(surQ == 0 && surR == 0){
                    //this is itself
                }
                else {
                    //Something can be played here on the initial board
                    tiles[checkSurQ][checkSurR].playable = true;
                }
            }
        }
    }


    private boolean checkPlayableTile(int checkQ, int checkR){
        //This function does not work for the initial board
        int playedNeighbors = 0;

        //Check if tile not already taken
        if(tiles[checkQ][checkR].state == -1 || tiles[checkQ][checkR].state == 1){
            return false;
        }

        //Check all the tiles around the tile
        for(int q = -1; q <= 1; q++){
            for(int r = -1; r <= 1; r++){
                int checkSurQ = checkQ + q;
                int checkSurR = checkR + r;
                if(checkSurQ + checkSurR < boardRadius || checkSurQ + checkSurR > 3*boardRadius || checkSurQ < 0 || checkSurR < 0
                        || checkSurQ > boardRadius*2 || checkSurR > boardRadius*2){
                    //Off the board, no neighbors
                }
                else if(Math.abs(q + r) > 1){
                    //Not a neighbor
                }
                else{
                    if(tiles[checkSurQ][checkSurR].state == -1 || tiles[checkSurQ][checkSurR].state == 1){
                        //Something is played here
                        playedNeighbors++;
                    }
                }
            }
        }

        if(playedNeighbors >= 2){
            //It is a valid move
            return true;
        }
        //Otherwise it is an invalid move
        return false;
    }


    public void markPlayableTiles(){
        for(int q = 0; q < boardRadius*2+1; q++) {
            for (int r = 0; r < boardRadius * 2 + 1; r++) {
                if (r + q >= boardRadius && r + q <= 3 * boardRadius) {
                   if(checkPlayableTile(q,r)){
                       //Mark as playable
                       tiles[q][r].playable = true;
                   }else if(tiles[q][r].playable){
                       //It is no longer possible to play something here
                       tiles[q][r].playable = false;
                   }
                }
            }
        }
    }

    public int getTileState(int q, int r){
        return tiles[q][r].state;
    }

    public Tile getTile(int q, int r){
        return tiles[q][r];
    }

    public void setTileState(int q, int r, int state){
        tiles[q][r].state = state;
        //Check for new possible places to put the next tile
        markPlayableTiles();
    }

    public void setTileStateFF(int q, int r, int state){
        //This one is for the flood fill, there you do not want to recheck the possible states
        tiles[q][r].state = state;
    }

    public Tile[][] getTiles(){
        Tile[][] tilesCopy = new Tile[boardRadius*2+1][boardRadius*2+1];
        //Really impractical that you cant copy an array easily
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                 if(r + q >= boardRadius&& r + q <= 3*boardRadius) {
                    tilesCopy[q][r] = new Tile(q,r,tiles[q][r].state);
                 }
             }
        }
        return tilesCopy;
    }

    public  int getBoardRadius(){
        return boardRadius;
    }

    public boolean tileIsPlayable(int q, int r){
        return tiles[q][r].playable;
    }
}
