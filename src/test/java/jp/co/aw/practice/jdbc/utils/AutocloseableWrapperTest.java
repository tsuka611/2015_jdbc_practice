package jp.co.aw.practice.jdbc.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.Closeable;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AutocloseableWrapperTest {

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

    @Test(expected = NullPointerException.class)
    public void wrap_引数がnull() {
        AutocloseableWrapper.wrap(null);
    }

    @Test
    public void wrap_通常() {
        MockCloseable c = new MockCloseable();
        AutocloseableWrapper<MockCloseable> ret = AutocloseableWrapper.wrap(c);
        assertThat(ret, is(notNullValue()));
        assertThat(ret.getCloseable(), is(sameInstance(c)));
    }

    @Test
    public void close_例外が発生しても問題無いこと() {
        MockCloseable c = new MockCloseable();
        c.setThrowException(true);
        AutocloseableWrapper<MockCloseable> w = AutocloseableWrapper.wrap(c);
        w.close();
        assertThat(c.getCallCount(), is(1));
    }

    @Test
    public void close_nullオブジェクトでも問題無いこと() {
        MockCloseable c = new MockCloseable();
        AutocloseableWrapper<MockCloseable> w = AutocloseableWrapper.wrap(c);
        w.closeable = null;
        w.close();
        assertThat(c.getCallCount(), is(0));
    }

    @Test
    public void close_通常() {
        MockCloseable c = new MockCloseable();
        AutocloseableWrapper<MockCloseable> w = AutocloseableWrapper.wrap(c);
        w.close();
        assertThat(c.getCallCount(), is(1));
    }

}
