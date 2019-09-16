package game;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;
import javax.swing.border.Border;


public class chooseGameScreen extends JPanel  {

    //public chooseGameScreen(){
    //    setLayout(new FlowLayout());

    //    JLabel lb = new JLabel("Adantino");

        //lb.setText("Adantino");

        //lb.setVerticalAlignment(JLabel.TOP);
        //lb.setHorizontalAlignment(JLabel.CENTER);
   //     lb.setBounds(200, 50, 100, 25); /*  set the position(cordinates) of label on frame */
        //setLayout(null);
   //     add(lb);
  //  }

    @Override
    public void paintComponent(Graphics g) {
        //super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        g2d.fillOval(0, 0, 30, 30);
        g2d.drawOval(0, 50, 30, 30);
        g2d.fillRect(50, 0, 30, 30);
        g2d.drawRect(50, 50, 30, 30);

        g2d.draw(new Ellipse2D.Double(0, 100, 30, 30));
    }
}
