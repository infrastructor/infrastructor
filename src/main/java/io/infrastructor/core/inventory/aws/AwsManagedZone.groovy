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
        
        current.each { cn ->
            def tc = target.find { it.name == cn.name }
            if (tc == null) { 
                cn.state = 'removed'
            } else if (needRebuild(tc, cn)) {
                tc.state = 'created'
                cn.state = 'removed'
            } else {
                tc.state = needUpdate(tc, cn) ? 'updated' : ''
                tc.instanceId = cn.instanceId
                tc.publicIp   = cn.publicIp
                tc.privateIp  = cn.privateIp
            }
        }
        
        [*target, *current.findAll { it.state == 'removed' }]
    }
    
    private static boolean needRebuild(def tc, def cn) {
        return (cn.imageId != tc.imageId) ||
        (cn.instanceType != tc.instanceType) ||
        (cn.subnetId != tc.subnetId) ||
        (cn.keyName != tc.keyName)
    }
    
    private static boolean needUpdate(def tc, def cn) {
        def cnTags = cn.tags.collectEntries { k, v -> [k as String, v as String] }
        def tcTags = tc.tags.collectEntries { k, v -> [k as String, v as String] }
        return ((cn.securityGroupIds as Set) != (tc.securityGroupIds as Set)) || (cnTags != tcTags)
    }
}
