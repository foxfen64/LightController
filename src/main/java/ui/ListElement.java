package ui;

import devices.SmartBulb;
import devices.SmartLightStrip;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ListElement extends JPanel implements ActionListener, ChangeListener {
    SmartBulb bulb;
    JLabel iconLbl, nameLbl;
    BufferedImage bulbImg;
    Dimension imgDimension = new Dimension(50, 50);
    Dimension buttonDimension = new Dimension(100, 50);
    JButton onOffBtn, colorBtn;
    JSlider brightnessSlider;
    private Color bulbColor;
    private long pollInfoRateMillis = 2 * 1000;
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    private static final Color offColor = Color.GRAY;
    private static final Color onColor = Color.GREEN;

    public ListElement(SmartBulb bulb) {
        try {
            initBulbUpdateSchedule();

            brightnessSlider = new JSlider();
            iconLbl = new JLabel();
            nameLbl = new JLabel("No Model: No Name");
            onOffBtn = new JButton("On/Off");
            colorBtn = new JButton("Color");

            iconLbl.setPreferredSize(imgDimension);
            iconLbl.setMinimumSize(imgDimension);
            onOffBtn.setPreferredSize(buttonDimension);
            colorBtn.setPreferredSize(buttonDimension);

            onOffBtn.addActionListener(this);
            colorBtn.addActionListener(this);
            brightnessSlider.addChangeListener(this);
            brightnessSlider.setMaximum(100);
            brightnessSlider.setSnapToTicks(true);
            brightnessSlider.setPaintTicks(true);
            brightnessSlider.setMinorTickSpacing(10);
//          onOffImg = resizeImage(onOffImg, imgDimension.width, imgDimension.height);

            this.setBulb(bulb);
            brightnessSlider.setValue(bulb.getBrightness());

            this.setLayout(new FlowLayout(FlowLayout.TRAILING));
            this.add(iconLbl);
            this.add(nameLbl);
            this.add(onOffBtn);
            this.add(colorBtn);
            this.add(brightnessSlider, FlowLayout.RIGHT);

            updateElement();
            iconLbl.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = originalImage;
        try {
            resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
            graphics2D.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resizedImage;
    }

    private void setBulb(SmartBulb bulb) {
        try {
            String labelText = (bulb == null) ? "No Model: No Name" : bulb.model + ": " + bulb.name;
            this.bulb = bulb;

            if (bulb == null) {
                bulbImg = ImageIO.read(new File("src/main/resources/icons/placeholder.png"));
            } else if (bulb instanceof SmartLightStrip) {
                bulbImg = ImageIO.read(new File("src/main/resources/icons/lightStrip.png"));
            } else {
                bulbImg = ImageIO.read(new File("src/main/resources/icons/bulb.png"));
            }

            iconLbl.setIcon(new ImageIcon(resizeImage(bulbImg, imgDimension.width, imgDimension.height)));
            nameLbl.setText(labelText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateElement(){
        if(bulb == null) return;

        onOffBtn.setBackground((bulb.isOn) ? onColor : offColor);
        bulbColor = bulb.getColor();
        float[] hsl = Color.RGBtoHSB(bulbColor.getRed(), bulbColor.getGreen(), bulbColor.getBlue(), null);
        Color brighterColor = Color.getHSBColor(hsl[0], hsl[1], Math.min(hsl[2] + .5f, 1));
        colorBtn.setBackground((bulb.isOn) ? brighterColor : offColor);
        this.repaint();
    }

    public void initBulbUpdateSchedule() {
        final Runnable beeper = () -> {
            try {
                bulb.getInfo();
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateElement();
        };

        scheduler.scheduleAtFixedRate(beeper, 1, pollInfoRateMillis, TimeUnit.MILLISECONDS);
    }

    public void turnOn() {
        bulb.turnOn();
        updateElement();
    }
    public void turnOff() {
        bulb.turnOff();
        updateElement();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == onOffBtn) {
            this.bulb.toggleOnOff();
        }
        else if (e.getSource() == colorBtn){
            JColorChooser chooser = new JColorChooser();
            chooser.setVisible(true);
            Color selectedColor = JColorChooser.showDialog(this, "Pick color for : " + bulb.name, bulbColor);
            bulbColor = (selectedColor == null) ? bulbColor : selectedColor;
            bulb.setRGB(bulbColor.getRed(), bulbColor.getGreen(), bulbColor.getBlue(), bulb.getBrightness(), null);
        }

        updateElement();
    }


    @Override
    public void stateChanged(ChangeEvent e) {
        bulb.setBrightness(brightnessSlider.getValue(), 0);
        updateElement();
    }
}