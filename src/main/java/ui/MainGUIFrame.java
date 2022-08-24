package ui;

import devices.SmartBulb;
import devices.SmartLightStrip;
import ui.timeline.BulbAnimationPanel;

import javax.swing.*;
import java.util.ArrayList;

public class MainGUIFrame extends JFrame {

    ArrayList<SmartBulb> bulbArray;
    public MainGUIFrame(){
        bulbArray = new ArrayList<>();
        bulbArray.add(new SmartLightStrip("192.168.0.196", null, null)); //front light strip
        bulbArray.add(new SmartLightStrip("192.168.0.112", null, null)); // back light strip
        bulbArray.add(new SmartBulb("192.168.0.206", null, null)); // orb lamp
        bulbArray.add(new SmartBulb("192.168.0.182", null, null)); // top lamp
        bulbArray.add(new SmartBulb("192.168.0.209", null, null)); // bottom light

        JTabbedPane tabbedPane = new JTabbedPane();
        BulbListPanel bulbListPanel = new BulbListPanel(bulbArray);
        tabbedPane.add("Bulb List", bulbListPanel);

        BulbAnimationPanel animationPanel = new BulbAnimationPanel(bulbArray);
        tabbedPane.add("Animator", animationPanel);

        this.add(tabbedPane);
        this.setLocationRelativeTo(null);
        this.pack();
        this.repaint();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

}
