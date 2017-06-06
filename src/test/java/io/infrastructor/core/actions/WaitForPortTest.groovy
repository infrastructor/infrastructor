package io.infrastructor.core.actions

import org.junit.Test
import io.infrastructor.core.processing.ActionProcessingException
import io.infrastructor.core.validation.ValidationException

public class WaitForPortTest extends ActionTestBase {
    
    @Test(expected = ActionProcessingException)
    public void waitForUnlistenedPort() {
        inventory.setup {
            nodes('as:root') { 
               waitForPort port: 10000
            }
        }
    }
    
    @Test(expected = ValidationException)
    public void waitForUnknownPort() {
        inventory.setup {
            nodes('as:root') { 
               waitForPort delay: 100, attempts: 10
            }
        }
    }
}

