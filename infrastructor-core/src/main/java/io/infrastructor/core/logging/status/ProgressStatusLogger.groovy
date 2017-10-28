package io.infrastructor.core.logging.status

import static io.infrastructor.core.logging.ConsoleLogger.*

class ProgressStatusLogger {
    
    private final char FILLED_CHAR   = '='
    private final char UNFILLED_CHAR = '-'

    private final int progressLineSize = 20

    def total = 0
    def progress = 0
    def status = ""
    def listener = {}
    
    public synchronized int increase() {
        progress++
        listener()
        return progress
    }
    
    public def setStatus(def status) {
        this.status = status
        listener()
    }
    
    public String statusLine() {
        int filledElements = (int) ((progressLineSize / (double) total) * progress)

        final StringBuilder stringBuilder = new StringBuilder("[")

        (0..filledElements).each { stringBuilder.append(FILLED_CHAR) }
        (0..(progressLineSize - filledElements)).each { stringBuilder.append(UNFILLED_CHAR) }

        stringBuilder.append("] ").append(progress).append(" / ").append(total).append(" ").append(status)
        
        return stringBuilder.toString()
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

