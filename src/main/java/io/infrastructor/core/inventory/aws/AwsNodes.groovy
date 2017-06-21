package io.infrastructor.core.inventory.aws

import static io.infrastructor.cli.ConsoleLogger.debug
import static io.infrastructor.core.utils.FilteringUtils.match

public class AwsNodes {
    
    def nodes = []

    def usePublicHost() {
        nodes.each { it.usePublicIp = true }
        this
    }
    
    def usePrivateHost() {
        nodes.each { it.usePublicIp = false }
        this
    }
    
    def usePublicHost(def usePublic) {
        nodes.each { it.usePublicIp = usePublic }
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
    
    def each(Closure closure) {
        nodes.each(closure)
        this
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
        
        def imageHasChanged = (existing.imageId != candidate.imageId)
        
        if (imageHasChanged) {
            debug "Image has changed for ${candidate.name}: existing imageId: ${existing.imageId}, current imageId: ${candidate.imageId}"
        }
        
        def instanceTypeHasChanged = (existing.instanceType != candidate.instanceType)
        
        if (instanceTypeHasChanged) {
            debug "Instance Type has changed for ${candidate.name}: existing instanceType: ${existing.instanceType}, current instanceType: ${candidate.instanceType}"
        }
        
        def subnetIdHasChanged = (existing.subnetId != candidate.subnetId)
        
        if (subnetIdHasChanged) {
            debug "Subnet has changed for ${candidate.name}: existing subnetId: ${existing.subnetId}, current subnetId: ${candidate.subnetId}"
        }
        
        def keyNameHasChanged = (existing.keyName != candidate.keyName)
        
        if (keyNameHasChanged) {
            debug "Key name has changed for ${candidate.name}: existing keyName: ${existing.keyName}, current keyName: ${candidate.keyName}"
        }
        
        def result = (imageHasChanged || instanceTypeHasChanged || subnetIdHasChanged || keyNameHasChanged) 
     
        if (candidate.blockDeviceMappings.size() > 0) {
            def hasBDMChange = (existing.blockDeviceMappings != candidate.blockDeviceMappings)
            
            if (hasBDMChange) {
                debug "A block device mapping has changed for instance ${existing.id}."
                debug "Existing BDM: $existing.blockDeviceMappings"
                debug "Current BDM:  $candidate.blockDeviceMappings"
            }
            
            return (result || hasBDMChange)
        } else {
            return result
        }
        
    }
    
    private static boolean needUpdate(def candidate, def existing) {
        def existingTags  =  existing.tags.collectEntries { k, v -> [k as String, v as String] }
        def candidateTags = candidate.tags.collectEntries { k, v -> [k as String, v as String] }
        ((existing.securityGroupIds as Set) != (candidate.securityGroupIds as Set)) || (existingTags != candidateTags)
    }
}

