package io.infrastructor.core.inventory.aws

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.CreateTagsRequest
import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.model.DescribeInstancesResult
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.ec2.model.ModifyInstanceAttributeRequest
import com.amazonaws.services.ec2.model.RunInstancesRequest
import com.amazonaws.services.ec2.model.Tag
import com.amazonaws.services.ec2.model.TerminateInstancesRequest
import groovy.transform.ToString
import io.infrastructor.core.inventory.Node

import static io.infrastructor.cli.ConsoleLogger.debug
import static io.infrastructor.cli.ConsoleLogger.info

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

    
    public static AwsNode fromEC2(def instance) {
        def node = new AwsNode()
        node.with {
            id               = instance.instanceId
            name             = instance.tags.find { it.key == 'Name' }?.value
            imageId          = instance.imageId
            instanceType     = instance.instanceType
            subnetId         = instance.subnetId
            keyName          = instance.keyName
            securityGroupIds = instance.securityGroups.collect { it.groupId }
            privateIp        = instance.privateIpAddress
            publicIp         = instance.publicIpAddress
            tags             = instance.tags.inject([:]) { tags, tag ->
                if (tag.key != 'Name') { tags << [(tag.key) : (tag.value)] } 
                tags
            }
        }
        node
    }
    
    
    def allTags() {
        def result = [:]
        
        // state tags
        if (state != '') { result << ['aws:state': state] }
        
        // aws tags
        result << ['aws:name': name]
        result << ['aws:imageid': imageId]
        result << ['aws:subnetid': subnetId]
        result << ['aws:instancetype': instanceType]
        result << ['aws:keyname': keyName]
        result << ['aws:publicip': publicIp]
        result << ['aws:privateip': privateIp]
        
        // node tags
        tags.inject(result) { map, k, v -> map << [(k as String) : (v as String)] }
        
        return result
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
        
        def instance = waitForInstanceIsRunning(amazonEC2, 50, 7000)
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
    
    
    private def waitForInstanceIsRunning(def amazonEC2, int attempts, int interval) {
        for (int i = attempts; i > 0; i--) {
            DescribeInstancesRequest request = new DescribeInstancesRequest()
            request.setInstanceIds([id])
            DescribeInstancesResult result = amazonEC2.describeInstances(request)
            Instance instance = result.getReservations().get(0).getInstances().get(0)
            debug "wait for instance '$id' state is running, current state: ${instance.getState().getCode()}"
            if (instance.getState().getCode() == 16) { // instance is running
                return instance
            }
            sleep(interval)
        }
        throw new RuntimeException("timeout waiting for instance $id state is running after $attempts attempts. node: $this")
    }
    
}