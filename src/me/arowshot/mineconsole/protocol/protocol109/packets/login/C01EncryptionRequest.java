package me.arowshot.mineconsole.protocol.protocol109.packets.login;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import me.arowshot.mineconsole.util.EncryptionUtil;
import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.ReadUtil;

public class C01EncryptionRequest extends Packet {
    String serverId;
    PublicKey publicKey;
    byte[] token;
    
    public C01EncryptionRequest(String serverId, PublicKey publicKey, byte[] token) {
        super(0x01);
        this.serverId = serverId;
        this.publicKey = publicKey;
        this.token = token;
    }
    
    public C01EncryptionRequest() {
        super(0x01);
    }
    
    @Override
    public List<Byte> getBytes() {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.addAll(ReadUtil.getString(this.serverId));
        bytes.addAll(ReadUtil.getByteArray(this.publicKey.getEncoded()));
        bytes.addAll(ReadUtil.getByteArray(this.token));
        return bytes;
    }

    @Override
    public void readFromCache(List<Byte> cache) {
        this.serverId = ReadUtil.readNextString(cache).trim();
        this.publicKey = EncryptionUtil.decodePublicKey(ReadUtil.readNextByteArray(cache));
        this.token = ReadUtil.readNextByteArray(cache);
    }

    public String getServerId() {
        return this.serverId;
    }
    
    public PublicKey getPublicKey() {
        return this.publicKey;
    }
    
    public byte[] getToken() {
        return this.token;
    }
}
