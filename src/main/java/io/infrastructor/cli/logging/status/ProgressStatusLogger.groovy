package io.infrastructor.cli.logging.status

import static io.infrastructor.cli.logging.ProgressLogger.*

class ProgressStatusLogger {
    
    private final char filledChar   = '=';
    private final char unfilledChar = '-'
    private final int progressLineSize = 15;

    def total = 0;
    def progress = 0;
    def status = "";
    def listener = {}
    
    public synchronized int increase() {
        progress++;
        listener()
        return progress
    }
    
    public def setStatus(def status) {
        this.status = status
        listener()
    }
    
    public String statusLine() {
        int filledElements = (int) ((progressLineSize / (double) total) * progress);

        final StringBuilder stringBuilder = new StringBuilder("[");

        for (int i = 0; i < filledElements; i++) {
            stringBuilder.append(filledChar);
        }
        
        for (int i = 0; i < progressLineSize - filledElements; i++) {
            stringBuilder.append(unfilledChar);
        }

        stringBuilder.append("] ").append(progress).append(" / ").append(total).append(" ").append(status);
        
        return stringBuilder.toString();
    }
    
    public static void withProgressStatus(def total, def status, Closure closure) {
        def progress = new ProgressStatusLogger(total: total, status: status)
        try {
            addStatusLogger progress
            closure(progress)
        } finally {
            removeStatusLogger progress
        }
    }
}

