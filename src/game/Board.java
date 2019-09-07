package game;

import java.awt.*;
import javax.swing.JPanel;

public class Board extends JPanel {
    private static int radius = 20;
    private static int boardRadius = 9;

    private Tile[][][] tiles = new Tile[boardRadius*2+1][boardRadius*2+1][boardRadius*2+1];

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        initHexGrid();
        drawHexGrid(g);
    }

    public void initHexGrid(){
        Dimension size = getSize();
        int w = (int) size.getWidth();
        int h = (int) size.getHeight();

        for (int x = -boardRadius; x <= boardRadius; x++){
            for (int y = -boardRadius; y <= boardRadius; y++){
                for (int z = -boardRadius; z <= boardRadius; z++){
                    if(x + y + z == 0){
                        //If not then the coordinates are invallid
                        if((Math.abs(x)+Math.abs(y)+Math.abs(z))/2 <= boardRadius ) {
                            tiles[x + boardRadius][y + boardRadius][z + boardRadius] =
                                    new Tile(x, y, z, radius, w, h, 0);
                        }
                    }
                }
            }
        }
    }

    public void drawHexGrid(Graphics g) {
        super.repaint();
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHints(rh);

        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.gray);

        for (int x = -boardRadius; x <= boardRadius; x++){
            for (int y = -boardRadius; y <= boardRadius; y++){
                for (int z = -boardRadius; z <= boardRadius; z++){
                    if(x + y + z == 0){
                        //If not then the coordinates are invallid
                        if((Math.abs(x)+Math.abs(y)+Math.abs(z))/2 <= boardRadius ) {
                            g2d.draw(tiles[x + boardRadius][y + boardRadius][z + boardRadius].getTile());
                        }
                    }
                }
            }
        }
    }
}