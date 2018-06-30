package me.arowshot.mineconsole.util.json;

public class JSONString extends JSONValue {
    private String data;
    
    public JSONString(String data) {
        this.data = data;
    }
    
    public String getData() {
        return this.data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
}
