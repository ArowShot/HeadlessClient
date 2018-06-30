package me.arowshot.mineconsole.util;
import java.util.List;


public class RawPacket {
    private int packetId;
    private List<Byte> bytes;
    
    public RawPacket(int packetId, List<Byte> bytes) {
        this.packetId = packetId;
        this.bytes = bytes;
    }
    
    public int getId() {
        return this.packetId;
    }
    
    public List<Byte> getRawBytes() {
        return this.bytes;
    }
}
