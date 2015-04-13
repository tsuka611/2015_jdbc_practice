package jp.co.aw.practice.jdbc.operations;

import static jp.co.aw.practice.jdbc.utils.UnitTestUtils.deleteTables;
import static jp.co.aw.practice.jdbc.utils.UnitTestUtils.newline;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.List;

import jp.co.aw.practice.jdbc.dbcp.ConnectionUtils;
import jp.co.aw.practice.jdbc.entity.Employee;
import jp.co.aw.practice.jdbc.service.EmployeeService;
import jp.co.aw.practice.jdbc.utils.CloseUtils;
import jp.co.aw.practice.jdbc.utils.ConsoleWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InsertOperationTest {

    Connection connection;
    EmployeeService employeeService;
    InsertOperation operation;

    @Before
    public void setUp() throws Exception {
        employeeService = new EmployeeService();
        operation = InsertOperation.builder().employeeService(employeeService).build();
        connection = ConnectionUtils.checkoutConnection();
        connection.setAutoCommit(true);

        deleteTables(connection, EmployeeService.tableName());
    }

    @After
    public void tearDown() {
        CloseUtils.autocloseQuietly(connection);
    }

    @Test(expected = NullPointerException.class)
    public void constractor_employeeServiceがnull() {
        new InsertOperation(null);
    }

    @Test
    public void execute_nameが不正() {
        StringBuilder input = new StringBuilder()//
                .append("").append(newline())//
                .append("mail@example.com").append(newline())//
                .append("001-1234").append(newline());
        StringBuilder expected = new StringBuilder()//
                .append("name: ").append("mail: ").append("tel: ")//
                .append("[]はnameとして不適切です。").append(newline())//
                .append("入力値が正しくないため処理を中断しました。").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);
        int ret = operation.execute(c);
        assertThat(ret, is(1));
        assertThat(w.getBuffer().toString(), is(expected.toString()));
    }

    @Test
    public void execute_mailが不正() {
        StringBuilder input = new StringBuilder()//
                .append("テスト太郎").append(newline())//
                .append("めーるあどれす").append(newline())//
                .append("001-1234").append(newline());
        StringBuilder expected = new StringBuilder()//
                .append("name: ").append("mail: ").append("tel: ")//
                .append("[めーるあどれす]はmailとして不適切です。").append(newline())//
                .append("入力値が正しくないため処理を中断しました。").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);
        int ret = operation.execute(c);
        assertThat(ret, is(1));
        assertThat(w.getBuffer().toString(), is(expected.toString()));
    }

    @Test
    public void execute_telが不正() {
        StringBuilder input = new StringBuilder()//
                .append("テスト太郎").append(newline())//
                .append("mail@example.com").append(newline())//
                .append("てる").append(newline());
        StringBuilder expected = new StringBuilder()//
                .append("name: ").append("mail: ").append("tel: ")//
                .append("[てる]はtelとして不適切です。").append(newline())//
                .append("入力値が正しくないため処理を中断しました。").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);
        int ret = operation.execute(c);
        assertThat(ret, is(1));
        assertThat(w.getBuffer().toString(), is(expected.toString()));
    }

    @Test
    public void execute_正常登録() {
        StringBuilder input = new StringBuilder()//
                .append("テスト太郎").append(newline())//
                .append("mail@example.com").append(newline())//
                .append("001-1234").append(newline());
        StringBuilder expected = new StringBuilder()//
                .append("name: ").append("mail: ").append("tel: ")//
                .append("テスト太郎を登録しました。").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);
        int ret = operation.execute(c);
        assertThat(ret, is(0));
        assertThat(w.getBuffer().toString(), is(expected.toString()));

        List<Employee> db = employeeService.findAll();
        assertThat(db.size(), is(1));
        {
            Employee e = db.get(0);
            assertThat(e.getId(), is(notNullValue()));
            assertThat(e.getName(), is("テスト太郎"));
            assertThat(e.getMail(), is("mail@example.com"));
            assertThat(e.getTel(), is("001-1234"));
            assertThat(e.getUpdateDate(), is(notNullValue()));
            assertThat(e.getIsDeleted(), is(false));
        }
    }

    @Test
    public void execute_mailとtelを空登録() {
        StringBuilder input = new StringBuilder()//
                .append("テスト太郎").append(newline())//
                .append("").append(newline())//
                .append("").append(newline());
        StringBuilder expected = new StringBuilder()//
                .append("name: ").append("mail: ").append("tel: ")//
                .append("テスト太郎を登録しました。").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);
        int ret = operation.execute(c);
        assertThat(ret, is(0));
        assertThat(w.getBuffer().toString(), is(expected.toString()));

        List<Employee> db = employeeService.findAll();
        assertThat(db.size(), is(1));
        {
            Employee e = db.get(0);
            assertThat(e.getId(), is(notNullValue()));
            assertThat(e.getName(), is("テスト太郎"));
            assertThat(e.getMail(), is(nullValue()));
            assertThat(e.getTel(), is(nullValue()));
            assertThat(e.getUpdateDate(), is(notNullValue()));
            assertThat(e.getIsDeleted(), is(false));
        }
    }

}
