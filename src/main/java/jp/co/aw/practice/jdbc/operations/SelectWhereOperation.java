package jp.co.aw.practice.jdbc.operations;

import static jp.co.aw.practice.jdbc.utils.ValidationUtils.validate;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validName;

import java.util.List;

import jp.co.aw.practice.jdbc.entity.Employee;
import jp.co.aw.practice.jdbc.service.EmployeeService;
import jp.co.aw.practice.jdbc.utils.ConsoleWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class SelectWhereOperation implements Operation {

    @NonNull
    EmployeeService employeeService;

    @Override
    public int execute(ConsoleWrapper console) {
        String name = console.readLine("検索したい名前を入力してください%n", "");

        boolean isValid = true;
        isValid &= validate(name, s -> console.println("[%s]はnameとして不適切です。", s), validName());

        if (!isValid) {
            console.println("入力値が正しくないため処理を中断しました。");
            return 1;
        }

        List<Employee> list = employeeService.findLikeName(name);
        if (list.isEmpty()) {
            console.println("[%s]ではレコードが選択されませんでした。", name);
            return 0;
        }

        for (Employee e : list) {
            console.println(e.toConsoleFormat());
        }
        return 0;
    }
}
