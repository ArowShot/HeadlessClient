package me.arowshot.mineconsole.util.json;

import java.util.ArrayList;

public class JSONArray extends JSONValue {
    private ArrayList<JSONValue> data;
    
    public JSONArray() {
        this.data = new ArrayList<JSONValue>();
    }
    
    public ArrayList<JSONValue> getData() {
        return this.data;
    }
    
    public void addItem(JSONValue item) {
        this.data.add(item);
    }
}
