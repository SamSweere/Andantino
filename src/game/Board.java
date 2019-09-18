package game;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Stack;
import javax.swing.*;
import javax.swing.event.ChangeListener;

public class Board extends JPanel implements MouseListener {
    private static int radius = 40;
    private static int boardRadius = 3;

    private int playerTurn = 1;

    private Tile[][] tiles = new Tile[boardRadius*2+1][boardRadius*2+1];

    private ArrayList<Tile> moveHistory = new ArrayList<>();

    public Board(){
        Color backgroundColor = new Color(249,189,59);
        setBackground(backgroundColor);

        initTiles();
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

        if(moveHistory.size() > 0){
            Tile lastTile = moveHistory.get(moveHistory.size() - 1);
            //System.out.println(lastTile.q + " " + lastTile.r);
            //reset the last played state back to 0
            tiles[lastTile.q][lastTile.r].state = 0; //new Tile(lastTile.q, lastTile.r, 0);

            //remove the last move
            moveHistory.remove(moveHistory.size() -1);

            //switch which player's move it is
            playerTurn = playerTurn*-1;

            //repaint the board
            this.repaint();
        }
    }

    private boolean checkValidMove(int clickQ, int clickR){
        int playedNeighbors = 0;
        //Check if tile not already taken
        if(tiles[clickQ][clickR].state != 0){
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

        if(moveHistory.size() == 0){
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

    private boolean checkWinRows(int lastMoveQ, int lastMoveR){

        int player = tiles[lastMoveQ][lastMoveR].state;
        int numInRow = 0;
        //Check q row
        for(int q = -4; q <= 4; q++){
            int checkQ = lastMoveQ + q;
            int checkR = lastMoveR ;
            if(!(checkQ + checkR < boardRadius || checkR + checkQ > 3*boardRadius || checkQ < 0 || checkR < 0
                    || checkQ > boardRadius*2 || checkR > boardRadius*2)){
                //Not off the board
                if(tiles[checkQ][checkR].state == player){
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
                if(tiles[checkQ][checkR].state == player){
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
                if(tiles[checkQ][checkR].state == player){
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

    private boolean floodFill(Tile startingPoint, int player){
        Stack st = new Stack();
        st.add(startingPoint);

        boolean encounteredOtherPlayer = false;

        while(!st.empty()){
            Tile tile = (Tile)st.pop();
            //For every neighbor
            for(int q = -1; q <= 1; q++){
                for(int r = -1; r <= 1; r++){
                    if(q != r){
                        //this goes through all the neighbors
                        //if(st.search(new Tile(tile.q + q, tile.r +r, 0)) == -1 || st.search(new Tile(tile.q + q, tile.r +r, 0)) == -1)
                    }
                }
            }
        }

        if(encounteredOtherPlayer){
            return true;
        }
        return false;
    }

    private boolean checkWinEnclose(int lastMoveQ, int lastMoveR) {
        //We use a flood-fill to detect this, the flood-fill starts at an emtpy or at an opponent tile
        //surrounding the new placed tile. If the flood-fill hits a wall it is impossible that it is encapsulated
        //thus stop and return false
        int player = tiles[lastMoveQ][lastMoveR].state;

        //Point[] axialDirections =  {new Point(1,0), new Point(1,-1),new Point(0,-1),
       //         new Point(-1,0),new Point(-1,+1),new Point(0,+1)};

        Stack startingPoints = new Stack();

        //For every neighbor
        for(int q = -1; q <= 1; q++){
            for(int r = -1; r <= 1; r++){
                if(q != r){
                    //this goes through all the neighbors
                    if(tiles[q][r].state != player){
                        startingPoints.add(tiles[q][r]);
                    }
                }
            }
        }

        while(!startingPoints.empty()){
            if(floodFill((Tile)startingPoints.pop(), player)){
                //win condition met
                return true;
            }
        }

        //No win condition met
        return false;
    }


    private int checkWin(int lastMoveQ, int lastMoveR){
        //Check win rows
        if(checkWinRows(lastMoveQ,lastMoveR)){
            //win condition met, return player number
            return tiles[lastMoveQ][lastMoveR].state;
        }

        //Check win enclode
        //if(checkWinEnclose(lastMoveQ,lastMoveR)){
            //win condition met, return player number
            //return tiles[lastMoveQ][lastMoveR].state;
        //}

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
                            if(tiles[q][r].state == 0){
                                //state is free, we can draw it
                                this.repaint();

                                tiles[q][r].state = playerTurn;

                                //add the move to the history
                                moveHistory.add(tiles[q][r]);

                                playerTurn = playerTurn*-1;

                                int winningPlayer = checkWin(q,r);
                                if(winningPlayer == 1){
                                    System.out.println("Player one won!");
                                    JOptionPane.showMessageDialog(null, "Player 1 won!");
                                }
                                else if(winningPlayer == -1){
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