package io.infrastructor.core.tasks

import io.infrastructor.core.processing.ActionProcessingException
import org.testng.annotations.Test

public class WaitForPortTest extends TaskTestBase {
    @Test(expectedExceptions = [ActionProcessingException])
    public void waitForUnlistenedPort() {
        inventory.setup {
            nodes('as:root') { 
               waitForPort port: 10000
            }
        }
    }
}

