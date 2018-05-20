package io.infrastructor.core.processing.actions

import org.junit.Test

public class ShellActionTest extends ActionTestBase {

    @Test
    public void singleLineShellAction() {
        def result = [:]
        inventory.provisionAs('devops') {
            task name: 'simpleShellAction', actions: {
                result = shell { 
                    command = "echo 'simple message!'"
                }
            }
        }
        
        assert result.output.contains('simple message!')
    }
    
    @Test
    public void multilineShellAction() {
        def result = [:]
        
        inventory.provisionAs('devops') {
            task name: 'simpleShellAction', actions: {
                result = shell { 
                    command = """ 
                        echo 'simple message!'
                        echo 'another message!'
                    """
                }

            }
        }
                        
        assert result.output.contains('simple message!')
        assert result.output.contains('another message!')
    }
    
    @Test
    public void multilineRestrictedShellActionWithoutSudo() {
        def result = [:]
        
        inventory.provisionAs('devops') {
            task name: 'simpleShellAction', actions: {
                result = shell { 
                    command = """ 
                        mkdir /etc/test
                    """
                }
            }
        }
        
        assert result.exitcode == 1
        assert result.error.contains("Permission denied")
    }
    
    @Test
    public void multilineRestrictedShellActionWithSudo() {
        def result = [:]
        
        inventory.provisionAs('devops') {
            task name: 'simpleShellAction', actions: {
                result = shell { 
                    command = """ 
                        mkdir /etc/test
                    """
                    user = 'root'
                }
            }
        }
        
        assert result.exitcode == 0
    }
    
    @Test
    public void multilineShellActionWithErrorScript() {
        def result = [:]
        
        inventory.provisionAs('devops') {
            task name: 'simpleShellAction', actions: {
                result = shell command: """ 
                        this script can not be executed
                    """
            }
        }
        
        assert result.exitcode != 0
    }
    
    @Test
    public void singlelineShellActionWithUserSwitch() {
        def result = [:]
        
        inventory.provisionAs('root') {
            task name: 'create test user', actions: {
                user name: 'test', uid: 1002, home: '/home/test', shell: '/bin/bash'
            }

            task name: 'run an action as test user', actions: {
                result = shell user: 'test', command: 'whoami' 
            }
        }
        
        assert result.exitcode == 0
        assert result.output.contains("test")
    }
    
    @Test
    public void multilineShellActionWithUserSwitch() {
        def result = [:]
        
        inventory.provisionAs('root') {
            task name: 'create test user', actions: {
                user name: 'test', uid: 1002, home: '/home/test', shell: '/bin/bash'
            }

            task name: 'run an action as test user', actions: {
                result = shell user: 'test', command: '''
                    whoami
                ''' 
            }
        }
        
        assert result.exitcode == 0
        assert result.output.contains("test")
    }
}
