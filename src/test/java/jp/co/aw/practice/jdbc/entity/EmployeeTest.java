package jp.co.aw.practice.jdbc.entity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EmployeeTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void setIsDeleted_通常() {

        Employee e = new Employee();

        assertThat(e.setIsDeleted((byte) 0), is(false));
        assertThat(e.getIsDeleted(), is(false));

        assertThat(e.setIsDeleted((byte) 1), is(true));
        assertThat(e.getIsDeleted(), is(true));

        assertThat(e.setIsDeleted((byte) 2), is(true));
        assertThat(e.getIsDeleted(), is(true));

        assertThat(e.setIsDeleted(null), is(true));
        assertThat(e.getIsDeleted(), is(true));

    }
}
