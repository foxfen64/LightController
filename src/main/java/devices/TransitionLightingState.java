package devices;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class TransitionLightingState {
    private String lightService;
    private String lightMethod;
    private Boolean ignoreDefault = true;
    private Boolean onOff;
    private Integer transitionPeriod;
    private Integer brightness;
    private Integer hue;
    private Integer saturation;
    private Integer colorTemp;

    // used for turning lights on and off
    public TransitionLightingState(SmartBulb bulb, Boolean onOff, Integer brightness){
        initLightingState(bulb);
        this.onOff = onOff;
        this.brightness = brightness;
    }

    // used or changing light colors
    public TransitionLightingState(SmartBulb bulb, Integer hue, Integer saturation, Integer brightness, Integer transitionPeriod){
        initLightingState(bulb);
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.transitionPeriod = transitionPeriod;
    }

    private void initLightingState(SmartBulb bulb){
        if(bulb instanceof SmartLightStrip) {
            lightService = "smartlife.iot.lightStrip";
            lightMethod = "set_light_state";
        }else{
            lightService = "smartlife.iot.smartbulb.lightingservice";
            lightMethod = "transition_light_state";
        }
    }

    //convert transition state to string
    public String toString(){
        String jsonMessageString = "{\"" + lightMethod + "\":{";
        JsonObject output = new JsonObject();

        ArrayList<String> elements = new ArrayList<>();

        if(ignoreDefault != null) elements.add("\"ignore_default\":" + (ignoreDefault.booleanValue() ? "1" : "2"));
        if(onOff != null) elements.add("\"on_off\":" + (onOff.booleanValue() ? "1" : "2"));
        if(transitionPeriod != null) elements.add("\"transition_period\":" + transitionPeriod);
        if(brightness != null) elements.add("\"brightness\":" + brightness);
        if(hue != null) elements.add("\"hue\":" + hue);
        if(saturation != null) elements.add("\"saturation\":" + saturation);
        if(colorTemp != null) elements.add("\"color_temp\":" + colorTemp);

        jsonMessageString += String.join(",", elements) + "}}";
        output.add(lightService,JsonParser.parseString(jsonMessageString));

        System.out.println(output.toString());

        return output.toString();
    }
}
