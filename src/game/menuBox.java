package game;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class menuBox{
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
        //addMouseListener(this);

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

  /**  //Required methods for MouseListener, though the only one you care about is click
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
*/
    /** Called whenever the mouse clicks.
     * Could be replaced with setting the value of a JLabel, etc.
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        if(box.contains(p)){
            System.out.println("Triangle contains point" + text);
            clicked = true;
        }
        else{
            System.out.println("Triangle Doesn't contain point" + text);
        }
    }*/
}
