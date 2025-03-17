package com.denyandconquer.controllers;

import java.util.List;

import com.denyandconquer.common.Board;
import com.denyandconquer.common.Player;

public class GameController {
    private Board board;
    private List<Player> players;

    public GameController(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
    }
    
}
