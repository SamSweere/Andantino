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
    private static int boardRadius = 9;

    private int playerTurn = 1;

    private Tile[][] tiles = new Tile[boardRadius*2+1][boardRadius*2+1];

    private boardChecker boardCheck = new boardChecker(boardRadius);

    //The reason I save every board for the history is the instant lose positions, these got overwritten when only
    //saving the moves when undoing and keep playing
    private ArrayList<Tile[][]> moveHistory = new ArrayList<>();

    public Board(){
        Color backgroundColor = new Color(249,189,59);
        setBackground(backgroundColor);

        initTiles();
        //Add the first board to the history
        addToHistory();
        //Initialise the winchecker


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

    private void playerClicked(MouseEvent mouseEvent){
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius){
                    if(polygonTile(q,r).contains(mouseEvent.getX(),mouseEvent.getY())){
                        System.out.println("Clicked tile: " + q + " " + r);

                        //Check if the move is valid
                        if(boardCheck.checkValidMove(q,r, tiles, moveHistory.size())){
                            int winningPlayer = boardCheck.checkWin(q,r, playerTurn, tiles);

                            tiles[q][r].state = playerTurn;

                            //add the move to the history
                            addToHistory();

                            //Change the player turn
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