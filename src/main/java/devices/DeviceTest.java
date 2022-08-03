package devices;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.net.*;
import java.util.Scanner;

public class DeviceTest {

    public static void main(String[] args) throws Exception{
//        SmartLightStrip bulb = new SmartLightStrip("192.168.0.100", null, null);
////        SmartLightStrip bulb = new SmartLightStrip("192.168.0.112", null, null);
//        Color[] colors = {
//            Color.red, Color.green, Color.blue, Color.magenta, Color.cyan
//        };
//        SmartLightStrip.ColorGroup[] groups = new SmartLightStrip.ColorGroup[bulb.getLength()];
//        int stripLength = bulb.getLength();
//        int subsetLength = colors.length;
//        int brightness = 10;
//
////        bulb.setRGB(Color.red.getRed(), Color.red.getBlue(), Color.red.getGreen(), 10, 10);
//
//        for(int i = 0; i < groups.length; i++){
//            float[] hsb = Color.RGBtoHSB(colors[i%colors.length].getRed(), colors[i%colors.length].getBlue(), colors[i%colors.length].getGreen(), null);
//            hsb[0] = hsb[0] * 360;
//            hsb[1] = hsb[1] * 100;
//            hsb[2] = hsb[2] * 100;
//            groups[i] = new SmartLightStrip.ColorGroup(i, i, (int)hsb[0], (int)hsb[1], brightness, 0);
//        }
//
//        bulb.setHSB(groups, 0);

//        Document doc = Jsoup.connect("https://en.wikipedia.org/").get();

        Color[] colors = generatePalette();

    }


    public static Color[] generatePalette() {
        int colorCount = 5;
        int targetR = (int)Math.ceil(Math.random() * 255);
        int targetG = (int)Math.ceil(Math.random() * 255);
        int targetB = (int)Math.ceil(Math.random() * 255);
//        int targetSat = (int)Math.ceil(Math.random() * 20) + 80;
//        int targetVal = (int)Math.ceil(Math.random() * 50) + 50;
        Color targetColor = new Color(targetR,targetG,targetB);
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
        int targetColorMode = (int)Math.floor(Math.random() * colorModes.length) ;
        Color[] colors = new Color[colorCount];

        String urlString = String.format("https://www.thecolorapi.com/scheme?rgb=%s,%s,%s&mode=%s&length=%s&named=false",
                targetR, targetG, targetB, colorModes[targetColorMode], colorCount
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
            colors[i++] = new Color(r,g,b);
        }

        return colors;
    }
}


