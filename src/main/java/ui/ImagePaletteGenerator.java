package ui;

import devices.SmartBulb;

import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class ImagePaletteGenerator extends JPanel implements ActionListener {

    JButton generateButton, resyncButton;
    JTextField urlTextField;
    JComboBox<String> algorithmComboBox;
    JLabel imageLabel;
    ArrayList<SmartBulb> bulbs;
    Color[] colors;
    private final int transitionSpeed = 500;

    public ImagePaletteGenerator(ArrayList<SmartBulb> bulbs){
        String[] algorithms = {
                "Sorted", "K-Means"
        };
        this.bulbs = bulbs;
        this.colors = new Color[bulbs.size()];
        this.algorithmComboBox = new JComboBox<>(algorithms);
        this.generateButton = new JButton("Generate...");
        this.resyncButton = new JButton("Resync...");
        this.imageLabel = new JLabel();
        this.urlTextField = new JTextField(10);
        this.urlTextField.setText("https://memorynotfound.com/wp-content/uploads/java-duke.png");

        this.algorithmComboBox.setSelectedIndex(1);
        this.generateButton.addActionListener(this);
        this.resyncButton.addActionListener(this);

        this.setLayout(new GridLayout(1, 5));

        this.add(imageLabel);
        this.add(algorithmComboBox);
        this.add(urlTextField);
        this.add(generateButton);
        this.add(resyncButton);
        this.setTransferHandler(new ImageTransferHandler());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == generateButton){
            updateColors(urlTextField.getText());
        }else if (e.getSource() == resyncButton){
            System.out.println("Resyncing...");
            syncColors(true);
            System.out.println("Resync done.");
        }
    }

    //fixme: find a way to use temp folder instead of getting from url for drag and drop images
    public void updateColors(String url){
        Map<String,String> queryMap = splitQuery(url);
        if(queryMap!= null && queryMap.get("imgurl") != null){
            url = queryMap.get("imgurl");
            System.out.println("getting image from google images..." + url);
        }
        BufferedImage bufferedImage = setImage(url);
        this.colors = ImageGeneratorUtils.findColors(bufferedImage,  bulbs.size(), algorithmComboBox.getSelectedIndex());
        syncColors(false);
    }

    public static Map<String, String> splitQuery(String urlString) {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        try {
            URL url = new URL(urlString);
            String query = url.getQuery();

            if(query == null) return null;

            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return query_pairs;
    }

    public BufferedImage setImage(String url){
        BufferedImage image = null;

        try{
            URL urlInput = new URL(url);
            image = ImageIO.read(urlInput);
            imageLabel.setIcon(new ImageIcon(image.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
            imageLabel.setMinimumSize(new Dimension(200, 200));
            imageLabel.validate();
            imageLabel.repaint();
            this.revalidate();

        }catch (Exception e){
            e.printStackTrace();
        }

        return image;
    }

    private void syncColors(boolean syncRandom){
        Color[] tmp = Arrays.copyOf(this.colors, this.colors.length);
        if(syncRandom) {
            Arrays.sort(tmp, new Comparator<Color>() {
                @Override
                public int compare(Color o1, Color o2) {
                    return ((int) (Math.random() * 50)) - 25;
                }
            });
        }

        System.out.println(Arrays.toString(tmp));

        int i= 0;
        for (SmartBulb b: bulbs) {
            if(tmp[i] != null){
                b.setColor(tmp[i++], b.getBrightness(), transitionSpeed);
            }
        }
    }

    class ImageTransferHandler extends TransferHandler{

        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY_OR_MOVE;
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            if (!support.isDrop()) {
                return false;
            }
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (!this.canImport(support)) {
                return false;
            }
            Transferable t = support.getTransferable();

            String data = null;
            try {
                data = (String) t.getTransferData(DataFlavor.stringFlavor);
                if (data == null) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            System.out.println("Importing from URL...: " + data);
            updateColors(data);

            return true;
        }
    }
}