package ui.timeline;

import devices.SmartBulb;
import javafx.animation.Animation;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

//fixme: animations are not moving
public class TimelinePanel extends JPanel implements ActionListener {
    SmartBulb bulb;
    JButton playBtn, pauseBtn, addEventBtn;
    JPanel mediaPanel, timelineContentPanel;
    ArrayList<AnimationEventPanel> animationEventPnls;
    AnimationTrack animationTrack;
    public TimelinePanel(SmartBulb bulb){
        this.bulb = bulb;
        //todo: will need to get animation track length from animation settings panel
        this.animationTrack = new AnimationTrack(1000);

        this.playBtn = new JButton("Play");
        this.pauseBtn = new JButton("Pause");
        this.addEventBtn = new JButton("Add Event");
        this.mediaPanel = new JPanel();
        this.timelineContentPanel = new JPanel();
        this.animationEventPnls = new ArrayList<>();
        this.setBorder(new TitledBorder(bulb.name));

        this.timelineContentPanel.setLayout(new BoxLayout(timelineContentPanel, BoxLayout.Y_AXIS));
        this.setLayout(new BorderLayout());

        playBtn.addActionListener(this);
        pauseBtn.addActionListener(this);
        addEventBtn.addActionListener(this);

        timelineContentPanel.setBackground(Color.white);
        this.setPreferredSize(new Dimension(400, 400));

        this.mediaPanel.add(playBtn);
        this.mediaPanel.add(pauseBtn);
        this.mediaPanel.add(addEventBtn);

        this.add(mediaPanel, BorderLayout.PAGE_START);
        JScrollPane scrollPane = new JScrollPane(timelineContentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(5);
        this.add(scrollPane, BorderLayout.CENTER);

        this.setMinimumSize(new Dimension(400, 600));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == addEventBtn){
            System.out.println("Adding event...");
            AnimationEventPanel animEvent = new AnimationEventPanel(this.bulb);
            animationEventPnls.add(animEvent);
            timelineContentPanel.add(animEvent);
            animationTrack.addEvent(animEvent.curAnimationEvent);
            dbg_showAnimationTrack();
        }else if (e.getSource() == playBtn){
            animationTrack.playTrack();
        }else if (e.getSource() == pauseBtn){
            animationTrack.pauseTrack();
        }
        updateTimelinePanel();
    }

    public void dbg_showAnimationTrack(){
        System.out.println("Cur size: " +animationTrack.keyFrameEventList.size() + ". Current anim queue: " + Arrays.toString(animationTrack.keyFrameEventList.toArray()));
    }

    public void setAnimationTrackLength(long length){
        this.animationTrack.setAnimationTrackLength(length);
    }

    public void play(){
        this.animationTrack.playTrack();
    }

    public void updateTimelinePanel(){
        this.revalidate();
        timelineContentPanel.revalidate();
        this.repaint();
    }

    private class AnimationEventPanel extends JPanel implements ActionListener, ItemListener {
        private JPanel headerPanel, eventContentPanel;
        private JComboBox<String> eventComboBox;
        private CardLayout eventCardLayout;
        private JButton removeBtn;
        private boolean isCollapsed;
        private SmartBulb bulb;
        private AnimationEventContentPanel[] animationEventContentPanels;
        private TitledBorder titledBorder;
        private KeyFrameEvent[] animationEvents;
        private KeyFrameEvent curAnimationEvent;

