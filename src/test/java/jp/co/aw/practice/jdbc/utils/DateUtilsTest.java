package jp.co.aw.practice.jdbc.utils;

import static jp.co.aw.practice.jdbc.utils.DateUtils.defaultZoneId;
import static jp.co.aw.practice.jdbc.utils.DateUtils.ts;
import static jp.co.aw.practice.jdbc.utils.DateUtils.z;
import static jp.co.aw.practice.jdbc.utils.UnitTestUtils.assertInstant;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.junit.Test;

public class DateUtilsTest {

    @Test
    public void defaultZoneId_通常() {
        assertThat(defaultZoneId(), is(notNullValue()));
        assertThat(defaultZoneId(), is(ZoneId.of("Asia/Tokyo")));
    }

    @Test
    public void now_通常() {
        ZonedDateTime z = DateUtils.now();
        assertThat(z, is(notNullValue()));
    }

    @Test
    public void ts_通常() {
        {
            ZonedDateTime z = ZonedDateTime.now();
            Timestamp ret = ts(z);
            assertInstant(ret.toInstant(), z.toInstant());
        }
        {
            assertThat(ts(null), is(nullValue()));
        }
    }

    @Test
    public void z_通常() throws Exception {
        {
            Date d = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2015/01/02 11:22:33");
            ZonedDateTime ret = z(d);
            assertInstant(ret.toInstant(), d.toInstant());
        }
        {
            assertThat(z(null), is(nullValue()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_引数がnull() {
        DateUtils.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_引数が空() {
        DateUtils.parse("");
    }

    @Test(expected = DateTimeParseException.class)
    public void parse_フォーマット不正() {
        DateUtils.parse("xxx");
    }

    @Test
    public void parse_通常() throws ParseException {
        ZonedDateTime ret = DateUtils.parse("2015/01/02 11:22:33");
        assertInstant(ret.toInstant(), new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2015/01/02 11:22:33").toInstant());
    }

    @Test
    public void format_通常() throws ParseException {
        Date d = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse("2015/01/02 11:22:33");
        String ret = DateUtils.format(d.toInstant().atZone(defaultZoneId()));
        assertThat(ret, is("2015/01/02 11:22:33"));
    }

    @Test(expected = NullPointerException.class)
    public void format_引数がnull() {
        DateUtils.format(null);
    }
}
