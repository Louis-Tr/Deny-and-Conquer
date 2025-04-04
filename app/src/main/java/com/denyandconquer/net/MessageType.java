package com.denyandconquer.net;

import java.io.Serializable;

/**
 * Enum of all possible message types exchanged between client and server.
 */
public enum MessageType implements Serializable {
    // Menu actions
    PLAYER_NAME_SET_REQUEST, // Client sets player name
    PLAYER_NAME_ACCEPTED, // Server accepts player name
    PLAYER_NAME_REJECTED, // Server rejects player name

    // Lobby actions
    CREATE_ROOM_REQUEST, // Client requests to create a room
    ROOM_CREATED, // Server creates a room
    ROOM_CREATE_FAILED, // Server fails to create a room
    ROOM_LIST_UPDATE, // Server updates room list
    JOIN_ROOM, // Client requests to join a room
    PASSWORD_REQUIRE, // Server requires password to join room
    JOIN_ROOM_WITH_PASSWORD, // Client sends password to join room
    ROOM_JOIN_SUCCESS, // Server accepts room join
    ROOM_JOIN_FAILED, // Server rejects room join
    LEAVE_ROOM, // Client leaves room
    PLAYER_SERVER_LIST_UPDATE, // Server updates player list in server
    PLAYER_ROOM_LIST_UPDATE, // Server updates player list in room

    // Game actions
    START_GAME,
    MOUSE_ACTION,
    CLICK_ON_TILE,
    DRAW_ON_TILE,
    RELEASE_TILE,
    TILE_UPDATE,
    SCORE_UPDATE,
    GAME_OVER,

    // Connection
    DISCONNECT,
    JOIN_SERVER, ERROR
}
