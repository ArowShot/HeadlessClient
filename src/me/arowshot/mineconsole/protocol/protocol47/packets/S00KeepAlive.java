package me.arowshot.mineconsole.protocol.protocol47.packets;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class S00KeepAlive extends Packet {
    int id;
    
    public S00KeepAlive(int id) {
        super(0x00);
        this.id = id;
    }
    
    public S00KeepAlive() {
        super(0x02);
    }
    
    @Override
    public List<Byte> getBytes() {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.addAll(ReadUtil.getVarInt(id));
        return bytes;
    }

    @Override
    public void readFromCache(List<Byte> cache) {
        this.id = ReadUtil.readNextVarInt(cache);
    }
}
