package ui;

import devices.SmartBulb;
import javax.swing.*;
import java.util.ArrayList;

public class BulbList extends JList<ListElement>{
    ArrayList<SmartBulb> bulbs;

    public BulbList(ArrayList<SmartBulb> bulbs){
        this.bulbs = bulbs;
        for(SmartBulb bulb : bulbs){
            this.add(new ListElement(bulb));
        }
    }
}


