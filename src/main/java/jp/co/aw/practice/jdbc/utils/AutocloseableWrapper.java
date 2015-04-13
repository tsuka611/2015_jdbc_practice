package jp.co.aw.practice.jdbc.utils;

import java.io.Closeable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class AutocloseableWrapper<T extends AutoCloseable> implements Closeable {

    @NonNull
    @Getter
    T closeable;

    public static <E extends AutoCloseable> AutocloseableWrapper<E> wrap(E closeable) {
        return new AutocloseableWrapper<E>(closeable);
    }

    @Override
    public void close() {
        CloseUtils.autocloseQuietly(closeable);
    }
}
