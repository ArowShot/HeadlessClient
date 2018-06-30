package me.arowshot.mineconsole.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import me.arowshot.mineconsole.util.json.JSONObject;
import me.arowshot.mineconsole.util.json.JSONReader;
import me.arowshot.mineconsole.util.json.JSONString;

public class AuthUtil {
    
    public static ArrayList<String> postData(URL url, String request) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type","text/xml");
        
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        
        
        writer.write(request);
        writer.flush();
        String read;
        ArrayList<String> out = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((read = reader.readLine()) != null) {
              out.add(read);
            }
            reader.close();
        } catch(Exception ex) {
            BufferedReader errreader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            while ((read = errreader.readLine()) != null) {
                out.add(read);
            }
            errreader.close();
        }
        writer.close();
        return out;
    }
    
    public static String joinServer(String token, String uuid, String serverHash) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/join");
            String request = "{\"accessToken\":\"" + token + "\",\"selectedProfile\":\"" + uuid + "\",\"serverId\":\"" + serverHash + "\"}"; //todo: use json util to make json object
            List<String> response = postData(url, request);
            if(response.size()>0) {
                JSONObject jsonResponse = JSONReader.decode(response.get(0));//in the furute, do some fancy json stuff here
                if(jsonResponse.getData().get("errorMessage")!=null)
                    if(jsonResponse.getData().get("errorMessage") instanceof JSONString)
                        return ((JSONString)jsonResponse.getData().get("errorMessage")).getData();
            } else {
                return "OK";
            }
            //new Gson().
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "";
    }
}
