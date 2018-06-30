package me.arowshot.mineconsole.protocol.protocol47;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;

import me.arowshot.mineconsole.event.EventManager;
import me.arowshot.mineconsole.event.PacketEvent;
import me.arowshot.mineconsole.protocol.protocol47.packets.C00KeepAlive;
import me.arowshot.mineconsole.protocol.protocol47.packets.C02ChatMessage;
import me.arowshot.mineconsole.protocol.protocol47.packets.C40Disconnect;
import me.arowshot.mineconsole.protocol.protocol47.packets.C46SetCompression;
import me.arowshot.mineconsole.protocol.protocol47.packets.Packets;
import me.arowshot.mineconsole.protocol.protocol47.packets.S00KeepAlive;
import me.arowshot.mineconsole.protocol.protocol47.packets.login.C00LoginRejected;
import me.arowshot.mineconsole.protocol.protocol47.packets.login.C01EncryptionRequest;
import me.arowshot.mineconsole.protocol.protocol47.packets.login.C02LoginSuccess;
import me.arowshot.mineconsole.protocol.protocol47.packets.login.C03SetCompression;
import me.arowshot.mineconsole.protocol.protocol47.packets.login.LoginPackets;
import me.arowshot.mineconsole.protocol.protocol47.packets.login.S00Handshake;
import me.arowshot.mineconsole.protocol.protocol47.packets.login.S00LoginStart;
import me.arowshot.mineconsole.util.AuthUtil;
import me.arowshot.mineconsole.util.CompressionUtil;
import me.arowshot.mineconsole.util.EncryptionUtil;
import me.arowshot.mineconsole.util.Packet;
import me.arowshot.mineconsole.util.RawPacket;
import me.arowshot.mineconsole.util.ReadUtil;
import me.arowshot.mineconsole.util.Session;
import me.arowshot.mineconsole.util.json.JSONArray;
import me.arowshot.mineconsole.util.json.JSONObject;
import me.arowshot.mineconsole.util.json.JSONReader;
import me.arowshot.mineconsole.util.json.JSONString;
import me.arowshot.mineconsole.util.json.JSONValue;


public class Protocol47 {
    Socket socket = null;
    
    private DataOutputStream stdOut;
    private DataInputStream stdIn;
    
    private CipherOutputStream cipherout;
    private CipherInputStream cipherin;
    
    private int compression = 0;
    private CompressionUtil compressionUtil;
    
    private Thread readingThread;
    
    private Session session;
    private String ip;
    private short port;
    
    public boolean isConnected = false;
    
    public EventManager eventManager;
    
    public Protocol47() {
        this.eventManager = new EventManager();
    }
    
