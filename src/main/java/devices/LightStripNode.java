package devices;

import java.io.ObjectInputStream;
import java.time.temporal.ValueRange;

public class LightStripNode {
    public int hue, saturation, brightness;
    public int startIndex, endIndex;

    public LightStripNode(int startIndex, int endIndex, int hue, int saturation, int brightness){
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public String toString(){
        return String.format("[%s, %s, %s, %s, %s, %s]", startIndex, endIndex, hue, saturation, brightness, 0);
    }
}
