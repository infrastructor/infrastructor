package io.infrastructor.core.utils

import static io.infrastructor.cli.logging.ConsoleLogger.*

class RetryUtils {
    public static def retry(def count, def interval, def action) {
        def index = count
        while(index > 0) {
            try {
                action()
                return
            } catch (Throwable ex) {
                debug "Retry :: attempt failed"
                sleep(interval)
                index--
            }
        }
        
        throw RuntimeException("retry failed after $count attemps")
    }
}

