package io.infrastructor.core.inventory.aws

import static io.infrastructor.core.utils.ParallelUtils.executeParallel

public class AwsManagedZone {
    
    def tags = [:]
    def parallel = 1
    
    private def targetState = []
    
    def ec2(Map params) {
        ec2(params, {})
    }
    
    def ec2(Closure closure) {
        ec2([:], closure)
    }
    
    def ec2(Map params, Closure closure) {
        def node = new AwsNode(params)
        node.with(closure)
        node.tags << tags
        targetState << node
    }
        
    def initialize(def amazonEC2) {
        def target  = AwsNodesBuilder.fromNodes(targetState)
        def current = AwsNodesBuilder.fromEC2(amazonEC2).filterByTags(tags)
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
