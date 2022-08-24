package ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class ImageGeneratorUtils {

    public static Color[] findColors(BufferedImage image, int numColors, int algorithm){
        Color[] output = null;

        try{
            if (algorithm == 0) { // sorted
                output = findColorsCount(image, numColors);
            }else if (algorithm == 1){ // kmeans
                output = findColorsKmeans(image, numColors);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return output;
    }

    public static Color[] findColorsCount(BufferedImage image, int numColors){
        Color[] output = new Color[numColors];

        HashMap<String, ColorCount> colorCountMap = new HashMap<>(); // color to count map
        for(int i = 0; i < image.getWidth(); i++){
            for(int j = 0; j < image.getHeight(); j++){
                int curRGB = image.getRGB(i, j);
                ColorCount curCount = colorCountMap.get(new Color(curRGB).toString());
                if(curCount != null){
                    curCount.count++;
                }else {
                    colorCountMap.put(new Color(curRGB).toString(), new ColorCount(curRGB));
                }
            }
        }

        ColorCount[] tmp = colorCountMap.values().toArray(new ColorCount[0]);
        Arrays.sort(tmp);

        for(int i = 0; i < numColors; i++){
            output[i] = tmp[i].color;
        }

        return output;
    }

    //todo: add functionality
    public static Color[] findColorsKmeans(BufferedImage image, int numColors){
        Color[] output = new Color[numColors];
        int numBinPerAxis = 2, totalBins = (int)Math.pow(numBinPerAxis, 3);
        ColorCluster[][][] clusters = new ColorCluster[numBinPerAxis][numBinPerAxis][numBinPerAxis];

        for(int i = 0; i < image.getWidth(); i++){
            for(int j = 0; j < image.getHeight(); j++){
                Color c = new Color(image.getRGB(i, j));
                int rIndex = (int) (c.getRed() / (256.0/numBinPerAxis));
                int gIndex = (int) (c.getGreen() / (256.0/numBinPerAxis));
                int bIndex = (int) (c.getBlue() / (256.0/numBinPerAxis));

                ColorCluster curCluster = clusters[rIndex][gIndex][bIndex];
                if( curCluster == null){
                    clusters[rIndex][gIndex][bIndex] = new ColorCluster(c.getRed(), c.getGreen(), c.getBlue());
                }else {
                    curCluster.add(c.getRed(), c.getGreen(), c.getBlue());
                }
            }
        }

        ArrayList<ColorCluster> flatClusters = new ArrayList<>();
        int l = 0;
        for(int i = 0; i < numBinPerAxis; i++){
            for(int j = 0; j < numBinPerAxis; j++){
                for(int k = 0; k < numBinPerAxis; k++){
                    if(clusters[i][j][k] == null) continue;
                    flatClusters.add(clusters[i][j][k]);
                }
            }
        }

        Collections.sort(flatClusters);

        for(int i = 0; i < Math.min(flatClusters.size(), output.length); i++){
            output[i] = flatClusters.get(i).toColor();
        }

        return output;
    }

    public static class ColorCluster implements Comparable{
        float r, g, b;
        int count;
        public ColorCluster(int r, int g, int b){
            this.r = r;
            this.g = g;
            this.b = b;
            this.count = 1;
        }

        public Color toColor(){
            int r = (int) (this.r/count);
            int g = (int) (this.g/count);
            int b = (int) (this.b/count);

            return new Color(r, g, b);
        }

        public void add(int r, int g, int b){
            this.r += r;
            this.g += g;
            this.b += b;
            this.count++;
        }

        @Override
        public int compareTo(Object o) {
            return ((ColorCluster)o).count - this.count;
        }
    }

    public static class ColorCount implements Comparable{
        Color color; int count;
        public ColorCount(int rgbColor){
            this.color = new Color(rgbColor);
            this.count = 0;
        }

        @Override
        public int compareTo(Object o) {
            return ((ColorCount)o).count - this.count;
        }

        @Override
        public String toString() {
            return "ColorCount{" +
                    "color=" + color +
                    ", count=" + count +
                    '}';
        }
    }

}
