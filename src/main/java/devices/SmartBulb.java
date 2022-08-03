package devices;

import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.Arrays;

import com.google.gson.*;


public class SmartBulb {
    
    protected int port;
    protected String ip;
    protected Socket socket;
    protected JsonObject jsonInfo;
    public String name;
    public String model;
    public boolean isOn;
    private int hue, saturation, brightness;
    private int colorTemp;

    protected static final String COMMAND_INFO = "{\"system\":{\"get_sysinfo\":{}}}";
    protected static final String COMMAND_GET_LIGHT_INFO = "{\"smartlife.iot.smartbulb.lightingservice\":{\"get_light_details\":\"\"}}";
    protected static String ON_COMMAND_INFO = "{\"smartlife.iot.smartbulb.lightingservice\":{\"transition_light_state\":{\"ignore_default\":1,\"on_off\":1,\"transition_period\":1000}}}";
    protected static String OFF_COMMAND_INFO = "{\"smartlife.iot.smartbulb.lightingservice\":{\"transition_light_state\":{\"ignore_default\":1,\"on_off\":0,\"transition_period\":1000}}}";

    /*
     * Creates a smart device object with port and ip address
     */
    public SmartBulb(String ip, Integer port, String jsonInfo) {
        this.ip = ip;
        this.port = (port == null) ? 9999: port;

        if(jsonInfo != null) {
            try{
                setInfo(jsonInfo);
            }catch  (Exception e){
                e.printStackTrace();
            }
        }else{
            initBulb(ip, this.port);
        }
    }

