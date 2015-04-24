package jp.co.aw.practice.jdbc.operations;

import static jp.co.aw.practice.jdbc.service.EmployeeServiceTest.findById;
import static jp.co.aw.practice.jdbc.service.EmployeeServiceTest.insert;
import static jp.co.aw.practice.jdbc.utils.DateUtils.now;
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

public class SelectIdOperationTest {

    Connection connection;
    EmployeeService employeeService;
    SelectIdOperation operation;

    @Before
    public void setUp() throws Exception {
        employeeService = new EmployeeService();
        operation = SelectIdOperation.builder().employeeService(employeeService).build();
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
        new SelectIdOperation(null);
    }

    @Test
    public void execute_IDが不正() throws Exception {
        StringBuilder input = new StringBuilder()//
                .append("").append(newline());
        StringBuilder expected = new StringBuilder()//
                .append("検索したいIDを入力してください。").append(newline())//
                .append("[]はIDとして不適切です。").append(newline())//
                .append("入力値が正しくないため処理を中断しました。").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);
        int ret = operation.execute(c);
        assertThat(ret, is(1));
        assertThat(w.getBuffer().toString(), is(expected.toString()));
    }

    @Test
    public void execute_削除済みIDが指定される() {
        long id = insert(connection, "テスト太郎", "mail@example.com", "001-1234", now(), true);
        StringBuilder input = new StringBuilder()//
                .append(id).append(newline());
        StringBuilder expected = new StringBuilder()//
                .append("検索したいIDを入力してください。").append(newline())//
                .append(String.format("ID[%s]のレコードは存在しませんでした。", id)).append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);
        int ret = operation.execute(c);
        assertThat(ret, is(0));
        assertThat(w.getBuffer().toString(), is(expected.toString()));
    }

    @Test
    public void execute_通常() {
        Employee e = findById(connection, insert(connection, "テスト太郎", "mail@example.com", "001-1234", now(), false));
        StringBuilder input = new StringBuilder()//
                .append(e.getId()).append(newline());
        StringBuilder expected = new StringBuilder()//
                .append("検索したいIDを入力してください。").append(newline())//
                .append(String.format("ID[%s]のユーザが見つかりました。", e.getId())).append(newline())//
                .append(e.toConsoleFormat()).append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);
        int ret = operation.execute(c);
        assertThat(ret, is(0));
        assertThat(w.getBuffer().toString(), is(expected.toString()));
    }
}
