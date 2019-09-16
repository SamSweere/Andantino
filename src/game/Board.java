package game;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

public class Board extends JPanel implements MouseListener {
    private static int radius = 20;
    private static int boardRadius = 9;

    private Tile[][] tiles = new Tile[boardRadius*2+1][boardRadius*2+1];

    public Board(){
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
                        tiles[q][r].state = 2;
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

    public void initHexGrid(){
        Dimension size = getSize();
        int w = (int) size.getWidth();
        int h = (int) size.getHeight();


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
        g2d.setColor(Color.gray);

        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius){
                    //System.out.println(tiles[q][r].q);
                    Polygon hexagon = polygonTile(q,r);
                    g2d.draw(hexagon);
                    //g2d.draw(calcDrawTile(tiles[q][r].q, tiles[q][r].r));
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



    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        System.out.println(mouseEvent.getX() +" "+ mouseEvent.getY());
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}