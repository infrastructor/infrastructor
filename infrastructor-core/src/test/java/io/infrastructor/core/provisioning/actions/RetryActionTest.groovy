package io.infrastructor.core.provisioning.actions

import io.infrastructor.core.inventory.InventoryAwareTestBase
import io.infrastructor.core.provisioning.TaskExecutionException
import org.junit.Test

class RetryActionTest extends InventoryAwareTestBase {

    @Test
    void retryWithoutFail() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'retry without fail', actions: {
                    retry actions: {
                        def result = shell "echo 'simple message!'"
                        assert result.output.contains('simple message!')
                    }
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void retryWithRuntimeException() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'retry with runtime exception', actions: {
                    retry count: 1, delay: 1, actions: {
                        throw new RuntimeException("test!")
                    }
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void retryWithAssertionError() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'retry with assertion error', actions: {
                    retry count: 1, delay: 1, actions: {
                        assert false
                    }
                }
            }
        }
    }
    
    @Test
    void retryThreeTimesWithoutAssertionError() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'retry three times without assertion error', actions: {
                    def x = 0
                    retry count: 3, delay: 1, actions: {
                        x++
                        assert x == 3
                    }
                }
            }
        }
    }

    @Test(expected = TaskExecutionException)
    void retryNegativeCountNumber() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'retry incorrect count number', actions: {
                    retry count: -1, delay: 1, actions: {}
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void retryZeroCountNumber() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'retry incorrect count number', actions: {
                    retry count: 0, delay: 1, actions: {}
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void retryNegativeDelay() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'retry negative delay', actions: {
                    retry count: 1, delay: -1, actions: {}
                }
            }
        }
    }
    
    @Test
    void retryWithZeroDelay() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'retry negative delay', actions: {
                    def x = false
                    retry count: 1, delay: 0, actions: { x = true }
                    assert x
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    void retryNullActions() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'retry with null action', actions: {
                    retry count: 1, delay: 1, actions: null
                }
            }
        }
    }
    
    @Test
    void retryWithDefaultCountAndDelay() {
        withUser(DEVOPS) { inventory ->
            inventory.provision {
                task name: 'retry with null action', actions: {
                    def x = false
                    retry actions: { x = true }
                    assert x
                }
            }
        }
    }
}