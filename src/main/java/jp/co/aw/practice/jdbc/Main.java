package jp.co.aw.practice.jdbc;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import jp.co.aw.practice.jdbc.operations.DeleteOperation;
import jp.co.aw.practice.jdbc.operations.FacadeOperation;
import jp.co.aw.practice.jdbc.operations.InsertOperation;
import jp.co.aw.practice.jdbc.operations.SelectAllOperation;
import jp.co.aw.practice.jdbc.service.EmployeeService;
import jp.co.aw.practice.jdbc.utils.ConsoleWrapper;

import com.google.common.io.Closer;

public class Main {

    public static void main(String... args) throws Exception {

        Closer closer = Closer.create();
        try {
            Reader r = System.console() != null ? System.console().reader() : new InputStreamReader(System.in);
            Writer w = System.console() != null ? System.console().writer() : new OutputStreamWriter(System.out);

            ConsoleWrapper cw = closer.register(new ConsoleWrapper(r, w));
            EmployeeService employeeService = new EmployeeService();
            InsertOperation insertOperation = InsertOperation.builder().employeeService(employeeService).build();
            DeleteOperation deleteOperation = DeleteOperation.builder().employeeService(employeeService).build();
            SelectAllOperation selectAllOperation = SelectAllOperation.builder().employeeService(employeeService).build();
            FacadeOperation exec = FacadeOperation.builder() //
                    .insertOperation(insertOperation)//
                    .deleteOperation(deleteOperation)//
                    .selectAllOperation(selectAllOperation)//
                    .build();
            exec.execute(cw);
        } catch (Exception e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }
}
