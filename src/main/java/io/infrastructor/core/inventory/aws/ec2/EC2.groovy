package io.infrastructor.core.inventory.aws.ec2

import io.infrastructor.core.inventory.aws.AwsNode

import static io.infrastructor.cli.ConsoleLogger.*
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
        awsNode.tags << tags
        if (awsNode.username    == null) awsNode.username    = username
        if (awsNode.keyfile     == null) awsNode.keyfile     = keyfile
        if (awsNode.usePublicIp == null) awsNode.usePublicIp = usePublicIp
        targetState << awsNode
    }
        
    def initialize(def amazonEC2) {
        info "EC2 :: Initializing managed AWS inventory"
        def existing = fromEC2(amazonEC2).filterByTags(tags)
        // apply defaults for existing nodes
        existing.each {
            it.username    = username
            it.keyfile     = keyfile
            it.usePublicIp = usePublicIp
        }
        targetState = fromNodes(targetState).merge(existing).nodes
        targetState.each { debug "EC2 node added to inventory: $it" }
    }
    
    def createInstances(def amazonEC2) {
        debug "EC2 :: Creating instances"
        executeParallel(targetState.findAll { it.state == 'created' }, parallel) { it.create(amazonEC2) }
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
