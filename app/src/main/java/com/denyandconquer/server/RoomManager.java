package com.denyandconquer.server;

import javax.management.AttributeList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoomManager {
    private List<Room> roomList;
    private int RoomID;

    public RoomManager() {
        this.roomList = Collections.synchronizedList(new ArrayList<>());
        this.RoomID = 1;
    }

    public synchronized Room CreateRoom(String roomName, int maxPlayers) {
        Room room = new Room(roomName, RoomID++, maxPlayers);
        roomList.add(room);
        return room;
    }
    public synchronized Room getRoomByID(int roomID) {
        for (Room room: roomList){
            if(room.getRoomId() == roomID)
                return room;
        }
        return null;
    }
    public synchronized void removeRoom(int roomID){
        roomList.removeIf(room -> room.getRoomId() == roomID);
    }
    public synchronized List<Room> getRoomList() {
        return new ArrayList<>(roomList);
    }
}
