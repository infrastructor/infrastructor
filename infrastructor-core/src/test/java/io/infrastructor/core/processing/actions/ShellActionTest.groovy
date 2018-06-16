package io.infrastructor.core.processing.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import org.junit.Test

class ShellActionTest extends InventoryAwareTestBase {

    @Test
    void singleLineShellAction() {
        def result = [:]
        withInventory { inventory ->
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
    void multilineShellAction() {
        def result = [:]

        withInventory { inventory ->
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

    @Test
    void multilineRestrictedShellActionWithoutSudo() {
        def result = [:]

        withInventory { inventory ->
            inventory.provision {
                task name: 'simpleShellAction', actions: {
                    result = shell {
                        command = """ 
                        mkdir /etc/test
                    """
                    }
                }
            }
        }

        assert result.exitcode == 1
        assert result.error.contains("Permission denied")
    }

    @Test
    void multilineRestrictedShellActionWithSudo() {
        def result = [:]

        withInventory { inventory ->
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

    @Test
    void multilineShellActionWithErrorScript() {
        def result = [:]

        withInventory { inventory ->
            inventory.provision {
                task name: 'simpleShellAction', actions: {
                    result = shell command: """ 
                        this script can not be executed
                    """
                }
            }
        }

        assert result.exitcode != 0
    }

    @Test
    void singlelineShellActionWithUserSwitch() {
        def result = [:]

        withInventory { inventory ->
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
    void multilineShellActionWithUserSwitch() {
        def result = [:]

        withInventory { inventory ->
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
}
