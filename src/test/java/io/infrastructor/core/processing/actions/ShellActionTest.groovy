package io.infrastructor.core.processing.actions

import org.junit.Test

import static io.infrastructor.core.processing.actions.Actions.*

public class ShellActionTest extends ActionTestBase {

    @Test
    public void simpleShellAction() {
        inventory.setup {
            nodes("as:devops") {
                def result = shell { 
                    command = "echo 'simple message!'"
                }
                assert result.output.contains('simple message!')
            }
        }
    }
}
