package jp.co.aw.practice.jdbc;

import jp.co.aw.practice.jdbc.service.EmployeeService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConsoleExecutorTest {

    ConsoleExecutor executor;

    @Before
    public void setUp() {
        executor = ConsoleExecutor.builder().employeeService(new EmployeeService()).build();
    }

    @After
    public void tearDown() {
    }

    @Test(expected = NullPointerException.class)
    public void constractor_employeeServiceがnull() {
        new ConsoleExecutor(null);
    }

    @Test(expected = NullPointerException.class)
    public void execute_consoleWrapperがnull() {
        executor.execute(null);
    }
}
