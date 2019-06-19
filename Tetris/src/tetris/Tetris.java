/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
/**HOW TO PLAY
Cursor to control movement
press d to fast drop
press space to instant drop
press p to pause*/
public class Tetris extends JFrame {

    private JLabel statusbar;

    public Tetris() {

        initUI();
    }

    private void initUI() {
//the score is displayed in the statusbar
        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);
//links the board and starts the tetris game
        Board board = new Board(this);
        add(board);
        board.start();
//title
        setTitle("Tetris");
        setSize(200, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public JLabel getStatusBar() {

        return statusbar;
    }

    public static void main(String[] args) {
//post event at the end of Swing event list and process after all previous GUI events are processed
        EventQueue.invokeLater(() -> {

            Tetris game = new Tetris();
            game.setVisible(true);
        });
    }
}
