package jp.co.aw.practice.jdbc.operations;

import static jp.co.aw.practice.jdbc.utils.UnitTestUtils.deleteTables;

import java.sql.Connection;

import jp.co.aw.practice.jdbc.dbcp.ConnectionUtils;
import jp.co.aw.practice.jdbc.service.EmployeeService;
import jp.co.aw.practice.jdbc.utils.CloseUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FacadeOperationTest {

    Connection connection;
    FacadeOperation operation;

    @Before
    public void setUp() throws Exception {
        EmployeeService employeeService = new EmployeeService();
        InsertOperation insertOperation = InsertOperation.builder().employeeService(employeeService).build();
        operation = FacadeOperation.builder()//
                .employeeService(employeeService)//
                .insertOperation(insertOperation)//
                .build();
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
        new FacadeOperation(null, c -> 0);
    }

    @Test(expected = NullPointerException.class)
    public void constractor_insertOperationがnull() {
        new FacadeOperation(new EmployeeService(), null);
    }

    @Test(expected = NullPointerException.class)
    public void execute_consoleがnull() {
        operation.execute(null);
    }
}
