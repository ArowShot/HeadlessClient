package me.arowshot.mineconsole.protocol.protocol109.packets;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class S1FKeepAlive extends Packet {
    int id;
    
    public S1FKeepAlive(int id) {
        super(0x1F);
        this.id = id;
    }
    
    public S1FKeepAlive() {
        super(0x1F);
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