    public boolean connect(String ip, short port) {
        System.out.println("Connecting...");
        try {
            socket = new Socket(ip, port);
            this.ip = ip;
            this.port = port;
        } catch (UnknownHostException e) {
            System.err.println("Unable to resolve host.");
            //e.printStackTrace();
            return false;
        } catch (IOException e) {
            System.err.println("IO exception while connecting.");
            //e.printStackTrace();
            return false;
        }
        DataOutputStream stdOut;
        DataInputStream stdIn;
        try {
            stdOut = new DataOutputStream(socket.getOutputStream());
            stdIn = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("IO exception while connecting.");
            //e.printStackTrace();
            return false;
        }
        this.stdOut = stdOut;
        this.stdIn = stdIn;
        return true;
    }
    
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error closing socket");
        }
    }
    
    public String getServerHash(String serverId, byte[] publicKey, byte[] secretKey) {
        String result = "";
        result = (new BigInteger(EncryptionUtil.digestOperation("SHA-1", new byte[][] {serverId.getBytes(StandardCharsets.ISO_8859_1),  secretKey, publicKey}))).toString(16);
        return result;
    }
    
    public void setEncryptionStreams(CipherInputStream in, CipherOutputStream out) {
        this.cipherin = in;
        this.cipherout = out;
    }
    
    public boolean doLogin(Session session, String ip, Short port) {
        this.session = session;
        if(!connect(ip, port)) {
            return false;
        }
        System.out.println("Logging in...");
        
        Packet handshake = new S00Handshake(47, this.ip, this.port, 2);
        sendPacket(handshake);
        
        Packet login = new S00LoginStart(this.session.getUsername());
        sendPacket(login);
        
        while(true) {
            RawPacket p;
            try {
                p = readNextPacket();
            } catch (Exception e) {
                //e.printStackTrace();
                System.err.println("Error reading packet");
                return false;
            }
            PacketEvent pEvent = new PacketEvent(p);
            this.eventManager.dispatchEvent(pEvent);
            if(pEvent.wasHandled())
                continue;
            Packet packet = LoginPackets.getFromRaw(p);
            //System.out.println(packet);
            switch(packet.getId()) {
                case 0x00: //Login rejected
                    C00LoginRejected rejectPacket = (C00LoginRejected) packet;
                    System.err.println("Login rejected: "+rejectPacket.getReason());
                    return false;
                case 0x01: //Encryption Request
                    C01EncryptionRequest encryptPacket = (C01EncryptionRequest) packet;
                    String serverId = encryptPacket.getServerId();
                    PublicKey publicKey = encryptPacket.getPublicKey();
                    byte[] token = encryptPacket.getToken();
                    
                    SecretKey secretKey = EncryptionUtil.createNewSharedKey();
                    
                    //server join request
                    if(!serverId.equals("-")) {
                        String result = AuthUtil.joinServer(this.session.getToken(), this.session.getUUID(), getServerHash(serverId, publicKey.getEncoded(), secretKey.getEncoded()));
                        if(result!="OK") {
                            System.err.println("Failed login ("+result+")");
                            return false;
                        }
                    }
                    
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
                    sendPacket(new RawPacket(0x01, packett));
                    this.setEncryptionStreams(new CipherInputStream(this.stdIn, EncryptionUtil.createCipher(2, secretKey)), new CipherOutputStream(this.stdOut, EncryptionUtil.createCipher(1, secretKey)));
                    //System.out.println("Sent encryption response");
                    break;
                case 0x02: //Login success
                    C02LoginSuccess successPacket = (C02LoginSuccess) packet;
                    System.out.println("Logged in to server "+ip+" as "+successPacket.getUsername()+"("+successPacket.getUUID()+")");
                    try {
                        startLoop();
                    } catch (Exception e) { }
                    return true;
                case 0x03: //Compression info
                    C03SetCompression compressionPacket = (C03SetCompression) packet;
                    this.compression = compressionPacket.getCompression();
                    //System.out.println("Set compression threshold to: " + this.compression);
                    break;
            }
        }
    }
    
    public void startLoop() {
        readingThread = new Thread() {
            public void run() {
                isConnected = true;
                while(true) {
                    RawPacket p;
                    try {
                        p = readNextPacket();
                        handlePacket(p, 1);
                    } catch (Exception e) {
                        isConnected = false;
                        System.err.println("Error while reading packets form stream. Aborting connection");
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        readingThread.start();
    }

    public byte readByte() throws IOException {
        int b;
        if(cipherin!=null) {
            b = cipherin.read();
        } else {
            b = stdIn.read();
        }
        if(b==-1) {
            throw new IOException("End of stream");
        }
        return (byte) b;
    }
    
    /*public void sendChat(String message) throws Exception {
        List<Byte> chat = new ArrayList<Byte>();
        chat.addAll(getString(message));
        
        sendPacket(new RawPacket(0x01, chat));
    }*/

    public void sendByte(byte b) throws IOException {
        if(cipherout!=null) {
            cipherout.write(b);
        } else {
            stdOut.write(b);
        }
    }
    
    public boolean sendPacket(Packet packet) {
        return sendPacket(new RawPacket(packet.getId(), packet.getBytes()));
    }
    
    public boolean sendPacket(RawPacket packet) {
        packet.getRawBytes().addAll(0, ReadUtil.getVarInt(packet.getId()));
        if(compression > 0) {
            if(packet.getRawBytes().size() > compression) {
                if(this.compressionUtil==null)
                    this.compressionUtil = new CompressionUtil(compression);
                
                this.compressionUtil.setThreshold(compression);
                
                List<Byte> out = new ArrayList<Byte>();
                try {
                    this.compressionUtil.encode(packet.getRawBytes(), out);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                
                packet.getRawBytes().clear();
                packet.getRawBytes().addAll(out);
                packet.getRawBytes().addAll(0, ReadUtil.getVarInt(packet.getRawBytes().size()));
            } else {
                packet.getRawBytes().addAll(0, ReadUtil.getVarInt(0));
            }
        }
        packet.getRawBytes().addAll(0, ReadUtil.getVarInt(packet.getRawBytes().size()));
        try {
            for(byte b:packet.getRawBytes()) {
                sendByte(b);
                stdOut.flush();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
        if(compression > 0) {
            int sizeUncomp = ReadUtil.readNextVarInt(packetData);
            if(sizeUncomp!=0) {
                if(this.compressionUtil==null)
                    this.compressionUtil = new CompressionUtil(compression);
                
                this.compressionUtil.setThreshold(compression);
                
                List<Byte> out = new ArrayList<Byte>();
                this.compressionUtil.decode(packetData, sizeUncomp, out);
                
                return new RawPacket(ReadUtil.readNextVarInt(out), out);
            }
        }
        return new RawPacket(ReadUtil.readNextVarInt(packetData), packetData);
    }
    
    public String chatToString(JSONObject text) {
        String outtext = "";
        if(text.getData().containsKey("text")) {
            JSONString text1 = (JSONString) text.getData().get("text");
            outtext += text1.getData();
        }
        
        if(text.getData().containsKey("extra")) {
            JSONArray extratext = (JSONArray) text.getData().get("extra");
            for(JSONValue o:extratext.getData()) {
                if(o instanceof JSONObject) {
                    JSONObject oo = (JSONObject) o;
                    if(oo.getData().containsKey("text")) {
                        JSONString text2 = (JSONString) oo.getData().get("text");
                        outtext += text2.getData();
                    }
                }
                if(o instanceof JSONString) {
                    JSONString os = (JSONString) o;
                    outtext += os.getData();
                }
            }
            //outtext += text1;
        }
        
        return outtext;
    }
    
    public void handlePacket(RawPacket p, int state) throws Exception {
        PacketEvent pEvent = new PacketEvent(p);
        this.eventManager.dispatchEvent(pEvent);
        if(pEvent.wasHandled())
            return;
        Packet packet = Packets.getFromRaw(p);
        if(packet==null)
            return;
        switch(packet.getId()) {
            case 0x00: //Keep-alive
                C00KeepAlive kaPacket = (C00KeepAlive) packet;
                sendPacket(new S00KeepAlive(kaPacket.getKAId()));
                break;
            case 0x02: //Chat
                C02ChatMessage chatPacket = (C02ChatMessage) packet;
                System.out.println("Chat: " + chatToString(JSONReader.decode(chatPacket.getMessage())));
                break;
            case 0x40: //Disconnect
                C40Disconnect kickPacket = (C40Disconnect) packet;
                //System.out.println(kickPacket.getReason());
                System.out.println("Kicked: " + kickPacket.getReason());
                disconnect();
                break;
            case 0x46: //Compression info
                C46SetCompression compressionPacket = (C46SetCompression) packet;
                this.compression = compressionPacket.getCompression();
                //System.out.println("Set compression threshold to: " + this.compression);
                break;
            default:
                //System.out.println(p.getId());
            }
        }
}
