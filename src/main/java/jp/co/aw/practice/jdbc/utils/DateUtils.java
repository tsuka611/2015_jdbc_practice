package jp.co.aw.practice.jdbc.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Date;

import com.google.common.base.Strings;

public class DateUtils {

    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

    public static ZoneId defaultZoneId() {
        return ZoneId.of("Asia/Tokyo");
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now(defaultZoneId());
    }

    public static Timestamp ts(ZonedDateTime z) {
        return z == null ? null : Timestamp.from(z.toInstant());
    }

    public static ZonedDateTime z(Date d) {
        return d == null ? null : d.toInstant().atZone(defaultZoneId());
    }

    public static ZonedDateTime parse(String s) {
        checkArgument(!Strings.isNullOrEmpty(s));
        return LocalDateTime.parse(s, DEFAULT_FORMATTER).atZone(defaultZoneId());
    }

    public static String format(ZonedDateTime z) {
        checkNotNull(z);
        return z.format(DEFAULT_FORMATTER);
    }
}
