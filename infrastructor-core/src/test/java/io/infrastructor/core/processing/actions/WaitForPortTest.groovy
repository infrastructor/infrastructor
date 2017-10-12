package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.provisioning.TaskExecutionException
import org.junit.Test

public class WaitForPortTest extends ActionTestBase {
    @Test(expected = TaskExecutionException)
    public void waitForUnlistenedPort() {
        inventory.provisionAs('root') {
            task actions: {
               waitForPort port: 10000
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    public void waitForUnknownPort() {
        inventory.provisionAs('root') {
            task actions: {
               waitForPort delay: 100, attempts: 10
            }
        }
    }
}

