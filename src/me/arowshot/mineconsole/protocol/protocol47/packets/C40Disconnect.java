package me.arowshot.mineconsole.protocol.protocol47.packets;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class C40Disconnect extends Packet {
    String reason;
    
    public C40Disconnect(String reason) {
        super(0x40);
        this.reason = reason;
    }
    
    public C40Disconnect() {
        super(0x40);
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
