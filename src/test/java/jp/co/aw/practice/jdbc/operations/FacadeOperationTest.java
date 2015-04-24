package jp.co.aw.practice.jdbc.operations;

import static jp.co.aw.practice.jdbc.utils.UnitTestUtils.deleteTables;
import static jp.co.aw.practice.jdbc.utils.UnitTestUtils.newline;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jp.co.aw.practice.jdbc.dbcp.ConnectionUtils;
import jp.co.aw.practice.jdbc.service.EmployeeService;
import jp.co.aw.practice.jdbc.utils.CloseUtils;
import jp.co.aw.practice.jdbc.utils.ConsoleWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public class FacadeOperationTest {

    Connection connection;
    FacadeOperation operation;
    String baseMess;
    String finMess;

    @Before
    public void setUp() throws Exception {
        Operation failOperation = c -> {
            fail("unsupported operation executed.");
            return 0;
        };
        operation = FacadeOperation.builder()//
                .insertOperation(failOperation)//
                .deleteOperation(failOperation)//
                .selectAllOperation(failOperation)//
                .selectWhereOperation(failOperation)//
                .updateOperation(failOperation)//
                .selectIdOperation(failOperation)//
                .build();
        connection = ConnectionUtils.checkoutConnection();
        connection.setAutoCommit(true);
        deleteTables(connection, EmployeeService.tableName());

        baseMess = String.format("操作を選択してください。%n(i: insert / d: delete / a: select all / s: select where / u: update/ f: find/ q: exit)%n");
        finMess = String.format("システムを終了します。%n");
    }

    @After
    public void tearDown() {
        CloseUtils.autocloseQuietly(connection);
    }

    @Test(expected = NullPointerException.class)
    public void constractor_insertOperationがnull() {
        new FacadeOperation(null, c -> 0, c -> 0, c -> 0, c -> 0, c -> 0);
    }

    @Test(expected = NullPointerException.class)
    public void constractor_deleteOperationがnull() {
        new FacadeOperation(c -> 0, null, c -> 0, c -> 0, c -> 0, c -> 0);
    }

    @Test(expected = NullPointerException.class)
    public void constractor_selectAllOperationがnull() {
        new FacadeOperation(c -> 0, c -> 0, null, c -> 0, c -> 0, c -> 0);
    }

    @Test(expected = NullPointerException.class)
    public void constractor_selectWhereOperationがnull() {
        new FacadeOperation(c -> 0, c -> 0, c -> 0, null, c -> 0, c -> 0);
    }

    @Test(expected = NullPointerException.class)
    public void constractor_updateOperationがnull() {
        new FacadeOperation(c -> 0, c -> 0, c -> 0, c -> 0, null, c -> 0);
    }

    @Test(expected = NullPointerException.class)
    public void constractor_selectIdOperationがnull() {
        new FacadeOperation(c -> 0, c -> 0, c -> 0, c -> 0, c -> 0, null);
    }

    @Test(expected = NullPointerException.class)
    public void execute_consoleがnull() {
        operation.execute(null);
    }

    void executeTestSuite(List<String> commands) {
        StringBuilder expected = new StringBuilder()//
                .append(Strings.repeat(baseMess, commands.size()))//
                .append(finMess);

        StringReader r = new StringReader(Joiner.on(newline()).join(commands));
        StringWriter w = new StringWriter();
        ConsoleWrapper c = new ConsoleWrapper(r, w);

        assertThat(operation.execute(c), is(0));
        assertThat(w.getBuffer().toString(), is(expected.toString()));
    }

    @Test
    public void execute_何も呼ばれずに終了する() {
        executeTestSuite(Arrays.asList("", "x", "123", ""));
        executeTestSuite(Arrays.asList("q"));
        executeTestSuite(Arrays.asList("Q"));
        executeTestSuite(Arrays.asList("quit"));
        executeTestSuite(Arrays.asList("Quit"));
        executeTestSuite(Arrays.asList("QUIT"));
    }

    @Test
    public void execute_insert処理の実施() {
        AtomicInteger callCount = new AtomicInteger();
        operation.insertOperation = c -> callCount.addAndGet(1);
        List<String> commands = Arrays.asList("i", "I", "insert", "Insert", "INSERT", "q");
        int expected = 5;

        executeTestSuite(commands);
        assertThat(callCount.get(), is(expected));
    }

    @Test
    public void execute_delete処理の実施() {
        AtomicInteger callCount = new AtomicInteger();
        operation.deleteOperation = c -> callCount.addAndGet(1);
        List<String> commands = Arrays.asList("d", "D", "delete", "Delete", "DELETE", "del", "q");
        int expected = 5;

        executeTestSuite(commands);
        assertThat(callCount.get(), is(expected));
    }

    @Test
    public void execute_selectAll処理の実施() {
        AtomicInteger callCount = new AtomicInteger();
        operation.selectAllOperation = c -> callCount.addAndGet(1);
        List<String> commands = Arrays.asList("a", "A", "all", "All", "ALL", "select all", "q");
        int expected = 5;

        executeTestSuite(commands);
        assertThat(callCount.get(), is(expected));
    }

    @Test
    public void execute_select処理の実施() {
        AtomicInteger callCount = new AtomicInteger();
        operation.selectWhereOperation = c -> callCount.addAndGet(1);
        List<String> commands = Arrays.asList("s", "S", "select", "Select", "SELECT", "select where", "q");
        int expected = 5;

        executeTestSuite(commands);
        assertThat(callCount.get(), is(expected));
    }

    @Test
    public void execute_update処理の実施() {
        AtomicInteger callCount = new AtomicInteger();
        operation.updateOperation = c -> callCount.addAndGet(1);
        List<String> commands = Arrays.asList("u", "U", "update", "Update", "UPDATE", "upd", "q");
        int expected = 5;

        executeTestSuite(commands);
        assertThat(callCount.get(), is(expected));
    }

    @Test
    public void execute_findid処理の実施() {
        AtomicInteger callCount = new AtomicInteger();
        operation.selectIdOperation = c -> callCount.addAndGet(1);
        List<String> commands = Arrays.asList("f", "F", "find", "Find", "FIND", "q");
        int expected = 5;

        executeTestSuite(commands);
        assertThat(callCount.get(), is(expected));
    }
}
