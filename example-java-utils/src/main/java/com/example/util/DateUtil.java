package com.example.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/20 17:00
 */
public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static final String DATE_PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN_1 = "yyyy-MM-dd";
    public static final String DATE_PATTERN_2 = "yyyyMMdd";
    public static final String DATE_PATTERN_3 = "yyyy.MM.dd";
    public static final String DATE_PATTERN_4 = "yyyy.MM.dd HH:mm:ss";
    public static final String DATE_PATTERN_5 = "HHmmss";
    public static final String DATE_PATTERN_6 = "yyyy.MM.dd HH:mm";
    public static final String DATE_PATTERN_7 = "HH:mm";
    public static final String DATE_PATTERN_8 = "yyyyMMdd_HHmmss";
    public static final String DATE_PATTERN_Y = "yyyy";
    public static final String DATE_PATTERN_YM = "yyyy-MM";
    public static final String DATE_PATTERN_YMDH = "yyyy-MM-dd HH";
    public static final String DATE_PATTERN_YM_1 = "yyyyMM";
    public static final String DATE_PATTERN_YMDH_2 = "yyyy-MM-dd_HH";

    public static final long DAY_PARSE_LONG_TIME = 86400000L;

    public static final int BEFORE = -1;
    public static final int AFTER = 1;
    public static final int EQUALS = 0;

    /**
     * 格式化日期格式
     *
     * @param date
     * @return
     */
    public static String getDateString(Date date) {
        return getDateString(date, DATE_PATTERN_DEFAULT);
    }

    /**
     * 日期计算
     *
     * @param current 当前日期
     * @param n       天数
     * @param type    1:加 -1:减
     * @return
     */
    public static Date addDate(Date current, int n, int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DAY_OF_MONTH, n * type);
        return calendar.getTime();
    }

    /**
     * 日期计算
     *
     * @param current 当前日期
     * @param n       小时数
     * @param type    1:加 -1:减
     * @return
     */
    public static Date addDateWithHours(Date current, int n, int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.HOUR_OF_DAY, n * type);
        return calendar.getTime();
    }

    /**
     * 格式化日期格式
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String getDateString(Date date, String pattern) {
        String dateStr = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            dateStr = simpleDateFormat.format(date);
        } catch (Exception e) {
            logger.error("get date string failed",e);
        }
        return dateStr;
    }

    public static Date parseStringToDate(String date) {
        return parseStringToDate(date, DATE_PATTERN_1);
    }

    public static Date parseStringToDate(String date, String format) {
        if(date == null) {
            return null;
        }
        Date result;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            result = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            logger.error(" parse {} to date error.", date);
            return null;
        }
        return result;
    }

    public static Long parseStringToLong(String date, String format) {
        long result;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            result = simpleDateFormat.parse(date).getTime();
        } catch (ParseException e) {
            logger.error(" parse {} to date error.", date);
            return null;
        }
        return result;
    }

    public static int getIntervalDays(Date start, Date end) {
        Objects.requireNonNull(start, "start is null");
        Objects.requireNonNull(end, "end is null");

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(start);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(end);
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);

        long diff = cal2.getTimeInMillis() - cal1.getTimeInMillis();
        return (int)((double)diff/DAY_PARSE_LONG_TIME);
    }

    /**
     * 获取某个日期之前days天的所有日期集合 日期集合[2017-05-03, 2017-05-04, 2017-05-05, 2017-05-06,
     * 2017-05-07]
     *
     * @param now
     * @param days
     * @return
     */
    public static List<String> getBetweenDates(Date now, int days) {
        try {
            Calendar startDay = Calendar.getInstance();
            Calendar endDay = Calendar.getInstance();
            Date start = getDateBefore(now, days);
            startDay.setTime(start);
            endDay.setTime(now);
            List<String> result = new ArrayList<String>();
            // 给出的日期开始日比结束日大则不执行打印
            if (endDay.before(startDay)) {
                result.add(getDateString(startDay.getTime(), "yyyy-MM-dd"));
                return result;
            }
            while (!endDay.before(startDay)) {
                result.add(getDateString(startDay.getTime(), "yyyy-MM-dd"));
                // 日期加一
                startDay.add(Calendar.DATE, 1);
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前月份天数
     * 2017-05-07]
     *
     * @return
     */
    public static List<String> getCurrentMonthDates() {
        List<String> result = new ArrayList<String>();
        try {

            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH) + 1;

            String start = getFirstDayOfMonth(year, month);

            String end = getLastDayOfMonth(year, month);

            result = getBetweenDates(parseStringToDate(start), parseStringToDate(end));

        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 得到几天前的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 获取某年第一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getYearFirst(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }

    /**
     * 获取某年最后一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getYearLast(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();

        return currYearLast;
    }

    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    public static String getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DATE));
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    /**
     * 获取两个日期之间的日期
     *
     * @param start
     *            开始日期
     * @param end
     *            结束日期
     * @return 日期集合[2017-05-03, 2017-05-04, 2017-05-05, 2017-05-06, 2017-05-07]
     */
    public static List<String> getBetweenDates(Date start, Date end) {
        Calendar startDay = Calendar.getInstance();
        Calendar endDay = Calendar.getInstance();
        startDay.setTime(start);
        endDay.setTime(end);
        List<String> result = new ArrayList<String>();
        // 给出的日期开始日比结束日大则不执行打印
        if (endDay.before(startDay)) {
            result.add(getDateString(startDay.getTime(), "yyyy-MM-dd"));
            return result;
        }
        while (!endDay.before(startDay)) {
            result.add(getDateString(startDay.getTime(), "yyyy-MM-dd"));
            // 日期加一
            startDay.add(Calendar.DATE, 1);
        }
        return result;
    }

    /**
     * 获取两个日期间的月份
     *
     * @param start 2018-01
     * @param end 2018-05
     * @return [2018-01, 2018-02, 2018-03, 2018-04, 2018-05]
     */
    public static List<String> getMonthsBetweenDates(Date start, Date end) {
        Calendar startDay = Calendar.getInstance();
        Calendar endDay = Calendar.getInstance();
        startDay.setTime(start);
        endDay.setTime(end);
        List<String> result = new ArrayList<String>();
        // 给出的日期开始日比结束日大则不执行打印
        if (endDay.before(startDay)) {
            result.add(getDateString(startDay.getTime(), DateUtil.DATE_PATTERN_YM));
            return result;
        }
        while (!endDay.before(startDay)) {
            result.add(getDateString(startDay.getTime(), DateUtil.DATE_PATTERN_YM));
            // 日期加一
            startDay.add(Calendar.MONTH, 1);
        }
        return result;
    }

    public static List<String> getMonthsOfYear() {
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.format("%02d", i));
        }
        return months;
    }

    public static List<String> getHoursOfDay() {
        List<String> hours = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            hours.add(String.format("%02d", i));
        }
        return hours;
    }

    /**
     * @param DATE1
     * @param DATE2
     * @param format
     * @return
     * @Description:
     */
    public static int compare_date(String DATE1, String DATE2, String format) {
        if (StringUtils.isEmpty(format)) {
            format = "yyyy-MM-dd";
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static Date getDateByTimeStr(String timeStr){
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN_DEFAULT);
        try {
            Date date = dateFormat.parse(timeStr);
            return date;
        } catch (ParseException e) {
            logger.error("parse date error",e);
        }
        return null;
    }

    /**
     * 获取两个日期之间的日期
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return 日期集合
     */
    public static List<Date> getBetweenDates(String start, String end) {
        List<Date> result = new ArrayList<Date>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(parseStringToDate(start, DATE_PATTERN_1));
//	    tempStart.add(Calendar.DAY_OF_YEAR, 1);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(parseStringToDate(end, DATE_PATTERN_1));
        while (!tempEnd.before(tempStart)) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    /**
     * 获取某月的所有日期
     *
     * @param yearAndMonth 2018-02
     * @return 日期集合
     */
    public static List<String> getDatesByYearAndMonth(String yearAndMonth){
        String[] temp = yearAndMonth.split("-");
        int year = Integer.parseInt(temp[0]);
        int month = Integer.parseInt(temp[1]);
        String firstDay = getFirstDayOfMonth(year, month);
        String lastDay = getLastDayOfMonth(year, month);
        return getBetweenDatesString(firstDay, lastDay);
    }

    public static List<String> getBetweenDatesString(String start, String end) {
        List<String> result = new ArrayList<String>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(parseStringToDate(start, DATE_PATTERN_1));

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(parseStringToDate(end, DATE_PATTERN_1));

        while (!tempEnd.before(tempStart)) {
            result.add(getDateString(tempStart.getTime(), DATE_PATTERN_1));
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    /**
     * 这个方法比getBetweenDatesString多返回了后面一天
     * @param start 开始日期
     * @param end 结束日期
     * @return 开始日期到结束日期的后一天
     */
    public static List<String> getBetweenMoreDatesString(String start, String end) {
        /*
        List<String> result = new ArrayList<String>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(parseStringToDate(start, DATE_PATTERN_1));

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(parseStringToDate(end, DATE_PATTERN_1));

        while (!tempEnd.before(tempStart)) {
            result.add(getDateString(tempStart.getTime(), DATE_PATTERN_1));
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        */

        //modified by zhouguanbao@sensetime.com, by 2018-12-13, by the principle 'don't repeat yourself'
        List<String> result = getBetweenDatesString(start, end);
        if(result.size() > 0 ){
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(parseStringToDate(end, DATE_PATTERN_1));
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
            result.add(getDateString(tempStart.getTime(), DATE_PATTERN_1));
        }
        return result;
    }

    public static List<String> getRunDays(String startDay, String endDay, String repeats) {
        List<String> days = new ArrayList<>();
        List<Date> dates = getBetweenDates(startDay, endDay);
        if (CollectionUtils.isEmpty(dates)) {
            return days;
        }
        dates.stream().forEach(day -> {
            int dayOfWeek = day.getDay();
            if (dayOfWeek == 0) {
                dayOfWeek = 7;
            }
            if (repeats.contains(String.valueOf(dayOfWeek))) {
                days.add(getDateString(day, DATE_PATTERN_1));
            }
        });

        return days;
    }

    /**
     * local时间转换成UTC时间
     * @param localTime yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String localToUTC(String localTime) {
        Date localDate = getDateByTimeStr(localTime);
        long localTimeInMillis=localDate.getTime();
        /** long时间转换成Calendar */
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(localTimeInMillis);
        /** 取得时间偏移量 */
        int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        /** 取得夏令时差 */
        int dstOffset = calendar.get(Calendar.DST_OFFSET);
        /** 从本地时间里扣除这些差量，即可以取得UTC时间*/
        calendar.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        /** 取得的时间就是UTC标准时间 */
        Date utcDate=new Date(calendar.getTimeInMillis());
        return getDateString(utcDate, "yyyy-MM-dd'T'HH:mm:ss'Z'");
    }

    /**
     * utc时间转成local时间
     * @param utcTime yyyy-MM-ddTHH:mm:ssZ
     * @return
     */
    public static Date utcToLocal(String utcTime){
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern(DATE_PATTERN_1).appendLiteral('T')
                .appendPattern("HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .optionalEnd()
                .appendLiteral('Z')
                .toFormatter();
        LocalDateTime ldt;
        try {
            ldt = LocalDateTime.parse(utcTime, formatter);
        } catch (Exception e) {
            return null;
        }
        ZonedDateTime zdt = ldt.atZone(ZoneId.of("UTC"));
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    /**
     * 转变时间格式
     *
     * @return
     */
    public static String changDataFormat(String dataStr, String formatFrom, String formatTo) {
        if (org.apache.commons.lang3.StringUtils.isBlank(dataStr)) {
            return dataStr;
        }
        try {
            SimpleDateFormat simpleFormatFrom = new SimpleDateFormat(formatFrom);
            return new SimpleDateFormat(formatTo).format(simpleFormatFrom.parse(dataStr));
        } catch (ParseException e) {
            logger.error("{} from {} to {} error!", dataStr, formatFrom, formatTo);
        }
        return dataStr;
    }


    public static Date startOfDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static void main(String[] args) throws ParseException {
//        System.out.println(getBetweenDates(new Date(), 10));
//        String start = DateUtil.getDateString(DateUtil.getYearFirst(2018), DateUtil.DATE_PATTERN_DEFAULT);
//        String end = DateUtil.getDateString(DateUtil.getYearLast(2018), DateUtil.DATE_PATTERN_DEFAULT);
//        System.out.println("start=" + start);
//        System.out.println("end = " + end);
//        System.out.println(getLastDayOfMonth(2017, 12));
//        System.out.println(getFirstDayOfMonth(2017, 12));
//        System.out.println(getCurrentMonthDates());
//        String currentDay = DateUtil.getDateString(new Date(), "yyyy-MM-dd");
//        String endDay = "2018.03.11".replace(".", "-");
//        System.out.println(compare_date(currentDay, endDay, null));
//        String date = DateTime.now().toString(SensefaceConstant.FORMART_DATE);
//        System.out.println(date);
//        date = new DateTime(System.currentTimeMillis()).toString(SensefaceConstant.FORMART_DATE);
//        System.out.println(date);
//        System.out.println(getRunDays("2017-03-13", "2017-03-16", "1"));
        /*List<String> haha = getBetweenDatesString("2018-05-13","2018-06-04");
		haha.stream().forEach(t -> { System.out.println(t);});*/
        String currentDay = DateUtil.getDateString(new Date(), DATE_PATTERN_1);
        System.out.println(currentDay);
    }
}
