package jp.co.aw.practice.jdbc.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

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
}
