package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class guiController implements ActionListener{
    public static boolean RIGHT_TO_LEFT = false;

    private static JButton AIPlayerButton;
    private static JButton onePlayerButton;
    private static JButton twoPlayerButton;
    private static JButton undoMoveButton;
    private static Game board;

    private Container pane;


    public void addComponentsToPane() {

        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }

        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(
                    java.awt.ComponentOrientation.RIGHT_TO_LEFT);
        }

        JPanel pageStart = new JPanel();

        AIPlayerButton = new JButton("AI vs AI");
        pageStart.add(AIPlayerButton);
        AIPlayerButton.addActionListener(this);

        onePlayerButton = new JButton("Player vs AI");
        pageStart.add(onePlayerButton);
        onePlayerButton.addActionListener(this);

        twoPlayerButton = new JButton("Two Players");
        pageStart.add(twoPlayerButton);
        twoPlayerButton.addActionListener(this);

        undoMoveButton = new JButton("Undo Move");
        pageStart.add(undoMoveButton);
        undoMoveButton.addActionListener(this);

        pane.add(pageStart, BorderLayout.PAGE_START);

        pane.add(board, BorderLayout.CENTER);
    }

    private void resetBoard(int gameMode){
        BorderLayout layout = (BorderLayout)pane.getLayout();
        pane.remove(layout.getLayoutComponent(BorderLayout.CENTER));

        board = new Game(gameMode);
        pane.add(board, BorderLayout.CENTER);
        board.revalidate();
        board.repaint();
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == AIPlayerButton) {
            int confirmed = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new AI vs AI game?", "New AJ vs AI game", JOptionPane.YES_NO_OPTION);
            if (confirmed == JOptionPane.YES_OPTION) {
                System.out.println("yes option");
                resetBoard(0);
            } else {
                System.out.println("no option");
            }
        }
        if(e.getSource() == onePlayerButton) {
            int confirmed = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new Player vs AI game?", "New Player vs AI game", JOptionPane.YES_NO_OPTION);
            if (confirmed == JOptionPane.YES_OPTION) {
                String[] buttons = { "White", "Black"};
                int rc = JOptionPane.showOptionDialog(null, "Start as player White or Black?", "Confirmation",
                        JOptionPane.DEFAULT_OPTION, -1, null, buttons, buttons[1]);
                System.out.println(rc);
                if(rc == 0){
                    //Player want's to start as White
                    resetBoard(1);
                }else{
                    //Player want's to start as Black
                    resetBoard(2);
                }
            } else {
                System.out.println("no option");
            }
        }
        if(e.getSource() == twoPlayerButton) {
            int confirmed = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new two Player game?", "New two Player game", JOptionPane.YES_NO_OPTION);
            if (confirmed == JOptionPane.YES_OPTION) {
                System.out.println("yes option");
                resetBoard(3);
            } else {
                System.out.println("no option");
            }
        }
         if(e.getSource() == undoMoveButton) {
            System.out.println("Undo move");
            board.undoLastMove();
        }
    }

    public guiController(){
        //Create and set up the window.
        JFrame frame = new JFrame("Adantino by Sam Sweere");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create a new board
        board = new Game(1);

        //Set up the content pane.
        pane = frame.getContentPane();

        addComponentsToPane();

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
