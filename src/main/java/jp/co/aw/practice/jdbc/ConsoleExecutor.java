package jp.co.aw.practice.jdbc;

import static com.google.common.base.Preconditions.checkNotNull;
import static jp.co.aw.practice.jdbc.utils.ValidationUtils.validate;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validName;

import java.io.IOException;

import jp.co.aw.practice.jdbc.service.EmployeeService;
import jp.co.aw.practice.jdbc.utils.CloseUtils;
import jp.co.aw.practice.jdbc.utils.ConsoleWrapper;
import jp.co.aw.practice.jdbc.utils.ValidationUtils;
import jp.co.aw.practice.jdbc.validators.EmployeeValidators;
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
        try {
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
        } catch (IOException e) {
            throw new ApplicationException(e);
        }

    }

    void insertOperation(ConsoleWrapper console) throws IOException {
        String name;
        do {
            name = console.readLine("name: ");
        } while (validate(name, s -> {
            try {
                console.println("[%s]はnameとして不適切です。", s);
            } catch (Exception e) {
                throw new ApplicationException(e);
            }
        }, validName()));

        String mail = console.readLine("mail: ");
        String tel = console.readLine("tel: ");
    }

}
