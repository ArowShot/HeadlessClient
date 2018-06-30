package me.arowshot.mineconsole.event;

import java.util.ArrayList;
import java.util.List;

public class EventManager { //todo: annotation based event system
    private List<PacketListener> listeners;
    
    public EventManager() {
        listeners = new ArrayList<PacketListener>();
    }
    
    public void addListener(PacketListener listener) {
        listeners.add(listener);
    }
    
    public void dispatchEvent(PacketEvent event) {
        for(PacketListener listener:listeners) {
            if(listener!=null) {
                listener.onRecievePacket(event);
            }
        }
    }
}
