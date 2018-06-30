package me.arowshot.mineconsole.protocol.protocol109.packets;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class C1ADisconnect extends Packet {
    String reason;
    
    public C1ADisconnect(String reason) {
        super(0x1A);
        this.reason = reason;
    }
    
    public C1ADisconnect() {
        super(0x1A);
    }
    
    @Override
    public List<Byte> getBytes() {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.addAll(ReadUtil.getString(reason));
        return bytes;
    }

    @Override
    public void readFromCache(List<Byte> cache) {
        this.reason = ReadUtil.readNextString(cache);
    }
    
    public String getReason() {
        return this.reason;
    }
}
