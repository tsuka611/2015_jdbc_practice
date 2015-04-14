package jp.co.aw.practice.jdbc.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
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

    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String readLine(String string) {
        try {
            writer.write(string);
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return readLine();
    }

    public String readLine(String fmt, Object... args) {
        try {
            writer.write(String.format(fmt, args));
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return readLine();
    }

    public void println(String string) {
        try {
            writer.write(string);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void println(String fmt, Object... args) {
        try {
            writer.write(String.format(fmt, args));
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        Closer c = Closer.create();
        try {
            c.register(reader);
            c.register(writer);
        } finally {
            CloseUtils.closeQuietly(c);
        }
    }
}
