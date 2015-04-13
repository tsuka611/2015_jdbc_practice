package jp.co.aw.practice.jdbc.entity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import jp.co.aw.practice.jdbc.utils.DateUtils;

import org.junit.Test;

public class EmployeeTest {

    @Test
    public void toConsoleFormat_全てのフィールドがnull() {
        Employee e = new Employee();
        String excepted = "<NoID> <NoName> <NoMail> <NoTel> <NoUpdateDate>";
        assertThat(e.toConsoleFormat(), is(excepted));
    }

    @Test
    public void toConsoleFormat_全てのフィールドが設定されている() {
        Employee e = Employee.builder()//
                .id(999L)//
                .name("テスト太郎")//
                .mail("mail@example.com")//
                .tel("001-1234")//
                .updateDate(DateUtils.parse("2015/04/10 01:02:03"))//
                .isDeleted(false)//
                .build();
        String excepted = "999 テスト太郎 mail@example.com 001-1234 2015/04/10 01:02:03";
        assertThat(e.toConsoleFormat(), is(excepted));
    }
}
