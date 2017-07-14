package io.infrastructor.core.processing.actions

import org.junit.Test
import io.infrastructor.core.processing.TaskExecutionException

public class WaitForPortTest extends ActionTestBase {
    
    @Test(expected = TaskExecutionException)
    public void waitForUnlistenedPort() {
        inventory.setup {
            task(filter: {'as:root'}) { 
               waitForPort port: 10000
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    public void waitForUnknownPort() {
        inventory.setup {
            task(filter: {'as:root'}) { 
               waitForPort delay: 100, attempts: 10
            }
        }
    }
}

