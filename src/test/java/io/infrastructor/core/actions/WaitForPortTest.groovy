package io.infrastructor.core.actions

import io.infrastructor.core.processing.ActionProcessingException
import io.infrastructor.core.validation.ValidationException
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
    
    @Test(expectedExceptions = [ValidationException])
    public void waitForUnknownPort() {
        inventory.setup {
            nodes('as:root') { 
               waitForPort delay: 100, attempts: 10
            }
        }
    }
}

