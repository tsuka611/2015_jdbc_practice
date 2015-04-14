package jp.co.aw.practice.jdbc.operations;

import java.util.List;

import jp.co.aw.practice.jdbc.entity.Employee;
import jp.co.aw.practice.jdbc.service.EmployeeService;
import jp.co.aw.practice.jdbc.utils.ConsoleWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class SelectAllOperation implements Operation {

    @NonNull
    EmployeeService employeeService;

    @Override
    public int execute(ConsoleWrapper console) {

        List<Employee> list = employeeService.findAll();
        for (Employee e : list) {
            console.println(e.toConsoleFormat());
        }
        console.println("総勢: %s名", list.size());
        return 0;
    }
}
