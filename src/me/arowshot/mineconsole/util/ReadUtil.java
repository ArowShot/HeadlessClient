package me.arowshot.mineconsole.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ReadUtil {
    
    public static List<Byte> getVarInt(int input) {
        List<Byte> bytes = new ArrayList<Byte>();
        while ((input & -128) != 0) {
            bytes.add((byte) (input & 127 | 128));
            input >>>= 7;
        }
        bytes.add((byte) input);
        return bytes;
    }
    
    public static List<Byte> getShort(short input) {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.add((byte)(input & 0xff));
        bytes.add((byte)((input >> 8) & 0xff));
        return bytes;
    }
    
    public static List<Byte> getString(String input) {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.addAll(getVarInt(input.length()));
        for(byte b:input.getBytes(StandardCharsets.UTF_8)) {
            bytes.add(b);
        }
        return bytes;
    }


    public static List<Byte> getByteArray(byte[] input) {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.addAll(getVarInt(input.length));
        for(byte b:input) {
            bytes.add(b);
        }
        return bytes;
    }

    public static byte readFromCache(List<Byte> cache) {
        byte b = cache.get(0);
        cache.remove(0);
        return b;
    }
    
    public static String readNextString(List<Byte> cache) {
        int len = readNextVarInt(cache);
        if(len>0) {
            byte[] str = new byte[len];
            for(int i = 0;i<len;i++) {
                str[i] = readFromCache(cache);
            }
            return new String(str, StandardCharsets.UTF_8);
        }
        return "";
    }
    
    public static byte[] readNextByteArray(List<Byte> cache) {
        int len = readNextVarInt(cache);
        if(len>0) {
            byte[] out = new byte[cache.size()];
            for(int i = 0;i<len;i++) {
                out[i] = readFromCache(cache);
            }
            return out;
        }
        return new byte[0];
    }
    
    public static int readNextVarInt(List<Byte> cache) {
        int o = 0;
        int p = 0;
        byte r;
        
        do {
            r = readFromCache(cache);
            o |= (r & 127) << p++ * 7;
            
            if(p > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((r & 128) == 128);
        
        return o;
    }

    public static short readNextShort(List<Byte> cache) { //Not quite sure if this will in face work.. meh
        short s = cache.get(1);
        cache.remove(1);
        s = (short) ((s << 8) | cache.get(0));
        cache.remove(0);
        return 0;
    }

    public static byte readNextByte(List<Byte> cache) {
        return readFromCache(cache);
    }
    
}
