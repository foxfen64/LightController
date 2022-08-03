package ui;

import devices.BulbManager;
import devices.SmartBulb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class UITest {
    static BulbManager manager;
    static ArrayList<ListElement> listElements = new ArrayList<>();
    public static void main (String[] args){
        manager = new BulbManager(){{
           initDevices();
        }};

        for(SmartBulb bulb : manager.devices){
            System.out.println(bulb.name);
        }

        JFrame mainFrame = new JFrame();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        panel.setSize(new Dimension(500, 500));
        for(SmartBulb bulb : manager.devices){
            ListElement x = new ListElement(bulb);
            listElements.add(x);
            panel.add(x);
        }

        JButton toggleAllBtn = new JButton("Toggle All Off");
        toggleAllBtn.setSize(new Dimension(100, 50));
        toggleAllBtn.addActionListener(new ActionManager());
        panel.add(toggleAllBtn);

        PaletteGeneratorPanel generatorPnl = new PaletteGeneratorPanel(manager.devices);
        panel.add(generatorPnl);

        mainFrame.add(panel);
        mainFrame.pack();
        mainFrame.repaint();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    public static class ActionManager implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            int onCount = 0;
            for(SmartBulb b : manager.devices){
                onCount += (b.isOn) ? 1 : 0;
            }

            if(listElements == null) return;

            for(ListElement element : listElements){
                if ((onCount == 0)) {
                    element.turnOn();
                } else {
                    element.turnOff();
                }
            }
        }
    }

}
