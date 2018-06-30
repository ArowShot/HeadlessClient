package me.arowshot.mineconsole.util;

import java.util.List;


public abstract class Packet  {
    private int packetId;
    
    public Packet(int id) {
        this.packetId = id;
    }
    
    public abstract List<Byte> getBytes();
    public abstract void readFromCache(List<Byte> cache);

    public int getId() {
        return packetId;
    }
}
