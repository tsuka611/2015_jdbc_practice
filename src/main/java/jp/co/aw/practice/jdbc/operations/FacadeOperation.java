package jp.co.aw.practice.jdbc.operations;

import static com.google.common.base.Preconditions.checkNotNull;
import jp.co.aw.practice.jdbc.service.EmployeeService;
import jp.co.aw.practice.jdbc.utils.ConsoleWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class FacadeOperation implements Operation {

    @NonNull
    EmployeeService employeeService;
    @NonNull
    Operation insertOperation;
    @NonNull
    Operation deleteOperation;

    @Override
    public int execute(ConsoleWrapper console) {
        checkNotNull(console);
        String readMess = "操作を選択してください。%n(i: insert / d: delete / a: select all / s: select where / u: update/ q: exit)%n";

        String line;
        outer: while ((line = console.readLine(readMess)) != null) {
            switch (line) {
            case "i":
            case "insert":
                insertOperation.execute(console);
                break;
            case "d":
            case "delete":
                deleteOperation.execute(console);
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
        console.println("システムを終了します。");
        return 0;
    }
}
