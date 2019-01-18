package java.commonutils.date.timer;

import java.util.Date;
import java.util.Timer;

public class SimpleTimer extends AbstractTimer implements TimerInterface {

    private Timer timer;
    private String name;

    public SimpleTimer(String name) {
        super(name);
        this.name = name;
        this.timer = new Timer(name);
    }

    public String name() {
        return name;
    }

    @Override
    public void scheduleImmediately(Runnable task) {
        super.logger.info(String.format("Task %s will run immediately, at %s !", name, super.sdf.format(new Date())));
        timer.schedule(super.getTimerTask(task), 0L);
    }

    @Override
    public void scheduleWithDelay(Runnable task, long fixedDelay) {
        super.logger.info(String.format("Task %s will run at %s !", name, super.sdf.format(new Date(System.currentTimeMillis() + fixedDelay))));
        timer.schedule(super.getTimerTask(task), fixedDelay);
    }

    @Override
    public void scheduleWithDelay(Runnable task, Date firstTime) {
        super.checkDate(firstTime);
        super.logger.info(String.format("Task %s will run at %s !", name, super.sdf.format(firstTime)));
        timer.schedule(getTimerTask(task), firstTime);
    }

    @Override
    public void scheduleAtFixedRate(Runnable task, long delay, long period) {
        super.logger.info(String.format("Task %s will run at %s with period %sms", name, super.sdf.format(new Date(System.currentTimeMillis() + delay)), period));
        timer.scheduleAtFixedRate(getTimerTask(task), delay, period);
    }

    @Override
    public void scheduleAtFixedRate(Runnable task, Date firstTime, long period) {
        super.checkDate(firstTime);
        super.logger.info(String.format("Task %s will run at %s with period %sms", name, super.sdf.format(firstTime), period));
        timer.scheduleAtFixedRate(getTimerTask(task), firstTime, period);
    }

    @Override
    public void shutdown() {
        timer.cancel();
    }
}
