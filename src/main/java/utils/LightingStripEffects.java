package utils;

import devices.SmartLightStrip;
import devices.LightStripNode;

import java.io.IOException;

public class LightingStripEffects {

    // rainbow effect for the entire strip, each node will be the same color
    public static void colorWaveA(SmartLightStrip strip, int brightness, int transTime) throws Exception {

        int curHue = 0;
        for(int i = 0;; i++){
            Thread.sleep(transTime);
            curHue += 10;
            curHue = curHue%360;
            strip.setHSB(curHue, 100, brightness, 0);
        }
    }

    // rainbow effect for the entire strip, each node will be the same color
    public static void colorWaveB(SmartLightStrip strip, int brightness, int transTime) throws Exception{
        SmartLightStrip.ColorGroup[] nodes = new SmartLightStrip.ColorGroup[20];
        //initialize array
        for(int i = 0; i < nodes.length; i++){
            nodes[i] = new SmartLightStrip.ColorGroup(i, i, 0, 100, 5, 0);
        }

        int curHueBase = 0;
        for(int i = 0;; i++){
            Thread.sleep(transTime);
            curHueBase += 10;
            curHueBase = curHueBase%360;
            for(int j = 0; j < nodes.length; j++){
                nodes[j].hue = ((j * 360/nodes.length) + curHueBase) % 360;
                nodes[j].brightness = brightness;
            }
            strip.setHSB(nodes, 0);
        }

    }

}
