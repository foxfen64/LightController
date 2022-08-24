package ui;

import java.awt.*;

public class UITest {

    public static void main (String[] args){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainGUIFrame guiFrame = new MainGUIFrame();
            }
        });
    }

}
