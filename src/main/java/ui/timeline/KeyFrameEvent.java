package ui.timeline;

import devices.SmartBulb;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.awt.*;
import java.time.Duration;


public class KeyFrameEvent implements Comparable{
    public long eventStartTimeMillis;
    public long durationTimeMillis;
    protected Runnable keyframeAction;
    protected String name;
    protected SmartBulb bulb;

    public KeyFrameEvent(SmartBulb bulb, long eventStartTimeMillis) {
        this.eventStartTimeMillis = eventStartTimeMillis;
        this.durationTimeMillis = 0;
        this.name = this.getClass().getSimpleName();
        this.keyframeAction = null;
        this.bulb = bulb;
    }

    public void run(){

        try{
            Thread t = new Thread(keyframeAction);
            t.start();
            t.join();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    protected String getColorName(Color x){
        Color color = x;
        Color[] constantColors = new Color[] { Color.black, Color.blue, Color.cyan, Color.darkGray, Color.gray, Color.green, Color.lightGray, Color.magenta, Color.orange, Color.pink, Color.red, Color.white, Color.yellow };
        Color nearestColor = null;
        Integer nearestDistance = Integer.MAX_VALUE;

        for (Color constantColor : constantColors) {
            if (nearestDistance > Math.sqrt(
                    Math.pow(color.getRed() - constantColor.getRed(), 2)
                            - Math.pow(color.getGreen() - constantColor.getGreen(), 2)
                            - Math.pow(color.getBlue() - constantColor.getBlue(), 2)
            )
            ) {
                nearestColor = color;
            }
        }

        Color c = nearestColor;
        if(Color.white == c) return "white";
        if(Color.lightGray == c) return "lightGray";
        if(Color.gray == c) return "gray";
        if(Color.darkGray == c) return "darkGray";
        if(Color.black == c) return "black";
        if(Color.red == c) return "red";
        if(Color.pink == c) return "pink";
        if(Color.orange == c) return "orange";
        if(Color.yellow == c) return "yellow";
        if(Color.green == c) return "green";
        if(Color.magenta == c) return "magenta";
        if(Color.cyan == c) return "cyan";
        if(Color.blue == c) return "blue";
        return String.format("R:%s,G:%s,B:%s", c.getRed(), c.getGreen(), c.getBlue());
    }

    public static void sleep(long durationMillis){
        long startTimestamp = System.currentTimeMillis();
        while(System.currentTimeMillis() - startTimestamp < durationMillis){
            continue;
        }
    }

    @Override
    public int compareTo(Object o) {
        return (int)(this.eventStartTimeMillis - ((KeyFrameEvent)o).eventStartTimeMillis);
    }

    public void setValues(long eventStartTimeMillis, long durationTimeMillis){
        this.eventStartTimeMillis = eventStartTimeMillis;
        this.durationTimeMillis = durationTimeMillis;
    }
}

class StaticKeyFrameEvent extends KeyFrameEvent {
    public Color targetColor;
    public StaticKeyFrameEvent(SmartBulb bulb, long eventStartTimeMillis, long durationTimeMillis, Color targetColor, boolean lockBrightness){
        super(bulb, eventStartTimeMillis);
        this.durationTimeMillis  = durationTimeMillis;
        this.targetColor = targetColor;
        this.keyframeAction = new Runnable() {
            @Override
            public void run() {
                bulb.setColor(targetColor, (lockBrightness) ? bulb.getBrightness() : null, null);
                KeyFrameEvent.sleep(durationTimeMillis);
            }
        };
    }

    public String toString(){
        return String.format("%s-%s Start:%s End:%s Color:%s",
                this.name,
                bulb.name
                , eventStartTimeMillis
                , eventStartTimeMillis + durationTimeMillis
                , getColorName(targetColor)
        );
    }

    public void setValues(Color targetColor, long eventStartTimeMillis, long durationTimeMillis){
        super.setValues(eventStartTimeMillis, durationTimeMillis);
        this.targetColor = targetColor;
    }
}

class FadeKeyFrameEvent extends KeyFrameEvent {
    public Color startColor, targetColor;

    public FadeKeyFrameEvent(SmartBulb bulb, long eventStartTimeMillis, long durationTimeMillis, Color startColor, Color targetColor, boolean lockBrightness){
        super(bulb, eventStartTimeMillis);
        this.durationTimeMillis  = durationTimeMillis;
        this.targetColor = targetColor;
        this.startColor = startColor;
        this.keyframeAction = new Runnable() {
            @Override
            public void run() {
                if(startColor != null)
                    bulb.setColor(startColor, (lockBrightness) ? bulb.getBrightness() : null, null);
                bulb.setColor(targetColor, (lockBrightness) ? bulb.getBrightness() : null, (int) durationTimeMillis);
                KeyFrameEvent.sleep(durationTimeMillis);
            }
        };
    }

    public String toString(){
        return String.format("%s-%s Start:%s End:%s StartColor:%s EndColor:%s",
                this.name, bulb.name
                , eventStartTimeMillis
                , eventStartTimeMillis + durationTimeMillis
                , getColorName(startColor)
                , getColorName(targetColor)
        );
    }

    public void setValues(Color startColor, Color targetColor, long eventStartTimeMillis, long durationTimeMillis){
        super.setValues(eventStartTimeMillis, durationTimeMillis);
        this.startColor = startColor;
        this.targetColor = targetColor;
    }
}