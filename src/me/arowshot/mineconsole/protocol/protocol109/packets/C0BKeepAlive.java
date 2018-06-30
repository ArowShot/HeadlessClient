package me.arowshot.mineconsole.protocol.protocol109.packets;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class C0BKeepAlive extends Packet {
    int id;
    
    public C0BKeepAlive(int id) {
        super(0x0B);
        this.id = id;
    }
    
    public C0BKeepAlive() {
        super(0x0B);
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
    
    public int getKAId() { //todo: think of a better name
        return this.id;
    }
}
