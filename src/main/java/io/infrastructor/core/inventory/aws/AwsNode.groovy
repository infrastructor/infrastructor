package io.infrastructor.core.inventory.aws

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.CreateTagsRequest
import com.amazonaws.services.ec2.model.ModifyInstanceAttributeRequest
import com.amazonaws.services.ec2.model.RunInstancesRequest
import com.amazonaws.services.ec2.model.Tag
import com.amazonaws.services.ec2.model.TerminateInstancesRequest
import groovy.transform.ToString
import io.infrastructor.core.inventory.Node

import static io.infrastructor.cli.ConsoleLogger.info
import static io.infrastructor.cli.ConsoleLogger.debug
import static io.infrastructor.core.inventory.aws.AwsNodeUtils.waitForInstanceStateIsRunning

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
public class AwsNode extends Node {
    
    def name
    def imageId
    def instanceType
    def subnetId
    def keyName
    def securityGroupIds
    def usePublicIp = false
    def privateIp
    def publicIp
    def state = ''

    
    public static AwsNode convert(def ec2instance) {
        def node = new AwsNode()
        node.with {
            id               = ec2instance.instanceId
            name             = ec2instance.tags.find { it.key == 'Name' }?.value
            imageId          = ec2instance.imageId
            instanceType     = ec2instance.instanceType
            subnetId         = ec2instance.subnetId
            keyName          = ec2instance.keyName
            securityGroupIds = ec2instance.securityGroups.collect { it.groupId }
            privateIp        = ec2instance.privateIpAddress
            publicIp         = ec2instance.publicIpAddress
            host = usePublicIp ? publicIp : privateIp
            tags             = ec2instance.tags.inject([:]) { tags, tag ->
                if (tag.key != 'Name') { tags << [(tag.key) : (tag.value)] } 
                tags
            }
        }
        node
    }
    
    
    def allTags() {
        def allTags = [:]
        
        // state tags
        if (state != '') { allTags << ['aws:state': state] }
        
        // aws tags
        allTags << ['aws:name': name]
        allTags << ['aws:imageid': imageId]
        allTags << ['aws:subnetid': subnetId]
        allTags << ['aws:instancetype': instanceType]
        allTags << ['aws:keyname': keyName]
        allTags << ['aws:publicip': publicIp]
        allTags << ['aws:privateip': privateIp]
        
        // node tags
        tags.inject(allTags) { map, k, v -> map << [(k as String): (v as String)] }
        
        return allTags
    }
    
    def create(def amazonEC2) {
        RunInstancesRequest request = new RunInstancesRequest()
        request.setImageId(imageId)
        request.setInstanceType(instanceType)
        request.setSubnetId(subnetId)
        request.setKeyName(keyName)
        request.setSecurityGroupIds(securityGroupIds)
        request.setMinCount(1)
        request.setMaxCount(1)
        request.setPrivateIpAddress(privateIp)
        
        info "creating EC2: $this" 
        id = amazonEC2.runInstances(request).getReservation().getInstances().get(0).getInstanceId()
        updateTags(amazonEC2)
        
        def instance = waitForInstanceStateIsRunning(amazonEC2, id, 50, 7000)
        publicIp  = instance.publicIpAddress
        privateIp = instance.privateIpAddress
    }
    
    def remove(def amazonEC2) {
        if (id) {
            info "removing EC2: $this" 
            amazonEC2.terminateInstances(new TerminateInstancesRequest([id]))
        }
    }
    
    def update(def amazonEC2) {
        info "updating EC2: $this"
        updateTags(amazonEC2)
        updateSecurityGroupIds(amazonEC2)
    }

    private def updateTags(def amazonEC2) {
        debug "updating tags: $tags to the instance: $id"
        CreateTagsRequest createTagsRequest = new CreateTagsRequest()
        createTagsRequest.withResources(id)
        tags.each { key, value -> createTagsRequest.withTags(new Tag(key as String, value as String)) }
        createTagsRequest.withTags(new Tag("Name", name))
        amazonEC2.createTags(createTagsRequest)
    }
    
    private def updateSecurityGroupIds(def amazonEC2) {
        debug "updating security groups: $securityGroupIds to the instance: $id"
        ModifyInstanceAttributeRequest request = new ModifyInstanceAttributeRequest();
        request.setInstanceId(id);
        request.setGroups(securityGroupIds);
        amazonEC2.modifyInstanceAttribute(request);
    }
    
}