package me.arowshot.mineconsole.protocol.protocol47.packets;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.RawPacket;

public enum Packets { //There's a more efficient way of doing this but I'll add that later
    //Clientbound
    C_00_KEEP_ALIVE(C00KeepAlive.class, Direction.CLIENTBOUND, 0x00),
    C_02_CHAT_MESSAGE(C02ChatMessage.class, Direction.CLIENTBOUND, 0x02),
    C_40_DISCONNECT(C40Disconnect.class, Direction.CLIENTBOUND, 0x40),
    C_46_SET_COMPRESSION(C46SetCompression.class, Direction.CLIENTBOUND, 0x46),
    
    //Serverbound
    S_00_KEEP_ALIVE(S00KeepAlive.class, Direction.SERVERBOUND, 0x00),
    S_01_CHAT_MESSAGE(S01ChatMessage.class, Direction.SERVERBOUND, 0x01);
    
    private Class<? extends Packet> clazz;
    private Direction direction;
    private int id;
    
    Packets(Class<? extends Packet> clazz, Direction direction, int id) {
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
        for(Packets lp:Packets.values()) {
            if(lp.getDirection()==Direction.SERVERBOUND)
                return null;
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
