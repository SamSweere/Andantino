package game;

import java.awt.*;
import javax.swing.JPanel;

public class Board extends JPanel {
    private int radius = 200;

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

        g2d.setStroke(new BasicStroke(4));
        g2d.setColor(Color.gray);

        Tile hexagon = new Tile(new Point(0, 0), radius, w, h, 0);
        Tile hexagon2 = new Tile(new Point(2, 0), radius, w, h, 0);
        g2d.draw(hexagon.getTile());
        g2d.draw(hexagon2.getTile());
       // g.setColor(Color.RED);
       // g.drawPolygon(hexagon.getHexagon());



    }
}