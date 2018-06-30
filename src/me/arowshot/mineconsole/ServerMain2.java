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
import me.arowshot.mineconsole.protocol.protocol109.packets.C02ChatMessage;
import me.arowshot.mineconsole.protocol.protocol109.packets.C0BKeepAlive;
import me.arowshot.mineconsole.protocol.protocol109.packets.C1ADisconnect;
import me.arowshot.mineconsole.protocol.protocol109.packets.Packets;
import me.arowshot.mineconsole.protocol.protocol109.packets.S1FKeepAlive;
import me.arowshot.mineconsole.protocol.protocol109.packets.login.C01EncryptionRequest;
import me.arowshot.mineconsole.protocol.protocol109.packets.login.C02LoginSuccess;
import me.arowshot.mineconsole.protocol.protocol109.packets.login.C03SetCompression;
import me.arowshot.mineconsole.protocol.protocol109.packets.login.LoginPackets;
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

public class ServerMain2 implements PacketListener {
    static DataOutputStream stdOut;
    static DataInputStream stdIn;
    int compression = 0;
    static String username = "";
    boolean shouldForward = false;
    private CompressionUtil compressionUtil;
    
    public ServerMain2(Protocol109 pro) throws Exception {
        pro.eventManager.addListener(this);
        ServerSocket serverSocket = new ServerSocket(25561);
        Socket clientSocket = serverSocket.accept();
        stdOut = new DataOutputStream(clientSocket.getOutputStream());
        stdIn = new DataInputStream(clientSocket.getInputStream());
        

        //RawPacket handshakePacket = readNextPacket();
        readNextPacket(); // just ignore handshake for now
        
        
        RawPacket loginPacket = readNextPacket();
        username = ReadUtil.readNextString(loginPacket.getRawBytes());
        
        

        //C03SetCompression setCompression = new C03SetCompression(Integer.MAX_VALUE);
        //sendPacket(setCompression);

        //C1ADisconnect disconnect = new C1ADisconnect("who knows");
        //sendPacket(disconnect);
        
        Session session = new Session("ArowShot", "dcf773e5e83244d480f1462df2b9324e", "90d9489ceda149f29ccde73ecf5cae25");
        pro.doLogin(session, "127.0.0.1", (short) 25563);

        

        //C02LoginSuccess loginSuccess = new C02LoginSuccess("90cda1dd-a3da-4415-b474-b33bdd3dd7ad", username);
        //sendPacket(loginSuccess);
        
        //RawPacket unknownPacket = readNextPacket();
        //System.out.println(unknownPacket.getId());
        
        System.out.println(username);
        
        
        /*List<Byte> test = new ArrayList<>(Arrays.asList((byte)120, (byte)156, (byte)237, (byte)157, (byte)77, (byte)111, (byte)219, (byte)200, (byte)29, (byte)198, (byte)105, (byte)197, (byte)40, (byte)176, (byte)123, (byte)88, (byte)121, (byte)70, (byte)114, (byte)162, (byte)220, (byte)196, (byte)145, (byte)34, (byte)179, (byte)40, (byte)82, (byte)68, (byte)22, (byte)208, (byte)30, (byte)11, (byte)9, (byte)106, (byte)238, (byte)54, (byte)32, (byte)244, (byte)108, (byte)3, (byte)229, (byte)182, (byte)183, (byte)58, (byte)64, (byte)171, (byte)28, (byte)99, (byte)98, (byte)81, (byte)179, (byte)197, (byte)30, (byte)118, (byte)123, (byte)90, (byte)93, (byte)251, (byte)9, (byte)138, (byte)61, (byte)118, (byte)47, (byte)253, (byte)8, (byte)249, (byte)2, (byte)6, (byte)22, (byte)251, (byte)1, (byte)2, (byte)127, (byte)3, (byte)245, (byte)255, (byte)54, (byte)124, (byte)25, (byte)145, (byte)146, (byte)108, (byte)217, (byte)78, (byte)90, (byte)204, (byte)227, (byte)151, (byte)135, (byte)51, (byte)28, (byte)206, (byte)239, (byte)225, (byte)112, (byte)100, (byte)24, (byte)28, (byte)153, (byte)238, (byte)6, (byte)65, (byte)112, (byte)0, (byte)95, (byte)63, (byte)219, (byte)251, (byte)205, (byte)79, (byte)255, (byte)108, (byte)236, (byte)55, (byte)131, (byte)63, (byte)29, (byte)168, (byte)195, (byte)111, (byte)62, (byte)107, (byte)221, (byte)236, (byte)189, (byte)223, (byte)251, (byte)215, (byte)222, (byte)191, (byte)247, (byte)222, (byte)53, (byte)190, (byte)223, (byte)123, (byte)245, (byte)190, (byte)241, (byte)174, (byte)161, (byte)62, (byte)182, (byte)66, (byte)163, (byte)66, (byte)13, (byte)166, (byte)195, (byte)80, (byte)169, (byte)161, (byte)86, (byte)108, (byte)58, (byte)84, (byte)80, (byte)217, (byte)130, (byte)50, (byte)236, (byte)162, (byte)79, (byte)40, (byte)41, (byte)42, (byte)130, (byte)180, (byte)86, (byte)208, (byte)32, (byte)228, (byte)109, (byte)168, (byte)167, (byte)70, (byte)67, (byte)133, (byte)149, (byte)180, (byte)173, (byte)121, (byte)159, (byte)198, (byte)230, (byte)67, (byte)106, (byte)163, (byte)164, (byte)35, (byte)114, (byte)104, (byte)192, (byte)45, (byte)176, (byte)1, (byte)240, (byte)149, (byte)193, (byte)16, (byte)33, (byte)118, (byte)138, (byte)37, (byte)106, (byte)169, (byte)49, (byte)2, (byte)86, (byte)27, (byte)19, (byte)42, (byte)131, (byte)199, (byte)25, (byte)101, (byte)140, (byte)134, (byte)79, (byte)77, (byte)149, (byte)10, (byte)107, (byte)67, (byte)168, (byte)128, (byte)214, (byte)70, (byte)89, (byte)172, (byte)130, (byte)6, (byte)35, (byte)232, (byte)132, (byte)78, (byte)2, (byte)78, (byte)9, (byte)0, (byte)61, (byte)236, (byte)172, (byte)31, (byte)182, (byte)66, (byte)219, (byte)59, (byte)124, (byte)193, (byte)238, (byte)17, (byte)20, (byte)53, (byte)82, (byte)160, (byte)215, (byte)16, (byte)59, (byte)214, (byte)216, (byte)51, (byte)28, (byte)104, (byte)40, (byte)12, (byte)85, (byte)132, (byte)154, (byte)78, (byte)157, (byte)178, (byte)133, (byte)230, (byte)5, (byte)237, (byte)52, (byte)112, (byte)216, (byte)9, (byte)238, (byte)129, (byte)143, (byte)153, (byte)226, (byte)44, (byte)134, (byte)142, (byte)67, (byte)120, (byte)123, (byte)130, (byte)13, (byte)66, (byte)53, (byte)158, (byte)194, (byte)33, (byte)80, (byte)59, (byte)198, (byte)221, (byte)192, (byte)104, (byte)227, (byte)78, (byte)232, (byte)112, (byte)170, (byte)135, (byte)97, (byte)28, (byte)234, (byte)254, (byte)88, (byte)171, (byte)179, (byte)115, (byte)104, (byte)161, (byte)195, (byte)9, (byte)19, (byte)233, (byte)83, (byte)227, (byte)119, (byte)67, (byte)39, (byte)5, (byte)60, (byte)238, (byte)24, (byte)116, (byte)26, (byte)114, (byte)229, (byte)41, (byte)183, (byte)11, (byte)57, (byte)37, (byte)54, (byte)87, (byte)225, (byte)148, (byte)90, (byte)105, (byte)0, (byte)161, (byte)233, (byte)118, (byte)155, (byte)198, (byte)40, (byte)28, (byte)183, (byte)169, (byte)216, (byte)159, (byte)66, (byte)14, (byte)168, (byte)104, (byte)79, (byte)167, (byte)122, (byte)160, (byte)98, (byte)211, (byte)159, (byte)78, (byte)84, (byte)28, (byte)63, (byte)53, (byte)253, (byte)177, (byte)249, (byte)85, (byte)124, (byte)118, (byte)110, (byte)204, (byte)4, (byte)62, (byte)238, (byte)164, (byte)190, (byte)248, (byte)84, (byte)138, (byte)226, (byte)211, (byte)162, (byte)15, (byte)206, (byte)251, (byte)232, (byte)113, (byte)28, (byte)3, (byte)215, (byte)252, (byte)37, (byte)119, (byte)168, (byte)55, (byte)131, (byte)47, (byte)227, (byte)24, (byte)218, (byte)196, (byte)247, (byte)195, (byte)119, (byte)115, (byte)144, (byte)15, (byte)98, (byte)202, (byte)49, (byte)0, (byte)144, (byte)240, (byte)113, (byte)23, (byte)251, (byte)192, (byte)48, (byte)255, (byte)15, (byte)27, (byte)56, (byte)47, (byte)110, (byte)153, (byte)167, (byte)168, (byte)65, (byte)204, (byte)223, (byte)227, (byte)130, (byte)167, (byte)232, (byte)231, (byte)198, (byte)36, (byte)224, (byte)231, (byte)187, (byte)240, (byte)255, (byte)81, (byte)42, (byte)69, (byte)214, (byte)19, (byte)246, (byte)52, (byte)53, (byte)230, (byte)156, (byte)125, (byte)16, (byte)15, (byte)206, (byte)77, (byte)148, (byte)194, (byte)101, (byte)56, (byte)39, (byte)39, (byte)46, (byte)57, (byte)198, (byte)250, (byte)251, (byte)6, (byte)254, (byte)15, (byte)108, (byte)191, (byte)16, (byte)255, (byte)225, (byte)251, (byte)133, (byte)84, (byte)47, (byte)40, (byte)64, (byte)106, (byte)189, (byte)192, (byte)157, (byte)149, (byte)157, (byte)70, (byte)33, (byte)37, (byte)190, (byte)41, (byte)240, (byte)127, (byte)203, (byte)252, (byte)175, (byte)133, (byte)211, (byte)119, (byte)188, (byte)158, (byte)191, (byte)16, (byte)62, (byte)129, (byte)211, (byte)132, (byte)60, (byte)66, (byte)158, (byte)17, (byte)127, (byte)33, (byte)124, (byte)201, (byte)51, (byte)200, (byte)198, (byte)129, (byte)26, (byte)192, (byte)148, (byte)99, (byte)62, (byte)57, (byte)107, (byte)134, (byte)243, (byte)167, (byte)119, (byte)140, (byte)71, (byte)246, (byte)205, (byte)232, (byte)152, (byte)115, (byte)140, (byte)120, (byte)95, (byte)239, (byte)184, (byte)28, (byte)168, (byte)219, (byte)224, (byte)243, (byte)14, (byte)130, (byte)6, (byte)121, (byte)151, (byte)221, (byte)158, (byte)127, (byte)97, (byte)28, (byte)138, (byte)30, (byte)165, (byte)206, (byte)121, (byte)229, (byte)175, (byte)187, (byte)209, (byte)232, (byte)152, (byte)185, (byte)163, (byte)227, (byte)83, (byte)240, (byte)222, (byte)136, (byte)192, (byte)214, (byte)141, (byte)152, (byte)196, (byte)226, (byte)171, (byte)186, (byte)170, (byte)200, (byte)241, (byte)205, (byte)42, (byte)240, (byte)145, (byte)192, (byte)252, (byte)107, (byte)225, (byte)94, (byte)191, (byte)188, (byte)102, (byte)254, (byte)203, (byte)235, (byte)17, (byte)129, (byte)193, (byte)87, (byte)249, (byte)189, (byte)158, (byte)237, (byte)0, (byte)247, (byte)245, (byte)70, (byte)54, (byte)241, (byte)214, (byte)25, (byte)236, (byte)225, (byte)47, (byte)175, (byte)201, (byte)143, (byte)95, (byte)94, (byte)95, (byte)19, (byte)215, (byte)186, (byte)121, (byte)137, (byte)57, (byte)12, (byte)241, (byte)165, (byte)245, (byte)117, (byte)126, (byte)224, (byte)49, (byte)3, (byte)145, (byte)59, (byte)226, (byte)126, (byte)70, (byte)199, (byte)63, (byte)73, (byte)156, (byte)15, (byte)91, (byte)194, (byte)223, (byte)224, (byte)129, (byte)199, (byte)212, (byte)123, (byte)143, (byte)185, (byte)120, (byte)193, (byte)201, (byte)9, (byte)107, (byte)185, (byte)37, (byte)254, (byte)81, (byte)61, (byte)255, (byte)151, (byte)43, (byte)252, (byte)55, (byte)27, (byte)18, (byte)4, (byte)94, (byte)94, (byte)94, (byte)94, (byte)119, (byte)211, (byte)205, (byte)93, (byte)15, (byte)108, (byte)238, (byte)198, (byte)93, (byte)10, (byte)222, (byte)241, (byte)199, (byte)226, (byte)47, (byte)151, (byte)130, (byte)45, (byte)251, (byte)163, (byte)233, (byte)199, (byte)47, (byte)216, (byte)62, (byte)124, (byte)16, (byte)255, (byte)226, (byte)113, (byte)249, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)255, (byte)171, (byte)218, (byte)255, (byte)60, (byte)56, (byte)120, (byte)117, (byte)179, (byte)167, (byte)222, (byte)239, (byte)181, (byte)222, (byte)53, (byte)14, (byte)127, (byte)220, (byte)115, (byte)86, (byte)4, (byte)140, (byte)117, (byte)45, (byte)91, (byte)37, (byte)111, (byte)13, (byte)185, (byte)96, (byte)189, (byte)221, (byte)38, (byte)155, (byte)78, (byte)167, (byte)99, (byte)44, (byte)129, (byte)115, (byte)185, (byte)212, (byte)37, (byte)183, (byte)61, (byte)60, (byte)161, (byte)239, (byte)179, (byte)217, (byte)76, (byte)156, (byte)202, (byte)10, (byte)74, (byte)103, (byte)107, (byte)151, (byte)11, (byte)202, (byte)26, (byte)222, (byte)162, (byte)109, (byte)73, (byte)57, (byte)119, (byte)38, (byte)197, (byte)44, (byte)199, (byte)153, (byte)156, (byte)225, (byte)153, (byte)44, (byte)54, (byte)220, (byte)38, (byte)143, (byte)122, (byte)54, (byte)159, (byte)243, (byte)134, (byte)245, (byte)219, (byte)243, (byte)207, (byte)13, (byte)175, (byte)98, (byte)196, (byte)226, (byte)103, (byte)214, (byte)213, (byte)74, (byte)158, (byte)167, (byte)110, (byte)183, (byte)79, (byte)191, (byte)100, (byte)238, (byte)211, (byte)88, (byte)248, (byte)241, (byte)89, (byte)77, (byte)203, (byte)218, (byte)241, (byte)111, (byte)39, (byte)194, (byte)187, (byte)172, (byte)226, (byte)63, (byte)123, (byte)142, (byte)30, (byte)11, (byte)38, (byte)62, (byte)155, (byte)207, (byte)47, (byte)208, (byte)17, (byte)179, (byte)80, (byte)99, (byte)216, (byte)0, (byte)135, (byte)235, (byte)254, (byte)154, (byte)184, (byte)99, (byte)113, (byte)106, (byte)33, (byte)92, (byte)235, (byte)50, (byte)39, (byte)218, (byte)52, (byte)83, (byte)104, (byte)174, (byte)72, (byte)253, (byte)52, (byte)53, (byte)45, (byte)230, (byte)107, (byte)55, (byte)51, (byte)242, (byte)23, (byte)212, (byte)73, (byte)37, (byte)127, (byte)138, (byte)92, (byte)246, (byte)233, (byte)235, (byte)51, (byte)158, (byte)127, (byte)175, (byte)57, (byte)7, (byte)237, (byte)40, (byte)242, (byte)45, (byte)215, (byte)78, (byte)209, (byte)118, (byte)238, (byte)151, (byte)170, (byte)165, (byte)46, (byte)84, (byte)187, (byte)2, (byte)94, (byte)212, (byte)183, (byte)248, (byte)173, (byte)60, (byte)170, (byte)22, (byte)135, (byte)252, (byte)169, (byte)117, (byte)91, (byte)193, (byte)189, (byte)103, (byte)109, (byte)153, (byte)175, (byte)195, (byte)62, (byte)59, (byte)174, (byte)88, (byte)40, (byte)218, (byte)232, (byte)147, (byte)95, (byte)108, (byte)160, (byte)87, (byte)170, (byte)205, (byte)47, (byte)59, (byte)58, (byte)239, (byte)105, (byte)193, (byte)243, (byte)179, (byte)180, (byte)146, (byte)117, (byte)50, (byte)92, (byte)179, (byte)233, (byte)139, (byte)27, (byte)201, (byte)97, (byte)84, (byte)189, (byte)36, (byte)213, (byte)51, (byte)199, (byte)87, (byte)37, (byte)131, (byte)171, (byte)166, (byte)246, (byte)234, (byte)194, (byte)87, (byte)71, (byte)246, (byte)161, (byte)79, (byte)96, (byte)74, (byte)165, (byte)176, (byte)219, (byte)224, (byte)157, (byte)105, (byte)118, (byte)131, (byte)62, (byte)49, (byte)107, (byte)3, (byte)84, (byte)241, (byte)23, (byte)142, (byte)99, (byte)125, (byte)229, (byte)193, (byte)29, (byte)201, (byte)149, (byte)242, (byte)143, (byte)167, (byte)78, (byte)10, (byte)227, (byte)51, (byte)33, (byte)62, (byte)185, (byte)65, (byte)159, (byte)174, (byte)231, (byte)87, (byte)9, (byte)184, (byte)191, (byte)86, (byte)5, (byte)254, (byte)243, (byte)58, (byte)62, (byte)142, (byte)2, (byte)94, (byte)133, (byte)34, (byte)31, (byte)46, (byte)11, (byte)251, (byte)132, (byte)115, (byte)176, (byte)223, (byte)85, (byte)163, (byte)202, (byte)218, (byte)231, (byte)171, (byte)85, (byte)227, (byte)54, (byte)229, (byte)160, (byte)57, (byte)207, (byte)252, (byte)177, (byte)74, (byte)185, (byte)252, (byte)252, (byte)187, (byte)187, (byte)243, (byte)171, (byte)181, (byte)112, (byte)188, (byte)66, (byte)60, (byte)57, (byte)211, (byte)244, (byte)171, (byte)113, (byte)62, (byte)77, (byte)238, (byte)93, (byte)223, (byte)110, (byte)106, (byte)144, (byte)166, (byte)233, (byte)101, (byte)253, (byte)222, (byte)75, (byte)219, (byte)136, (byte)172, (byte)35, (byte)77, (byte)173, (byte)75, (byte)181, (byte)90, (byte)51, (byte)120, (byte)173, (byte)245, (byte)244, (byte)17, (byte)244, (byte)133, (byte)157, (byte)185, (byte)23, (byte)177, (byte)115, (byte)57, (byte)100, (byte)0, (byte)121, (byte)71, (byte)50, (byte)118, (byte)4, (byte)152, (byte)178, (byte)231, (byte)213, (byte)54, (byte)214, (byte)173, (byte)249, (byte)116, (byte)24, (byte)125, (byte)255, (byte)216, (byte)191, (byte)145, (byte)120, (byte)121, (byte)121, (byte)121, (byte)121, (byte)121, (byte)121, (byte)121, (byte)121, (byte)121, (byte)121, (byte)121, (byte)121, (byte)121, (byte)121, (byte)61, (byte)188, (byte)246, (byte)63, (byte)11, (byte)14, (byte)14, (byte)111, (byte)246, (byte)222, (byte)53, (byte)90, (byte)234, (byte)213, (byte)123, (byte)119, (byte)61, (byte)64, (byte)233, (byte)144, (byte)238, (byte)71, (byte)24, (byte)67, (byte)142, (byte)239, (byte)245, (byte)86, (byte)236, (byte)138, (byte)171, (byte)141, (byte)42, (byte)86, (byte)247, (byte)249, (byte)254, (byte)159, (byte)206, (byte)238, (byte)242, (byte)114, (byte)39, (byte)237, (byte)241, (byte)230, (byte)155, (byte)17, (byte)27, (byte)148, (byte)221, (byte)193, (byte)100, (byte)128, (byte)220, (byte)163, (byte)182, (byte)110, (byte)239, (byte)226, (byte)245, (byte)237, (byte)141, (byte)200, (byte)205, (byte)252, (byte)147, (byte)157, (byte)35, (byte)221, (byte)78, (byte)135, (byte)98, (byte)179, (byte)178, (byte)171, (byte)89, (byte)101, (byte)235, (byte)237, (byte)53, (byte)44, (byte)22, (byte)98, (byte)185, (byte)255, (byte)254, (byte)212, (byte)222, (byte)127, (byte)143, (byte)101, (byte)35, (byte)171, (byte)56, (byte)252, (byte)253, (byte)174, (byte)124, (byte)103, (byte)93, (byte)98, (byte)68, (byte)189, (byte)159, (byte)137, (byte)11, (byte)38, (byte)150, (byte)32, (byte)197, (byte)10, (byte)27, (byte)232, (byte)174, (byte)220, (byte)26, (byte)190, (byte)18, (byte)126, (byte)235, (byte)86, (byte)124, (byte)153, (byte)78, (byte)246, (byte)198, (byte)184, (byte)93, (byte)229, (byte)194, (byte)233, (byte)148, (byte)45, (byte)129, (byte)57, (byte)109, (byte)100, (byte)197, (byte)68, (byte)185, (byte)3, (byte)103, (byte)253, (byte)68, (byte)109, (byte)49, (byte)254, (byte)134, (byte)99, (byte)103, (byte)188, (byte)10, (byte)55, (byte)97, (byte)245, (byte)190, (byte)153, (byte)245, (byte)45, (byte)114, (byte)212, (byte)202, (byte)89, (byte)213, (byte)43, (byte)48, (byte)140, (byte)245, (byte)7, (byte)229, (byte)91, (byte)229, (byte)99, (byte)43, (byte)63, (byte)64, (byte)56, (byte)17, (byte)254, (byte)145, (byte)201, (byte)29, (byte)198, (byte)159, (byte)214, (byte)35, (byte)79, (byte)242, (byte)117, (byte)201, (byte)219, (byte)168, (byte)106, (byte)157, (byte)243, (byte)182, (byte)114, (byte)215, (byte)69, (byte)183, (byte)145, (byte)187, (byte)218, (byte)101, (byte)28, (byte)223, (byte)81, (byte)43, (byte)183, (byte)127, (byte)117, (byte)133, (byte)95, (byte)56, (byte)94, (byte)149, (byte)107, (byte)91, (byte)201, (byte)45, (byte)232, (byte)78, (byte)193, (byte)105, (byte)97, (byte)164, (byte)163, (byte)212, (byte)92, (byte)250, (byte)183, (byte)254, (byte)108, (byte)46, (byte)229, (byte)121, (byte)185, (byte)108, (byte)125, (byte)211, (byte)169, (byte)84, (byte)201, (byte)114, (byte)241, (byte)6, (byte)57, (byte)114, (byte)172, (byte)99, (byte)46, (byte)119, (byte)141, (byte)118, (byte)158, (byte)241, (byte)47, (byte)164, (byte)124, (byte)81, (byte)226, (byte)15, (byte)177, (byte)82, (byte)126, (byte)94, (byte)195, (byte)244, (byte)226, (byte)91, (byte)244, (byte)135, (byte)53, (byte)235, (byte)45, (byte)5, (byte)254, (byte)156, (byte)249, (byte)207, (byte)132, (byte)207, (byte)107, (byte)195, (byte)233, (byte)234, (byte)26, (byte)113, (byte)238, (byte)204, (byte)71, (byte)46, (byte)78, (byte)29, (byte)226, (byte)15, (byte)165, (byte)195, (byte)156, (byte)111, (byte)203, (byte)178, (byte)231, (byte)66, (byte)149, (byte)93, (byte)36, (byte)252, (byte)142, (byte)156, (byte)119, (byte)71, (byte)114, (byte)208, (byte)248, (byte)59, (byte)92, (byte)89, (byte)56, (byte)155, (byte)253, (byte)142, (byte)203, (byte)179, (byte)217, (byte)159, (byte)137, (byte)63, (byte)163, (byte)69, (byte)225, (byte)225, (byte)199, (byte)254, (byte)125, (byte)196, (byte)203, (byte)203, (byte)203, (byte)203, (byte)203, (byte)203, (byte)203, (byte)203, (byte)203, (byte)203, (byte)203, (byte)203, (byte)203, (byte)203, (byte)235, (byte)225, (byte)181, (byte)255, (byte)121, (byte)208, (byte)194, (byte)63, (byte)17, (byte)160, (byte)181, (byte)128, (byte)119, (byte)79, (byte)254, (byte)3, (byte)38, (byte)207, (byte)14, (byte)8, (byte)29, (byte)55, (byte)246, (byte)89, (byte)21, (byte)35, (byte)199, (byte)143, (byte)107, (byte)189, (byte)39, (byte)91, (byte)214, (byte)69, (byte)244, (byte)78, (byte)100, (byte)186, (byte)243, (byte)168, (byte)197, (byte)121, (byte)195, (byte)122, (byte)141, (byte)110, (byte)207, (byte)175, (byte)209, (byte)102, (byte)62, (byte)191, (byte)105, (byte)122, (byte)67, (byte)142, (byte)109, (byte)189, (byte)86, (byte)150, (byte)162, (byte)100, (byte)99, (byte)75, (byte)254, (byte)255, (byte)137, (byte)170, (byte)30, (byte)106, (byte)243, (byte)73, (byte)201, (byte)121, (byte)250, (byte)207, (byte)170, (byte)78, (byte)217, (byte)94, (byte)156, (byte)110, (byte)236, (byte)106, (byte)224, (byte)248, (byte)58, (byte)217, (byte)23, (byte)11, (byte)191, (byte)95, (byte)222, (byte)28, (byte)205, (byte)165, (byte)108, (byte)61, (byte)227, (byte)205, (byte)102, (byte)214, (byte)121, (byte)227, (byte)5, (byte)248, (byte)185, (byte)112, (byte)42, (byte)188, (byte)183, (byte)129, (byte)111, (byte)185, (byte)242, (byte)178, (byte)58, (byte)146, (byte)211, (byte)158, (byte)11, (byte)55, (byte)203, (byte)81, (byte)224, (byte)158, (byte)150, (byte)248, (byte)224, (byte)196, (byte)131, (byte)124, (byte)248, (byte)56, (byte)156, (byte)94, (byte)129, (byte)11, (byte)82, (byte)220, (byte)111, (byte)152, (byte)249, (byte)38, (byte)254, (byte)124, (byte)51, (byte)223, (byte)205, (byte)113, (byte)202, (byte)231, (byte)127, (byte)106, (byte)250, (byte)5, (byte)110, (byte)139, (byte)141, (byte)185, (byte)33, (byte)58, (byte)47, (byte)36, (byte)106, (byte)118, (byte)254, (byte)226, (byte)125, (byte)186, (byte)152, (byte)163, (byte)164, (byte)249, (byte)155, (byte)220, (byte)145, (byte)118, (byte)244, (byte)70, (byte)248, (byte)111, (byte)242, (byte)28, (byte)112, (byte)222, (byte)253, (byte)9, (byte)79, (byte)107, (byte)112, (byte)81, (byte)152, (byte)45, (byte)115, (byte)180, (byte)132, (byte)171, (byte)194, (byte)178, (byte)103, (byte)252, (byte)226, (byte)79, (byte)191, (byte)68, (byte)158, (byte)181, (byte)51, (byte)127, (byte)43, (byte)39, (byte)63, (byte)47, (byte)185, (byte)113, (byte)29, (byte)102, (byte)7, (byte)33, (byte)39, (byte)71, (byte)238, (byte)179, (byte)142, (byte)132, (byte)79, (byte)75, (byte)93, (byte)188, (byte)220, (byte)18, (byte)22, (byte)61, (byte)63, (byte)239, (byte)140, (byte)159, (byte)164, (byte)38, (byte)74, (byte)35, (byte)244, (byte)232, (byte)232, (byte)237, (byte)219, (byte)196, (byte)164, (byte)196, (byte)133, (byte)154, (byte)196, (byte)68, (byte)243, (byte)43, (byte)248, (byte)6, (byte)31, (byte)243, (byte)43, (byte)216, (byte)159, (byte)68, (byte)209, (byte)213, (byte)156, (byte)179, (byte)30, (byte)93, (byte)125, (byte)149, (byte)224, (byte)115, (byte)167, (byte)210, (byte)232, (byte)237, (byte)36, (byte)161, (byte)39, (byte)5, (byte)69, (byte)17, (byte)121, (byte)146, (byte)96, (byte)59, (byte)216, (byte)99, (byte)20, (byte)150, (byte)34, (byte)115, (byte)121, (byte)153, (byte)116, (byte)116, (byte)66, (byte)207, (byte)18, (byte)234, (byte)36, (byte)116, (byte)236, (byte)229, (byte)165, (byte)166, (byte)6, (byte)144, (byte)35, (byte)66, (byte)108, (byte)146, (byte)166, (byte)63, (byte)95, (byte)44, (byte)18, (byte)228, (byte)95, (byte)189, (byte)77, (byte)65, (byte)208, (byte)10, (byte)248, (byte)96, (byte)105, (byte)58, (byte)191, (byte)74, (byte)129, (byte)106, (byte)249, (byte)216, (byte)0, (byte)179, (byte)166, (byte)81, (byte)148, (byte)166, (byte)72, (byte)194, (byte)45, (byte)236, (byte)83, (byte)248, (byte)208, (byte)32, (byte)73, (byte)225, (byte)35, (byte)130, (byte)115, (byte)72, (byte)13, (byte)243, (byte)19, (byte)140, (byte)141, (byte)13, (byte)19, (byte)124, (byte)110, (byte)82, (byte)2, (byte)155, (byte)88, (byte)17, (byte)113, (byte)34, (byte)232, (byte)6, (byte)142, (byte)32, (byte)62, (byte)130, (byte)163, (byte)244, (byte)143, (byte)11, (byte)230, (byte)167, (byte)127, (byte)21, (byte)126, (byte)138, (byte)124, (byte)220, (byte)131, (byte)109, (byte)83, (byte)234, (byte)40, (byte)165, (byte)50, (byte)236, (byte)48, (byte)100, (byte)148, (byte)6, (byte)59, (byte)79, (byte)240, (byte)76, (byte)18, (byte)234, (byte)135, (byte)248, (byte)48, (byte)174, (byte)28, (byte)52, (byte)194, (byte)79, (byte)218, (byte)69, (byte)137, (byte)97, (byte)127, (byte)132, (byte)84, (byte)57, (byte)30, (byte)143, (byte)88, (byte)44, (byte)22, (byte)223, (byte)225, (byte)129, (byte)127, (byte)227, (byte)28, (byte)184, (byte)149, (byte)62, (byte)154, (byte)62, (byte)246, (byte)175, (byte)36, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)15, (byte)175, (byte)253, (byte)253, (byte)128, (byte)22, (byte)2, (byte)246, (byte)241, (byte)175, (byte)3, (byte)236, (byte)141, (byte)113, (byte)101, (byte)242, (byte)63, (byte)6, (byte)176, (byte)111, (byte)213, (byte)93, (byte)247, (byte)14, (byte)201, (byte)251, (byte)146, (byte)233, (byte)6, (byte)65, (byte)233, (byte)78, (byte)189, (byte)117, (byte)109, (byte)3, (byte)101, (byte)127, (byte)158, (byte)112, (byte)87, (byte)173, (byte)123, (byte)47, (byte)172, (byte)38, (byte)62, (byte)221, (byte)143, (byte)239, (byte)6, (byte)249, (byte)45, (byte)50, (byte)149, (byte)223, (byte)177, (byte)87, (byte)214, (byte)237, (byte)159, (byte)71, (byte)208, (byte)163, (byte)255, (byte)183, (byte)225, (byte)133, (byte)182, (byte)108, (byte)223, (byte)130, (byte)93, (byte)49, (byte)174, (byte)244, (byte)8, (byte)231, (byte)134, (byte)41, (byte)230, (byte)48, (byte)221, (byte)82, (byte)14, (byte)189, (byte)146, (byte)67, (byte)103, (byte)57, (byte)202, (byte)227, (byte)178, (byte)238, (byte)109, (byte)232, (byte)5, (byte)207, (byte)134, (byte)147, (byte)110, (byte)12, (byte)118, (byte)113, (byte)70, (byte)152, (byte)114, (byte)142, (byte)70, (byte)85, (byte)142, (byte)149, (byte)241, (byte)208, (byte)218, (byte)233, (byte)48, (byte)31, (byte)167, (byte)210, (byte)68, (byte)170, (byte)112, (byte)26, (byte)3, (byte)99, (byte)4, (byte)11, (byte)64, (byte)201, (byte)1, (byte)78, (byte)21, (byte)13, (byte)9, (byte)198, (byte)243, (byte)19, (byte)182, (byte)148, (byte)179, (byte)130, (byte)82, (byte)26, (byte)15, (byte)199, (byte)117, (byte)238, (byte)37, (byte)110, (byte)105, (byte)130, (byte)203, (byte)205, (byte)91, (byte)198, (byte)226, (byte)200, (byte)91, (byte)231, (byte)10, (byte)201, (byte)241, (byte)68, (byte)201, (byte)134, (byte)147, (byte)163, (byte)91, (byte)197, (byte)55, (byte)14, (byte)63, (byte)203, (byte)217, (byte)45, (byte)58, (byte)47, (byte)183, (byte)101, (byte)55, (byte)130, (byte)229, (byte)37, (byte)185, (byte)38, (byte)7, (byte)109, (byte)84, (byte)228, (byte)192, (byte)46, (byte)130, (byte)181, (byte)60, (byte)186, (byte)142, (byte)153, (byte)107, (byte)195, (byte)215, (byte)151, (byte)174, (byte)171, (byte)229, (byte)119, (byte)45, (byte)127, (byte)115, (byte)142, (byte)160, (byte)156, (byte)67, (byte)217, (byte)28, (byte)74, (byte)114, (byte)228, (byte)101, (byte)186, (byte)110, (byte)74, (byte)227, (byte)132, (byte)46, (byte)56, (byte)77, (byte)112, (byte)154, (byte)86, (byte)193, (byte)19, (byte)57, (byte)247, (byte)70, (byte)246, (byte)252, (byte)242, (byte)174, (byte)227, (byte)121, (byte)142, (byte)174, (byte)184, (byte)41, (byte)143, (byte)7, (byte)227, (byte)138, (byte)92, (byte)106, (byte)33, (byte)156, (byte)64, (byte)184, (byte)141, (byte)220, (byte)13, (byte)79, (byte)108, (byte)62, (byte)103, (byte)26, (byte)131, (byte)194, (byte)105, (byte)175, (byte)104, (byte)83, (byte)14, (byte)101, (byte)10, (byte)215, (byte)67, (byte)220, (byte)230, (byte)177, (byte)110, (byte)247, (byte)7, (byte)60, (byte)159, (byte)249, (byte)5, (byte)102, (byte)196, (byte)185, (byte)219, (byte)70, (byte)29, (byte)127, (byte)139, (byte)28, (byte)206, (byte)56, (byte)232, (byte)34, (byte)111, (byte)197, (byte)237, (byte)84, (byte)179, (byte)215, (byte)213, (byte)246, (byte)187, (byte)173, (byte)86, (byte)115, (byte)200, (byte)56, (byte)40, (byte)59, (byte)14, (byte)117, (byte)174, (byte)187, (byte)197, (byte)246, (byte)187, (byte)62, (byte)176, (byte)62, (byte)203, (byte)145, (byte)205, (byte)207, (byte)108, (byte)94, (byte)112, (byte)158, (byte)60, (byte)87, (byte)183, (byte)232, (byte)249, (byte)76, (byte)191, (byte)31, (byte)101, (byte)175, (byte)151, (byte)236, (byte)122, (byte)88, (byte)119, (byte)242, (byte)132, (byte)166, (byte)148, (byte)247, (byte)190, (byte)229, (byte)190, (byte)78, (byte)235, (byte)220, (byte)203, (byte)203, (byte)203, (byte)203, (byte)203, (byte)203, (byte)203, (byte)235, (byte)211, (byte)209, (byte)114, (byte)185, (byte)227, (byte)255, (byte)18, (byte)185, (byte)39, (byte)254, (byte)71, (byte)138, (byte)113, (byte)179, (byte)148, (byte)127, (byte)62, (byte)98, (byte)115, (byte)60, (byte)114, (byte)12, (byte)192, (byte)19, (byte)216, (byte)230, (byte)184, (byte)121, (byte)220, (byte)28, (byte)136, (byte)69, (byte)240, (byte)141, (byte)228, (byte)184, (byte)225, (byte)34, (byte)121, (byte)51, (byte)120, (byte)248, (byte)217, (byte)65, (byte)120, (byte)96, (byte)44, (byte)37, (byte)135, (byte)20, (byte)3, (byte)41, (byte)6, (byte)98, (byte)59, (byte)92, (byte)157, (byte)166, (byte)227, (byte)85, (byte)252, (byte)229, (byte)218, (byte)28, (byte)205, (byte)138, (byte)28, (byte)245, (byte)255, (byte)176, (byte)70, (byte)56, (byte)238, (byte)191, (byte)214, (byte)201, (byte)254, (byte)197, (byte)78, (byte)57, (byte)135, (byte)240, (byte)155, (byte)219, (byte)228, (byte)168, (byte)26, (byte)143, (byte)101, (byte)214, (byte)79, (byte)83, (byte)48, (byte)203, (byte)114, (byte)69, (byte)157, (byte)223, (byte)216, (byte)89, (byte)95, (byte)82, (byte)179, (byte)236, (byte)205, (byte)74, (byte)190, (byte)140, (byte)71, (byte)179, (byte)220, (byte)109, (byte)214, (byte)95, (byte)83, (byte)114, (byte)84, (byte)122, (byte)249, (byte)128, (byte)166, (byte)203, (byte)119, (byte)181, (byte)54, (byte)71, (byte)21, (byte)183, (byte)224, (byte)246, (byte)229, (byte)84, (byte)235, (byte)249, (byte)120, (byte)111, (byte)84, (byte)101, (byte)142, (byte)102, (byte)5, (byte)111, (byte)27, (byte)207, (byte)174, (byte)107, (byte)62, (byte)222, (byte)219, (byte)170, (byte)156, (byte)99, (byte)75, (byte)158, (byte)157, (byte)191, (byte)203, (byte)140, (byte)191, (byte)245, (byte)121, (byte)111, (byte)200, (byte)225, (byte)244, (byte)27, (byte)84, (byte)206, (byte)147, (byte)156, (byte)223, (byte)12, (byte)118, (byte)229, (byte)186, (byte)42, (byte)247, (byte)219, (byte)92, (byte)159, (byte)167, (byte)48, (byte)238, (byte)247, (byte)29, (byte)163, (byte)58, (byte)135, (byte)235, (byte)21, (byte)218, (byte)111, (byte)4, (byte)248, (byte)23, (byte)1, (byte)193, (byte)142, (byte)178, (byte)143, (byte)29, (byte)174, (byte)247, (byte)131, (byte)93, (byte)17, (byte)91, (byte)201, (byte)82, (byte)236, (byte)83, (byte)144, (byte)221, (byte)28, (byte)143, (byte)173, (byte)44, (byte)143, (byte)127, (byte)44, (byte)179, (byte)151, (byte)151, (byte)151, (byte)151, (byte)151, (byte)151, (byte)151, (byte)151, (byte)151, (byte)151, (byte)151, (byte)151, (byte)215, (byte)39, (byte)160, (byte)123, (byte)189, (byte)13, (byte)81, (byte)231, (byte)247, (byte)127, (byte)251, (byte)165, (byte)78, (byte)206, (byte)237, (byte)204, (byte)135, (byte)190, (byte)253, (byte)178, (byte)81, (byte)229, (byte)219, (byte)85, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)94, (byte)159, (byte)136, (byte)26, (byte)219, (byte)75, (byte)149, (byte)75, (byte)74, (byte)229, (byte)198, (byte)239, (byte)42, (byte)111, (byte)100, (byte)91, (byte)91, (byte)149, (byte)221, (byte)221, (byte)202, (byte)249, (byte)143, (byte)5, (byte)143, (byte)162, (byte)255, (byte)2, (byte)79, (byte)125, (byte)141, (byte)253));
        List<Byte> testout = new ArrayList<>();
        if(this.compressionUtil==null)
            this.compressionUtil = new CompressionUtil(compression);
        
        this.compressionUtil.setThreshold(256);
        
        this.compressionUtil.decode(test, 37232, testout);
        System.out.println(testout.size());
        System.exit(0);*/
        
        while(true) {
            pro.sendPacket(readNextPacket());
        }
        
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
    
    @Override
    public void onRecievePacket(PacketEvent event) {
        //System.out.println("recieved packet " + event.getPacket().getId());
        if(shouldForward) {
            try {
                switch(event.getPacket().getId()) {
                    case 0x2D:
                        int action = ReadUtil.readNextVarInt(event.getPacket().getRawBytes());
                        int numplayers = ReadUtil.readNextVarInt(event.getPacket().getRawBytes());
                        //int size = ReadUtil.readNextVarInt(packet.getBytes());
                        byte uuid[] = new byte[16];
                        for(int i = 0;i<16;i++) { // "read" uuid
                            uuid[i] = ReadUtil.readFromCache(event.getPacket().getRawBytes());
                        }
                        if(action == 0) {
                            String username = ReadUtil.readNextString(event.getPacket().getRawBytes());
                            int numProps = ReadUtil.readNextVarInt(event.getPacket().getRawBytes());
                            String propName = ReadUtil.readNextString(event.getPacket().getRawBytes());
                            String textures = ReadUtil.readNextString(event.getPacket().getRawBytes());
                            boolean isSigned = ReadUtil.readFromCache(event.getPacket().getRawBytes())==1;
                            String signature = "";
                            if(isSigned) {
                                signature = ReadUtil.readNextString(event.getPacket().getRawBytes());
                            }
                            System.out.println(numProps);
                            System.out.println(propName);
                            System.out.println(textures);
                            System.out.println(signature);
                            
                            
                            int gamemode = ReadUtil.readNextVarInt(event.getPacket().getRawBytes());
                            int ping = ReadUtil.readNextVarInt(event.getPacket().getRawBytes());
                            boolean hasDisplayName = ReadUtil.readFromCache(event.getPacket().getRawBytes())==1;
                            String displayName = "";
                            if(hasDisplayName) {
                                displayName = ReadUtil.readNextString(event.getPacket().getRawBytes());
                            }
                            
                            List<Byte> newPacket = new ArrayList<Byte>();
                            newPacket.addAll(ReadUtil.getVarInt(action));
                            newPacket.addAll(ReadUtil.getVarInt(numplayers));
                            for(int i = 0;i<16;i++) { // "read" uuid
                                newPacket.add(uuid[i]);
                            }
                            newPacket.addAll(ReadUtil.getString("INightLightDX"));
                            newPacket.addAll(ReadUtil.getVarInt(numProps));
                            newPacket.addAll(ReadUtil.getString(propName));
                            newPacket.addAll(ReadUtil.getString("eyJ0aW1lc3RhbXAiOjE0NjE1MDYzMjg5NzYsInByb2ZpbGVJZCI6IjQ0NWY3NTgxNjBiZTQzY2U5ODg2ZmNiMjE5Njk0ZjQ4IiwicHJvZmlsZU5hbWUiOiJJTmlnaHRMaWdodERYIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2IxNjAxNDU4OTA0Y2QzOWI0ZGNkMjEzMDg5NTU4Mzc3YThmODIwODkzODBiNzc2OGE2NTEyZWJmNWJhZGUifX19"));
                            newPacket.add((byte) 0);
                            //newPacket.addAll(ReadUtil.getString(signature));
                            newPacket.addAll(ReadUtil.getVarInt(gamemode));
                            newPacket.addAll(ReadUtil.getVarInt(ping));
                            newPacket.add((byte) 0);
                            sendPacket(new RawPacket(0x2D, newPacket));
                        }
                        return;
                    default:
                        sendPacket(event.getPacket());
                }
                event.setHandled(true);
                Packet packet = Packets.getFromRaw(event.getPacket());
                if(packet==null)
                    return;
                switch(packet.getId()) {
                    case 0x0F: //Chat
                        C02ChatMessage chatPacket = (C02ChatMessage) packet;
                        System.out.println("Chat: " + chatToString(JSONReader.decode(chatPacket.getMessage())));
                        break;
                    case 0x1A: //Disconnect
                        C1ADisconnect kickPacket = (C1ADisconnect) packet;
                        //System.out.println(kickPacket.getReason());
                        System.out.println("Kicked: " + kickPacket.getReason());
                        System.exit(0);
                        break;
                    default:
                        //System.out.println(p.getId());
                }
                return;
            } catch (Exception e) {
                return;
            }
        }
        switch(event.getPacket().getId()) {
            case 0x00:
                System.out.println("Login failed");
                try {
                    sendPacket(event.getPacket());
                } catch (Exception e) { }
                break;
            case 0x02:
                try {
                    sendPacket(event.getPacket());
                } catch (Exception e) { }
                shouldForward = true;
                System.out.println("should start forwarding now");
                break;
            case 0x03:
                //Packet packet = LoginPackets.getFromRaw(new RawPacket(event.getPacket().getId(), cloneList(event.getPacket().getRawBytes())));
                //C03SetCompression compressionPacket = (C03SetCompression) packet;
                try {
                    sendPacket(event.getPacket());
                } catch (Exception e) { }
                this.compression = 256;
                System.out.println("Setting compression to 256 in server");
                break;
        }
    }
    
    public static List<Byte> cloneList(List<Byte> list) {
        List<Byte> clone = new ArrayList<Byte>(list.size());
        for(Byte item: list) clone.add(item);
        return clone;
    }
    
    static Protocol109 pro;
    static ServerMain2 sm;
    public static void main(String[] args) throws Exception {
        pro = new Protocol109();
        new Thread() {
            public void run() {
                try {
                    sm = new ServerMain2(pro);
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
        if(packet!=null)
        sendPacket(new RawPacket(packet.getId(), packet.getBytes()));
    }
    //static boolean first = true;
    public boolean sendPacket(RawPacket packet) {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.addAll(ReadUtil.getVarInt(packet.getId()));
        bytes.addAll(packet.getRawBytes());
        List<Byte> uncompressedLen = ReadUtil.getVarInt(bytes.size());
        if(compression > 0) {
            if(bytes.size() > compression) {
                if(this.compressionUtil==null)
                    this.compressionUtil = new CompressionUtil(compression);
                
                this.compressionUtil.setThreshold(compression);
                
                List<Byte> out = new ArrayList<Byte>();
                try {
                    this.compressionUtil.encode(bytes, out);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                
                bytes.clear();
                bytes.addAll(out);
                bytes.addAll(0, uncompressedLen);
            } else {
                bytes.addAll(0, ReadUtil.getVarInt(0));
            }
        }
        bytes.addAll(0, ReadUtil.getVarInt(bytes.size()));
        try {
            for(byte b:bytes) {
                sendByte(b);
            }
            stdOut.flush();
            return true;
        } catch (IOException e) {
            //if(first)
            //e.printStackTrace();
            //first=false;
            return false;
        }
    }
    
    /*public void sendPacket(RawPacket packet) throws Exception {
        List<Byte> bytes = new ArrayList<Byte>();
        bytes.addAll(packet.getRawBytes());
        bytes.addAll(0, ReadUtil.getVarInt(packet.getId()));
        bytes.addAll(0, ReadUtil.getVarInt(bytes.size()));
        for(byte b:bytes) {
            sendByte(b);
        }
        stdOut.flush();
    }*/
    
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
}
