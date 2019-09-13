package game;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;


public class chooseGameScreen extends JPanel  {

    public chooseGameScreen(){
        setLayout(new FlowLayout());

        JLabel lb = new JLabel("Adantino");

        //lb.setText("Adantino");

        //lb.setVerticalAlignment(JLabel.TOP);
        //lb.setHorizontalAlignment(JLabel.CENTER);
        lb.setBounds(200, 50, 100, 25); /*  set the position(cordinates) of label on frame */
        //setLayout(null);
        add(lb);
    }


    //@Override
    //public void paintComponent(Graphics g) {
     //   super.paintComponent(g);


    //}
}
