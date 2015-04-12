package jp.co.aw.practice.jdbc.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ValidationUtils {

    @SafeVarargs
    public static <T> boolean validate(T value, Consumer<T> failoperation, Predicate<T>... validators) {
        boolean isValid = true;
        for (Predicate<T> validator : checkNotNull(validators)) {
            isValid &= validator.test(value);
        }
        if (!isValid && failoperation != null) {
            failoperation.accept(value);
        }
        return isValid;
    }
}
