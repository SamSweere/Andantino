package game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.*;

public class Game extends JPanel implements MouseListener {
    private final int radius = 25;
    private final int boardRadius = 9;
    //Game mode 0: AI vs AI, 1: Player vs AI (player is white); 2: AI vs Player (player is black); 3: Player vs player
    private final int gameMode;

    private boolean humanTurn;

    private boolean gameFinished = false;

    private AI ai;

    private Board board;

    //private Tile[][] tiles = new Tile[boardRadius*2+1][boardRadius*2+1];

    private final boardChecker boardCheck = new boardChecker();

    //The reason I save every board for the history is the instant lose positions, these got overwritten when only
    //saving the moves when undoing and keep playing
    private ArrayList<Board> boardHistory = new ArrayList<>();

    public Game(int gameMode){
        this.gameMode = gameMode;

        //Player one always starts
        int playerTurn = 1;

        this.board = new Board(boardRadius, playerTurn);

        //Game is not finished yet
        gameFinished = false;

        Color backgroundColor = new Color(249,189,59);
        setBackground(backgroundColor);

        //Add the first board to the history
        addToHistory();
        this.addMouseListener(this);

        //Start the game
        switch (gameMode){
            case 0:
                //AI vs Ai game
                aiVSaiGame();
                break;
            case 1:
                // Player vs AI, player is white
                ai = new AI(-1, boardRadius);
                break;
            case 2:
                // AI vs Player, player is black
                ai = new AI(1, boardRadius);
                playerVSaiGameAIMove();
                break;
            case 3:
                // Player vs player
                break;
        }
    }

