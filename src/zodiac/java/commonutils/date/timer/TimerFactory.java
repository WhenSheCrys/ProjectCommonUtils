package zodiac.java.commonutils.date.timer;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class TimerFactory {

    private static final ConcurrentHashMap<String, TimerInterface> timerMap = new ConcurrentHashMap<>();

    public static SimpleTimer newSimpleTimer(String name) {
        checkAlreadyExists(name);
        SimpleTimer simpleTimer = new SimpleTimer(name);
        timerMap.put(name, simpleTimer);
        return simpleTimer;
    }

    public static ThreadTimer newThreadTimer(String name, int nThread) {
        checkAlreadyExists(name);
        ThreadTimer threadTimer = new ThreadTimer(name, nThread);
        timerMap.put(name, threadTimer);
        return threadTimer;
    }

    public ThreadTimer newThreadTimer(String name) {
        checkAlreadyExists(name);
        ThreadTimer threadTimer = new ThreadTimer(name);
        timerMap.put(name, threadTimer);
        return threadTimer;
    }

    public static ThreadTimer newSingleThreadTimer(String name) {
        checkAlreadyExists(name);
        ThreadTimer threadTimer = new ThreadTimer(name, 1);
        timerMap.put(name, threadTimer);
        return threadTimer;
    }

    public static void shutdown(String name) {
        synchronized (timerMap) {
            if (timerMap.contains(name)) {
                timerMap.get(name).shutdown();
                timerMap.remove(name);
            }
        }
    }

    public static boolean isRunning(String name) {
        return timerMap.contains(name);
    }

    public static ArrayList<String> listTimers() {
        ArrayList<String> ret = new ArrayList<String>();
        Enumeration<String> enumeration = timerMap.keys();
        while (enumeration.hasMoreElements()) {
            ret.add(enumeration.nextElement());
        }
        return ret;
    }

    private static void checkAlreadyExists(String name) {
        assert !timerMap.contains(name) : String.format("Timer \"%s\" already exists, please use another name or shutdown timer \"%s\" first!", name, name);
    }
}
