package me.arowshot.mineconsole.util;

public class Session {
    private String username;
    private String uuid;
    private String accessToken;
    
    public Session(String username, String accessToken, String uuid) {
        this.username = username;
        this.uuid = uuid;
        this.accessToken = accessToken;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getUUID() {
        return this.uuid;
    }
    
    public String getToken() {
        return this.accessToken;
    }
}
