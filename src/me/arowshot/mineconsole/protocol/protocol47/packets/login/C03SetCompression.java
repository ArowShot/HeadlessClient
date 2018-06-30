package me.arowshot.mineconsole.protocol.protocol47.packets.login;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class C03SetCompression extends Packet {
    int compression;
    
    public C03SetCompression(int compression) {
        super(0x03);
        this.compression = compression;
    }
    
    public C03SetCompression() {
        super(0x03);
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
