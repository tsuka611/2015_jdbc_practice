package jp.co.aw.practice.jdbc.operations;

import static jp.co.aw.practice.jdbc.utils.ValidationUtils.validate;
import static jp.co.aw.practice.jdbc.validators.EmployeeValidators.validId;
import jp.co.aw.practice.jdbc.service.EmployeeService;
import jp.co.aw.practice.jdbc.utils.ConsoleWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class DeleteOperation implements Operation {

    @NonNull
    EmployeeService employeeService;

    @Override
    public int execute(ConsoleWrapper console) {
        String id = console.readLine("削除したいIDを入力してください。%n", "");

        boolean isValid = true;
        isValid &= validate(id, s -> console.println("[%s]はIDとして不適切です。", s), validId());

        if (!isValid) {
            console.println("入力値が正しくないため処理を中断しました。");
            return 1;
        }

        int delCount = employeeService.delete(Long.parseLong(id));
        if (delCount < 1) {
            console.println("ID%sは存在しませんでした。", id);
            return 2;
        }
        console.println("ID%sを削除しました。", id);
        return 0;
    }

}
