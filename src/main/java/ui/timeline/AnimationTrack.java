package ui.timeline;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class AnimationTrack{
    private boolean isPlaying;
    private boolean loopEnabled;
    private long trackLengthMillis;
    private long trackStartTimestamp;
    public volatile long currentTimestampMillis;
    private Thread trackThread;
    private Thread trackTimestampThread;
    private boolean hasCompletedLoop = false;
    ArrayList<KeyFrameEvent> keyFrameEventList;
    ArrayList<KeyFrameEvent> firedEventList;

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public AnimationTrack(long trackLengthMillis){
        this.keyFrameEventList = new ArrayList<>();
        this.firedEventList = new ArrayList<>();

        this.trackLengthMillis = trackLengthMillis;
        this.currentTimestampMillis = 0;
        this.trackStartTimestamp = -1;
        this.trackTimestampThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(trackStartTimestamp == -1){
                        trackStartTimestamp = System.currentTimeMillis();
                    }
                    currentTimestampMillis = (System.currentTimeMillis() - trackStartTimestamp);
                }
            }
        });
        this.trackThread = initMainThread();
    }

    public Thread initMainThread(){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    //reset loop
                    if(currentTimestampMillis >= trackLengthMillis){
                        hasCompletedLoop = true;
                        currentTimestampMillis = 0;
                        trackStartTimestamp = System.currentTimeMillis();
                    }else if (hasCompletedLoop) hasCompletedLoop = false;

                    if(hasCompletedLoop) debugLog("Looping...");

                    if(hasCompletedLoop){
                        firedEventList = new ArrayList<>();
                    }
                    for(KeyFrameEvent e : keyFrameEventList){
                        if(e.eventStartTimeMillis <= currentTimestampMillis && !firedEventList.contains(e)){
                            firedEventList.add(e);

                            debugLog(currentTimestampMillis + " - Starting " + e);
                            e.run();
                            debugLog(currentTimestampMillis + " - Ending " + e);
                            break;
                        }
                    }
                }
            }
        });
    }

    /***
     * Add event to event list; will not add if the job runs past the length of the event
     * @param event
     * @return
     */
    public boolean addEvent(KeyFrameEvent event){
        if(event.eventStartTimeMillis + event.durationTimeMillis <= this.trackLengthMillis){
            keyFrameEventList.add(event);
            Collections.sort(keyFrameEventList);
            return true;
        }

        return false;
    }

    public void setAnimationTrackLength(long length){
        this.trackLengthMillis = length;
        this.trackThread = initMainThread();
    }

    public void playTrack(){
        isPlaying = true;
        synchronized (trackThread) {
            if (trackThread.getState() == Thread.State.WAITING) {
                trackThread.notify();
            } else {
                trackThread.start();
            }
        }
        synchronized (trackTimestampThread) {
            if (trackTimestampThread.getState() == Thread.State.WAITING) {
                trackTimestampThread.notify();
            } else {
                trackTimestampThread.start();
            }
        }
    }

    public synchronized void pauseTrack(){
        isPlaying = false;

        try {
            synchronized (trackThread) { trackThread.wait(); }
            synchronized (trackTimestampThread) {trackTimestampThread.wait();}
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Paused");
    }

    public static void debugLog(String message){
        System.out.println(formatter.format(Date.from(Instant.now())) + " | " + message);
    }

    public void remove(KeyFrameEvent e){
        keyFrameEventList.remove(e);
    }


}
