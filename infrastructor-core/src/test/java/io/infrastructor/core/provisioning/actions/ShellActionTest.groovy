package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

class ShellActionTest extends InventoryAwareTestBase {

    @Test
    void "singleline shell action"() {
        def result = [:]
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'simpleShellAction', actions: {
                    result = shell {
                        command = "echo 'simple message!'"
                    }
                }
            }
        }

        assert result.output.contains('simple message!')
    }

    @Test
    void "multiline shell action"() {
        def result = [:]

        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'simpleShellAction', actions: {
                    result = shell {
                        command = """ 
                        echo 'simple message!'
                        echo 'another message!'
                        """
                    }

                }
            }
        }

        assert result.output.contains('simple message!')
        assert result.output.contains('another message!')
    }

    @Test(expected = TaskExecutionException)
    void "multiline restricted shell action without sudo"() {

        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'simpleShellAction', actions: {
                    shell {
                        command = """ 
                        mkdir /etc/test
                        """
                    }
                }
            }
        }
    }

    @Test
    void "multiline restricted shell action with sudo"() {
        def result = [:]

        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'simpleShellAction', actions: {
                    result = shell {
                        command = """ 
                        mkdir /etc/test
                        """
                        user = 'root'
                    }
                }
            }
        }

        assert result.exitcode == 0
    }

    @Test(expected = TaskExecutionException)
    void "multiline shell action with error script"() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'simpleShellAction', actions: {
                    shell command: """ 
                        this script can not be executed
                    """
                }
            }
        }
    }

    @Test
    void "singleline shell action with user switch"() {
        def result = [:]

        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'create test user', actions: {
                    user user: 'root', name: 'test', uid: 1002, home: '/home/test', shell: '/bin/bash'
                }

                task name: 'run an action as test user', actions: {
                    result = shell user: 'test', command: 'whoami'
                }
            }
        }

        assert result.exitcode == 0
        assert result.output.contains("test")
    }

    @Test
    void "singleline shell action with sudo and a password"() {
        def result = [:]

        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    result = shell user: 'root', command: 'whoami', sudopass: 'sudops'
                }
            }
        }

        assert result.exitcode == 0
        assert result.output.contains("root")
    }

    @Test(expected = TaskExecutionException)
    void "singleline shell action with sudo and a wrong password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    result = shell user: 'root', command: 'whoami', sudopass: 'sudops'
                }
            }
        }
    }

    @Test
    void "multiline shell action with user switch"() {
        def result = [:]

        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'run an action as test user', actions: {
                    user name: 'test', user: 'root'
                    result = shell user: 'test', command: '''
                    whoami
                    '''
                }
            }
        }

        assert result.exitcode == 0
        assert result.output.contains("test")
    }

    @Test
    void "multiline shell action with sudo and a password"() {
        def result = [:]

        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    result = shell user: 'root', sudopass: 'sudops', command: '''
                    whoami
                    '''
                }
            }
        }

        assert result.exitcode == 0
        assert result.output.contains("root")
    }

    @Test(expected = TaskExecutionException)
    void "multiline shell action with sudo and a wrong password"() {
        withUser(SUDOPS) { inventory ->
            inventory.provision {
                task actions: {
                    result = shell user: 'root', sudopass: 'wrong', command: '''
                    whoami
                    '''
                }
            }
        }
    }
}
