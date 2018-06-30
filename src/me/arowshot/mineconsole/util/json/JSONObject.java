package me.arowshot.mineconsole.util.json;

import java.util.HashMap;

public class JSONObject extends JSONValue{
    private HashMap<String, JSONValue> data;
    
    public JSONObject() {
        this.data = new HashMap<String, JSONValue>();
    }
    
    public HashMap<String, JSONValue> getData() {
        return this.data;
    }
    
    public void addValue(String key, JSONValue data) {
        this.data.put(key, data);
    }
}
