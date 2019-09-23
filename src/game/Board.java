package game;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import javax.swing.*;
import javax.swing.event.ChangeListener;

public class Board extends JPanel implements MouseListener {
    private static int radius = 25;
    private static int boardRadius = 3;

    private int playerTurn = 1;

    private Tile[][] tiles = new Tile[boardRadius*2+1][boardRadius*2+1];

    //The reason I save every board for the history is the instant lose positions, these got overwritten when only
    //saving the moves when undoing and keep playing
    private ArrayList<Tile[][]> moveHistory = new ArrayList<>();

    public Board(){
        Color backgroundColor = new Color(249,189,59);
        setBackground(backgroundColor);

        initTiles();
        //Add the first board to the history
        addToHistory();

        this.addMouseListener(this);
    }

    private void initTiles(){
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawHexGrid(g);
    }


    public void drawHexGrid(Graphics g) {
        //super.repaint();
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHints(rh);

        g2d.setStroke(new BasicStroke(2));

        //Bit of a cheat but it works, first draw the border
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius){
                    Polygon hexagon = polygonTile(q,r);
                    if(tiles[q][r].state == 1){
                        g2d.setColor(Color.white);
                        g2d.fillPolygon(hexagon);
                        g2d.draw(hexagon);
                    }else if(tiles[q][r].state == -1){
                        g2d.setColor(Color.black);
                        g2d.fillPolygon(hexagon);
                        g2d.draw(hexagon);
                    }
                }
            }
        }

        //Now draw the grid, this fixes the tiles being drawn over the border
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius){
                    Polygon hexagon = polygonTile(q,r);
                    g2d.setColor(Color.GRAY);
                    g2d.draw(hexagon);
                }
            }
        }

        g2d.dispose();
    }
    private Point locationToCoordinates(int q, int r){
        Point coordinates = new Point(0,0);
        //Offset for center
        r = r - boardRadius;
        q = q - boardRadius;

        coordinates.x = (int)(0.5*Math.sqrt(3)*radius*r + q*Math.sqrt(3)*radius);
        coordinates.y = (int)(0.75 * 2 * radius*r);

        //Center everything
        Dimension size = getSize();
        int w = (int) size.getWidth();
        int h = (int) size.getHeight();
        coordinates.x += w/2;
        coordinates.y += h/2;

        return coordinates;
    }

    private Polygon polygonTile(int q, int r) {
        Point coordinates = locationToCoordinates(q, r);

        Polygon polygon = new Polygon();
        double w = Math.sqrt(3) * radius;
        double h = 2 * radius;
        polygon.addPoint((int)(coordinates.x - 0.5*w),(int)(coordinates.y + 0.25*h));
        polygon.addPoint( (int)(coordinates.x),(int)(coordinates.y + 0.5*h));
        polygon.addPoint((int)(coordinates.x + 0.5*w),(int)(coordinates.y + 0.25*h));
        polygon.addPoint((int)(coordinates.x + 0.5*w),(int)(coordinates.y - 0.25*h));
        polygon.addPoint( (int)(coordinates.x),(int)(coordinates.y - 0.5*h));
        polygon.addPoint((int)(coordinates.x - 0.5*w),(int)(coordinates.y - 0.25*h));

        return polygon;
    }

    public void undoLastMove(){
        //System.out.println(moveHistory.size() );

        if(moveHistory.size() > 1){
            //Remove the last move
            moveHistory.remove(moveHistory.size() -1);

            //Get the second to last move
            Tile[][] localTiles = moveHistory.get(moveHistory.size() - 1);


            for(int q = 0; q < boardRadius*2+1; q++){
                for(int r = 0; r < boardRadius*2+1; r++){
                    if(r + q >= boardRadius&& r + q <= 3*boardRadius) {
                        tiles[q][r].state = localTiles[q][r].state;
                    }
                }
            }
            //Tile[][] lastTile = moveHistory.get(moveHistory.size() - 1);
            //System.out.println(lastTile.q + " " + lastTile.r);
            //reset the last played state back to 0
            //tiles[lastTile.q][lastTile.r].state = 0; //new Tile(lastTile.q, lastTile.r, 0);

            //remove the last move
            //moveHistory.remove(moveHistory.size() -1);

            //switch which player's move it is
            playerTurn = playerTurn*-1;

            //repaint the board
            this.repaint();
        }
    }

    private void addToHistory(){
        Tile[][] tilesCopy = new Tile[boardRadius*2+1][boardRadius*2+1];

        //Really impractical that you cant copy an array easily
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius) {
                    tilesCopy[q][r] = new Tile(q,r,tiles[q][r].state);
                }
            }
        }

        moveHistory.add(tilesCopy);
    }

    private boolean checkValidMove(int clickQ, int clickR){
        int playedNeighbors = 0;
        //Check if tile not already taken
        if(tiles[clickQ][clickR].state != 0 && tiles[clickQ][clickR].state != -2 && tiles[clickQ][clickR].state != 2){
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
                    if(tiles[checkQ][checkR].state != 0){
                        //Something is played here
                        playedNeighbors++;
                    }
                }
            }
        }

        if(moveHistory.size() == 1){
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
                else if(tiles[checkQ][checkR].state == player){
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
                else if(tiles[checkQ][checkR].state == player){
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
                else if(tiles[checkQ][checkR].state == player){
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

    //The tilesFF have the visited places marked, it is completely seperate from tiles
    private Tile[][] tilesFF;

    //Local visit: state = 4, global visit state = 3
    //The reason for the difference is that is speeds up the calculation
    //If one global visit did not result in a winstate then when visiting a globally visited tile in the
    //next check will not give a winstate, thus it can be skipped at that moment
    private void setLocalVisitToGlobalVisit(int state){
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius) {
                    if(tilesFF[q][r].state == 4){
                        tilesFF[q][r].state = state;
                    }
                }
            }
        }
    }

    private void setGlobalLossStates(int state){
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius) {
                    //Check in the local tiles field and change is the global
                    //The reason for this is that loss states are permanent to the game
                    if(tilesFF[q][r].state == 4){
                        tiles[q][r].state = state;
                    }
                }
            }
        }
    }

    private boolean floodFill(Tile startingPoint, int player){
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
            tilesFF[tile.q][tile.r].state = 4;

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
                            setLocalVisitToGlobalVisit(3);
                            return false;
                        }else if(tilesFF[checkQ][checkR].state == 3){
                            //Already visited in previous runs, thus no encaptulation
                            //Set all the locally visited tiles to globally visited tiles
                            setLocalVisitToGlobalVisit(3);
                            return false;
                        }
                        else if(tilesFF[checkQ][checkR].state == player){
                            //Tile of player
                            continue;
                        }
                        else if(tilesFF[checkQ][checkR].state == 4){
                            //Tile already visited
                            continue;
                        }
                        else{
                           //add tile to the stack
                            st.add(tilesFF[checkQ][checkR]);
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
        setGlobalLossStates(player*2);

        return false;
    }

    private boolean checkWinEnclose(int lastMoveQ, int lastMoveR, int player) {
        //We use a flood-fill to detect this, the flood-fill starts at an emtpy or at an opponent tile
        //surrounding the new placed tile. If the flood-fill hits a wall it is impossible that it is encapsulated
        //thus stop and return false
        //int player = tiles[lastMoveQ][lastMoveR].state;

        //Point[] axialDirections =  {new Point(1,0), new Point(1,-1),new Point(0,-1),
       //         new Point(-1,0),new Point(-1,+1),new Point(0,+1)};

        //Copy the tiles such that we can mark where we have been
        //2d clone doesnt work so loop through it
        tilesFF = new Tile[boardRadius*2+1][boardRadius*2+1];
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius) {
                    tilesFF[q][r] = new Tile(tiles[q][r].q, tiles[q][r].r, tiles[q][r].state);
                }
            }
        }

        //Set the state for the dwarn tile in the FF. Since in this case the check already has been done
        //The reason for this ugly contstruction is that in this way we can keep the check win in one function
        tilesFF[lastMoveQ][lastMoveR].state = player;

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
                        if (tiles[checkQ][checkR].state != player) {
                            startingPoints.add(tiles[checkQ][checkR]);
                        }
                    }
                }
            }
        }

        while(!startingPoints.empty()){
            Tile startingPoint = (Tile)startingPoints.pop();
            if(tilesFF[startingPoint.q][startingPoint.r].state == 2){
                //tile already visited, no use checking
            }
            else if(floodFill(startingPoint, player)){
                //win condition met
                return true;
            }
        }

        //No win condition met
        return false;
    }


    private int checkWin(int lastMoveQ, int lastMoveR, int player){
        //Check if the player put a move in a define losing position (previously found by the enclosement algorithm
        if(tiles[lastMoveQ][lastMoveR].state == playerTurn*-2){
            //The other player wins because they put their own in an enclosed area
            return player*-1;
        }

        //Check win rows
        if(checkWinRows(lastMoveQ,lastMoveR, player)){
            //win condition met, return player number
            return player;
        }

        //Check win enclode
        if(checkWinEnclose(lastMoveQ,lastMoveR, player)){
            //win condition met, return player number
            return player;
        }

        //No win conditions met, thus nobody won
        return 0;
    }

    private void playerClicked(MouseEvent mouseEvent){
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius){
                    if(polygonTile(q,r).contains(mouseEvent.getX(),mouseEvent.getY())){
                        System.out.println("Clicked tile: " + q + " " + r);

                        //Check if the move is valid
                        if(checkValidMove(q,r)){
                            //Valid move
                            if(tiles[q][r].state <= 2 && tiles[q][r].state >= -2 ){
                                int winningPlayer = checkWin(q,r, playerTurn);

                                tiles[q][r].state = playerTurn;

                                //add the move to the history
                                addToHistory();

                                playerTurn = playerTurn*-1;

                                //We did all the checks we can now draw it (this makes it more nice visually)
                                this.repaint();

                                //When the tile is drawn show the possible win message
                                if(winningPlayer == 1){
                                    System.out.println("Player one won!");
                                    JOptionPane.showMessageDialog(null, "Player 1 won!");
                                }
                                else if(winningPlayer == -1){
                                    System.out.println("Player two won!");
                                    JOptionPane.showMessageDialog(null, "Player 2 won!");
                                }
                            }
                        }else{
                            System.out.println("Invalid move, do nothing");
                        }
                        //This return fixes the problem when the border between two tiles is clicked
                        return;
                    }
                }
            }
        }
    }


    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        playerClicked(mouseEvent);
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}