package com.denyandconquer.controllers;

import com.denyandconquer.Launcher;
import com.denyandconquer.screens.LoadingScene;
import javafx.application.Platform;

/**
 * Manages loading screen transitions.
 */
public class LoadingManager {

    private static SceneController controller;
    private static final LoadingScene loadingScene = new LoadingScene();
    private static String currentType = "generic";

    public static void init(SceneController controller) {
        LoadingManager.controller = controller;
    }

    public static void showLoading(String type) {
        if (controller == null) return;
        currentType = type;

        Platform.runLater(() -> {
            switch (type) {
                case "create" -> controller.getPrimaryStage().setScene(loadingScene.getServerCreateLoadingScene());
                case "join" -> controller.getPrimaryStage().setScene(loadingScene.getJoinServerLoadingScene());
                default -> controller.getPrimaryStage().setScene(loadingScene.getGenericLoadingScene());
            }
        });
    }

    public static void setLoading(boolean active) {
        if (!active) return; // This method is optional, can use showLoading instead
        currentType = "generic";
        showLoading(currentType);
    }

    public static void setLoading(boolean active, String type) {
        if (!active) return; // This method is optional, can use showLoading instead
        currentType = type;
        showLoading(type);
    }
}
