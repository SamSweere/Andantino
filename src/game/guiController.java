package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class guiController implements ActionListener{
    public static boolean RIGHT_TO_LEFT = false;

    private static JButton onePlayerButton;
    private static JButton twoPlayerButton;
    private static JButton undoMoveButton;
    private static Board board;


    private AI ai;

    public void addComponentsToPane(Container pane) {

        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }

        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(
                    java.awt.ComponentOrientation.RIGHT_TO_LEFT);
        }

        JPanel pageStart = new JPanel();

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

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == onePlayerButton) {
            int confirmed = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new Player vs AI game?", "New Player vs AI game", JOptionPane.YES_NO_OPTION);
            if (confirmed == JOptionPane.YES_OPTION) {
                System.out.println("yes option");
            } else {
                System.out.println("no option");
            }
        }
        if(e.getSource() == twoPlayerButton) {
            int confirmed = JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new two Player game?", "New two Player game", JOptionPane.YES_NO_OPTION);
            if (confirmed == JOptionPane.YES_OPTION) {
                System.out.println("yes option");
            } else {
                System.out.println("no option");
            }
        }
         if(e.getSource() == undoMoveButton) {
            System.out.println("Undo move");
        }
    }

    public guiController(){
        //Create and set up the window.
        JFrame frame = new JFrame("Adantino by Sam Sweere");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create a new board
        board = new Board();

        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
