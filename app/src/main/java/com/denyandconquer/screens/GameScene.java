package com.denyandconquer.screens;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class GameScene {
    public static Scene getGameScene() {
        StackPane layout = new StackPane();
        Label gameLabel = new Label("Welcome to the game!");
        layout.getChildren().add(gameLabel);
        return new Scene(layout, 400, 300);
    }
}
