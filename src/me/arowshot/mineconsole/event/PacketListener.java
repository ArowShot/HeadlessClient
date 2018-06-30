package me.arowshot.mineconsole.event;


public interface PacketListener {
    public abstract void onRecievePacket(PacketEvent event);
}
