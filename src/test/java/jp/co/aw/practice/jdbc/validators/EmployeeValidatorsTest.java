package jp.co.aw.practice.jdbc.validators;

import static jp.co.aw.practice.jdbc.utils.ValidationUtils.validate;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.required;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validId;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validMail;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validName;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validTel;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EmployeeValidatorsTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void required_test_通常() {
        assertThat(required().test(null), is(false));
        assertThat(required().test(""), is(false));
        assertThat(required().test("xxxx"), is(true));
    }

    @Test
    public void validId_通常() {
        List<String> successFormat = Arrays.asList(//
                "0",//
                "01",//
                "12345678901");
        for (String value : successFormat) {
            AtomicLong failCount = new AtomicLong();
            assertThat(String.format("check [%s] failed.", value), validate(value, s -> {
                failCount.addAndGet(1L);
                fail(String.format("[%s]This case should not fail.", s));
            }, validId()), is(true));
            assertThat(failCount.get(), is(0L));
        }

        List<String> failFormat = Arrays.asList( //
                null, //
                "",//
                "1a", "-1",//
                "123456789012");
        for (String value : failFormat) {
            AtomicLong failCount = new AtomicLong();
            assertThat(String.format("check [%s] failed.", value), validate(value, s -> {
                failCount.addAndGet(1L);
                assertThat(s, is(value));
            }, validId()), is(false));
            assertThat(failCount.get(), is(1L));
        }
    }

    @Test
    public void validName_通常() {
        List<String> successFormat = Arrays.asList(//
                "hello");
        for (String value : successFormat) {
            AtomicLong failCount = new AtomicLong();
            assertThat(String.format("check [%s] failed.", value), validate(value, s -> {
                failCount.addAndGet(1L);
                fail(String.format("[%s]This case should not fail.", s));
            }, validName()), is(true));
            assertThat(failCount.get(), is(0L));
        }

        List<String> failFormat = Arrays.asList( //
                null, //
                "");
        for (String value : failFormat) {
            AtomicLong failCount = new AtomicLong();
            assertThat(String.format("check [%s] failed.", value), validate(value, s -> {
                failCount.addAndGet(1L);
                assertThat(s, is(value));
            }, validName()), is(false));
            assertThat(failCount.get(), is(1L));
        }
        {
            AtomicLong failCount = new AtomicLong();
            StringBuilder sb = new StringBuilder();
            IntStream.range(0, 300).forEach(i -> {
                sb.append("あ");
            });
            String value = sb.toString();
            assertThat(validate(value, s -> {
                failCount.addAndGet(1L);
                fail("This case should not fail.");
            }, validName()), is(true));
            assertThat(failCount.get(), is(0L));
        }
    }

    @Test
    public void validMail_通常() {
        List<String> successFormat = Arrays.asList(//
                null,//
                "",//
                "mail@example.com", //
                "mail__mail@example.com", //
                "mail--mail@example.com", //
                "mail+m@example.com");
        for (String value : successFormat) {
            AtomicLong failCount = new AtomicLong();
            assertThat(String.format("check [%s] failed.", value), validate(value, s -> {
                failCount.addAndGet(1L);
                fail(String.format("[%s]This case should not fail.", s));
            }, validMail()), is(true));
            assertThat(failCount.get(), is(0L));
        }

        List<String> failFormat = Arrays.asList( //
                "errormail", //
                "error@mail", //
                "error@mail.", //
                "error:error@mail.com", //
                "error{},error@mail.com", //
                "error@mail..com");
        for (String value : failFormat) {
            AtomicLong failCount = new AtomicLong();
            assertThat(String.format("check [%s] failed.", value), validate(value, s -> {
                failCount.addAndGet(1L);
                assertThat(s, is(value));
            }, validMail()), is(false));
            assertThat(failCount.get(), is(1L));
        }

        {
            AtomicLong failCount = new AtomicLong();
            StringBuilder sb = new StringBuilder();
            IntStream.range(0, 300).forEach(i -> {
                sb.append("a");
            });
            String value = sb.append("@example.com").toString();
            assertThat(validate(value, s -> {
                failCount.addAndGet(1L);
                fail("This case should not fail.");
            }, validMail()), is(true));
            assertThat(failCount.get(), is(0L));
        }
    }

    @Test
    public void validTel_通常() {

        List<String> successFormat = Arrays.asList(//
                null,//
                "",//
                "123", //
                "123-123", //
                "123-123-123");
        for (String value : successFormat) {
            AtomicLong failCount = new AtomicLong();
            assertThat(String.format("check [%s] failed.", value), validate(value, s -> {
                failCount.addAndGet(1L);
                fail(String.format("[%s]This case should not fail.", s));
            }, validTel()), is(true));
            assertThat(failCount.get(), is(0L));
        }

        List<String> failFormat = Arrays.asList( //
                "-000", //
                "-", //
                "000-000-", //
                "00--00", //
                "000.000", //
                "0 0", //
                "000+000");
        for (String value : failFormat) {
            AtomicLong failCount = new AtomicLong();
            assertThat(String.format("check [%s] failed.", value), validate(value, s -> {
                failCount.addAndGet(1L);
                assertThat(s, is(value));
            }, validTel()), is(false));
            assertThat(failCount.get(), is(1L));
        }
        {
            AtomicLong failCount = new AtomicLong();
            StringBuilder sb = new StringBuilder();
            IntStream.range(0, 300).forEach(i -> {
                sb.append("1");
            });
            String value = sb.toString();
            assertThat(validate(value, s -> {
                failCount.addAndGet(1L);
                fail("This case should not fail.");
            }, validTel()), is(true));
            assertThat(failCount.get(), is(0L));
        }
    }

}
