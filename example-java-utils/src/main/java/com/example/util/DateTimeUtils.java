package com.example.util;

import java.time.*;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author: zhouyuyang
 * @Date: 2020/7/20 17:36
 */
public class DateTimeUtils {

    private DateTimeUtils() {}

    /**
     * 系统时区Offset
     */
    private static final ZoneOffset SYSTEM_ZONE_OFFSET = OffsetDateTime.now().getOffset();


    /**
     * 时间戳转本地日期时间
     * @param timestamp
     * @return
     */
    public static LocalDateTime localDateTimeOf(Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), SYSTEM_ZONE_OFFSET);
    }

    /**
     * 时间戳本地日期
     * @param timestamp
     * @return
     */
    public static LocalDate localDateOf(Long timestamp) {
        LocalDateTime localDateTime = localDateTimeOf(timestamp);
        return localDateTime.toLocalDate();
    }


    /**
     * LocalDateTime转为Date对象
     * @param ldt
     * @return
     */
    public static Date toDate(LocalDateTime ldt) {
        return Optional.ofNullable(ldt)
                .map(l -> Date.from(l.toInstant(SYSTEM_ZONE_OFFSET)))
                .orElse(null);
    }


    /**
     * 将ZonedDateTime转为Date
     * @param zdt
     * @return
     */
    public static Date toDate(ZonedDateTime zdt) {
        return Optional.ofNullable(zdt)
                .map(z -> Date.from(z.toInstant()))
                .orElse(null);
    }

    /**
     * 将OffsetDateTime转为Date
     * @param odt
     * @return
     */
    public static Date toDate(OffsetDateTime odt) {
        return Optional.ofNullable(odt)
                .map(o -> Date.from(o.toInstant()))
                .orElse(null);
    }


    /**
     *
     * @param timestamp
     * @return
     */
    public static Date getDate(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp);
    }


    /**
     * 转Date对象
     * @param localDate
     * @param localTime
     * @return
     */
    public static Date toDate(LocalDate localDate, LocalTime localTime) {
        return toDate(LocalDateTime.of(
                Objects.requireNonNull(localDate, "localDate is null"),
                Objects.requireNonNull(localTime, "localTime is null")));
    }

    /**
     * LocalDate转Date对象，并以一天开始时间返回
     * @param localDate
     * @return
     */
    public static Date toDateAtFirstOfDay(LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(ld -> toDate(ld.atStartOfDay()))
                .orElse(null);
    }


    /**
     * LocalDate转Date对象，并以一天结束时间返回
     * @param localDate
     * @return
     */
    public static Date toDateAtEndOfDay(LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(ld -> toDate(ld, LocalTime.of(23, 59, 59, 999_999_999)))
                .orElse(null);
    }


    /**
     * 转为UTC时间
     * @param date
     * @return
     */
    public static ZonedDateTime toUtcTime(Date date) {
        return Optional.ofNullable(date)
                .map(d -> ZonedDateTime.ofInstant(d.toInstant(), ZoneOffset.UTC))
                .orElse(null);
    }


    public static void main(String[] args) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        System.out.println(zdt);
    }
}
