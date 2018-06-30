package me.arowshot.mineconsole.protocol.protocol109.packets;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class S0FChatMessage extends Packet {
    String message;
    
    public S0FChatMessage(String message) {
        super(0x02);
        this.message = message;
    }
    
    public S0FChatMessage() {
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
}
