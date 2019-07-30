package zodiac.java.commonutils.date.timer;

import java.util.Date;
import java.util.TimerTask;

public interface TimerInterface {

    /**
     * 立即执行
     *
     * @param task
     */
    void scheduleImmediately(Runnable task);

    /**
     * 延迟执行
     *
     * @param task
     * @param fixedDelay
     */
    void scheduleWithDelay(Runnable task, long fixedDelay);

    /**
     * 延迟执行
     *
     * @param task
     * @param firstTime
     */
    void scheduleWithDelay(Runnable task, Date firstTime);

    /**
     * 固定延迟之后周期性执行
     *
     * @param task   要调度的任务
     * @param delay  延迟，毫秒
     * @param period 周期，毫秒
     */
    void scheduleAtFixedRate(Runnable task, long delay, long period);

    /**
     * 固定延迟之后周期性执行
     *
     * @param task      要调度的任务
     * @param firstTime 第一次执行的时间
     * @param period    执行周期
     */
    void scheduleAtFixedRate(Runnable task, Date firstTime, long period);

    /**
     * 停止执行
     */
    void shutdown();

    TimerTask getTimerTask(Runnable task);

}
