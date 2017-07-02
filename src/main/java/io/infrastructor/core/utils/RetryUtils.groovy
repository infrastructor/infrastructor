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
                debug "retry :: attempt failed, exception: $ex"
                sleep(interval)
                index--
            }
        }
        
        throw new RuntimeException("retry failed after $count attemps")
    }
}

