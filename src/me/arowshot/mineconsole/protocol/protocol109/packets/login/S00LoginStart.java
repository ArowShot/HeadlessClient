package me.arowshot.mineconsole.protocol.protocol109.packets.login;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class S00LoginStart extends Packet {
    String username;
    
    public S00LoginStart(String username) {
        super(0x00);
        this.username = username;
    }
    
    public S00LoginStart() {
        super(0x00);
    }
    
    @Override
    public List<Byte> getBytes() {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.addAll(ReadUtil.getString(username));
        return bytes;
    }

    @Override
    public void readFromCache(List<Byte> cache) {
        this.username = ReadUtil.readNextString(cache);
    }
}
