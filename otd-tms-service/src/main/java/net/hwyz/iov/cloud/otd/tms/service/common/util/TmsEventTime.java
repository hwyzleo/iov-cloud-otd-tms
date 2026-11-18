package net.hwyz.iov.cloud.otd.tms.service.common.util;

import net.hwyz.iov.cloud.framework.common.util.ExplicitTimeUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * 事件时间格式化（OTD-TMS-DSN-CR-001 INV-006）
 * <p>
 * 对外 JSON 与事件时间统一 ISO-8601：带时区偏移、固定 3 位毫秒。
 * 时刻来源遵循框架时间治理：事件时刻取 {@link ExplicitTimeUtil#nowInstant()}，时区显式传入。
 *
 * @author hwyz_leo
 */
public final class TmsEventTime {

    private static final DateTimeFormatter EVENT_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSXXX");

    private TmsEventTime() {
    }

    /**
     * 当前事件时刻
     *
     * @return 绝对时刻
     */
    public static Instant now() {
        return ExplicitTimeUtil.nowInstant();
    }

    /**
     * 格式化绝对时刻
     *
     * @param instant 绝对时刻
     * @param zone    时区
     * @return ISO-8601 文本
     */
    public static String format(Instant instant, ZoneId zone) {
        Objects.requireNonNull(instant, "instant");
        Objects.requireNonNull(zone, "zone");
        return EVENT_FORMATTER.format(ExplicitTimeUtil.toOffsetDateTime(instant, zone));
    }

    /**
     * 格式化时间对象（null 透传）
     *
     * @param date 时间
     * @param zone 时区
     * @return ISO-8601 文本
     */
    public static String format(Date date, ZoneId zone) {
        if (date == null) {
            return null;
        }
        return format(date.toInstant(), zone);
    }

    /**
     * 严格解析事件时间（必须带偏移）
     *
     * @param text ISO-8601 文本
     * @return 绝对时刻
     */
    public static Instant parse(String text) {
        return ExplicitTimeUtil.parseInstantIso(text);
    }
}
