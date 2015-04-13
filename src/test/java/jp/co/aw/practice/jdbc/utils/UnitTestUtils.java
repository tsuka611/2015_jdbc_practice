package jp.co.aw.practice.jdbc.utils;

import static jp.co.aw.practice.jdbc.utils.AutocloseableWrapper.wrap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.time.Instant;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

import com.google.common.base.Strings;
import com.google.common.io.Closer;

public class UnitTestUtils {
    public static class MockCloseable implements Closeable, AutoCloseable {
        @Setter
        boolean throwException = false;
        @Getter
        int callCount = 0;

        @Override
        public void close() throws IOException {
            callCount++;
            if (throwException) {
                throw new IOException("Dummy Exception.");
            }
        }
    }

    public static void assertZonedDateTime(ZonedDateTime actual, ZonedDateTime expected) {
        assertInstant(actual == null ? null : actual.toInstant(), expected == null ? null : expected.toInstant());
    }

    public static void assertInstant(Instant actual, Instant expected) {
        if (expected == null) {
            assertThat(actual, is(nullValue()));
            return;
        }
        assertThat(actual, is(notNullValue()));
        assertThat(String.format(String.format("Expected [%s] but was [%s]", expected, actual)), actual.getEpochSecond(), is(expected.getEpochSecond()));
    }

    public static void deleteTables(Connection c, String... tables) {
        Closer closer = Closer.create();
        try {
            Statement st = closer.register(wrap(c.createStatement())).getCloseable();
            for (String table : tables) {
                if (Strings.isNullOrEmpty(table)) {
                    continue;
                }
                st.executeUpdate(String.format("delete from %s", table));
            }
        } catch (Exception e) {
            throw CloseUtils.rethrow(closer, e);
        } finally {
            CloseUtils.closeQuietly(closer);
        }
    }

    public static String newline() {
        return String.format("%n");
    }
}
