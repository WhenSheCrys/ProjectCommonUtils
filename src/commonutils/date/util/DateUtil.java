package commonutils.date.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Date transform(Date in, long millisecond) {
        return new Date(in.getTime() + millisecond);
    }

    public static Date transform(Date in, int mount, TimeUnit timeUnit) {
        Calendar calendar = getCalendar(in);
        switch (timeUnit) {
            case MILLISECOND:
                transformCalendar(calendar, Calendar.MILLISECOND, mount);
                break;
            case SECOND:
                transformCalendar(calendar, Calendar.SECOND, mount);
                break;
            case MINUTE:
                transformCalendar(calendar, Calendar.MINUTE, mount);
                break;
            case HOUR:
                transformCalendar(calendar, Calendar.HOUR_OF_DAY, mount);
                break;
            case DAY:
                transformCalendar(calendar, Calendar.DAY_OF_YEAR, mount);
                break;
            case MONTH:
                transformCalendar(calendar, Calendar.MONTH, mount);
                break;
            case YEAR:
                transformCalendar(calendar, Calendar.YEAR, mount);
                break;
            case CENTURY:
                transformCalendar(calendar, Calendar.YEAR, mount * 100);
                break;
            default:
                throw new RuntimeException();
        }
        return calendar.getTime();
    }

    private static void transformCalendar(Calendar calendar, int field, int mount) {
        calendar.set(field, calendar.get(field) + mount);
    }

    public static int firstDayOfQuarter(Date date) {
        Calendar calendar = getCalendar(date);
        int quarter = inWhichQuarter(date);
        int year = calendar.get(Calendar.YEAR);
        return firstDayOfQuarter(year, quarter);
    }

    public static int firstDayOfQuarter(int year, int quarter) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        int firstMonthOfQuarter = firstMonthOfQuarter(quarter);
        calendar.set(Calendar.MONTH, firstMonthOfQuarter - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static Date firstDateOfQuarter(Date date) {
        int firstDayOfQuarter = firstDayOfQuarter(date);
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.DAY_OF_YEAR, firstDayOfQuarter);
        setToStartTimeOfDay(calendar);
        return calendar.getTime();
    }

    public static Date firstDateOfQuarter(int year, int quarter) {
        int firstDayOfQuarter = firstDayOfQuarter(year, quarter);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, firstDayOfQuarter);
        setToStartTimeOfDay(calendar);
        return calendar.getTime();
    }

    public static int inWhichQuarter(Date date) {
        Calendar cal = getCalendar(date);
        int month = cal.get(Calendar.MONTH) + 1;
        return inWhichQuarter(month);
    }

    public static int firstMonthOfQuarter(int quarter) {
        int month = 0;
        switch (quarter) {
            case 1:
                month = 1;
                break;
            case 2:
                month = 4;
                break;
            case 3:
                month = 7;
                break;
            case 4:
                month = 10;
                break;
            default:
                throw new RuntimeException("quarter should between 1 and 4!");
        }
        return month;
    }

    public static int inWhichQuarter(int month) {
        int quarter = 0;
        switch (month) {
            case 1:
            case 2:
            case 3:
                quarter = 1;
                break;
            case 4:
            case 5:
            case 6:
                quarter = 2;
                break;
            case 7:
            case 8:
            case 9:
                quarter = 3;
                break;
            case 10:
            case 11:
            case 12:
                quarter = 4;
                break;
            default:
                throw new RuntimeException("month should between 1 and 12!");
        }
        return quarter;
    }

    private static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private static void setToStartTimeOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

}
