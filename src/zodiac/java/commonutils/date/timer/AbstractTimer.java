package zodiac.java.commonutils.date.timer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public abstract class AbstractTimer {

    private String name;

    public AbstractTimer(String name) {
        this.name = name;
    }

    public final Logger logger = LogManager.getLogger(this.getClass());
    public final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public void checkDate(Date date) {
        assert date.getTime() > System.currentTimeMillis() : "Time should not be earlier than current time!";
    }

    public TimerTask getTimerTask(Runnable task) {
        return new TimerTask() {
            @Override
            public void run() {
                logger.info("Task " + name + " started at " + sdf.format(new Date()));
                task.run();
            }
        };
    }
}
