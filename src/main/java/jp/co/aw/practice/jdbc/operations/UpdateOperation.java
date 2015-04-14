package jp.co.aw.practice.jdbc.operations;

import static jp.co.aw.practice.jdbc.utils.ValidationUtils.validate;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validId;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validMail;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validName;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validTel;
import jp.co.aw.practice.jdbc.entity.Employee;
import jp.co.aw.practice.jdbc.service.EmployeeService;
import jp.co.aw.practice.jdbc.utils.ConsoleWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class UpdateOperation implements Operation {

    @NonNull
    EmployeeService employeeService;

    @Override
    public int execute(ConsoleWrapper console) {
        String id = console.readLine("更新対象IDを入力してください。%n", "");

        boolean idIsValid = true;
        idIsValid &= validate(id, s -> console.println("[%s]はIDとして不適切です。", s), validId());
        if (!idIsValid) {
            console.println("入力値が正しくないため処理を中断しました。");
            return 1;
        }

        Employee e = employeeService.findById(Long.parseLong(id));
        if (e == null) {
            console.println("ID%sは存在しませんでした。", id);
            return 2;
        }

        console.println(e.toConsoleFormat());

        String name = console.readLine("name: ");
        String mail = console.readLine("mail: ");
        String tel = console.readLine("tel: ");

        boolean isInputValid = true;
        isInputValid &= validate(name, s -> console.println("[%s]はnameとして不適切です。", s), validName());
        isInputValid &= validate(mail, s -> console.println("[%s]はmailとして不適切です。", s), validMail());
        isInputValid &= validate(tel, s -> console.println("[%s]はtelとして不適切です。", s), validTel());

        if (!isInputValid) {
            console.println("入力値が正しくないため処理を中断しました。");
            return 1;
        }

        employeeService.update(Long.parseLong(id), name, mail, tel);
        console.println("更新処理が完了しました。");
        console.println(employeeService.findById(Long.parseLong(id)).toConsoleFormat());
        return 0;
    }
}
