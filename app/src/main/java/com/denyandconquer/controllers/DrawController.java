package com.denyandconquer.controllers;

import javafx.scene.input.MouseEvent;

public class DrawController { // handle mouse listener
    private GameController gameController;

    public DrawController(GameController gameController) {
        this.gameController = gameController;
    }

    public void mousePressed(MouseEvent e) {} // if click on unoccupied square pop up square to draw.  

    public void mouseDragged(MouseEvent e) {} // if player.isDrawing color pixels on square

    public void mouseReleased(MouseEvent e) {} // If mouse released under threshold, empty square else fill square
    
}
