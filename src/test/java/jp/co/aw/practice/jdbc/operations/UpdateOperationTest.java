package jp.co.aw.practice.jdbc.operations;

import static jp.co.aw.practice.jdbc.service.EmployeeServiceTest.findById;
import static jp.co.aw.practice.jdbc.service.EmployeeServiceTest.insert;
import static jp.co.aw.practice.jdbc.utils.DateUtils.now;
import static jp.co.aw.practice.jdbc.utils.UnitTestUtils.deleteTables;
import static jp.co.aw.practice.jdbc.utils.UnitTestUtils.newline;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
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

public class UpdateOperationTest {

    Connection connection;
    EmployeeService employeeService;
    UpdateOperation operation;

    @Before
    public void setUp() throws Exception {
        employeeService = new EmployeeService();
        operation = UpdateOperation.builder().employeeService(employeeService).build();
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
        new UpdateOperation(null);
    }

    @Test
    public void execute_通常() throws Exception {
        Employee before = findById(connection, insert(connection, "初期の名前", "メールアドレス", "電話番号", now(), false));

        StringBuilder input = new StringBuilder()//
                .append(before.getId()).append(newline())//
                .append("更新太郎").append(newline())//
                .append("mail_update@example.com").append(newline())//
                .append("123-1234").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);

        int ret = operation.execute(c);
        assertThat(ret, is(0));

        Employee after = findById(connection, before.getId());
        StringBuilder expected = new StringBuilder()//
                .append("更新対象IDを入力してください。").append(newline())//
                .append(before.toConsoleFormat()).append(newline())//
                .append("name: ").append("mail: ").append("tel: ")//
                .append("更新処理が完了しました。").append(newline())//
                .append(after.toConsoleFormat()).append(newline());
        assertThat(w.getBuffer().toString(), is(expected.toString()));

