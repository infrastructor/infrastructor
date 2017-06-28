package io.infrastructor.core.utils;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;

public class ProgressLogger {

    public static final int DEBUG = 3;
    public static final int INFO = 2;
    public static final int ERROR = 1;
    public static final int OFF = 0;

    private final Ansi.Color PROGRESSBAR_COLOR = DEFAULT;

    private final char filledChar;
    private final char unfilledChar;

    private int total;
    private int progress;
    private int progressLineSize;
    private String status = "";

    public ProgressLogger() {
        this(30);
    }

    public ProgressLogger(int progressLineSize) {
        this(progressLineSize, '=', '-');
    }

    public ProgressLogger(int progressLineSize, char filledChar, char unfilledChar) {
        this.progressLineSize = progressLineSize;
        this.filledChar = filledChar;
        this.unfilledChar = unfilledChar;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setProgressLineSize(int progressLineSize) {
        this.progressLineSize = progressLineSize;
    }

    public synchronized void increase() {
        progress++;
        printStatus(status);
    }

    public void status(String status) {
        this.status = status;
    }

    public void finish(String message) {
        this.progress = this.total;
        printStatus(message + "\n");
    }

    public void info(String message) {
        if (logLevel() >= INFO) {
            print(message, DEFAULT);
        }
    }

    public void debug(String message) {
        if (logLevel() >= DEBUG) {
            print(message, YELLOW);
        }
    }

    public void error(String message) {
        if (logLevel() >= ERROR) {
            print(message, RED);
        }
    }

    public synchronized void print(String message, Ansi.Color color) {
        System.out.println(Ansi.ansi().cursorToColumn(0).eraseLine(Ansi.Erase.FORWARD).fg(color).a(message).reset());
        printStatus(status);
    }

    public void printStatus(String status) {
        int filledElements = (int) ((progressLineSize / (double) total) * progress);

        final StringBuilder stringBuilder = new StringBuilder("[");

        for (int i = 0; i < filledElements; i++) {
            stringBuilder.append(filledChar);
        }

        for (int i = 0; i < progressLineSize - filledElements; i++) {
            stringBuilder.append(unfilledChar);
        }

        stringBuilder.append("] ").append(progress).append(" / ").append(total).append(" ").append(status);
        System.out.print(Ansi.ansi().cursorToColumn(0).eraseLine(Ansi.Erase.FORWARD).bold().fg(PROGRESSBAR_COLOR).a(stringBuilder.toString()).reset());
    }

    public static final int logLevel() {
        return Integer.parseInt(System.getProperty("LOG_LEVEL", String.valueOf(INFO)));
    }
}
