package io.infrastructor.aws.inventory

import static io.infrastructor.core.logging.ConsoleLogger.debug
import static io.infrastructor.core.utils.FilteringUtils.match

class AwsNodes {
    
    def nodes = []

    def each(Closure closure) {
        nodes.each(closure)
        this
    }
    
    def usePublicHost(def usePublicHost) {
        nodes.each { it.host = usePublicHost ? it.publicIp : it.privateIp }
        this
    }
    
    def filter(def expression) {
        new AwsNodes(nodes: nodes.findAll { match(it.listTags(), expression) })
    }
    
    def filterByTags(def tags) {
        def stringTags = asStringMap(tags)
        new AwsNodes(nodes: nodes.findAll { it.allTags().intersect(stringTags) == stringTags })
    }
    
    def merge(AwsNodes current) {
        current.nodes*.state = ''
        nodes*.state = 'created'
        
        current.nodes.each { existing ->
            def candidate = nodes.find { it.name == existing.name }
            if (needRebuild(candidate, existing)) { 
                existing.state = 'removed'
            } else {
                candidate.state               = needUpdate(candidate, existing) ? 'updated' : ''
                candidate.id                  = existing.id
                candidate.publicIp            = existing.publicIp
                candidate.privateIp           = existing.privateIp
                candidate.blockDeviceMappings = existing.blockDeviceMappings
            }
        }
        
        nodes = [*nodes, *current.nodes.findAll { it.state == 'removed' }].toSorted { a, b -> a.name <=> b.name }
        this
    }
    
    private static boolean needRebuild(def candidate, def existing) {
        if (candidate == null) return true
        
        if (existing.imageId != candidate.imageId) {
            debug "needRebuild ${candidate.name} - ${existing.id} / image id E: ${existing.imageId}, C: ${candidate.imageId}"
            return true
        }
        
        if (existing.instanceType != candidate.instanceType) {
            debug "needRebuild ${candidate.name} - ${existing.id} / instance type E: ${existing.instanceType}, C: ${candidate.instanceType}"
            return true
        }
        
        if (existing.subnetId != candidate.subnetId) {
            debug "needRebuild ${candidate.name} - ${existing.id} / subnet id E: ${existing.subnetId}, C: ${candidate.subnetId}"
            return true
        }
        
        if (existing.keyName != candidate.keyName) {
            debug "needRebuild ${candidate.name} - ${existing.id} / keyname E: ${existing.keyName}, C: ${candidate.keyName}"
            return true
        }
        
        if (candidate.blockDeviceMappings.size() > 0) {
            if (existing.blockDeviceMappings != candidate.blockDeviceMappings) {
                debug "needRebuild ${candidate.name} - ${existing.id} / BDM E: $existing.blockDeviceMappings, C: $candidate.blockDeviceMappings"
                return true
            }
        }
        
        return false
    }
    
    private static boolean needUpdate(def candidate, def existing) {
        if ((existing.securityGroupIds as Set) != (candidate.securityGroupIds as Set)) {
            debug "needUpdate ${candidate.name} - ${existing.id} / security groups E: $existing.securityGroupIds, C: $candidate.securityGroupIds"
            return true
        }
        
        def existingTags  = asStringMap(existing.tags)
        def candidateTags = asStringMap(candidate.tags)
        
        if (existingTags != candidateTags) {
            debug "needUpdate ${candidate.name} - ${existing.id} / tags E: $existingTags, C: $candidateTags"
            return true
        }
        
        return false
    }
    
    private static asStringMap(def map) {
        map.collectEntries { k, v -> [k as String, v as String] }
    }
}

