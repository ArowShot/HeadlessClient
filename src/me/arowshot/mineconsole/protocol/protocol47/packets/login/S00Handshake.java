package me.arowshot.mineconsole.protocol.protocol47.packets.login;

import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class S00Handshake extends Packet {
    int protocolVersion;
    String ip;
    short port;
    int state;
    
    public S00Handshake(int protocolVersion, String ip, short port, int state) {
        super(0x00);
        this.protocolVersion = protocolVersion;
        this.ip = ip;
        this.port = port;
        this.state = state;
    }
    
    public S00Handshake() {
        super(0x00);
    }
    
    @Override
    public List<Byte> getBytes() {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.addAll(ReadUtil.getVarInt(protocolVersion));
        bytes.addAll(ReadUtil.getString(ip));
        bytes.addAll(ReadUtil.getShort(port));
        bytes.addAll(ReadUtil.getVarInt(state));
        return bytes;
    }

    @Override
    public void readFromCache(List<Byte> cache) {
        this.protocolVersion = ReadUtil.readNextVarInt(cache);
        this.ip = ReadUtil.readNextString(cache);
        this.port = ReadUtil.readNextShort(cache);
        this.state = ReadUtil.readNextVarInt(cache);
    }

}
