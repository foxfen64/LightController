package ui;

import devices.BulbManager;
import devices.SmartBulb;
import devices.SmartLightStrip;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BulbListPanel extends JPanel{
    private BulbManager manager;
    private ArrayList<ListElement> listElements = new ArrayList<>();
    private ArrayList<SmartBulb> bulbArray;

    public BulbListPanel(ArrayList<SmartBulb> bulbArray){
        this.bulbArray = bulbArray;

        for(SmartBulb bulb : bulbArray){
            System.out.println(bulb.name);
        }

        this.setLayout(new GridLayout(0, 1));

        this.setSize(new Dimension(500, 500));
        for(SmartBulb bulb : bulbArray){
            ListElement x = new ListElement(bulb);
            listElements.add(x);
            this.add(x);
        }

        JButton toggleAllBtn = new JButton("Toggle All Off");
        toggleAllBtn.setSize(new Dimension(100, 50));
        toggleAllBtn.addActionListener(new ActionManager());
        this.add(toggleAllBtn);

        PaletteGeneratorPanel generatorPnl = new PaletteGeneratorPanel(bulbArray);
        generatorPnl.setBorder(new TitledBorder("Palette Generator"));
        this.add(generatorPnl);

        AddBulbPanel addBulbPanel = new AddBulbPanel();
        addBulbPanel.setBorder(new TitledBorder("Add Bulb by IP"));
        this.add(addBulbPanel);

        ImagePaletteGenerator generator = new ImagePaletteGenerator(bulbArray);
        generator.setBorder(new TitledBorder("Image Palette Generator"));
        this.add(generator);
    }

    public void addBulb(String ip){
        SmartBulb bulb = new SmartBulb(ip, null, null);
        ListElement x = new ListElement(bulb);
        listElements.add(x);
        this.add(x);

        this.revalidate();
    }

    public class ActionManager implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int onCount = 0;
            for(SmartBulb b : bulbArray){
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

    public class AddBulbPanel extends JPanel implements ActionListener{
        JButton addBulbButton;
        JTextField ipAddressTextField;

        public AddBulbPanel(){
            addBulbButton = new JButton("Add Bulb");
            ipAddressTextField = new JTextField("Add a bulb ip address");

            addBulbButton.addActionListener(this);
            this.add(addBulbButton);
            this.add(ipAddressTextField);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == addBulbButton){
                addBulb(ipAddressTextField.getText());
            }
        }
    }

}


