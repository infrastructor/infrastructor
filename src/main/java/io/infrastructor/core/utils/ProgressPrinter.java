package io.infrastructor.core.utils;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.Color.BLUE;

public class ProgressPrinter {

    private final Ansi.Color progressColor = BLUE;

    private final char filledChar;
    private final char unfilledChar;

    private int total;
    private int progress;
    private int progressLineSize;
    private String status = "";

    public ProgressPrinter() {
        this(30);
    }

    public ProgressPrinter(int progressLineSize) {
        this(progressLineSize, '=', '-');
    }

    public ProgressPrinter(int progressLineSize, char filledChar, char unfilledChar) {
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

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProgressLineSize(int progressLineSize) {
        this.progressLineSize = progressLineSize;
    }

    public synchronized void increase() {
        progress++;
        printStatus(status);
    }

    public void finish(String message) {
        this.progress = this.total;
        printStatus("DONE.");

    }

    public void print(String message, Ansi.Color color) throws InterruptedException {
        System.out.println(Ansi.ansi().cursorToColumn(0).eraseLine(Ansi.Erase.FORWARD).fg(color).a(message).reset());
        printStatus(status);
    }

    public void print(String message) throws InterruptedException {
        print(message, Ansi.Color.DEFAULT);
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
        System.out.print(Ansi.ansi().cursorToColumn(0).eraseLine(Ansi.Erase.FORWARD).fg(progressColor).a(stringBuilder.toString()).reset());
    }

}
