package jp.co.aw.practice.jdbc.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CloseUtilsTest {
    static class MockCloseable implements Closeable {
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

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void closeQuietly_通常() {
        List<MockCloseable> cs = new LinkedList<>();
        cs.add(new MockCloseable());
        cs.add(new MockCloseable());
        cs.add(null);
        cs.add(new MockCloseable());

        cs.get(1).setThrowException(true);

        CloseUtils.closeQuietly(cs.toArray(new Closeable[0]));

        assertThat(cs.get(0).getCallCount(), is(1));
        assertThat(cs.get(1).getCallCount(), is(1));
        assertThat(cs.get(2), is(nullValue()));
        assertThat(cs.get(3).getCallCount(), is(1));
    }

    @Test
    public void closeQuietly_引数がnullもしくは空でエラーにならない() {
        CloseUtils.closeQuietly();
        CloseUtils.closeQuietly((Closeable[]) null);
    }
}
