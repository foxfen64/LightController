package devices;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;

public class BulbUtils {

    public static final Integer SMART_BULB = 1;
    public static final Integer SMART_STRIP = 2;

    public static HashMap<String, Integer> bulbTypeMap = new HashMap<>() {{
        put("KL130", SMART_BULB);
        put("KL430", SMART_STRIP);
        put("KL420L5", SMART_STRIP);
    }};

    public static Integer getBulbTypeFromModel(String model){
        for(String key : bulbTypeMap.keySet()){
            Integer type = bulbTypeMap.get(key);

            if(model.contains(key)) return type;
        }
        return null;
    }

    public static String sendComamnd(String message, String host, int port){
        String output = null;
        try{
            DatagramSocket dgram = new DatagramSocket();

            byte[] sendPayload = encrypt(message);
            byte[] recievePayload = new byte[50000];

            InetAddress inetAddress = InetAddress.getByName(host);
            DatagramPacket dgramPacketSend = new DatagramPacket(sendPayload, sendPayload.length, inetAddress, port);
            DatagramPacket dgramPacketRecieve = new DatagramPacket(recievePayload, recievePayload.length, inetAddress, port);

            dgram.send(dgramPacketSend);

            while(true) {
                dgram.setSoTimeout(2000);
                try {
                    dgram.receive(dgramPacketRecieve);
                    dgram.setSoTimeout(0);
                    return decrypt(Arrays.copyOfRange(recievePayload, 0, dgramPacketRecieve.getLength()));
                }catch(SocketTimeoutException e) {
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return output;
    }

    private static byte[] encrypt(String command) {

        byte[] buffer = new byte[command.length()];
        int key = 0xAB;
        for(int i = 0; i < command.length(); i++) {
            buffer[i] = (byte) (command.charAt(i) ^ key);
            key = buffer[i];
        }
        return buffer;
    }

    private static String decrypt(byte[] buffer) throws IOException {

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
}
