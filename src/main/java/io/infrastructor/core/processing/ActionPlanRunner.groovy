package io.infrastructor.core.processing

import static io.infrastructor.cli.ConsoleLogger.debug


public class ActionPlanRunner {
    
    def hosts = []
    
    def static setup(def hosts, Closure closure) {
        closure.delegate = new ActionPlanRunner(hosts: hosts)
        closure()
    }
    
    def nodes(String tag, Closure closure) {
        nodes([tags: { tag as Boolean }], closure)
    }
    
    def nodes(Closure closure) {
        nodes([:], closure)
    }
    
    def nodes(Map params, Closure closure) {
        debug "creating nodes configuration: $params"
        def nodes = new Nodes(params) 
        nodes.execute(hosts, closure)
    }
}
