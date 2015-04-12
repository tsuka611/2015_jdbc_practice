package jp.co.aw.practice.jdbc.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.co.aw.practice.jdbc.ApplicationException;

import com.google.common.base.Strings;

public class DateUtils {

    public static final String FORMAT = "yyyy/MM/dd HH:mm:ss";

    public static Timestamp now() {
        return ts(new Date());
    }

    public static Timestamp ts(Date d) {
        if (d == null) {
            return null;
        }
        if (d instanceof Timestamp) {
            return (Timestamp) d;
        }
        return new Timestamp(d.getTime());
    }

    public static Timestamp parse(String s) {
        checkArgument(!Strings.isNullOrEmpty(s));
        try {
            return ts(new SimpleDateFormat(FORMAT).parse(s));
        } catch (ParseException e) {
            throw new ApplicationException(e);
        }
    }

    public static String format(Date d) {
        checkNotNull(d);
        return new SimpleDateFormat(FORMAT).format(d);
    }
}
