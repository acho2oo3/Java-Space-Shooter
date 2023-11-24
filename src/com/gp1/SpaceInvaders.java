package com.gp1;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class SpaceInvaders extends JFrame  {

    public SpaceInvaders() {

        initUI();
    }

    private void initUI() {
 // Use JOptionPane to get the username from the user
        String username = JOptionPane.showInputDialog(this, "Enter your username:");

        // Check if the user canceled the input or provided an empty username
        if (username == null || username.trim().isEmpty()) {
            // Handle the case where no username was provided
            System.out.println("No username provided. Exiting...");
            System.exit(0);
        }

        // Pass the username to the Board constructor
        add(new Board(username));

        setTitle("Space Invaders");
        setSize(Commons.BOARD_WIDTH, Commons.BOARD_HEIGHT);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            var ex = new SpaceInvaders();
            ex.setVisible(true);
        });
    }
}
