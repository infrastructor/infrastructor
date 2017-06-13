package io.infrastructor.core.inventory.aws.ec2

import io.infrastructor.core.inventory.aws.AwsNode

import static io.infrastructor.core.inventory.aws.AwsNodesBuilder.fromEC2
import static io.infrastructor.core.inventory.aws.AwsNodesBuilder.fromNodes
import static io.infrastructor.core.utils.ParallelUtils.executeParallel

public class EC2 {
    
    def tags = [:]
    def parallel = 1
    
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
        targetState << awsNode
    }
        
    def initialize(def amazonEC2) {
        def target  = fromNodes(targetState)
        def current = fromEC2(amazonEC2).filterByTags(tags)
        targetState = target.merge(current).nodes
    }
    
    def createInstances(def amazonEC2) {
        executeParallel(targetState.findAll { it.state == 'created' }, parallel) { it.create(amazonEC2) }
    }

    def removeInstances(def amazonEC2) {
        executeParallel(targetState.findAll { it.state == 'removed' }, parallel) { it.remove(amazonEC2) }
    }
    
    def updateInstances(def amazonEC2) {
        executeParallel(targetState.findAll { it.state == 'updated' }, parallel) { it.update(amazonEC2) }
    }
    
    def getInventory() {
        targetState
    }
}
