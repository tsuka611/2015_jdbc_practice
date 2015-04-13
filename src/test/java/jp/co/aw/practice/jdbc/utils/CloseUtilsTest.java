package jp.co.aw.practice.jdbc.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;

import jp.co.aw.practice.jdbc.utils.UnitTestUtils.MockCloseable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Closer;

public class CloseUtilsTest {

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

    @Test
    public void autocloseQuietly_通常() {
        List<MockCloseable> cs = new LinkedList<>();
        cs.add(new MockCloseable());
        cs.add(new MockCloseable());
        cs.add(null);
        cs.add(new MockCloseable());

        cs.get(1).setThrowException(true);

        CloseUtils.autocloseQuietly(cs.toArray(new AutoCloseable[0]));

        assertThat(cs.get(0).getCallCount(), is(1));
        assertThat(cs.get(1).getCallCount(), is(1));
        assertThat(cs.get(2), is(nullValue()));
        assertThat(cs.get(3).getCallCount(), is(1));
    }

    @Test
    public void autocloseQuietly_引数がnullもしくは空でエラーにならない() {
        CloseUtils.autocloseQuietly();
        CloseUtils.autocloseQuietly((AutoCloseable[]) null);
    }

    @Test(expected = NullPointerException.class)
    public void rethrow_closerがnull() {
        CloseUtils.rethrow(null, new Exception());
    }

    @Test(expected = NullPointerException.class)
    public void rethrow_causeがnull() {
        CloseUtils.rethrow(Closer.create(), null);
    }

    @Test(expected = RuntimeException.class)
    public void rethrow_通常() {
        CloseUtils.rethrow(Closer.create(), new Exception());
    }
}
