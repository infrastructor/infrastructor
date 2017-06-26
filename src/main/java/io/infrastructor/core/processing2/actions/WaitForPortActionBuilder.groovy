package io.infrastructor.core.processing2.actions

public class WaitForPortActionBuilder extends AbstractNodeActionBuilder {
    
    def static execute(closure) {
        closure.delegate = new WaitForPortActionBuilder()
        closure()
    }

    def waitForPort(def port) {
        waitForPort([port: port], {})
    }
    
    def waitForPort(Map params) {
        waitForPort(params, {})
    }
    
    def waitForPort(Closure closure) {
        waitForPort([:], closure)
    }
    
    def waitForPort(Map params, Closure closure) {
        def action = new WaitForPortAction(params)
        action.with(closure)
        validate(action)
        action.execute(node, logger)
        node.lastResult
    }
}

