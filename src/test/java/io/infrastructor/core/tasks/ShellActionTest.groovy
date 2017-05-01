package io.infrastructor.core.tasks

import org.testng.annotations.Test


public class ShellActionTest extends TaskTestBase {

    @Test
    public void simpleShellAction() {
        inventory.setup {
            nodes("test") {
                def result = shell { 
                    command = "echo 'simple message!'"
                }
                assert result.output.contains('simple message!')
            }
        }
    }
}
