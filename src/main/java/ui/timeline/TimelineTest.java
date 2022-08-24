package ui.timeline;

import devices.BulbManager;
import devices.SmartBulb;
import javafx.css.SimpleStyleableIntegerProperty;

import javax.swing.*;
import java.awt.*;
import java.sql.Array;
import java.util.ArrayList;

public class TimelineTest {

//    static BulbManager manager;
    public static void main(String[] args){
//        manager = new BulbManager(){{
//            initDevices();
//        }};

        ArrayList<SmartBulb> bulbs = new ArrayList<>();
        bulbs.add(new SmartBulb("192.168.0.209", null, null));

        JFrame mainFrame = new JFrame();
        mainFrame.setLayout(new BorderLayout());

        BulbAnimationPanel animationPanel = new BulbAnimationPanel(bulbs);
        mainFrame.add(animationPanel, BorderLayout.CENTER);


        mainFrame.setMinimumSize(new Dimension(400, 400));
        mainFrame.setTitle("Bulb Animation");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

}
