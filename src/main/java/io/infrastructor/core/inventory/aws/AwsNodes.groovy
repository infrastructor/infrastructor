package io.infrastructor.core.inventory.aws

import static io.infrastructor.core.utils.FilteringUtils.match

public class AwsNodes {
    
    def nodes = []

    def usePublicHost() {
        nodes.each { it.host = it.publicIp }
        this
    }
    
    def usePrivateHost() {
        nodes.each { it.host = it.privateIp }
        this
    }
    
    def usePublicHost(def usePublic) {
        usePublic ? usePublicHost() : usePrivateHost()
        this
    }
    
    def filter(def expression) {
        def newNodes = nodes.findAll { node ->
            match(node.listTags(), expression)
        }
        new AwsNodes(nodes: newNodes)
    }
    
    def filterByTags(def tags) {
        def stringTags = tags.collectEntries { k, v -> [(k as String), (v as String)] }
        def newNodes = nodes.findAll { node ->
            node.allTags().intersect(stringTags) == stringTags
        }
        new AwsNodes(nodes: newNodes)
    }
    
    def merge(AwsNodes current) {
        
        current.nodes*.state = ''
        nodes*.state = 'created'
        
        current.nodes.each { existing ->
            def candidate = nodes.find { it.name == existing.name }
            if (candidate == null) { 
                existing.state = 'removed'
            } else if (needRebuild(candidate, existing)) {
                candidate.state = 'created'
                existing.state = 'removed'
            } else {
                candidate.state     = needUpdate(candidate, existing) ? 'updated' : ''
                candidate.id        = existing.id
                candidate.publicIp  = existing.publicIp
                candidate.privateIp = existing.privateIp
                candidate.host      = candidate.usePublicIp ? existing.publicIp : existing.privateIp
            }
        }
        
        nodes = [*nodes, *current.nodes.findAll { it.state == 'removed' }].toSorted { a, b -> a.name <=> b.name }
        this
    }
    
    private static boolean needRebuild(def candidate, def existing) {
        ((existing.imageId      != candidate.imageId)      ||
         (existing.instanceType != candidate.instanceType) ||
         (existing.subnetId     != candidate.subnetId)     ||
         (existing.keyName      != candidate.keyName))
    }
    
    private static boolean needUpdate(def candidate, def existing) {
        def existingTags  =  existing.tags.collectEntries { k, v -> [k as String, v as String] }
        def candidateTags = candidate.tags.collectEntries { k, v -> [k as String, v as String] }
        ((existing.securityGroupIds as Set) != (candidate.securityGroupIds as Set)) || (existingTags != candidateTags)
    }
}

