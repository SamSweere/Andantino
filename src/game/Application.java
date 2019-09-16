package game;

import java.awt.*;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class Application {
    private int screenWidth = 600;
    private int screenHeight = 600;

   // private menuBox onePlayerBox;
   // private menuBox twoPlayerBox;

    public int numOfPlayers = 0;


    public Application() throws InterruptedException {
        JFrame f = new JFrame();
        f.setLayout(new BorderLayout());
        f.setSize(screenWidth, screenHeight);

        Dimension size = f.getSize();
        int w = (int) size.getWidth();
        int h = (int) size.getHeight();
        f.setTitle("Adantino");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);

        f.setVisible(true);



        //chooseGameScreen selectionScreen = new chooseGameScreen(w,h);
        //f.add(selectionScreen, BorderLayout.CENTER);

        //chooseGameScreen(w,h,f);
    }
/*
    private void chooseGameScreen(int w, int h, JPanel f){
        int boxWidth = 200;
        int boxHeight = 100;
        int boxOnePlayerX = w/2-boxWidth/2;
        int boxOnePlayerY = h/2-boxHeight/2;
        onePlayerBox = new menuBox(boxWidth, boxHeight, boxOnePlayerX, boxOnePlayerY, "One Player");
        twoPlayerBox = new menuBox(boxWidth, boxHeight, boxOnePlayerX, boxOnePlayerY+2*boxHeight, "Two Players");
        //paintComponent(g);
        f.add(onePlayerBox);

    }*/

    /** Draws the triangle as this frame's painting */
/*    public void paint(Graphics g){
        //super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        RenderingHints rh
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHints(rh);

        onePlayerBox.paintBox(g2d);
        twoPlayerBox.paintBox(g2d);
    }

    private void chooseGameScreen(){

    }

    private void twoPlayerUI() {
        //add(new Board());


    }*/

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            guiController ex = null;

            public void run() {
                ex = new guiController();
            }
        });
    }
}