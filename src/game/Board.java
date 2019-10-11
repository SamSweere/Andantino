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
    }

    public int getTileState(int q, int r){
        return tiles[q][r].state;
    }

    public Tile getTile(int q, int r){
        return tiles[q][r];
    }

    public void setTileState(int q, int r, int state){
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
}
