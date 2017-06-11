package io.infrastructor.core.processing

import java.util.concurrent.Executors
import java.util.concurrent.ExecutionException

import static io.infrastructor.cli.ConsoleLogger.debug
import static io.infrastructor.core.utils.ParallelUtils.executeParallel

public class Nodes {
    
    def tags = { true }
    def parallel = 1
        
    def execute(def nodes, Closure closure) {
        debug "launching a group of actions for nodes: $nodes, filtering: $tags, parallel: $parallel"
        def filtered = tags ? nodes.findAll { filter(it) } : nodes
        
        debug "filtered group of nodes: $filtered"
        def executor = Executors.newFixedThreadPool(parallel)
        
        executeParallel(filtered, parallel) { node ->
            debug "launching setup for $node, thread: ${Thread.currentThread().id}"
            ActionProcessor.setup(node, closure.clone())
        }
    }
    
    def filter(def node) {
        def proxy = ProxyMetaClass.getInstance(String.class)
        proxy.use {
            def closure = { context -> context.contains(delegate) }
            def stringTags = node.allTags().inject([]) { list, k, v -> list << ("$k:$v" as String) }
            String.metaClass.asBoolean = closure.curry(stringTags)
            return (tags() as Boolean)
        }
    }
}

