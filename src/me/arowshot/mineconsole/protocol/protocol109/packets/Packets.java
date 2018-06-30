package me.arowshot.mineconsole.protocol.protocol109.packets;

import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.RawPacket;

public enum Packets { //There's a more efficient way of doing this but I'll add that later
    //Clientbound
    C_1F_KEEP_ALIVE(C0BKeepAlive.class, Direction.CLIENTBOUND, 0x1F),
    C_02_CHAT_MESSAGE(C02ChatMessage.class, Direction.CLIENTBOUND, 0x0F),
    C_1A_DISCONNECT(C1ADisconnect.class, Direction.CLIENTBOUND, 0x1A),
    
    //Serverbound
    S_1F_KEEP_ALIVE(S1FKeepAlive.class, Direction.SERVERBOUND, 0x1F),
    S_0F_CHAT_MESSAGE(S0FChatMessage.class, Direction.SERVERBOUND, 0x02);
    
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
