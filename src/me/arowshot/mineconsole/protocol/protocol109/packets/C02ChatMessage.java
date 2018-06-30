package me.arowshot.mineconsole.protocol.protocol109.packets;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class C02ChatMessage extends Packet {
    String message;
    byte position;
    
    public C02ChatMessage(String message, byte position) {
        super(0x0F);
        this.message = message;
        this.position = position;
    }
    
    public C02ChatMessage() {
        super(0x0F);
    }
    
    @Override
    public List<Byte> getBytes() {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.addAll(ReadUtil.getString(message));
        bytes.add(position);
        return bytes;
    }

    @Override
    public void readFromCache(List<Byte> cache) {
        this.message = ReadUtil.readNextString(cache);
        this.position = ReadUtil.readNextByte(cache);
    }

    public String getMessage() {
        return this.message;
    }
}
