package game;

import java.awt.*;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class Application {

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                //Create and set up the window.
                JFrame frame = new JFrame("Adantino by Sam Sweere");
                frame.setSize(600, 600);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                //Set up the content pane.
                Container pane = frame.getContentPane();

                //Display the window.
                frame.pack();
                frame.setVisible(true);
                new guiController(pane);
            }
        });
    }
}