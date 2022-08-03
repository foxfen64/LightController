package devices;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BulbManager {

    public  ArrayList<SmartBulb> devices;
    protected static final String COMMAND_GET_INFO = "{\"system\":{\"get_sysinfo\":{}}}";

    public BulbManager(){
        devices = new ArrayList<>();
    }

    public void initDevices(){
        String broadcastAddress = "255.255.255.255";
        int broadcastPort = 9999;
        int broadcastTimeoutMillis = 5000;
        try {
            HashMap<String,JsonObject> map = broadcast(COMMAND_GET_INFO, broadcastAddress, broadcastPort, broadcastTimeoutMillis);
            for(String bulbIp : map.keySet()){
                JsonObject bulbJson = map.get(bulbIp);
                String bulbModel = bulbJson.getAsJsonObject("system").getAsJsonObject("get_sysinfo").get("model").getAsString();
                if(BulbUtils.getBulbTypeFromModel(bulbModel) == BulbUtils.SMART_BULB){
                    devices.add(new SmartBulb(bulbIp, broadcastPort, bulbJson.toString()));
                }
                else if(BulbUtils.getBulbTypeFromModel(bulbModel) == BulbUtils.SMART_STRIP){
                    devices.add(new SmartLightStrip(bulbIp, broadcastPort, bulbJson.toString()));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private HashMap<String,JsonObject> broadcast(String message, String host, int port, int timeout) throws IOException {
        HashMap<String,JsonObject> addressList = new HashMap<String,JsonObject>();

        DatagramSocket dgram = new DatagramSocket();
        dgram.setBroadcast(true);

        byte[] sendPayload = encrypt(message);
        byte[] recievePayload = new byte[50000];

        InetAddress inetAddress = InetAddress.getByName(host);
        DatagramPacket dgramPacketSend = new DatagramPacket(sendPayload, sendPayload.length, inetAddress, port);
        DatagramPacket dgramPacketRecieve = new DatagramPacket(recievePayload, recievePayload.length, inetAddress, port);

        dgram.send(dgramPacketSend);

        while(true) {
            dgram.setSoTimeout(timeout);
            try {
                dgram.receive(dgramPacketRecieve);
                String decryptedString = decrypt(Arrays.copyOfRange(recievePayload, 0, dgramPacketRecieve.getLength()));
                addressList.put(dgramPacketRecieve.getAddress().getHostAddress()
                        , JsonParser.parseString((decryptedString)).getAsJsonObject());
                dgram.setSoTimeout(0);
            }catch(SocketTimeoutException e) {
                break;
            }
        }

        dgram.close();

        return addressList;
    }

    private byte[] encrypt(String command) {

        byte[] buffer = new byte[command.length()];
        int key = 0xAB;
        for(int i = 0; i < command.length(); i++) {
            buffer[i] = (byte) (command.charAt(i) ^ key);
            key = buffer[i];
        }
        return buffer;
    }

    private String decrypt(byte[] buffer) throws IOException {

        int in;
        int key = 0xAB;
        int nextKey;
        StringBuilder sb = new StringBuilder();

        for(byte b: buffer){
            in = b;
            nextKey = in;
            in = in ^ key;
            key = nextKey;
            sb.append((char) in);
        }

        return "{" + sb.toString().substring(sb.toString().indexOf('"'));
    }

    public String toString(){
        String output = "";

        for(SmartBulb bulb : devices){
            output += bulb.ip + "; ";
        }

        return output;
    }

}