    /* Initialize bulb, get info on initialization*/
    private void initBulb(String ip, int port){
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //get info on construction
        try {
            this.getInfo();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Return a map containing plug system information
     *
     * @return Map of Information
     */
    public void getInfo() throws IOException {
        sendCommand(COMMAND_INFO);
    }

    public void setInfo(String jsonInfo) {
        if(jsonInfo == null) return;
        this.jsonInfo = JsonParser.parseString(jsonInfo).getAsJsonObject();
        updateBulbInfo();
    }

    /*
        {
           "system":{
              "get_sysinfo":{
                 "sw_ver":"1.0.12 Build 210329 Rel.141126",
                 "hw_ver":"2.0",
                 "model":"KL130(US)",
                 "deviceId":"80126EB10F1BDA4171DA9780F236C5341D61A3EC",
                 "oemId":"936CC35F9C27073112B5651D1B17EFD4",
                 "hwId":"1E97141B9F0E939BD8F9679F0B6167C8",
                 "rssi":-52,
                 "latitude_i":407169,
                 "longitude_i":-741953,
                 "alias":"bottom light",
                 "status":"new",
                 "description":"Smart Wi-Fi LED Bulb with Color Changing",
                 "mic_type":"IOT.SMARTBULB",
                 "mic_mac":"6032B120F5AF",
                 "dev_state":"normal",
                 "is_factory":false,
                 "disco_ver":"1.0",
                 "ctrl_protocols":{
                    "name":"Linkie",
                    "version":"1.0"
                 },
                 "active_mode":"none",
                 "is_dimmable":1,
                 "is_color":1,
                 "is_variable_color_temp":1,
                 "light_state":{
                    "on_off":0,
                    "dft_on_state":{
                       "mode":"normal",
                       "hue":0,
                       "saturation":0,
                       "color_temp":2700,
                       "brightness":5
                    }
                 },
                 "preferred_state":[
                    {
                       "index":0,
                       "hue":0,
                       "saturation":0,
                       "color_temp":2500,
                       "brightness":2
                    },
                    {
                       "index":1,
                       "hue":0,
                       "saturation":100,
                       "color_temp":0,
                       "brightness":100
                    },
                    {
                       "index":2,
                       "hue":120,
                       "saturation":100,
                       "color_temp":0,
                       "brightness":100
                    },
                    {
                       "index":3,
                       "hue":240,
                       "saturation":100,
                       "color_temp":0,
                       "brightness":100
                    }
                 ],
                 "err_code":0
              }
           }
        }
    */
    protected void updateBulbInfo(){
        if(jsonInfo != null){
            if(jsonInfo.has("system")){
                this.model = jsonInfo.getAsJsonObject("system").getAsJsonObject("get_sysinfo").get("model").getAsString();
                this.name = jsonInfo.getAsJsonObject("system").getAsJsonObject("get_sysinfo").get("alias").getAsString();
                JsonObject lightState = jsonInfo.getAsJsonObject("system").getAsJsonObject("get_sysinfo").getAsJsonObject("light_state");
                this.isOn = lightState.get("on_off").getAsInt() == 1;
                if(!isOn){
                    lightState = lightState.getAsJsonObject("dft_on_state");
                }
                this.saturation = lightState.get("hue").getAsInt();
                this.saturation = lightState.get("saturation").getAsInt();
                this.brightness = lightState.get("brightness").getAsInt();
                this.colorTemp = lightState.get("color_temp").getAsInt();

            }
            else if (jsonInfo.has("smartlife.iot.smartbulb.lightingservice")){
                JsonObject transitionLightState = jsonInfo.getAsJsonObject("smartlife.iot.smartbulb.lightingservice")
                        .getAsJsonObject("transition_light_state");
                this.isOn = transitionLightState.get("on_off").getAsInt() == 1;
                if(isOn){
                    this.hue = transitionLightState.get("hue").getAsInt();
                    this.saturation = transitionLightState.get("saturation").getAsInt();
                    this.brightness = transitionLightState.get("brightness").getAsInt();
                    this.colorTemp = transitionLightState.get("color_temp").getAsInt();
                }
            }
        }
    }

    public JsonObject getLightInfo() throws IOException {
        String jsonData = sendCommand(COMMAND_GET_LIGHT_INFO);
        return JsonParser.parseString(jsonData).getAsJsonObject();
    }

    public boolean isReachable(String addr, int port, int timeout) {
        try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress(addr, port), timeout);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public void toggleOnOff(){
        try{
            if(isOn) turnOff() ;
            else turnOn();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void turnOn() {
        sendCommand(ON_COMMAND_INFO);
    }

    public void turnOff() {
        sendCommand(OFF_COMMAND_INFO);
    }

    public void setHSB(Integer hue, Integer saturation, Integer value, Integer transition){
        try{
            String command = String.format("{\"smartlife.iot.smartbulb.lightingservice\":{\"transition_light_state\":" +
                            "{\"ignore_default\":1, \"on_off\":1,\"hue\":%s,\"saturation\":%s,\"brightness\":%s, \"color_temp\":0,\"transition_period\":%s}}}",
                    hue, saturation, value, (transition == null) ? 0 : transition
            );
            String response = sendCommand(command);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setRGB(Integer red, Integer green, Integer blue, Integer brightness, Integer transition){
        float[] hsb = new float[3];
        Color.RGBtoHSB(red, green, blue, hsb);
        int hue = (int) Math.ceil(hsb[0] * 360);
        int sat = (int) Math.ceil(hsb[1] * 100);
        int val = (brightness == null) ? (int) Math.ceil(hsb[1] * 100) : brightness;
        setHSB(hue, sat, val, transition);
    }

    public void setBrightness(int brightness, int transition){
        setHSB(this.hue , this.saturation, brightness, transition);
    }

    public Color getColor(){
        return Color.getHSBColor(this.hue/360f, this.saturation/100f, this.brightness/100f);
    }

    public int getBrightness(){ return brightness; }

    public String sendCommand(String command) {
        String response = BulbUtils.sendComamnd(command, this.ip, this.port);
        setInfo(response);
        return response;
    }

    public String toString(){
        return jsonInfo.toString();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public JsonObject getJsonInfo(){
        return this.jsonInfo;
    }
}
