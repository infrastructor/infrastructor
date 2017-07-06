package io.infrastructor.core.processing.actions

import org.junit.Test
import io.infrastructor.core.processing.TaskExecutionException

import static io.infrastructor.core.processing.actions.Actions.*

public class WaitForPortTest extends ActionTestBase {
    
    @Test(expected = TaskExecutionException)
    public void waitForUnlistenedPort() {
        inventory.setup {
            nodes('as:root') { 
               waitForPort port: 10000
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    public void waitForUnknownPort() {
        inventory.setup {
            nodes('as:root') { 
               waitForPort delay: 100, attempts: 10
            }
        }
    }
}

