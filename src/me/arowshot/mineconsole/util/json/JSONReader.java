package me.arowshot.mineconsole.util.json;

import java.nio.CharBuffer;

public class JSONReader { // I could use like GSON or something else here but nah
    
    public static JSONObject decode(String json) {
        CharBuffer cb = CharBuffer.wrap(json);
        return readObject(cb);
    }
    
    private static JSONObject readObject(CharBuffer buf) {
        if(buf.get()!='{') throw new RuntimeException("Invalid JSON format");
        
        JSONObject obj = new JSONObject();
        
        while(buf.remaining()>0) {
            char read = buf.get();
            switch(read) {
                case '}':
                    return obj;
                case '"':
                    buf.position(buf.position()-1);
                    String key = readString(buf);
                    
                    if(buf.get()!=':') throw new RuntimeException("Invalid JSON format");
                    
                    JSONValue value = readValue(buf);
                    
                    obj.addValue(key, value);
                case ',':
                    continue;
            }
        }
        return obj;
    }
    
    private static JSONValue readValue(CharBuffer buf) {
        while(buf.remaining()>0) {
            char read = buf.get(buf.position());
            switch(read) {
                case '[':
                    return readArray(buf);
                case '{':
                    return readObject(buf);
                case '"':
                    return new JSONString(readString(buf));
                case 'F':
                case 'f':
                    if(Character.toLowerCase(buf.get())!='f') throw new RuntimeException("Invalid JSON format");
                    if(Character.toLowerCase(buf.get())!='a') throw new RuntimeException("Invalid JSON format");
                    if(Character.toLowerCase(buf.get())!='l') throw new RuntimeException("Invalid JSON format");
                    if(Character.toLowerCase(buf.get())!='s') throw new RuntimeException("Invalid JSON format");
                    if(Character.toLowerCase(buf.get())!='e') throw new RuntimeException("Invalid JSON format");
                    
                    return new JSONBoolean(false);
                case 'T':
                case 't':
                    if(Character.toLowerCase(buf.get())!='t') throw new RuntimeException("Invalid JSON format");
                    if(Character.toLowerCase(buf.get())!='r') throw new RuntimeException("Invalid JSON format");
                    if(Character.toLowerCase(buf.get())!='u') throw new RuntimeException("Invalid JSON format");
                    if(Character.toLowerCase(buf.get())!='e') throw new RuntimeException("Invalid JSON format");
                    
                    return new JSONBoolean(true);
                case 'N':
                case 'n':
                    if(Character.toLowerCase(buf.get())!='n') throw new RuntimeException("Invalid JSON format");
                    if(Character.toLowerCase(buf.get())!='u') throw new RuntimeException("Invalid JSON format");
                    if(Character.toLowerCase(buf.get())!='l') throw new RuntimeException("Invalid JSON format");
                    if(Character.toLowerCase(buf.get())!='l') throw new RuntimeException("Invalid JSON format");
                    
                    return null;
                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    return null;
                default:
                    throw new RuntimeException("Invalid JSON format");
            }
        }
        return null;
    }

    private static String readString(CharBuffer buf) {
        if(buf.get()!='"') throw new RuntimeException("Invalid JSON format");
        
        String str = "";
        loop: while(buf.remaining()>0) {
            char read = buf.get();
            switch(read) {
                case '"':
                    break loop;
                case '\\':
                    char subRead = buf.get();
                    switch(subRead) {
                        case '"':
                            str += subRead;
                        case '\\':
                            str += '\\';
                        case '/':
                            str += '/';
                        case 'b':
                            str += '\b';
                        case 'f':
                            str += '\f';
                        case 'n':
                            str += '\n';
                        case 'r':
                            str += '\r';
                        case 't':
                            str += '\t';
                        case 'u':
                            String hexStr = ""+buf.get()+buf.get()+buf.get()+buf.get();
                            str += "\\u"+hexStr; //todo: convert 4 hex digits to char
                    }
                default:
                    str += read;
            }
        }
        return str;
    }
    
    private static JSONArray readArray(CharBuffer buf) {
        if(buf.get()!='[') throw new RuntimeException("Invalid JSON format");
        
        JSONArray arr = new JSONArray();
        loop: while(buf.remaining()>0) {
            char read = buf.get();
            switch(read) {
                case ',':
                    continue;
                case ']':
                    break loop;
                default:
                    buf.position(buf.position()-1);
                    arr.addItem(readValue(buf));
            }
        }
        return arr;
    }
}
