package io.infrastructor.core.processing.actions

import static io.infrastructor.core.validation.ValidationHelper.validate

public class WaitForPortActionBuilder extends AbstractNodeActionBuilder {
    
    def waitForPort(def port) {
        waitForPort(port: port)
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
    }
}

