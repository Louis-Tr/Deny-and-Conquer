package com.denyandconquer.net;

import java.io.Serializable;

/**
 * Represents a message passed between client and server.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private final MessageType type;
    private final Object data;

    public Message(MessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}
