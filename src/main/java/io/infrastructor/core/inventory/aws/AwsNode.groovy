package io.infrastructor.core.inventory.aws

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.BlockDeviceMapping
import com.amazonaws.services.ec2.model.CreateTagsRequest
import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.model.DescribeInstancesResult
import com.amazonaws.services.ec2.model.DescribeVolumesRequest
import com.amazonaws.services.ec2.model.EbsBlockDevice
import com.amazonaws.services.ec2.model.Instance
import com.amazonaws.services.ec2.model.ModifyInstanceAttributeRequest
import com.amazonaws.services.ec2.model.RunInstancesRequest
import com.amazonaws.services.ec2.model.Tag
import com.amazonaws.services.ec2.model.TerminateInstancesRequest
import groovy.transform.ToString
import io.infrastructor.core.inventory.Node

import static io.infrastructor.cli.ConsoleLogger.debug
import static io.infrastructor.cli.ConsoleLogger.info

@ToString(includePackage = false, includeNames = true, ignoreNulls = true, includeSuperProperties = true)
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
    def blockDeviceMappings = [] as Set
    def state = ''
    
    def blockDeviceMapping(Map params) {
        blockDeviceMapping(params, {})
    }
    
    def blockDeviceMapping(Closure closure) {
        blockDeviceMapping([:], closure)
    }
    
    def blockDeviceMapping(Map params, Closure closure) {
        def blockDeviceMapping = new AwsBlockDeviceMapping(params)
        blockDeviceMapping.with(closure)
        blockDeviceMappings << blockDeviceMapping
    }
    
    def allTags() {
        def result = [:]
        
        if (state != '') { result << ['aws:state': state] }
        result << [
            'aws:name': name,
            'aws:imageid': imageId,
            'aws:subnetid': subnetId,
            'aws:instancetype': instanceType,
            'aws:keyname': keyName,
            'aws:publicip': publicIp,
            'aws:privateip': privateIp
        ]
        tags.each { k, v -> result << [(k as String) : (v as String)] }
        
        result
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
        request.setBlockDeviceMappings(buildBlockDeviceMappings())
        
        info "creating EC2: $this" 
        id = amazonEC2.runInstances(request).getReservation().getInstances().get(0).getInstanceId()
        updateTags(amazonEC2)
        
        def instance = waitForInstanceIsRunning(amazonEC2, 50, 7000)
        
        debug "updating host to publicIp: ${candidate.usePublicIp}"
        host      = usePublicIp ? instance.publicIpAddress : instance.privateIpAddress
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
    
    private def buildBlockDeviceMappings() {
        def mappings = []
        blockDeviceMappings.each { mapping ->
            mappings << new BlockDeviceMapping().
                withDeviceName(mapping.name).
                withEbs(new EbsBlockDevice().
                    withDeleteOnTermination(mapping.deleteOnTermination).
                    withEncrypted(mapping.encrypted).
                    withIops(mapping.iops).
                    withVolumeSize(mapping.volumeSize).
                    withVolumeType(mapping.volumeType))
        }
        mappings
    }
}