package io.infrastructor.core.logging.status

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
        def status = new StringBuilder()
        status << "[TASK] '$name'"
        status << "\n"
        status << "[NODES] "
        status << "${bold(yellow('waiting: ' + waiting))} "
        status << "${bold(blue('running: ' + running))} "
        status << "${bold(red('failed: ' + failed))} "
        status << "${bold(green('done: ' + done))}"
        status << "\n"
        status << progressLine('<', '=', '-', '>', status.size() - 108, waiting + running + failed + done, failed + done)
        status << "\n"
        return status
    }

    String progressLine(def start, def filled, def unfilled, def end, def size, def total, def progress) {
        int filledElements = (int) ((size / (double) total) * progress)

        final StringBuilder builder = new StringBuilder(start)

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
