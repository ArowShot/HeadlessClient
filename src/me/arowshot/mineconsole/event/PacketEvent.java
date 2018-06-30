package me.arowshot.mineconsole.event;

import me.arowshot.mineconsole.util.RawPacket;

public class PacketEvent {
    private boolean wasHandled = false;
    private RawPacket packet;
    
    public PacketEvent(RawPacket packet) {
        this.packet = packet;
    }
    
    public void setHandled(boolean bool) {
        this.wasHandled = bool;
    }
    
    public boolean wasHandled() {
        return this.wasHandled;
    }
    
    public RawPacket getPacket() {
        return packet;
    }
}
