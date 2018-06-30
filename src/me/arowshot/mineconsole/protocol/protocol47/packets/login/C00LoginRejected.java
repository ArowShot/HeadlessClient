package me.arowshot.mineconsole.protocol.protocol47.packets.login;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class C00LoginRejected extends Packet {
    String reason;
    
    public C00LoginRejected(String reason) {
        super(0x00);
        this.reason = reason;
    }
    
    public C00LoginRejected() {
        super(0x00);
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
