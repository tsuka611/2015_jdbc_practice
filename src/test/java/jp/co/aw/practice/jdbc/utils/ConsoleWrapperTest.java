package jp.co.aw.practice.jdbc.utils;

import static jp.co.aw.practice.jdbc.utils.UnitTestUtils.newline;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import lombok.Getter;
import lombok.Setter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConsoleWrapperTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @SuppressWarnings("resource")
    @Test(expected = NullPointerException.class)
    public void constructor_readerがnull() {
        StringReader r = null;
        StringWriter w = new StringWriter();
        new ConsoleWrapper(r, w);
    }

    @SuppressWarnings("resource")
    @Test(expected = NullPointerException.class)
    public void constructor_writerがnull() {
        StringReader r = new StringReader("test");
        StringWriter w = null;
        new ConsoleWrapper(r, w);
    }

    @SuppressWarnings("resource")
    @Test
    public void readLine_noarg_通常() {
        String readLines = String.format("test01%ntest02%ntest03");
        StringReader r = new StringReader(readLines);
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);

        assertThat(c.readLine(), is("test01"));
        assertThat(c.readLine(), is("test02"));
        assertThat(c.readLine(), is("test03"));
        assertThat(c.readLine(), is(nullValue()));
    }

    @SuppressWarnings("resource")
    @Test
    public void readLine_withArg_通常() {
        String readLines = String.format("test01%ntest02%ntest03");
        StringReader r = new StringReader(readLines);
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);

        assertThat(c.readLine("NoArg"), is("test01"));
        assertThat(c.readLine("WithStr[%s]", "str"), is("test02"));
        assertThat(c.readLine("WithInt[%d]", 999), is("test03"));
        assertThat(c.readLine("%s%s"), is(nullValue()));
        assertThat(c.readLine("newLine%nnewLine", ""), is(nullValue()));

        StringBuilder expected = new StringBuilder() //
                .append("NoArg")//
                .append("WithStr[str]")//
                .append("WithInt[999]")//
                .append("%s%s")//
                .append("newLine" + newline() + "newLine");
        assertThat(w.getBuffer().toString(), is(expected.toString()));
    }

    @SuppressWarnings("resource")
    @Test
    public void println_通常() {
        StringReader r = new StringReader("");
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);

        c.println("hello");
        c.println("Test%02d", 1);
        c.println("xxx", 1);
        c.println("%s");

        assertThat(w.getBuffer().toString(), is("hello" + newline() + "Test01" + newline() + "xxx" + newline() + "%s" + newline()));
    }

    class MockReader extends StringReader {

        @Setter
        boolean throwException = false;

        @Getter
        int callCount = 0;

        public MockReader(String s) {
            super(s);
        }

        @Override
        public void close() {
            callCount++;
            if (throwException) {
                throw new RuntimeException("Dummy Excepiton.");
            }
            super.close();
        }
    }

    class MockWriter extends StringWriter {
        @Setter
        boolean throwException = false;

        @Getter
        int callCount = 0;

        @Override
        public void close() throws IOException {
            callCount++;
            if (throwException) {
                throw new RuntimeException("Dummy Excepiton.");
            }
            super.close();
        }
    }

    @Test
    public void close_通常() {
        MockReader r = new MockReader("mock");
        MockWriter w = new MockWriter();

        ConsoleWrapper c = new ConsoleWrapper(r, w);
        c.close();

        assertThat(r.getCallCount(), is(1));
        assertThat(w.getCallCount(), is(1));
    }

    @Test
    public void close_例外が発生するケース() {
        MockReader r = new MockReader("mock");
        r.setThrowException(true);
        MockWriter w = new MockWriter();
        w.setThrowException(true);

        ConsoleWrapper c = new ConsoleWrapper(r, w);
        try {
            c.close();
            fail("Exception must occur.");
        } catch (RuntimeException e) {
        }
        assertThat(r.getCallCount(), is(1));
        assertThat(w.getCallCount(), is(1));
    }
}
