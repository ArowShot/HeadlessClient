package me.arowshot.mineconsole;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import me.arowshot.mineconsole.event.PacketEvent;
import me.arowshot.mineconsole.event.PacketListener;
import me.arowshot.mineconsole.protocol.protocol109.Protocol109;
import me.arowshot.mineconsole.protocol.protocol109.packets.login.C01EncryptionRequest;
import me.arowshot.mineconsole.protocol.protocol47.packets.Packets;
import me.arowshot.mineconsole.util.EncryptionUtil;
import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.RawPacket;
import me.arowshot.mineconsole.util.ReadUtil;
import me.arowshot.mineconsole.util.Session;

public class ServerMain implements PacketListener {
    static DataOutputStream stdOut;
    static DataInputStream stdIn;
    static int compression = 0;
    static String username = "";
    
    public ServerMain(Protocol109 pro) throws Exception {
        pro.eventManager.addListener(this);
        ServerSocket serverSocket = new ServerSocket(25564);
        Socket clientSocket = serverSocket.accept();
        stdOut = new DataOutputStream(clientSocket.getOutputStream());
        stdIn = new DataInputStream(clientSocket.getInputStream());
        

        RawPacket handshakePacket = readNextPacket();
        
        RawPacket loginPacket = readNextPacket();
        username = ReadUtil.readNextString(loginPacket.getRawBytes());

        Session session = new Session(username, "ca7ae02567904878b506de41f22a3744", "90cda1dd-a3da-4415-b474-b33bdd3dd7ad");
        pro.doLogin(session, "localhost", (short) 25565);
        System.out.println(username);
        
        while(true) {
            readNextPacket();
        }
        
    }
    
    public static SecretKey getPrivateKey(byte[] key, Key pubKey) {
        SecretKeySpec sks = new SecretKeySpec(EncryptionUtil.cipherOperation(2, pubKey, key), "AES");
        return sks;
    }
    
    static byte[] trim(byte[] bytes)
    {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0)
        {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }
    
    @Override
    public void onRecievePacket(PacketEvent event) {
        if(event.getPacket().getId()==0x01) {
            event.setHandled(true);
            C01EncryptionRequest encryptPacket = (C01EncryptionRequest) Packets.getFromRaw(event.getPacket());
            
            PublicKey publicKey = encryptPacket.getPublicKey();
            
            byte[] token = encryptPacket.getToken();
            System.out.println("Veryfy token:" + Arrays.toString(token));
            
            List<Byte> packet = new ArrayList<Byte>();
            packet.addAll(encryptPacket.getBytes());
            packet.addAll(0, ReadUtil.getVarInt(encryptPacket.getId()));
            packet.addAll(0, ReadUtil.getVarInt(packet.size()));
            //System.out.println(Arrays.toString(packet.toArray()));
            for(byte b:packet) {
                try {
                    stdOut.writeByte(b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //System.out.println(Arrays.toString(packet.toArray()));
            RawPacket encryptionResponse = null;
            try {
                encryptionResponse = readNextPacket();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            //System.out.println(Arrays.toString(encryptionResponse.getRawBytes().toArray()));
            
            byte[] privKey = ReadUtil.readNextByteArray(encryptionResponse.getRawBytes());
            System.out.println("Veryfy token:" + Arrays.toString(encryptionResponse.getRawBytes().toArray()));
            //privKey = trim(privKey);
            System.out.println(Arrays.toString(privKey));
            SecretKey secretKey = getPrivateKey(privKey, publicKey);
            
            byte[] secretEncrypted = EncryptionUtil.cipherOperation(1, publicKey, secretKey.getEncoded());
            byte[] tokenEncrypted = EncryptionUtil.cipherOperation(1, publicKey, token);
            
            List<Byte> packett = new ArrayList<Byte>();
            packett.addAll(ReadUtil.getVarInt(secretEncrypted.length));
            for(byte b:secretEncrypted) {
                packett.add(b);
            }
            packett.addAll(ReadUtil.getVarInt(tokenEncrypted.length));
            for(byte b:tokenEncrypted) {
                packett.add(b);
            }
            try {
                pro.sendPacket(new RawPacket(0x01, packett));
            } catch (Exception e) {e.printStackTrace();}
            //pro.cipherout = new CipherOutputStream(stdOut, EncryptionUtil.createCipher(1, secretKey));
            //pro.cipherin = new CipherInputStream(stdIn, EncryptionUtil.createCipher(2, secretKey));
        }
    }
    
    static Protocol109 pro;
    static ServerMain sm;
    public static void main(String[] args) throws Exception {
        pro = new Protocol109();
        new Thread() {
            public void run() {
                try {
                    sm = new ServerMain(pro);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    

    public byte readByte() throws IOException {
        int b;
        b = stdIn.read();
        if(b==-1) {
            throw new IOException("End of stream");
        }
        return (byte) b;
    }

    public void sendByte(byte b) throws IOException {
        stdOut.write(b);
    }
    
    public void sendPacket(Packet packet) throws Exception {
        sendPacket(new RawPacket(packet.getId(), packet.getBytes()));
    }
    
    public void sendPacket(RawPacket packet) throws Exception {
        packet.getRawBytes().addAll(0, ReadUtil.getVarInt(packet.getId()));
        packet.getRawBytes().addAll(0, ReadUtil.getVarInt(packet.getRawBytes().size()));
        for(byte b:packet.getRawBytes()) {
            sendByte(b);
        }
        stdOut.flush();
    }
    
    public int readNextVarIntRaw() throws IOException {
        int o = 0;
        int p = 0;
        byte r;
        
        do {
            r = readByte();
            o |= (r & 127) << p++ * 7;
            
            if(p > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((r & 128) == 128);
        
        return o;
    }
    
    public List<Byte> readDataRaw(int size) throws IOException {
        if(size>0) {
            List<Byte> packetData = new ArrayList<Byte>();
            for(int i=0;i<size;i++)
                packetData.add(readByte());
            return packetData;
        }
        return new ArrayList<Byte>();
    }
    
    public RawPacket readNextPacket() throws Exception {
        int size = readNextVarIntRaw();
        List<Byte> packetData = readDataRaw(size);
        return new RawPacket(ReadUtil.readNextVarInt(packetData), packetData);
    }
}
