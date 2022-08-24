package ui.timeline;
import devices.SmartBulb;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.NumberFormatter;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;

public class BulbAnimationPanel extends JPanel{
    private final String notImplementedMessage = "not implemented";

    ArrayList<SmartBulb> bulbs;

    HashMap<SmartBulb, TimelinePanel> timelinePanelMap;
    AnimationSettingsPanel animationSettingsPanel;
    JPanel mainTimelinePanel;
    CardLayout cardLayout;
    BulbListPanel bulbListPanel;

    public BulbAnimationPanel(ArrayList<SmartBulb> bulbs){
        this.bulbs = bulbs;
        timelinePanelMap = new HashMap<>();
        bulbListPanel = new BulbListPanel();
        mainTimelinePanel = new JPanel();
        cardLayout = new CardLayout();

        mainTimelinePanel.setLayout(cardLayout);
        for(SmartBulb bulb : bulbs){
            TimelinePanel t = new TimelinePanel(bulb);
            bulbListPanel.addBulb(bulb);
            timelinePanelMap.put(bulb, t);
            mainTimelinePanel.add(t, Integer.toString(bulb.hashCode()));
        }
        animationSettingsPanel = new AnimationSettingsPanel();

        this.setLayout(new BorderLayout());
        this.add(
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        animationSettingsPanel,
                        bulbListPanel
                ), mainTimelinePanel)
                , BorderLayout.CENTER
        );
        this.setBackground(Color.gray);
    }

    public void setAnimationTimelineLength(long length){
        for(TimelinePanel t : timelinePanelMap.values()){
            System.out.println("Setting timeline length of " + t.toString());
            t.setAnimationTrackLength(length);
        }
    }

    public void playAll(){
        for(TimelinePanel t : timelinePanelMap.values()){
            t.play();
        }
    }

    public void pauseAll(){

    }

    private class AnimationSettingsPanel extends JPanel implements ActionListener {
        JPanel settingsHeaderPanel, settingsContentPanel;
        JButton playAllButton, pauseAllButton, applyButton;
        JFormattedTextField animationLengthField,
            currentAnimationTimeField;
        JCheckBox loopCheckBox;
        public final int defaultAnimationLength = 1000;

        private AnimationSettingsPanel(){
            settingsContentPanel = new JPanel();
            settingsHeaderPanel = new JPanel();

            playAllButton = new JButton("Play All");
            pauseAllButton = new JButton("Pause All");
            applyButton = new JButton("Apply");
            animationLengthField = new JFormattedTextField(new NumberFormatter());
            currentAnimationTimeField = new JFormattedTextField(new NumberFormatter());
            loopCheckBox = new JCheckBox();

            animationLengthField.setColumns(5);
            currentAnimationTimeField.setColumns(5);
            animationLengthField.setValue(defaultAnimationLength);
            setAnimationTimelineLength(defaultAnimationLength);
            currentAnimationTimeField.setValue(0);

            settingsContentPanel.setLayout(new GridLayout(4, 2));
            settingsContentPanel.add(new JLabel("Animation Length (ms)"));
            settingsContentPanel.add(animationLengthField);
            settingsContentPanel.add(new JLabel("Current Time"));
            settingsContentPanel.add(currentAnimationTimeField);
            settingsContentPanel.add(new JLabel("Loop?"));
            settingsContentPanel.add(loopCheckBox);
            settingsContentPanel.add(applyButton);

            settingsHeaderPanel.setLayout(new FlowLayout());
            settingsHeaderPanel.add(playAllButton);
            settingsHeaderPanel.add(pauseAllButton);

            this.setLayout(new BorderLayout());
            this.add(settingsHeaderPanel, BorderLayout.PAGE_START);
            this.add(settingsContentPanel, BorderLayout.CENTER);
        }

        //todo: add current timestamp change functionality
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == applyButton){
                setAnimationTimelineLength(Long.parseLong(animationLengthField.getText()));
            }else if (e.getSource() == playAllButton) {
                playAll();
            }else if (e.getSource() == pauseAllButton) {
                pauseAll();
            }
        }
    }

    private class BulbListPanel extends JPanel implements ActionListener, ListSelectionListener {
        JList<SmartBulb> contentList;
        JPanel headerPnl;
        JButton addBulbButton;
        ArrayList<BulbElementPanel> bulbElementPanels;
        DefaultListModel<SmartBulb> bulbElementModel;

        private BulbListPanel(){
            bulbElementModel = new DefaultListModel<>();

            bulbElementPanels = new ArrayList<>();
            addBulbButton = new JButton("Add Bulb");
            headerPnl = new JPanel();
            contentList = new JList<>(bulbElementModel);

            contentList.setCellRenderer(new BulbLabel());
            contentList.setVisibleRowCount(5);
            contentList.addListSelectionListener(this);

            this.headerPnl.add(addBulbButton);
            addBulbButton.addActionListener(this);

            this.setLayout(new BorderLayout());
            this.add(contentList, BorderLayout.CENTER);
            this.add(headerPnl, BorderLayout.PAGE_END);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == addBulbButton) {
                JOptionPane.showMessageDialog(this, notImplementedMessage); //todo: placeholder
            }
        }

        public void addBulb(SmartBulb bulb){
            bulbElementModel.addElement(bulb);
            updateBulbListPanel();
        }

        private void updateBulbListPanel(){
            this.revalidate();
            contentList.revalidate();
            this.repaint();
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            SmartBulb selectedBulb = contentList.getSelectedValue();
            cardLayout.show(mainTimelinePanel, Integer.toString(selectedBulb.hashCode()));
        }

        private class BulbLabel extends JLabel implements ListCellRenderer<SmartBulb> {

            public BulbLabel() {
                setOpaque(true);
            }

            @Override
            public Component getListCellRendererComponent(JList<? extends SmartBulb> list, SmartBulb value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                setText(value.name);
                setFont(new Font("Arial", Font.BOLD, 17));

                if (isSelected) {
                    setBackground(Color.green);
                } else {
                    setBackground(null);
                    setForeground(null);
                }

                return this;
            }

        }

        //fixme: panel being tiled across list element
        //fixme: buttons are not selectable through list
        private class BulbElementPanel extends JPanel implements ActionListener, ListCellRenderer<SmartBulb>{
            JButton removeBtn;
            JToggleButton lockBrightnessToggleBtn;
            JFormattedTextField brightnessField;
            JLabel nameLbl;
            SmartBulb bulb;
            Color selectedColor = Color.blue;
            boolean isBrightnessLocked;

            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == lockBrightnessToggleBtn){
                    isBrightnessLocked = lockBrightnessToggleBtn.isSelected();
                }
                else if(e.getSource() == removeBtn){
                    bulbElementPanels.remove(this);
                    contentList.remove(this);
                }
                updateBulbListPanel();
            }

            @Override
            public Component getListCellRendererComponent(JList<? extends SmartBulb> list, SmartBulb value, int index, boolean isSelected, boolean cellHasFocus) {
                this.setOpaque(true);
                this.bulb = value;
                isBrightnessLocked = false;

                removeBtn = new JButton("X");
                lockBrightnessToggleBtn = new JToggleButton("Lock");
                brightnessField = new JFormattedTextField(new NumberFormatter());
                nameLbl = new JLabel(bulb.name);

                brightnessField.setValue(bulb.getBrightness());
                nameLbl.setText(bulb.name);

                this.setMinimumSize(new Dimension( 0, 100));
                this.setMaximumSize(new Dimension( Short.MAX_VALUE, 100));

                this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                this.add(removeBtn);
                this.add(nameLbl);
                this.add(lockBrightnessToggleBtn);
                this.add(brightnessField);

                if(isSelected){
                    setBackground(selectedColor);
                }else{
                    setBackground(null);
                    setForeground(null);
                }

                return this;
            }
        }
    }
}

@FunctionalInterface
interface SimpleDocumentListener extends DocumentListener {
    void update(DocumentEvent e);

    @Override
    default void insertUpdate(DocumentEvent e) {
        update(e);
    }
    @Override
    default void removeUpdate(DocumentEvent e) {
        update(e);
    }
    @Override
    default void changedUpdate(DocumentEvent e) {
        update(e);
    }
}
