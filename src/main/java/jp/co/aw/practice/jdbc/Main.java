package jp.co.aw.practice.jdbc;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

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
            ConsoleExecutor exec = ConsoleExecutor.builder().employeeService(new EmployeeService()).build();
            exec.execute(cw);
        } catch (Exception e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }
}
