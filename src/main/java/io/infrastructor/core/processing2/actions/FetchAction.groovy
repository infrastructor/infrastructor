package io.infrastructor.core.processing2.actions

import groovy.transform.ToString
import javax.validation.constraints.NotNull

@ToString
public class FetchAction extends AbstractNodeAction {
    
    @NotNull
    def source
    @NotNull
    def target
    def sudo = false
    
    def execute() {
        
        println "FetchAction node: $node, action: $this" 
        
        new FileOutputStream(target).withCloseable {
            
            println "FetchAction::readFile -> node: $node"
            
            node.readFile(source, it, sudo) 
        }
        
        println "FetchAction result: $node.lastResult"
        node.lastResult
    }
}

