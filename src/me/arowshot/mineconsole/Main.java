package me.arowshot.mineconsole;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import me.arowshot.mineconsole.protocol.protocol109.Protocol109;
import me.arowshot.mineconsole.protocol.protocol109.packets.S0FChatMessage;
import me.arowshot.mineconsole.util.Session;

public class Main {
    
    public static void main(String[] args) throws Exception {
        //JSONReader.decode("{\"error\":\"ForbiddenOperationException\",\"errorMessage\":\"Invalid token\",\"anotherone\":{\"fawrgrlse\":false,\"trewrgue\":true}}");
        Protocol109 pro = new Protocol109();
        Session session = new Session("ArowShot", "486abd6073e44b858e691fb5c0ca4c79", "90d9489ceda149f29ccde73ecf5cae25");
        pro.doLogin(session, "164.132.137.211", (short) 25565);
        
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            String input = br.readLine();
            if(input.startsWith("/")) {
                switch(input.split(" ")[0].substring(1)) {
                    case "connect":
                        if(pro.isConnected) {
                            pro.disconnect(); 
                        }
                        pro = new Protocol109();
                        pro.doLogin(session, "localhost", (short) 25566); 
                        //System.err.println("Not implemented");
                        break;
                    case "exit":
                        System.exit(0);
                    default: 
                        if(pro.isConnected) {
                            //pro.sendChat(input); 
                            pro.sendPacket(new S0FChatMessage(input));
                        } else {
                            System.err.println("Not connected to any server!");
                        }
                }
            } else {
                if(pro.isConnected) {
                    pro.sendPacket(new S0FChatMessage(input));
                } else {
                    System.err.println("Not connected to any server!");
                }
            }
        }
    }
}
