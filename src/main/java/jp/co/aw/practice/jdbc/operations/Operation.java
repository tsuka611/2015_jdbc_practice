package jp.co.aw.practice.jdbc.operations;

import jp.co.aw.practice.jdbc.utils.ConsoleWrapper;

@FunctionalInterface
public interface Operation {
    int execute(ConsoleWrapper console);
}