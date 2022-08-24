package ui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import devices.SmartBulb;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class PaletteGeneratorPanel extends JPanel implements ActionListener {

    ArrayList<SmartBulb> bulbs;
    JButton generatePaletteBtn;
    JButton retrySyncBtn;
    JButton colorSeedBtn;
    Color colorSeed;
    JComboBox<String> colorModeList;
    Color[] colors;

    String[] colorModes = {
            "monochrome",
            "monochrome-dark",
            "monochrome-light",
            "analogic",
            "complement",
            "analogic-complement",
            "triad",
            "quad"
    };

    public PaletteGeneratorPanel(ArrayList<SmartBulb> bulbs){
        this.bulbs = bulbs;
        generatePaletteBtn = new JButton("Generate Palette");
        retrySyncBtn = new JButton("Retry Sync");
        colorModeList = new JComboBox<>(colorModes);
        colorModeList.addItem("random");
        colorSeedBtn = new JButton("Seed..");

        generatePaletteBtn.addActionListener(this);
        retrySyncBtn.addActionListener(this);
        colorSeedBtn.addActionListener(this);

        this.add(colorModeList);
        this.add(generatePaletteBtn);
        this.add(retrySyncBtn);
        this.add(colorSeedBtn);
        this.setVisible(true);
        this.repaint();
    }

    public Color[] generatePalette(int colorCount, String colorMode) {
        int targetR, targetG, targetB;
        if(colorSeed == null){
            targetR = (int)Math.ceil(Math.random() * 255);
            targetG = (int)Math.ceil(Math.random() * 255);
            targetB = (int)Math.ceil(Math.random() * 255);
        }else{
            targetR = colorSeed.getRed();
            targetG = colorSeed.getGreen();
            targetB = colorSeed.getBlue();
        }

        Color targetColor = new Color(targetR,targetG,targetB); // for debugging purposes

        String targetColorMode = (colorMode == null || colorMode.equals("random"))
                ? colorModes[(int)Math.floor(Math.random() * colorModes.length)]
                : colorMode;
        Color[] colors = new Color[colorCount];

        String urlString = String.format("https://www.thecolorapi.com/scheme?rgb=%s,%s,%s&mode=%s&length=%s&named=false",
                targetR, targetG, targetB, targetColorMode, colorCount
        );
        JsonObject responseJson = null;

        try{
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.connect();
            try(Scanner scanner = new Scanner(connection.getInputStream())){
                responseJson = JsonParser.parseString(scanner.nextLine()).getAsJsonObject();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        if(responseJson == null) return null;

        int i = 0;
        for(JsonElement colorJson : responseJson.getAsJsonArray("colors").getAsJsonArray()){
            int r,g,b;
            r = colorJson.getAsJsonObject().getAsJsonObject("rgb").get("r").getAsInt();
            g = colorJson.getAsJsonObject().getAsJsonObject("rgb").get("g").getAsInt();
            b = colorJson.getAsJsonObject().getAsJsonObject("rgb").get("b").getAsInt();
            colors[i] = new Color(r,g,b);
            i++;
        }

        return colors;
    }

    private void updateButtonColors(Color[] colors){
        this.setIgnoreRepaint(true);
        Graphics g = this.getGraphics();
        int width = this.getWidth();
        int height = this.getHeight();
        for(int i = colors.length-1; i > 0; i--){
            Color curColor = colors[i];
            g.setColor(curColor);
            g.fillRect(0, 0, (i+1) * width/colors.length , height);
        }

        generatePaletteBtn.repaint();
        retrySyncBtn.repaint();
        colorModeList.repaint();
        colorSeedBtn.repaint();
    }

    private void syncColors(Color[] colors){
        if(colors == null) return;

        Arrays.sort(colors, new Comparator<Color>() {
            @Override
            public int compare(Color o1, Color o2) {
                return (int)((Math.random() -.5 ) * 100);
            }
        });

        for(int i = 0; i < bulbs.size(); i++){
            SmartBulb bulb = bulbs.get(i);
            bulb.setRGB(colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue(), bulb.getBrightness(), null);
        }
        updateButtonColors(colors);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == generatePaletteBtn){
            Color[] colors = generatePalette(bulbs.size(), (String)colorModeList.getSelectedItem());
            this.colors = colors;
            syncColors(colors);
        }
        else if (e.getSource() == retrySyncBtn){
            syncColors(this.colors);
        }
        else if(e.getSource() == colorSeedBtn){
            this.colorSeed = JColorChooser.showDialog(this, "choose color", null);
            colorSeedBtn.setBackground(colorSeed);
        }
    }
}
