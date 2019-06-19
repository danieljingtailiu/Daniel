/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.Timer;

import java.util.TimerTask;
import tetris.Shape.Tetrominoe;
/**HOW TO PLAY
Cursor to control movement
press d to fast drop
press space to instant drop
press p to pause*/
public class Board extends JPanel {
//four constants for the game board
    private final int BOARD_WIDTH = 9;
    private final int BOARD_HEIGHT = 20;
//initial delay of the game    
    private final int INITIAL_DELAY = 200;
//speed of the game    
    private final int PERIOD_INTERVAL = 300;

    private Timer timer;
    //determines if the tetris shape has finished falling so next shape start
    private boolean isFallingFinished = false;
    //check if game started
    private boolean isStarted = false;
    //check if pasued
    private boolean isPaused = false;
    //number of lines that are removed
    private int numLinesRemoved = 0;
    //position of the falling tetris shape
    private int curX = 0;
    private int curY = 0;
    private JLabel statusbar;
    private Shape curPiece;
    private Tetrominoe[] board;

    public Board(Tetris parent) {

        initBoard(parent);
    }


    private void initBoard(Tetris parent) {

        setFocusable(true);
        //timer for the game
        timer = new Timer();
        //specified speed of the game after a delay
        timer.scheduleAtFixedRate(new ScheduleTask(),
                INITIAL_DELAY, PERIOD_INTERVAL);

        curPiece = new Shape();

        statusbar = parent.getStatusBar();
        board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];
        addKeyListener(new TAdapter());
        clearBoard();
    }

    private int squareWidth() {
    //width of a tetrominoe square
        return (int) getSize().getWidth() / BOARD_WIDTH;
    }

    private int squareHeight() {
    //hieght of a tetrominoe square   
        return (int) getSize().getHeight() / BOARD_HEIGHT;
    }

    private Tetrominoe shapeAt(int x, int y) {
    //determine the tetrominoe shape at a given coordinate and stored them in the board arry   
        return board[(y * BOARD_WIDTH) + x];
    }

    public void start() {
    //when the game is started, clear the board a give new piece
        isStarted = true;
        clearBoard();
        newPiece();
    }

    private void pause() {
//paused the game and display a paused message in the statusbar
        if (!isStarted) {
            return;
        }

        isPaused = !isPaused;

        if (isPaused) {

            statusbar.setText("paused");
        } else {

            statusbar.setText(String.valueOf(numLinesRemoved));
        }
    }

    private void doDrawing(Graphics g) {
//draw out all objects on board
        Dimension size = getSize();
//paint all the shapes that have been dropped to the bottom   
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i < BOARD_HEIGHT; ++i) {

            for (int j = 0; j < BOARD_WIDTH; ++j) {
// access all the squares that were stored in the board array
                Tetrominoe shape = shapeAt(j, BOARD_HEIGHT - i - 1);

                if (shape != Tetrominoe.NoShape) {
                    
                    drawSquare(g, 0 + j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
                }
            }
        }
//paint the falling piece
        if (curPiece.getShape() != Tetrominoe.NoShape) {

            for (int i = 0; i < 4; ++i) {

                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(),
                        boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

    private void dropDown() {
//instant drop to the bottom
//drop the piece one line down unitil it reaches the bottom or the top of another fallen tetris piece
        int newY = curY;

        while (newY > 0) {

            if (!tryMove(curPiece, curX, newY - 1)) {
                
                break;
            }
            
            --newY;
        }

        pieceDropped();
    }

    private void oneLineDown() {
//moving the falling piece on line down until it reaches bottom
        if (!tryMove(curPiece, curX, curY - 1)) {
            
            pieceDropped();
        }
    }

    private void clearBoard() {
    //fills board with NoShape which clears the board
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; ++i) {
            board[i] = Tetrominoe.NoShape;
        }
    }

    private void pieceDropped() {
//puts falling piece into the board array
        for (int i = 0; i < 4; ++i) {

            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }
//check if any lines can be removed
        removeFullLines();
//creates a new piece when a piece finished falling
        if (!isFallingFinished) {
            newPiece();
        }
    }

    private void newPiece() {
//creates a new tetris piece that is random shape
        curPiece.setRandomShape();
//compute the initial curX and curY values       
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();
//if a piece reaches the top and cannot drop any more piece
        if (!tryMove(curPiece, curX, curY)) {
//cancel timer and display game over
            curPiece.setShape(Tetrominoe.NoShape);
            timer.cancel();
            isStarted = false;
            statusbar.setText("Game over");
        }
    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {
//moves the tetris pieces
        for (int i = 0; i < 4; ++i) {

            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
//if a piece reaches the edge it stops
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
                return false;
            }
//if a piece is adjacent to any fallen tetris pieces it stops
            if (shapeAt(x, y) != Tetrominoe.NoShape) {
                return false;
            }
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;

        repaint();

        return true;
    }

    private void removeFullLines() {

        int numFullLines = 0;
//check if any full lines remains
        for (int i = BOARD_HEIGHT - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BOARD_WIDTH; ++j) {
  
                if (shapeAt(j, i) == Tetrominoe.NoShape) {
                    
                    lineIsFull = false;
                    break;
                }
            }
//move all the lines above the full row one line down
            if (lineIsFull) {
                
                ++numFullLines;
                
                for (int k = i; k < BOARD_HEIGHT - 1; ++k) {
                    for (int j = 0; j < BOARD_WIDTH; ++j) {
                        
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }
//remove full lines
        if (numFullLines > 0) {

            numLinesRemoved += numFullLines;
            statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoe.NoShape);
            repaint();
        }
    }

    private void drawSquare(Graphics g, int x, int y, 
            Tetrominoe shape) {
//draw each square for every tetris(4 square in each)
        Color colors[] = {
            new Color(0, 0, 0), new Color(204, 102, 102),
            new Color(102, 204, 102), new Color(102, 102, 204),
            new Color(204, 204, 102), new Color(204, 102, 204),
            new Color(102, 204, 204), new Color(218, 170, 0)
        };
//left and top sides are brighter, bottom and right sides to look 3d
        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);

    }

    private void doGameCycle() {
//divide game to game cycles, each cycle the game is updated then redraw pieces
        update();
        repaint();
    }

    private void update() {
       
        if (isPaused) {
            return;
        }
//falling piece goes one line down or a new piece is created if the previous one finished falling
        if (isFallingFinished) {

            isFallingFinished = false;
            newPiece();
        } else {

            oneLineDown();
        }
    }
//chech key events for cursor keys
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            
            System.out.println("key pressed");

            if (!isStarted || curPiece.getShape() == Tetrominoe.NoShape) {
                return;
            }
//get the key code
            int keycode = e.getKeyCode();

            if (keycode == KeyEvent.VK_P) {
                pause();
                return;
            }
//press p to pause
            if (isPaused) {
                return;
            }

            switch (keycode) {
//press the left cursor for left
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, curX - 1, curY);
                    break;
//right
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY);
                    break;
//down
                case KeyEvent.VK_DOWN:
                    tryMove(curPiece.rotateRight(), curX, curY);
                    break;
//up
                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotateLeft(), curX, curY);
                    break;
//space for instant drop
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
//move falling piece one line down
                case KeyEvent.VK_D:
                    oneLineDown();
                    break;
            }
        }
    }

    private class ScheduleTask extends TimerTask {

        @Override
        public void run() {

            doGameCycle();
        }
    }
}
