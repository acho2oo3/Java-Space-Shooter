package com.gp1.sprite;

import javax.swing.ImageIcon;

import com.gp1.Commons;

public class Alien extends Sprite {

    private boolean dying;

    public Alien(int x, int y) {
        initAlien(x, y);
        dying = false; // Initialize dying state
    }

    private void initAlien(int x, int y) {
        this.x = x;
        this.y = y;

        var alienImg = "src/images/aliencool.png";
        var ii = new ImageIcon(alienImg);

        setImage(ii.getImage());
    }

    public void act(int direction) {
        int newX = this.x + direction;

        // Check if the new x-coordinate is within the frame boundaries
        if (newX >= 0 && newX <= Commons.BOARD_WIDTH - Commons.ALIEN_WIDTH) {
            this.x = newX;
        }
    }

    public void setDying(boolean dying) {
        this.dying = dying;
    }

    public boolean isDying() {
        return dying;
    }
    public void dead(){
        
    }

    public void die() {
        // Perform any actions needed when the alien dies
        // For example, reset the position or perform cleanup
    }
}