        assertThat(after.getId(), is(before.getId()));
        assertThat(after.getName(), is("更新太郎"));
        assertThat(after.getMail(), is("mail_update@example.com"));
        assertThat(after.getTel(), is("123-1234"));
        assertThat(after.getUpdateDate(), is(notNullValue()));
        assertThat(after.getIsDeleted(), is(false));
    }

    @Test
    public void execute_空文字で更新する() throws Exception {
        Employee before = findById(connection, insert(connection, "初期の名前", "メールアドレス", "電話番号", now(), false));

        StringBuilder input = new StringBuilder()//
                .append(before.getId()).append(newline())//
                .append("更新太郎").append(newline())//
                .append("").append(newline())//
                .append("").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);

        int ret = operation.execute(c);
        assertThat(ret, is(0));

        Employee after = findById(connection, before.getId());
        StringBuilder expected = new StringBuilder()//
                .append("更新対象IDを入力してください。").append(newline())//
                .append(before.toConsoleFormat()).append(newline())//
                .append("name: ").append("mail: ").append("tel: ")//
                .append("更新処理が完了しました。").append(newline())//
                .append(after.toConsoleFormat()).append(newline());
        assertThat(w.getBuffer().toString(), is(expected.toString()));

        assertThat(after.getId(), is(before.getId()));
        assertThat(after.getName(), is("更新太郎"));
        assertThat(after.getMail(), is(nullValue()));
        assertThat(after.getTel(), is(nullValue()));
        assertThat(after.getUpdateDate(), is(notNullValue()));
        assertThat(after.getIsDeleted(), is(false));
    }

    @Test
    public void execute_不正なID入力() throws Exception {
        Employee before = findById(connection, insert(connection, "初期の名前", "メールアドレス", "電話番号", now(), false));

        StringBuilder input = new StringBuilder()//
                .append("xxx").append(newline())//
                .append("更新太郎").append(newline())//
                .append("mail_update@example.com").append(newline())//
                .append("123-1234").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);

        int ret = operation.execute(c);
        assertThat(ret, is(1));

        Employee after = findById(connection, before.getId());
        StringBuilder expected = new StringBuilder()//
                .append("更新対象IDを入力してください。").append(newline())//
                .append("[xxx]はIDとして不適切です。").append(newline())//
                .append("入力値が正しくないため処理を中断しました。").append(newline());
        assertThat(w.getBuffer().toString(), is(expected.toString()));

        assertThat(after.getId(), is(before.getId()));
        assertThat(after.getName(), is(before.getName()));
        assertThat(after.getMail(), is(before.getMail()));
        assertThat(after.getTel(), is(before.getTel()));
        assertThat(after.getUpdateDate(), is(before.getUpdateDate()));
        assertThat(after.getIsDeleted(), is(before.getIsDeleted()));
    }

    @Test
    public void execute_削除済み記事が更新できない() throws Exception {
        Employee before = findById(connection, insert(connection, "初期の名前", "メールアドレス", "電話番号", now(), true));

        StringBuilder input = new StringBuilder()//
                .append(before.getId()).append(newline())//
                .append("更新太郎").append(newline())//
                .append("mail_update@example.com").append(newline())//
                .append("123-1234").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);

        int ret = operation.execute(c);
        assertThat(ret, is(2));

        Employee after = findById(connection, before.getId());
        StringBuilder expected = new StringBuilder()//
                .append("更新対象IDを入力してください。").append(newline())//
                .append(String.format("ID%sは存在しませんでした。", before.getId())).append(newline());
        assertThat(w.getBuffer().toString(), is(expected.toString()));

        assertThat(after.getId(), is(before.getId()));
        assertThat(after.getName(), is(before.getName()));
        assertThat(after.getMail(), is(before.getMail()));
        assertThat(after.getTel(), is(before.getTel()));
        assertThat(after.getUpdateDate(), is(before.getUpdateDate()));
        assertThat(after.getIsDeleted(), is(before.getIsDeleted()));
    }

    @Test
    public void execute_nameの入力が不正() throws Exception {
        Employee before = findById(connection, insert(connection, "初期の名前", "メールアドレス", "電話番号", now(), false));

        StringBuilder input = new StringBuilder()//
                .append(before.getId()).append(newline())//
                .append("").append(newline())//
                .append("mail_update@example.com").append(newline())//
                .append("123-1234").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);

        int ret = operation.execute(c);
        assertThat(ret, is(1));

        Employee after = findById(connection, before.getId());
        StringBuilder expected = new StringBuilder()//
                .append("更新対象IDを入力してください。").append(newline())//
                .append(before.toConsoleFormat()).append(newline())//
                .append("name: ").append("mail: ").append("tel: ")//
                .append("[]はnameとして不適切です。").append(newline())//
                .append("入力値が正しくないため処理を中断しました。").append(newline());
        assertThat(w.getBuffer().toString(), is(expected.toString()));

        assertThat(after.getId(), is(before.getId()));
        assertThat(after.getName(), is(before.getName()));
        assertThat(after.getMail(), is(before.getMail()));
        assertThat(after.getTel(), is(before.getTel()));
        assertThat(after.getUpdateDate(), is(before.getUpdateDate()));
        assertThat(after.getIsDeleted(), is(before.getIsDeleted()));
    }

    @Test
    public void execute_mailの入力が不正() throws Exception {
        Employee before = findById(connection, insert(connection, "初期の名前", "メールアドレス", "電話番号", now(), false));

        StringBuilder input = new StringBuilder()//
                .append(before.getId()).append(newline())//
                .append("更新太郎").append(newline())//
                .append("xxx").append(newline())//
                .append("123-1234").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);

        int ret = operation.execute(c);
        assertThat(ret, is(1));

        Employee after = findById(connection, before.getId());
        StringBuilder expected = new StringBuilder()//
                .append("更新対象IDを入力してください。").append(newline())//
                .append(before.toConsoleFormat()).append(newline())//
                .append("name: ").append("mail: ").append("tel: ")//
                .append("[xxx]はmailとして不適切です。").append(newline())//
                .append("入力値が正しくないため処理を中断しました。").append(newline());
        assertThat(w.getBuffer().toString(), is(expected.toString()));

        assertThat(after.getId(), is(before.getId()));
        assertThat(after.getName(), is(before.getName()));
        assertThat(after.getMail(), is(before.getMail()));
        assertThat(after.getTel(), is(before.getTel()));
        assertThat(after.getUpdateDate(), is(before.getUpdateDate()));
        assertThat(after.getIsDeleted(), is(before.getIsDeleted()));
    }

    @Test
    public void execute_telの入力が不正() throws Exception {
        Employee before = findById(connection, insert(connection, "初期の名前", "メールアドレス", "電話番号", now(), false));

        StringBuilder input = new StringBuilder()//
                .append(before.getId()).append(newline())//
                .append("更新太郎").append(newline())//
                .append("mail_update@example.com").append(newline())//
                .append("xxx").append(newline());
        StringReader r = new StringReader(input.toString());
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);

        int ret = operation.execute(c);
        assertThat(ret, is(1));

        Employee after = findById(connection, before.getId());
        StringBuilder expected = new StringBuilder()//
                .append("更新対象IDを入力してください。").append(newline())//
                .append(before.toConsoleFormat()).append(newline())//
                .append("name: ").append("mail: ").append("tel: ")//
                .append("[xxx]はtelとして不適切です。").append(newline())//
                .append("入力値が正しくないため処理を中断しました。").append(newline());
        assertThat(w.getBuffer().toString(), is(expected.toString()));

        assertThat(after.getId(), is(before.getId()));
        assertThat(after.getName(), is(before.getName()));
        assertThat(after.getMail(), is(before.getMail()));
        assertThat(after.getTel(), is(before.getTel()));
        assertThat(after.getUpdateDate(), is(before.getUpdateDate()));
        assertThat(after.getIsDeleted(), is(before.getIsDeleted()));
    }
}
