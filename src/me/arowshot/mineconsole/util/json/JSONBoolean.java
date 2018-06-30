package me.arowshot.mineconsole.util.json;

public class JSONBoolean extends JSONValue {
    private boolean data;
    
    public JSONBoolean(boolean data) {
        this.data = data;
    }
    
    public boolean getData() {
        return this.data;
    }
    
    public void setData(boolean data) {
        this.data = data;
    }
}
