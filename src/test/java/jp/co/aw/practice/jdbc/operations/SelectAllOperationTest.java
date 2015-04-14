package jp.co.aw.practice.jdbc.operations;

import static jp.co.aw.practice.jdbc.service.EmployeeServiceTest.findById;
import static jp.co.aw.practice.jdbc.service.EmployeeServiceTest.insert;
import static jp.co.aw.practice.jdbc.utils.DateUtils.parse;
import static jp.co.aw.practice.jdbc.utils.UnitTestUtils.deleteTables;
import static jp.co.aw.practice.jdbc.utils.UnitTestUtils.newline;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;

import jp.co.aw.practice.jdbc.dbcp.ConnectionUtils;
import jp.co.aw.practice.jdbc.entity.Employee;
import jp.co.aw.practice.jdbc.service.EmployeeService;
import jp.co.aw.practice.jdbc.utils.CloseUtils;
import jp.co.aw.practice.jdbc.utils.ConsoleWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SelectAllOperationTest {

    Connection connection;
    EmployeeService employeeService;
    SelectAllOperation operation;

    @Before
    public void setUp() throws Exception {
        employeeService = new EmployeeService();
        operation = SelectAllOperation.builder().employeeService(employeeService).build();
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
        new SelectAllOperation(null);
    }

    @Test
    public void execute_DBが空の状態() throws Exception {
        StringBuilder input = new StringBuilder();
        StringBuilder expected = new StringBuilder()//
                .append("総勢: 0名").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);
        int ret = operation.execute(c);
        assertThat(ret, is(0));
        assertThat(w.getBuffer().toString(), is(expected.toString()));
    }

    @Test
    public void execute_通常() throws Exception {
        long id01 = insert(connection, "テスト太郎", "mail_taro@example.com", null, parse("2015/04/10 11:22:33"), false);
        Employee e01 = findById(connection, id01);

        long id02 = insert(connection, "テスト二郎", "mail_jiro@example.com", null, parse("2015/04/11 11:22:33"), false);
        Employee e02 = findById(connection, id02);

        insert(connection, "テスト三郎", "mail_sabu@example.com", null, parse("2015/04/12 11:22:33"), true);

        long id04 = insert(connection, "テスト四郎", "mail_siro@example.com", null, parse("2015/04/13 11:22:33"), false);
        Employee e04 = findById(connection, id04);

        StringBuilder input = new StringBuilder();
        StringBuilder expected = new StringBuilder()//
                .append(e01.toConsoleFormat()).append(newline())//
                .append(e02.toConsoleFormat()).append(newline())//
                .append(e04.toConsoleFormat()).append(newline())//
                .append("総勢: 3名").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);
        int ret = operation.execute(c);
        assertThat(ret, is(0));
        assertThat(w.getBuffer().toString(), is(expected.toString()));
    }
}
