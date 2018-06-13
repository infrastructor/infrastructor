package io.infrastructor.core.processing.actions

import org.junit.Test

public class ShellActionTest extends ActionTestBase {

    @Test
    void singleLineShellAction() {
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
    void multilineShellAction() {
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
    void multilineRestrictedShellActionWithoutSudo() {
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
    void multilineRestrictedShellActionWithSudo() {
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
    void multilineShellActionWithErrorScript() {
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
    void singlelineShellActionWithUserSwitch() {
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
    void multilineShellActionWithUserSwitch() {
        def result = [:]
        
        inventory.provisionAs('devops') {
            task name: 'run an action as test user', actions: {
                user name: 'test', user: 'root'
                result = shell user: 'test', command: '''
                    whoami
                ''' 
            }
        }
        
        assert result.exitcode == 0
        assert result.output.contains("test")
    }
}
