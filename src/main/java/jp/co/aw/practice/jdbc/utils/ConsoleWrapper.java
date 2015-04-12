package jp.co.aw.practice.jdbc.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.google.common.io.Closer;

public class ConsoleWrapper implements Closeable, AutoCloseable {

    BufferedReader reader;
    BufferedWriter writer;

    public ConsoleWrapper(Reader reader, Writer writer) {
        checkNotNull(reader);
        checkNotNull(writer);
        this.reader = new BufferedReader(reader);
        this.writer = new BufferedWriter(writer);
    }

    public String readLine() throws IOException {
        return reader.readLine();
    }

    public String readLine(String fmt, Object... args) throws IOException {
        writer.write(String.format(fmt, args));
        writer.flush();
        return readLine();
    }

    @Override
    public void close() throws IOException {
        Closer c = Closer.create();
        try {
            c.register(reader);
            c.register(writer);
        } finally {
            c.close();
        }
    }
}
