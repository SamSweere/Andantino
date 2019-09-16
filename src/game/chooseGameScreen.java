package game;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import javax.swing.border.Border;


public class chooseGameScreen extends JPanel{
    private menuBox onePlayerBox;
    private menuBox twoPlayerBox;

    public int numOfPlayers = 0;

    public chooseGameScreen(int w, int h){
        int boxWidth = 200;
        int boxHeight = 100;
        int boxOnePlayerX = w/2-boxWidth/2;
        int boxOnePlayerY = h/2-boxHeight/2;
        onePlayerBox = new menuBox(boxWidth, boxHeight, boxOnePlayerX, boxOnePlayerY, "One Player");
        twoPlayerBox = new menuBox(boxWidth, boxHeight, boxOnePlayerX, boxOnePlayerY+2*boxHeight, "Two Players");



    }

    public int getNumOfPlayers(){
        while(numOfPlayers == 0){
            //do nothing
        }
        return numOfPlayers;
    }

    /** Draws the triangle as this frame's painting */
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        RenderingHints rh
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHints(rh);

        onePlayerBox.paintBox(g2d);
        twoPlayerBox.paintBox(g2d);


        g2d.dispose();
    }

    class menuBox implements MouseListener{
        private Polygon box;
        private String text;
        private int boxWidth;
        private int boxHeight;
        private int x;
        private int y;
        public Boolean clicked = false;

        public menuBox(int boxWidth, int boxHeight, int x, int y, String text){
            //Create triangle
            this.text = text;
            this.boxWidth = boxWidth;
            this.boxHeight = boxHeight;
            this.x =x;
            this.y = y;
            box = new Polygon();
            box.addPoint(x, y);
            box.addPoint(x+boxWidth, y);
            box.addPoint(x+boxWidth, y+boxHeight);
            box.addPoint(x, y+boxHeight);
            //Add mouse Listener
            addMouseListener(this);

            //Set size to make sure that the whole triangle is shown
            //setPreferredSize(new Dimension(300, 300));
        }

        public void paintBox(Graphics2D g2d){
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(Color.RED);

            g2d.draw(box);

            g2d.setColor(Color.BLACK);

            FontMetrics fm = g2d.getFontMetrics();
            int onePlayerTextX = x + ((boxWidth - fm.stringWidth(text)) / 2);
            int onePlayerTextY = y + (((boxHeight - fm.getHeight()) / 2) + fm.getAscent());
            g2d.drawString(text, onePlayerTextX, onePlayerTextY);
        }

        public Polygon getBox(){
            return  box;
        }

        //Required methods for MouseListener, though the only one you care about is click
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}

        /** Called whenever the mouse clicks.
         * Could be replaced with setting the value of a JLabel, etc. */
        public void mouseClicked(MouseEvent e) {
            Point p = e.getPoint();
            if(box.contains(p)){
                System.out.println("Triangle contains point" + text);
                clicked = true;
            }
            else{
                System.out.println("Triangle Doesn't contain point" + text);
            }
        }
    }
}
