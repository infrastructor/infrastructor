package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.provisioning.TaskExecutionException
import org.junit.Test

class RetryActionTest extends ActionTestBase {

    @Test
    public void retryWithoutFail() {
        inventory.provision {
            task name: 'retry without fail', filter: {'as:devops'}, actions: {
                retry actions: {
                    def result = shell "echo 'simple message!'"
                    assert result.output.contains('simple message!')
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    public void retryWithRuntimeException() {
        inventory.provision {
            task name: 'retry with runtime exception', filter: {'as:devops'}, actions: {
                retry count: 1, delay: 1, actions: {
                    throw new RuntimeException("test!")
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    public void retryWithAssertionError() {
        inventory.provision {
            task name: 'retry with assertion error', filter: {'as:devops'}, actions: {
                retry count: 1, delay: 1, actions: {
                    assert false
                }
            }
        }
    }
    
    @Test
    public void retryThreeTimesWithoutAssertionError() {
        inventory.provision {
            task name: 'retry three times without assertion error', filter: {'as:devops'}, actions: {
                def x = 0
                retry count: 3, delay: 1, actions: {
                    x++
                    assert x == 3
                }
            }
        }
    }

    @Test(expected = TaskExecutionException)
    public void retryNegativeCountNumber() {
        inventory.provision {
            task name: 'retry incorrect count number', filter: {'as:devops'}, actions: {
                retry count: -1, delay: 1, actions: {}
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    public void retryZeroCountNumber() {
        inventory.provision {
            task name: 'retry incorrect count number', filter: {'as:devops'}, actions: {
                retry count: 0, delay: 1, actions: {}
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    public void retryNegativeDelay() {
        inventory.provision {
            task name: 'retry negative delay', filter: {'as:devops'}, actions: {
                retry count: 1, delay: 0, actions: {}
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    public void retryNullActions() {
        inventory.provision {
            task name: 'retry with null action', filter: {'as:devops'}, actions: {
                retry count: 1, delay: 1, actions: null
            }
        }
    }
    
    @Test
    public void retryWithDefaultCountAndDelay() {
        inventory.provision {
            task name: 'retry with null action', filter: {'as:devops'}, actions: {
                def x = false
                retry actions: { x = true }
                assert x
            }
        }
    }
}