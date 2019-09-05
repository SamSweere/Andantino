package game;

import java.awt.*;
import javax.swing.JPanel;

public class Board extends JPanel {
    private static int radius = 20;
    private static int boardRadius = 9;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawHexGrid(g);
    }



    public void drawHexGrid(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHints(rh);

        Dimension size = getSize();
        int w = (int) size.getWidth();
        int h = (int) size.getHeight();

        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.gray);

        Tile[][][] tiles = new Tile[boardRadius*2+1][boardRadius*2+1][boardRadius*2+1];

        for (int x = -boardRadius; x <= boardRadius; x++){
            for (int y = -boardRadius; y <= boardRadius; y++){
                for (int z = -boardRadius; z <= boardRadius; z++){
                    if(x + y + z == 0){
                        //If not then the coordinates are invallid
                        if((Math.abs(x)+Math.abs(y)+Math.abs(z))/2 <= boardRadius ) {
                            tiles[x + boardRadius][y + boardRadius][z + boardRadius] =
                                    new Tile(x, y, z, radius, w, h, 0);
                            g2d.draw(tiles[x + boardRadius][y + boardRadius][z + boardRadius].getTile());
                        }
                    }
                }
            }
        }


        /*for (int x = -boardSize/2; x < boardSize/2; x++){
            for (int y = -boardSize/2; y < boardSize/2+1; y++){
                if((y%2) == 0){
                    if(Math.abs(x)*2 <= boardSize - Math.abs(y)) {
                        tiles[x + boardSize / 2][y + boardSize / 2] = new Tile(new Point(x * 2, y), radius, w, h, 0);
                        g2d.draw(tiles[x + boardSize / 2][y + boardSize / 2].getTile());
                    }
                }
                else {
                    if (Math.abs(x) * 2 <= boardSize - Math.abs(y)) {
                        //ofset of 1
                        tiles[x + boardSize / 2][y + boardSize / 2] = new Tile(new Point(x * 2 + 1, y), radius, w, h, 0);
                        g2d.draw(tiles[x + boardSize / 2][y + boardSize / 2].getTile());

                    }
                }
            }
        }*/

        //Tile hexagon = new Tile(0,0,0, radius, w, h, 0);
        //Tile hexagon2 = new Tile(1,-1,0, radius, w, h, 0);
        //g2d.draw(hexagon.getTile());
        //g2d.draw(hexagon2.getTile());
       // g.setColor(Color.RED);
       // g.drawPolygon(hexagon.getHexagon());



    }
}