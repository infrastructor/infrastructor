package io.infrastructor.core.utils

import static io.infrastructor.cli.logging.ConsoleLogger.*

class RetryUtils {
    
    public static def retry(def count, def interval, def action) {
        def index = count
        def exception = new RuntimeException("retry failed after $count of attemps")
        while(index > 0) {
            try {
                action()
                return
            } catch (Throwable ex) {
                debug "retry - attempt ${count - index + 1} of $count failed due to: $ex"
                sleep(interval)
                index--
                exception = ex
            }
        }
        
        throw exception
    }
}

