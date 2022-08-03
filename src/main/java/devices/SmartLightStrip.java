package devices;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.awt.*;
import java.util.Arrays;


public class SmartLightStrip extends SmartBulb{
    private int length;
    public final String COMMAND_LIGHTING_SERVICE = "smartlife.iot.lightstrip.lightingservice";
    public ColorGroup[] colorGroups;
    private boolean hasInitColorGroup = false; // since color group doesnt come through get info service, only set once.

    // Smart Strip constructor with port and ip
    public SmartLightStrip(String ip, Integer port, String jsonInfo) {
        super(ip, port, jsonInfo);
    }

    @Override
    protected void updateBulbInfo(){
        if(jsonInfo != null){
            if(jsonInfo.has("system")){
                if(jsonInfo.getAsJsonObject("system").has("err_code")) return;
                this.model = jsonInfo.getAsJsonObject("system").getAsJsonObject("get_sysinfo").get("model").getAsString();
                this.name = jsonInfo.getAsJsonObject("system").getAsJsonObject("get_sysinfo").get("alias").getAsString();
                length = jsonInfo.getAsJsonObject("system").getAsJsonObject("get_sysinfo").get("length").getAsInt();
                JsonObject lightState = jsonInfo.getAsJsonObject("system").getAsJsonObject("get_sysinfo").getAsJsonObject("light_state");
                this.isOn = lightState.get("on_off").getAsInt() == 1;
                if(!isOn){
                    lightState = lightState.getAsJsonObject("dft_on_state");
                }
                if(!hasInitColorGroup) {
                    int hue = lightState.get("hue").getAsInt();
                    int saturation = lightState.get("saturation").getAsInt();
                    int brightness = lightState.get("brightness").getAsInt();
                    int colorTemp = lightState.get("color_temp").getAsInt();
                    colorGroups = new ColorGroup[]{
                            new ColorGroup(0, length - 1, hue, saturation, brightness, colorTemp)
                    };
                    hasInitColorGroup = true;
                }
            }
            else if (jsonInfo.has("smartlife.iot.smartbulb.lightingservice")){
                if(jsonInfo.getAsJsonObject("smartlife.iot.smartbulb.lightingservice").has("err_code")) return;
                this.isOn = jsonInfo.getAsJsonObject("smartlife.iot.smartbulb.lightingservice")
                        .getAsJsonObject("transition_light_state")
                        .get("on_off").getAsInt() == 1;
            }
            else if (jsonInfo.has("smartlife.iot.lightStrip")){
                JsonObject lightState = jsonInfo.getAsJsonObject("smartlife.iot.lightStrip")
                        .getAsJsonObject("set_light_state");
                this.isOn = lightState.get("on_off").getAsInt() == 1;
                if(!isOn){
                    lightState = lightState.getAsJsonObject("dft_on_state");
                }
                //parse groups array
                JsonArray groupsJsonArray = lightState.getAsJsonArray("groups");
                this.colorGroups = new ColorGroup[groupsJsonArray.size()];
                for(int i = 0; i < groupsJsonArray.size(); i++){
                    colorGroups[i] = new ColorGroup(groupsJsonArray.get(i).toString());
                }
            }
        }
    }

    @Override
    public void turnOn()  {
        String command = "{\"smartlife.iot.lightStrip\":{\"set_light_state\":{\"ignore_default\":1,\"on_off\":1}}}";
        super.sendCommand(command);
    }

    @Override
    public void turnOff()  {
        String command = "{\"smartlife.iot.lightStrip\":{\"set_light_state\":{\"ignore_default\":1,\"on_off\":0}}}";
        super.sendCommand(command);
    }

    // Set entire light the same color
    // TODO: get able to update transition
    @Override
    public void setHSB(Integer hue, Integer saturation, Integer value, Integer transition){
        ColorGroup[] groups = {
                new ColorGroup(0, this.length-1, hue, saturation, value, 0)
        };
        setHSB(groups, (transition == null) ? 0 : transition);
    }

    // Set individual elements of the light a certain color
    // FIXME: lightstrip does not transition
    // FIXME: can set at most 16 color groups in one call
    public void setHSB(ColorGroup[] colorGroups, int transition){
        int batchSize = length/5;
        if(!isOn) turnOn();
        for(int i = 0; i < ((colorGroups.length-1)/batchSize) + 1; i++){
            ColorGroup[] tmpGroups = Arrays.copyOfRange(colorGroups, i*batchSize, Math.min(((i+1) * batchSize), colorGroups.length));
            String command = String.format("{\"smartlife.iot.lightStrip\":{\"set_light_state\":{\"on_off\":1, \"ignore_default\":1, \"groups\":%s}}}", Arrays.toString(tmpGroups));
            sendCommand(command);
        }
    }

    @Override
    public void setBrightness(int brightness, int transition ){
        ColorGroup[] tmpGroups = new ColorGroup[this.colorGroups.length];

        for(int i = 0; i < tmpGroups.length; i++){
            ColorGroup curGroup = colorGroups[i];
            tmpGroups[i] = new ColorGroup(curGroup.startIndex, curGroup.endIndex, curGroup.hue, curGroup.saturation, brightness, curGroup.colorTemp);
        }

        setHSB(tmpGroups, transition);
    }

    @Override
    public int getBrightness(){
        return colorGroups[0].brightness;
    }

    @Override
    public Color getColor(){
        int hue, saturation, brightness;
        ColorGroup firstColor = colorGroups[0];
        hue = firstColor.hue;
        saturation = firstColor.saturation;
        brightness = firstColor.brightness;
        return Color.getHSBColor(hue/360f, saturation/100f, brightness/100f);
    }

    public int getLength(){
        return length;
    }

    public static class ColorGroup{
        public int startIndex, endIndex;
        public int hue, saturation, brightness;
        public int colorTemp;

        public ColorGroup(int startIndex, int endIndex, int hue, int saturation, int value, int colorTemp){
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.hue = hue;
            this.saturation = saturation;
            this.brightness = value;
            this.colorTemp = colorTemp;
        }
        /*
            [0,49,48,100,10,200]
            > [start, end, hue, sat, bright, color_temp]
        */
        public ColorGroup(String groupText){
            String vals[] = groupText.substring(1, groupText.length()-1).split(",");
            this.startIndex = Integer.parseInt(vals[0]);
            this.endIndex = Integer.parseInt(vals[1]);
            this.hue = Integer.parseInt(vals[2]);
            this.saturation = Integer.parseInt(vals[3]);
            this.brightness = Integer.parseInt(vals[4]);
            this.colorTemp = Integer.parseInt(vals[5]);
        }

        public String toString(){
            int[] output = {
                    startIndex, endIndex, hue, saturation, brightness, colorTemp
            };
            return Arrays.toString(output);
        }

    }
}
