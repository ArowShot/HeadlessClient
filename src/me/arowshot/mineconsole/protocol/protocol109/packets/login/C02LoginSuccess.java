package me.arowshot.mineconsole.protocol.protocol109.packets.login;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class C02LoginSuccess extends Packet {
    String uuid;
    String username;
    
    public C02LoginSuccess(String uuid, String username) {
        super(0x02);
        this.uuid = uuid;
        this.username = username;
    }
    
    public C02LoginSuccess() {
        super(0x02);
    }
    
    @Override
    public List<Byte> getBytes() {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.addAll(ReadUtil.getString(uuid));
        bytes.addAll(ReadUtil.getString(username));
        return bytes;
    }

    @Override
    public void readFromCache(List<Byte> cache) {
        this.uuid = ReadUtil.readNextString(cache);
        this.username = ReadUtil.readNextString(cache);
    }

    public String getUsername() {
        return this.username;
    }
    
    public String getUUID() {
        return this.uuid;
    }
}
