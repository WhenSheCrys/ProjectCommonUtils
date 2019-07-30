package zodiac.java.commonutils.date.util;

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
            case WEEK:
                transformCalendar(calendar, Calendar.WEEK_OF_YEAR, mount);
                break;
            case MONTH:
                transformCalendar(calendar, Calendar.MONTH, mount);
                break;
            case QUARTER:
                transformCalendar(calendar, Calendar.MONTH, mount * 3);
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

    public static Date earliestDateOf(Date date, TimeUnit timeUnit) {
        Date result = date;
        Calendar calendar = getCalendar(result);
        switch (timeUnit) {
            case CENTURY:
                int years = calendar.get(Calendar.YEAR) % 100;
                result = transform(result, -years, TimeUnit.YEAR);
                calendar = getCalendar(result);
            case YEAR:
                calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
                result = calendar.getTime();
            case QUARTER:
                int month = firstMonthOfQuarter(inWhichQuarter(result));
                calendar.set(Calendar.MONTH, month - 1);
                result = calendar.getTime();
            case MONTH:
            case WEEK:
                if (timeUnit == TimeUnit.WEEK) {
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
                    result = calendar.getTime();
                } else {
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                    result = calendar.getTime();
                }
            case DAY:
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
                result = calendar.getTime();
            case HOUR:
                calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
                result = calendar.getTime();
            case MINUTE:
                calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
                result = calendar.getTime();
            case SECOND:
                calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
                result = calendar.getTime();
            case MILLISECOND:
            default:
                break;
        }
        return result;
    }

    public static Date latestDateOf(Date date, TimeUnit timeUnit) {
        Date result = date;
        Calendar calendar = getCalendar(result);
        switch (timeUnit) {
            case CENTURY:
                int years = calendar.get(Calendar.YEAR) % 100;
                result = transform(result, 100 - years, TimeUnit.YEAR);
                calendar = getCalendar(result);
            case YEAR:
                calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
                result = calendar.getTime();
            case QUARTER:
                int month = lastMonthOfQuarter(inWhichQuarter(date));
                calendar.set(Calendar.MONTH, month - 1);
                result = calendar.getTime();
            case MONTH:
            case WEEK:
                if (timeUnit == TimeUnit.WEEK) {
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
                    result = calendar.getTime();
                } else {
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    result = calendar.getTime();
                }
            case DAY:
                calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
                result = calendar.getTime();
            case HOUR:
                calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
                result = calendar.getTime();
            case MINUTE:
                calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
                result = calendar.getTime();
            case SECOND:
                calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
                result = calendar.getTime();
            case MILLISECOND:
            default:
                break;
        }
        return result;
    }

    private static int firstDayOfQuarter(int year, int quarter) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        int firstMonthOfQuarter = firstMonthOfQuarter(quarter);
        calendar.set(Calendar.MONTH, firstMonthOfQuarter - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    private static int inWhichQuarter(Date date) {
        Calendar cal = getCalendar(date);
        int month = cal.get(Calendar.MONTH) + 1;
        return inWhichQuarter(month);
    }

    private static int firstMonthOfQuarter(int quarter) {
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

    private static int lastMonthOfQuarter(int quarter) {
        int month = 0;
        switch (quarter) {
            case 1:
                month = 3;
                break;
            case 2:
                month = 6;
                break;
            case 3:
                month = 9;
                break;
            case 4:
                month = 12;
                break;
            default:
                throw new RuntimeException("quarter should between 1 and 4!");
        }
        return month;
    }

    private static int inWhichQuarter(int month) {
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

    public static Date yesterday(Date date) {
        return setToStartTimeOfDay(transform(date, -1, TimeUnit.DAY));

    }

    public static Date tomorrow(Date date) {
        return setToStartTimeOfDay(transform(date, 1, TimeUnit.DAY));
    }

    public static int totalDaysBetween(Date start, Date end) {
        return daysBetween(start, end).size();
    }

    public static int totalDaysBetween(Date start, Date end, boolean includeStart, boolean includeEnd) {
        return daysBetween(start, end, includeStart, includeEnd).size();
    }

    public static ArrayList<Date> daysBetween(Date start, Date end) {
        return daysBetween(start, end, false, false);
    }

    public static ArrayList<Date> daysBetween(Date start, Date end, boolean includeStart, boolean includeEnd) {
        if (!includeStart) {
            start = setToStartTimeOfDay(tomorrow(start));
        }
        if (!includeEnd) {
            end = setToStartTimeOfDay(yesterday(end));
        }
        assert start.compareTo(end) <= 0 : "Start date must <= End date";
        ArrayList<Date> arrayList = new ArrayList<>();
        while (start.compareTo(end) <= 0) {
            arrayList.add(start);
            start = setToStartTimeOfDay(tomorrow(start));
        }
        return arrayList;
    }

    public static boolean isLeapYear(Date date) {
        Calendar calendar = getCalendar(date);
        int year = calendar.get(Calendar.YEAR);
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
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

    private static Date setToStartTimeOfDay(Date date) {
        Calendar calendar = getCalendar(date);
        setToStartTimeOfDay(calendar);
        return calendar.getTime();
    }

    private static Date setToEndTimeOfDay(Date date) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

}