    private void aiVSaiGame(){
        AI aiWhite = new AI(-1, boardRadius);
        //AITest aiBlack = new AITest(1, boardRadius);
        AI aiBlack = new AI(1, boardRadius);

        //Roughly 30 fps
        int timerDelay = 33;
        new Timer(timerDelay, new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                //While not won
                if(!gameFinished){
                    if(board.getPlayerTurn() == 1){
                        //aiWhite.updateBoard(board);
                        Move aiMove = aiWhite.makeMove(board);
                        gameFinished = makeMove(aiMove);
                    }else{
                        //aiBlack.updateBoard(board);
                        Move aiMove  = aiBlack.makeMove(board);
                        gameFinished = makeMove(aiMove);
                    }
                }
                repaint();
            }
        }).start();

        //aiBlack.closeFile();
        //aiWhite.closeFile();
    }

    private void playerVSaiGameAIMove(){
        //Update the ai board
        //ai.updateBoard(board);

        Move aiMove = ai.makeMove(board);

        //Make the move on the board
        gameFinished = makeMove(aiMove);
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawHexGrid(g);
    }


    public void drawHexGrid(Graphics g) {
        //super.repaint();

        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh
                = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHints(rh);

        g2d.setStroke(new BasicStroke(2));

        //Bit of a cheat but it works, first draw the border
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius){
                    Polygon hexagon = polygonTile(q,r);
                    if(board.getTileState(q,r) == 1){
                        g2d.setColor(Color.white);
                        g2d.fillPolygon(hexagon);
                        g2d.draw(hexagon);
                    }else if(board.getTileState(q,r) == -1){
                        g2d.setColor(Color.black);
                        g2d.fillPolygon(hexagon);
                        g2d.draw(hexagon);
                    }
                    else if(board.tileIsPlayable(q,r)){
                        g2d.setColor(new Color(203, 153, 48));
                        g2d.fillPolygon(hexagon);
                        g2d.draw(hexagon);
                    }
                }
            }
        }

        //Now draw the grid, this fixes the tiles being drawn over the border
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius){
                    Polygon hexagon = polygonTile(q,r);
                    g2d.setColor(Color.GRAY);
                    g2d.draw(hexagon);
                }
            }
        }

        g2d.dispose();
    }
    private Point locationToCoordinates(int q, int r){
        Point coordinates = new Point(0,0);
        //Offset for center
        r = r - boardRadius;
        q = q - boardRadius;

        coordinates.x = (int)(0.5*Math.sqrt(3)*radius*r + q*Math.sqrt(3)*radius);
        coordinates.y = (int)(0.75 * 2 * radius*r);

        //Center everything
        Dimension size = getSize();
        int w = (int) size.getWidth();
        int h = (int) size.getHeight();
        coordinates.x += w/2;
        coordinates.y += h/2;

        return coordinates;
    }

    private Polygon polygonTile(int q, int r) {
        Point coordinates = locationToCoordinates(q, r);

        Polygon polygon = new Polygon();
        double w = Math.sqrt(3) * radius;
        double h = 2 * radius;
        polygon.addPoint((int)(coordinates.x - 0.5*w),(int)(coordinates.y + 0.25*h));
        polygon.addPoint( (int)(coordinates.x),(int)(coordinates.y + 0.5*h));
        polygon.addPoint((int)(coordinates.x + 0.5*w),(int)(coordinates.y + 0.25*h));
        polygon.addPoint((int)(coordinates.x + 0.5*w),(int)(coordinates.y - 0.25*h));
        polygon.addPoint( (int)(coordinates.x),(int)(coordinates.y - 0.5*h));
        polygon.addPoint((int)(coordinates.x - 0.5*w),(int)(coordinates.y - 0.25*h));

        return polygon;
    }

    public void undoLastMove(){
        //System.out.println(moveHistory.size() );
        //This funtion goes back to the last player move, we have to jump over the ai moves
        //This function is disabled when playing AI vs AI (the game is deterministic anyways)
        if(gameMode == 0){
            return;
        }

        if(gameMode == 1 || gameMode == 2){
            int playerNum;
            if(gameMode == 1){
                //Player is white
                playerNum = 1;
            }
            else{
                //Player is black
                playerNum = -1;
            }
            //At least one move had to be made

            if (boardHistory.size() > 1) {
                //If the last move was of the player (remember, AI is at turn then)
                if(boardHistory.get(boardHistory.size()-1).getPlayerTurn() == -1*playerNum){
                    //Remove the last move
                    boardHistory.remove(boardHistory.size() - 1);

                    //Get the now last move
                    board = new Board(boardHistory.get(boardHistory.size() - 1));
                }
                else{
                    //In this case the last move was of the AI, we need to get two move back
                    //Check if two move is possible
                    if(boardHistory.size() > 2){
                        //Remove the last two moves
                        boardHistory.remove(boardHistory.size() - 1);
                        boardHistory.remove(boardHistory.size() - 1);

                        //Get the now last move
                        board = new Board(boardHistory.get(boardHistory.size() - 1));
                    }
                }

            }
        }
        else if(gameMode == 3) {
            if (boardHistory.size() > 1) {
                //Remove the last move
                boardHistory.remove(boardHistory.size() - 1);

                //Get the second to last move
                board = new Board(boardHistory.get(boardHistory.size() - 1));
            }
        }

        //repaint the board
        this.repaint();
    }

    private void addToHistory(){
        boardHistory.add(new Board(board));
    }

    private boolean makeMove(Move move){
        int winningPlayer = boardCheck.checkWin(move.q, move.r, board);

        board.setTileState(move.q, move.r,board.getPlayerTurn());

        //add the move to the history
        addToHistory();

        //Change the player turn
        //playerTurn = playerTurn*-1;

        //We did all the checks we can now draw it (this makes it more nice visually)
        this.repaint();

        //When the tile is drawn show the possible win message
        if(winningPlayer == 1){
            System.out.println("Player one (White) won!");
            JOptionPane.showMessageDialog(null, "Player 1 won!");
            //Game is finished
            return true;
        }
        else if(winningPlayer == -1){
            System.out.println("Player two (Black) won!");
            JOptionPane.showMessageDialog(null, "Player 2 won!");
            //Game is finished
            return true;
        }
        return false;
    }

    private void playerClicked(MouseEvent mouseEvent){
        for(int q = 0; q < boardRadius*2+1; q++){
            for(int r = 0; r < boardRadius*2+1; r++){
                if(r + q >= boardRadius&& r + q <= 3*boardRadius){
                    if(polygonTile(q,r).contains(mouseEvent.getX(),mouseEvent.getY())){
                        System.out.println("Clicked tile: " + q + " " + r);

                        //Check if the move is valid
                        if(boardCheck.checkValidMove(q,r, board, boardHistory.size())){
                            gameFinished = makeMove(new Move(q,r));
                            //Let a possible AI know it is his turn
                            if(gameMode == 1 || gameMode == 2){
                                if(!gameFinished){
                                    //Let the AI move
                                    playerVSaiGameAIMove();
                                }
                            }
                        }else{
                            System.out.println("Invalid move, do nothing");
                        }
                        //The return here fixes the problem when the border between two tiles is clicked
                        return;
                    }
                }
            }
        }
    }


    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        playerClicked(mouseEvent);
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}