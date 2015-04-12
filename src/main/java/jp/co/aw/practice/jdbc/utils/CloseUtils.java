package jp.co.aw.practice.jdbc.utils;

import java.io.Closeable;
import java.io.IOException;

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
}
