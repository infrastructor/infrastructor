package io.infrastructor.core.processing.actions

import io.infrastructor.core.processing.provisioning.TaskExecutionException
import org.junit.Test

class RetryActionTest extends ActionTestBase {

    @Test
    public void retryWithoutFail() {
        inventory.provisionAs('devops') {
            task name: 'retry without fail', actions: {
                retry actions: {
                    def result = shell "echo 'simple message!'"
                    assert result.output.contains('simple message!')
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    public void retryWithRuntimeException() {
        inventory.provisionAs('devops') {
            task name: 'retry with runtime exception', actions: {
                retry count: 1, delay: 1, actions: {
                    throw new RuntimeException("test!")
                }
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    public void retryWithAssertionError() {
        inventory.provisionAs('devops') {
            task name: 'retry with assertion error', actions: {
                retry count: 1, delay: 1, actions: {
                    assert false
                }
            }
        }
    }
    
    @Test
    public void retryThreeTimesWithoutAssertionError() {
        inventory.provisionAs('devops') {
            task name: 'retry three times without assertion error', actions: {
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
        inventory.provisionAs('devops') {
            task name: 'retry incorrect count number', actions: {
                retry count: -1, delay: 1, actions: {}
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    public void retryZeroCountNumber() {
        inventory.provisionAs('devops') {
            task name: 'retry incorrect count number', actions: {
                retry count: 0, delay: 1, actions: {}
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    public void retryNegativeDelay() {
        inventory.provisionAs('devops') {
            task name: 'retry negative delay', actions: {
                retry count: 1, delay: -1, actions: {}
            }
        }
    }
    
    @Test
    public void retryWithZeroDelay() {
        inventory.provisionAs('devops') {
            task name: 'retry negative delay', actions: {
                def x = false
                retry count: 1, delay: 0, actions: { x = true }
                assert x
            }
        }
    }
    
    @Test(expected = TaskExecutionException)
    public void retryNullActions() {
        inventory.provisionAs('devops') {
            task name: 'retry with null action', actions: {
                retry count: 1, delay: 1, actions: null
            }
        }
    }
    
    @Test
    public void retryWithDefaultCountAndDelay() {
        inventory.provisionAs('devops') {
            task name: 'retry with null action', actions: {
                def x = false
                retry actions: { x = true }
                assert x
            }
        }
    }
}