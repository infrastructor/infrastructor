package io.infrastructor.core.processing2.actions

public class FetchActionBuilder extends AbstractNodeActionBuilder {
    
    def static execute(closure) {
        closure.delegate = new FetchActionBuilder()
        closure()
    }

    def fetch(Map params) {
        fetch(params, {})
    }
    
    def fetch(Closure closure) {
        fetch([:], closure)
    }
    
    def fetch(Map params, Closure closure) {
        def action = new FetchAction(params)
        action.with(closure)
        println "FetchActionBuilder node: $node"
        action.node = node
        action
    }
}

