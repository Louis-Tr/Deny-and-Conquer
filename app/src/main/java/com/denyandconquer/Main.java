package com.denyandconquer;

import javafx.application.Application;

import com.denyandconquer.screens.GameUI;
import com.denyandconquer.screens.Launcher;

public class Main {
    public static void main(String[] args) {

        Application.launch(Launcher.class, args);
        // Application.launch(GameUI.class, args);
    }
}
