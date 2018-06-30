package me.arowshot.mineconsole.protocol.protocol47.packets;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class C46SetCompression extends Packet {
    int compression;
    
    public C46SetCompression(int compression) {
        super(0x46);
        this.compression = compression;
    }
    
    public C46SetCompression() {
        super(0x46);
    }
    
    @Override
    public List<Byte> getBytes() {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.addAll(ReadUtil.getVarInt(compression));
        return bytes;
    }

    @Override
    public void readFromCache(List<Byte> cache) {
        this.compression = ReadUtil.readNextVarInt(cache);
    }

    public int getCompression() {
        return this.compression;
    }
}
