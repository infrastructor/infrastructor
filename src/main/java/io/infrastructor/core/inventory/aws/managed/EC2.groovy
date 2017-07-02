package io.infrastructor.core.inventory.aws.managed

import io.infrastructor.core.inventory.aws.AwsNode

import static io.infrastructor.cli.logging.ConsoleLogger.*
import static io.infrastructor.core.inventory.aws.AwsNodesBuilder.fromEC2
import static io.infrastructor.core.inventory.aws.AwsNodesBuilder.fromNodes
import static io.infrastructor.core.utils.ParallelUtils.executeParallel

public class EC2 {
    
    def tags = [:]
    def parallel = 1
    
    def username
    def keyfile
    def usePublicIp = false
    
    private def targetState = []
    
    def node(Map params) {
        node(params, {})
    }
    
    def node(Closure closure) {
        node([:], closure)
    }
    
    def node(Map params, Closure closure) {
        def awsNode = new AwsNode(params)
        awsNode.with(closure)
        targetState << awsNode
    }
        
    def initialize(def amazonEC2) {
        info "EC2 :: Initializing managed AWS inventory"

        def existing = fromEC2(amazonEC2).filterByTags(tags)
        existing.each {
            it.username    = username
            it.keyfile     = keyfile
        }.usePublicHost(usePublicIp)
        
        def defined = fromNodes(targetState)
        defined.each {
            if (it.username == null) it.username = username
            if (it.keyfile  == null)  it.keyfile = keyfile
            it.tags << tags 
        }.usePublicHost(usePublicIp)
        
        targetState = defined.merge(existing).nodes
        
        targetState.each { debug "EC2 node added to inventory: $it" }
    }
    
    def createInstances(def amazonEC2) {
        debug "EC2 :: Creating instances"
        executeParallel(targetState.findAll { it.state == 'created' }, parallel) { it.create(amazonEC2, usePublicIp) }
    }

    def removeInstances(def amazonEC2) {
        debug "EC2 :: Removing instances"
        executeParallel(targetState.findAll { it.state == 'removed' }, parallel) { it.remove(amazonEC2) }
    }
    
    def updateInstances(def amazonEC2) {
        debug "EC2 :: Updating instances"
        executeParallel(targetState.findAll { it.state == 'updated' }, parallel) { it.update(amazonEC2) }
    }
    
    def getInventory() {
        targetState
    }
}
