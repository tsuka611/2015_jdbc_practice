package jp.co.aw.practice.jdbc;

import static com.google.common.base.Preconditions.checkNotNull;
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
public class ConsoleExecutor {

    @NonNull
    EmployeeService employeeService;

    public void execute(ConsoleWrapper consoleWrapper) {
        checkNotNull(consoleWrapper);
        String readMess = "操作を選択してください。%n(i: insert / d: delete / a: select all / s: select where / u: update/ q: exit)%n";

        String line;
        outer: while ((line = consoleWrapper.readLine(readMess)) != null) {
            switch (line) {
            case "i":
            case "insert":
                insertOperation(consoleWrapper);
                break;
            case "d":
            case "delete":
                break;
            case "a":
            case "all":
                break;
            case "s":
            case "select":
                break;
            case "u":
            case "update":
                break;
            case "q":
            case "quit":
                break outer;
            default:
                continue;
            }
        }
        consoleWrapper.println("システムを終了します。");
    }

    void insertOperation(ConsoleWrapper console) {
        String name = console.readLine("name: ");
        String mail = console.readLine("mail: ");
        String tel = console.readLine("tel: ");

        boolean isValid = true;
        isValid &= validate(name, s -> console.println("[%s]はnameとして不適切です。", s), validName());
        isValid &= validate(mail, s -> console.println("[%s]はmailとして不適切です。", s), validMail());
        isValid &= validate(tel, s -> console.println("[%s]はtelとして不適切です。", s), validTel());

        if (!isValid) {
            console.println("入力値が正しくないため処理を中断しました。");
            return;
        }

        employeeService.insert(name, mail, tel);
        console.println("%sを登録しました。", name);
    }

}