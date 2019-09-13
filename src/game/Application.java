package game;

import java.awt.EventQueue;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;

public class Application extends JFrame {
    private int screenWidth = 600;
    private int screenHeight = 600;

    public Application() throws InterruptedException {

        setSize(screenWidth, screenHeight);

        setTitle("Adantino");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);

        chooseGameScreen selectionScreen = new chooseGameScreen();
        add(selectionScreen);


        //int numOfPlayers = ;
    }

    private void twoPlayerUI() {
        add(new Board());


    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            Application ex = null;
            try {
                ex = new Application();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
    }
}