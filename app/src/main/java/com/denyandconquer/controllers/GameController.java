package com.denyandconquer.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.denyandconquer.common.Board;
import com.denyandconquer.common.Player;

public class GameController {
    private Board board;
    private List<Player> players;

    public GameController(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
    }

    public GameController(List<Player> players) {
        this.players = players;
    }

    public List<Player> determineWinner() {
          int maxScore = players.stream()
                              .mapToInt(Player::getScore)
                              .max()
                              .orElse(0);
        return players.stream()
                      .filter(p -> p.getScore() == maxScore)
                      .collect(Collectors.toList());

    }

    public boolean isGameComplete() {
        return board.isGameComplete();
    }

    
    
}