        private AnimationEventPanel(SmartBulb bulb){
            this.bulb = bulb;
            animationEventContentPanels = new AnimationEventContentPanel[]{
                    new StaticEventContentPanel(this.bulb),
                    new FadeEventContentPanel(this.bulb)
            }; // TODO: add more events here
            animationEvents = new KeyFrameEvent[animationEventContentPanels.length];
            headerPanel = new JPanel();
            eventContentPanel = new JPanel();
            eventCardLayout = new CardLayout();
            removeBtn = new JButton("X");
            eventComboBox = new JComboBox<>();
            titledBorder = new TitledBorder("");
            isCollapsed = true;

            int i = 0;
            for(AnimationEventContentPanel panel : animationEventContentPanels){
                eventComboBox.addItem(panel.eventName);
                eventCardLayout.addLayoutComponent(panel, panel.eventName);
                eventContentPanel.add(panel, panel.eventName);
                animationEvents[i++] = panel.animationEvent;
            }
            titledBorder.setTitle(eventComboBox.getItemAt(0));
            curAnimationEvent = animationEventContentPanels[0].getEvent();
            eventContentPanel.setLayout(eventCardLayout);

            this.setBorder(titledBorder);

            this.headerPanel.setLayout(new BorderLayout());
            this.headerPanel.add(eventComboBox, BorderLayout.CENTER);
            this.headerPanel.add(removeBtn, BorderLayout.LINE_END);
            this.removeBtn.addActionListener(this);
            this.eventComboBox.addItemListener(this);

            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.add(headerPanel);
            this.add(eventContentPanel);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == removeBtn){
                animationEventPnls.remove(this);
                timelineContentPanel.remove(this);
                animationTrack.remove(this.curAnimationEvent);
                dbg_showAnimationTrack();
            }
            updateTimelinePanel();
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            if(e.getStateChange() == ItemEvent.SELECTED){
                System.out.println("Getting item: " + (String)e.getItem());
                eventCardLayout.show(eventContentPanel, (String)e.getItem());
                titledBorder.setTitle((String)e.getItem());
                animationTrack.remove(curAnimationEvent);
                curAnimationEvent = animationEvents[eventComboBox.getSelectedIndex()];
                animationTrack.addEvent(curAnimationEvent);
                dbg_showAnimationTrack();
                this.repaint();
                this.revalidate();
            }
        }
    }

    private abstract class AnimationEventContentPanel extends JPanel implements ActionListener, FocusListener {
        public String eventName;
        protected JLabel startTimeLabel, durationLabel;
        protected JFormattedTextField startTimeField, durationField;
        protected JButton colorAButton;
        protected Color colorA;
        protected SmartBulb bulb;
        protected KeyFrameEvent animationEvent;

        public AnimationEventContentPanel(SmartBulb bulb){
            this.bulb = bulb;
            this.animationEvent = initKeyframeEvent();

            this.colorA = new Color(0, 0, 0);

            this.startTimeLabel = new JLabel("Start Time: ");
            this.durationLabel = new JLabel("Duration: ");
            this.startTimeField = new JFormattedTextField(new NumberFormatter());
            this.durationField = new JFormattedTextField(new NumberFormatter());
            this.colorAButton = new JButton("Starting Color");

            this.setMaximumSize(new Dimension(0, 100));
            this.setPreferredSize(new Dimension(0, 100));

            this.startTimeField.setColumns(5);
            this.durationField.setColumns(5);
            this.startTimeField.setValue(0);
            this.durationField.setValue(0);
            this.colorAButton.addActionListener(this);
            startTimeField.addFocusListener(this);
            durationField.addFocusListener(this);

            this.setLayout(new GridLayout(3,2));
            this.add(startTimeLabel);
            this.add(startTimeField);
            this.add(durationLabel);
            this.add(durationField);
            this.add(colorAButton);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == colorAButton) {
                Color c = JColorChooser.showDialog(null, "Starting color...", null);
                if(c != null) {
                    colorA = c;
                    colorAButton.setBackground(colorA);
                }
            }
        }

        protected KeyFrameEvent initKeyframeEvent(){
            return new KeyFrameEvent(bulb, 0);
        }

        public KeyFrameEvent getEvent(){
            return this.animationEvent;
        }

        @Override public void focusGained(FocusEvent e) {}

        @Override
        public void focusLost(FocusEvent e) {
            if(startTimeField.getText().replace(",", "").compareTo("") != 0){
                animationEvent.eventStartTimeMillis = Long.parseLong(startTimeField.getText().replace(",", ""));
            }

            if(durationField.getText().replace(",", "").compareTo("") != 0){
                animationEvent.durationTimeMillis = Long.parseLong(durationField.getText().replace(",", ""));
            }
            dbg_showAnimationTrack();
        }
    }

    private class StaticEventContentPanel extends AnimationEventContentPanel {
        private StaticEventContentPanel(SmartBulb bulb){
            super(bulb);
            this.eventName = "Static";
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            StaticKeyFrameEvent event = (StaticKeyFrameEvent) animationEvent;
            event.targetColor = colorA;
            dbg_showAnimationTrack();
        }

        @Override
        protected KeyFrameEvent initKeyframeEvent(){
            return new StaticKeyFrameEvent(this.bulb, 0, 0, Color.black, true);
        }
    }

    private class FadeEventContentPanel extends AnimationEventContentPanel {
        public JButton colorBButton;
        public Color colorB;
        private FadeEventContentPanel(SmartBulb bulb){
            super(bulb);
            this.colorB = new Color(0, 0, 0);
            this.eventName = "Fade";
            colorBButton = new JButton("Ending Color");
            colorBButton.addActionListener(this);

            this.add(colorBButton);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            if(e.getSource() == colorBButton) {
                Color c = JColorChooser.showDialog(null, "Ending color...", null);
                if(c != null) {
                    colorB = c;
                    colorBButton.setBackground(colorB);
                }
            }
            FadeKeyFrameEvent event = (FadeKeyFrameEvent) animationEvent;
            event.startColor = colorA;
            event.targetColor = colorB;
            dbg_showAnimationTrack();
        }

        @Override
        protected KeyFrameEvent initKeyframeEvent(){
            return new FadeKeyFrameEvent(this.bulb, 0, 0, Color.black, Color.black, true);
        }

    }
}