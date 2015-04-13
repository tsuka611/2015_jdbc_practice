package jp.co.aw.practice.jdbc.operations;

import static jp.co.aw.practice.jdbc.utils.ValidationUtils.validate;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validMail;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validName;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validTel;
import jp.co.aw.practice.jdbc.service.EmployeeService;
import jp.co.aw.practice.jdbc.utils.ConsoleWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class InsertOperation implements Operation {

    @NonNull
    EmployeeService employeeService;

    @Override
    public int execute(ConsoleWrapper console) {
        String name = console.readLine("name: ");
        String mail = console.readLine("mail: ");
        String tel = console.readLine("tel: ");

        boolean isValid = true;
        isValid &= validate(name, s -> console.println("[%s]はnameとして不適切です。", s), validName());
        isValid &= validate(mail, s -> console.println("[%s]はmailとして不適切です。", s), validMail());
        isValid &= validate(tel, s -> console.println("[%s]はtelとして不適切です。", s), validTel());

        if (!isValid) {
            console.println("入力値が正しくないため処理を中断しました。");
            return 1;
        }

        employeeService.insert(name, mail, tel);
        console.println("%sを登録しました。", name);
        return 0;
    }

}
