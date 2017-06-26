package io.infrastructor.core.processing

import java.util.concurrent.Executors
import java.util.concurrent.ExecutionException
import io.infrastructor.core.utils.FilteringUtils

import static io.infrastructor.cli.ConsoleLogger.debug
import static io.infrastructor.core.utils.ParallelUtils.executeParallel

public class Nodes {
    
    def tags = { true }
    def parallel = 1
    def description
    def closure
        
    def execute(def nodes, def printer) {
        // debug "launching a group of actions for nodes: $nodes, filtering: $tags, parallel: $parallel"
        def filtered = filteredNodes(nodes)
        
        // debug "filtered group of nodes: $filtered"
        executeParallel(filtered, parallel) { node ->
            printer.print("launching setup for $node, thread: ${Thread.currentThread().id}")
            ActionProcessor.setup(node, printer, closure.clone())
            printer.increase()
        }
    }
    
    def filteredNodes(def nodes) {
        tags ? nodes.findAll { FilteringUtils.match(it.listTags(), tags) } : nodes
    }
}

