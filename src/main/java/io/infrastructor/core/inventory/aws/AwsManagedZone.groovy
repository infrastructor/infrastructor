package io.infrastructor.core.inventory.aws

import io.infrastructor.core.inventory.Node
import static io.infrastructor.core.utils.ParallelUtils.executeParallel


public class AwsManagedZone {
    
    def tags = [:]
    def parallel = 1
    
    private def inventory = []
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
        node.tags << tags.collectEntries { k, v -> [(k as String), (v as String)] }
        targetState << node
    }
        
    def initialize(def amazonEC2) {
        inventory = merge(findAllInstancesWithTags(amazonEC2), targetState)
    }
    
    def createInstances(def amazonEC2) {
        executeParallel(inventory.findAll { it.state == 'created' }, parallel) { it.create(amazonEC2) }
    }

    def removeInstances(def amazonEC2) {
        executeParallel(inventory.findAll { it.state == 'removed' }, parallel) { it.remove(amazonEC2) }
    }
    
    def updateInstances(def amazonEC2) {
        executeParallel(inventory.findAll { it.state == 'updated' }, parallel) { it.update(amazonEC2) }
    }
    
    def getInventory() {
        inventory.collect { it as Node }
    }
    
    def getAwsInventory() {
        inventory
    }
    
    def findAllInstancesWithTags(def amazonEC2) {
        def reservations = amazonEC2.describeInstances().getReservations();
        def allExistingRunningInstances = reservations.collect { 
            it.getInstances().findAll { 
                it.getState().getCode() == 16 // running
            } 
        }.flatten()
        
        return allExistingRunningInstances.collect { 
            AwsNode.convert(it) 
        }.findAll { 
            it.tags.intersect(tags) == tags
        }
    }

    public static def merge(def current, def target) {
        current*.state = ''
        target*.state = 'created'
        
        current.each { existing ->
            def candidate = target.find { it.name == existing.name }
            if (candidate == null) { 
                existing.state = 'removed'
            } else if (needRebuild(candidate, existing)) {
                candidate.state = 'created'
                existing.state = 'removed'
            } else {
                candidate.state = needUpdate(candidate, existing) ? 'updated' : ''
                candidate.instanceId = existing.instanceId
                candidate.publicIp   = existing.publicIp
                candidate.privateIp  = existing.privateIp
            }
        }
        
        [*target, *current.findAll { it.state == 'removed' }].toSorted { a, b -> a.name <=> b.name }
    }
    
    private static boolean needRebuild(def candidate, def existing) {
        ((existing.imageId != candidate.imageId) ||
        (existing.instanceType != candidate.instanceType) ||
        (existing.subnetId != candidate.subnetId) ||
        (existing.keyName != candidate.keyName))
    }
    
    private static boolean needUpdate(def candidate, def existing) {
        def existingTags = existing.tags.collectEntries { k, v -> [k as String, v as String] }
        def candidateTags = candidate.tags.collectEntries { k, v -> [k as String, v as String] }
        return ((existing.securityGroupIds as Set) != (candidate.securityGroupIds as Set)) || (existingTags != candidateTags)
    }
}
