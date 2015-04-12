package jp.co.aw.practice.jdbc.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Closeable;
import java.io.IOException;

import jp.co.aw.practice.jdbc.ApplicationException;

import com.google.common.io.Closer;

public class CloseUtils {

    public static void closeQuietly(Closeable... closeables) {
        if (closeables == null || closeables.length < 1) {
            return;
        }
        for (Closeable c : closeables) {
            if (c == null) {
                continue;
            }
            try {
                c.close();
            } catch (IOException ignore) {
            }
        }
    }

    public static <E extends RuntimeException> RuntimeException rethrow(Closer closer, Throwable cause) throws E {
        try {
            return checkNotNull(closer).rethrow(checkNotNull(cause));
        } catch (IOException e) {
            throw new ApplicationException(e); // Unreachable block
        }
    }
}
