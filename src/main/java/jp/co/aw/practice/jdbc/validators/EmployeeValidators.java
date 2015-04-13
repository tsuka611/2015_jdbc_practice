package jp.co.aw.practice.jdbc.validators;

import java.util.function.Predicate;

import com.google.common.base.Strings;

public class EmployeeValidators {

    public static Predicate<String> required() {
        return s -> !Strings.isNullOrEmpty(s);
    }

    @SuppressWarnings("unchecked")
    public static Predicate<String>[] validName() {
        return new Predicate[] { required() };
    }

    @SuppressWarnings("unchecked")
    public static Predicate<String>[] validMail() {
        Predicate<String> mailCheck = s -> Strings.isNullOrEmpty(s) || s.matches("[\\w\\.\\-\\+]+@(?:[\\w\\-]+\\.)+[\\w\\-]+");
        return new Predicate[] { mailCheck };
    }

    @SuppressWarnings("unchecked")
    public static Predicate<String>[] validTel() {
        Predicate<String> mailCheck = s -> Strings.isNullOrEmpty(s) || s.matches("\\d+(?:\\-[\\d]+)*");
        return new Predicate[] { mailCheck };
    }
}
