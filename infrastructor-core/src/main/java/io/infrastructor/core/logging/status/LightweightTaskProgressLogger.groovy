package io.infrastructor.core.logging.status

import org.fusesource.jansi.AnsiString

import java.util.concurrent.atomic.AtomicInteger

import static io.infrastructor.core.logging.ConsoleLogger.*

class LightweightTaskProgressLogger {

    def name = ''
    def listener = {}

    AtomicInteger waiting = new AtomicInteger()
    AtomicInteger running = new AtomicInteger()
    AtomicInteger failed = new AtomicInteger()
    AtomicInteger done = new AtomicInteger()

    def synchronized run() {
        waiting.decrementAndGet()
        running.incrementAndGet()
        listener()
    }

    def synchronized fail() {
        running.decrementAndGet()
        failed.incrementAndGet()
        listener()
    }

    def synchronized done() {
        running.decrementAndGet()
        done.incrementAndGet()
        listener()
    }

    String statusLine() {
        def taskStatus = new StringBuilder()
        taskStatus << "[TASK] '$name'"

        def nodesStatus = new StringBuilder()
        nodesStatus << "[NODES] "
        nodesStatus << "${bold(yellow('WAITING: ' + waiting))} "
        nodesStatus << "${bold(blue('RUNNING: ' + running))} "
        nodesStatus << "${bold(red('FAILED: ' + failed))} "
        nodesStatus << "${bold(green('DONE: ' + done))}"

        def plain = new AnsiString(nodesStatus.toString())

        def status = new StringBuilder()
        status << taskStatus << "\n"
        status << nodesStatus << "\n"
        status << progressLine('<', '=', '-', '>', plain.length() - 4 - 8, waiting + running + failed + done, failed + done)
        status << "\n"
        return status
    }

    String progressLine(def start, def filled, def unfilled, def end, def size, def total, def progress) {
        int filledElements = (int) ((size / (double) total) * progress)

        def prercent = String.format("%6.0f", (((double) progress) / total) * 100)

        final StringBuilder builder = new StringBuilder(prercent + '% ' + start)

        (0..filledElements).each { builder.append(filled) }
        (0..(size - filledElements)).each { builder.append(unfilled) }

        builder.append(end)

        return builder.toString()
    }

    static void withLightweightTaskProgressLogger(String name, int waiting, Closure closure) {
        def logger = new LightweightTaskProgressLogger(name: name, waiting: new AtomicInteger(waiting))
        try {
            addStatusLogger logger
            closure(logger)
        } finally {
            removeStatusLogger logger
        }
    }
}
