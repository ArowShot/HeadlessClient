package me.arowshot.mineconsole.protocol.protocol109.packets.login;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.RawPacket;


public enum LoginPackets {
    //Clientbound
    C_00_LOGIN_REJECTED(C00LoginRejected.class, Direction.CLIENTBOUND, 0x00),
    C_01_ENCRYPTION_REQUEST(C01EncryptionRequest.class, Direction.CLIENTBOUND, 0x01),
    C_02_LOGIN_SUCCESS(C02LoginSuccess.class, Direction.CLIENTBOUND, 0x02),
    C_03_SET_COMPRESSION(C03SetCompression.class, Direction.CLIENTBOUND, 0x03);
    
    private Class<? extends Packet> clazz;
    private Direction direction;
    private int id;
    
    LoginPackets(Class<? extends Packet> clazz, Direction direction, int id) {
        this.clazz = clazz;
        this.direction = direction;
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
    
    public Class<? extends Packet> getClazz() {
        return this.clazz;
    }

    public Direction getDirection() {
        return this.direction;
    }
    
    public static Packet getFromRaw(RawPacket p) {
        for(LoginPackets lp:LoginPackets.values()) {
            if(lp.getDirection()==Direction.SERVERBOUND)
                return null; //Once it comes to a serverbound packet, all future packets will be serverbound too
            if(lp.getId()==p.getId()) {
                try {
                    Packet out = lp.getClazz().newInstance();
                    out.readFromCache(p.getRawBytes());
                    return out;
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }
    
    enum Direction {
        CLIENTBOUND,
        SERVERBOUND;
    }
}
