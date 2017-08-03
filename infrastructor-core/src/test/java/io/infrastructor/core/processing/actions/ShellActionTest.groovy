package io.infrastructor.core.processing.actions

import org.junit.Test

public class ShellActionTest extends ActionTestBase {

    @Test
    public void simpleShellAction() {
        inventory.provision {
            task name: 'simpleShellAction', filter: {'as:devops'}, actions: {
                def result = shell { 
                    command = "echo 'simple message!'"
                }
                assert result.output.contains('simple message!')
            }
        }
    }
}
