package jp.co.aw.practice.jdbc.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import org.junit.After;
import org.junit.Test;

import com.google.common.base.Strings;

public class ValidationUtilsTest {

    static Predicate<String> isNotEmpty = s -> {
        return !Strings.isNullOrEmpty(s);
    };

    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void validate_通常() {
        AtomicLong failCount = new AtomicLong();
        assertThat(ValidationUtils.validate("", s -> {
            failCount.addAndGet(1L);
            assertThat(s, is(""));
        }, isNotEmpty), is(false));
        assertThat(ValidationUtils.validate("xxx", s -> {
            failCount.addAndGet(1L);
            fail("This case should not fail.");
        }, isNotEmpty), is(true));
        assertThat(failCount.get(), is(1L));
    }

    @Test
    public void validate_validationsが無い() {
        assertThat(ValidationUtils.validate("", s -> {
            fail("This case should not fail.");
        }), is(true));
    }

    @Test(expected = NullPointerException.class)
    public void validate_validationsがnull() {
        Predicate<String>[] vs = null;
        assertThat(ValidationUtils.validate("", s -> {
            fail("This case should not fail.");
        }, vs), is(true));
    }

    public void validate_failoperationがnull() {
        assertThat(ValidationUtils.validate("", null, isNotEmpty), is(false));
        assertThat(ValidationUtils.validate("xxx", null, isNotEmpty), is(true));
    }
}
