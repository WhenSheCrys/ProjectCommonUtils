package j.commonutils.date.timer;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadTimer extends AbstractTimer implements TimerInterface {

    private final TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private String name;
    private ScheduledExecutorService scheduledExecutorService;


    public ThreadTimer(String name) {
        super(name);
        int nProcessor = Runtime.getRuntime().availableProcessors();
        scheduledExecutorService = Executors.newScheduledThreadPool(nProcessor * 2 - 1);
    }

    public ThreadTimer(String name, int nThread) {
        super(name);
        if (nThread == 1) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        } else {
            scheduledExecutorService = Executors.newScheduledThreadPool(nThread);
        }
    }

    @Override
    public void scheduleImmediately(Runnable task) {
        super.logger.info(String.format("Task %s will run immediately, at %s !", name, super.sdf.format(new Date())));
        scheduledExecutorService.schedule(task, 0, timeUnit);
    }

    @Override
    public void scheduleWithDelay(Runnable task, long fixedDelay) {
        super.logger.info(String.format("Task %s will run at %s !", name, super.sdf.format(new Date(System.currentTimeMillis() + fixedDelay))));
        scheduledExecutorService.schedule(task, fixedDelay, timeUnit);
    }

    @Override
    public void scheduleWithDelay(Runnable task, Date firstTime) {
        super.checkDate(firstTime);
        super.logger.info(String.format("Task %swill run at %s !", name, super.sdf.format(firstTime)));
        long delay = firstTime.getTime() - System.currentTimeMillis();
        scheduledExecutorService.schedule(task, delay, timeUnit);
    }

    @Override
    public void scheduleAtFixedRate(Runnable task, long delay, long period) {
        super.logger.info(String.format("Task %s will run at %s with period %sms", name, super.sdf.format(new Date(System.currentTimeMillis() + delay)), period));
        scheduledExecutorService.scheduleAtFixedRate(task, delay, period, timeUnit);
    }

    @Override
    public void scheduleAtFixedRate(Runnable task, Date firstTime, long period) {
        super.checkDate(firstTime);
        super.logger.info(String.format("Task %s will run at %s with period %sms", name, super.sdf.format(firstTime), period));
        long delay = firstTime.getTime() - System.currentTimeMillis();
        scheduledExecutorService.scheduleAtFixedRate(task, delay, period, timeUnit);
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
    }
}
