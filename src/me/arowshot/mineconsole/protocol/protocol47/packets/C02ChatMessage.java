package me.arowshot.mineconsole.protocol.protocol47.packets;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class C02ChatMessage extends Packet {
    String message;
    
    public C02ChatMessage(String message) {
        super(0x02);
        this.message = message;
    }
    
    public C02ChatMessage() {
        super(0x02);
    }
    
    @Override
    public List<Byte> getBytes() {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.addAll(ReadUtil.getString(message));
        return bytes;
    }

    @Override
    public void readFromCache(List<Byte> cache) {
        this.message = ReadUtil.readNextString(cache);
    }

    public String getMessage() {
        return this.message;
    }
}
