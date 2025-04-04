package com.denyandconquer.screens;

import com.denyandconquer.controllers.SceneController;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * MenuScene represents the main menu where users can choose to create or join a server.
 */
public class MenuScene {
    private final Scene scene;
    private final SceneController controller;

    /**
     * Constructs the MenuScene with buttons to navigate to Create or Join Server.
     *
     * @param sceneController Handles scene transitions
     */
    public MenuScene(SceneController sceneController) {
        this.controller = sceneController;

        Button createServerBtn = new Button("Create Server");
        Button joinServerBtn = new Button("Join Server");

        createServerBtn.setOnAction(e -> {
            System.out.println("Create Server clicked.");
            controller.showCreateServerScene();
        });

        joinServerBtn.setOnAction(e -> {
            System.out.println("Join Server clicked.");
            controller.showJoinServerScene();
        });

        VBox layout = new VBox(20, createServerBtn, joinServerBtn);
        layout.setAlignment(Pos.CENTER);
        this.scene = new Scene(layout, 400, 300);
    }

    /**
     * Returns the JavaFX Scene object for this menu.
     */
    public Scene getScene() {
        return scene;
    }
}
