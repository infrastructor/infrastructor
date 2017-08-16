package io.infrastructor.core.processing.actions

import org.junit.Test

public class ShellActionTest extends ActionTestBase {

    @Test
    public void singleLineShellAction() {
        inventory.provision {
            task name: 'simpleShellAction', filter: {'as:devops'}, actions: {
                def result = shell { 
                    command = "echo 'simple message!'"
                }
                assert result.output.contains('simple message!')
            }
        }
    }
    
    @Test
    public void multilineShellAction() {
        inventory.provision {
            task name: 'simpleShellAction', filter: {'as:devops'}, actions: {
                def result = shell { 
                    command = """ 
                        echo 'simple message!'
                        echo 'another message!'
                    """
                }
                
                assert result.output.contains('simple message!')
                assert result.output.contains('another message!')
            }
        }
    }
    
    @Test
    public void multilineRestrictedShellActionWithoutSudo() {
        inventory.provision {
            task name: 'simpleShellAction', filter: {'as:devops'}, actions: {
                def result = shell { 
                    command = """ 
                        mkdir /etc/test
                    """
                }
                
                assert result.exitcode == 1
                assert result.error.contains("Permission denied")
            }
        }
    }
    
    @Test
    public void multilineRestrictedShellActionWithSudo() {
        inventory.provision {
            task name: 'simpleShellAction', filter: {'as:devops'}, actions: {
                def result = shell { 
                    command = """ 
                        mkdir /etc/test
                    """
                    sudo = true
                }
                
                assert result.exitcode == 0
            }
        }
    }
    
    @Test
    public void multilineShellActionWithErrorScript() {
        inventory.provision {
            task name: 'simpleShellAction', filter: {'as:devops'}, actions: {
                def result = shell command: """ 
                        this script can not be executed
                    """
                assert result.exitcode != 0
            }
        }
    }
}
