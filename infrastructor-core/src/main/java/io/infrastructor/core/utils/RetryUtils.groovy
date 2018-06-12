package io.infrastructor.core.utils

import static io.infrastructor.core.logging.ConsoleLogger.debug

class RetryUtils {
    
   def static retry(def count, def delay, def actions) {
        def attempt = 1
        while (attempt <= count) {
            try {
                return actions()
            } catch (AssertionError | Exception ex) {
                debug "retry - attempt ${attempt} of $count failed due to:\n" + "$ex"
                if (attempt == count) throw new RuntimeException("retry failed after $attempt attempts. Last error: $ex")
                attempt++
                sleep(delay)
            }
        }
    }
}

