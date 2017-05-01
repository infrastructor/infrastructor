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

import static io.infrastructor.cli.ConsoleLogger.info
import static io.infrastructor.cli.ConsoleLogger.debug


@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
public class AwsNode {
    def name
    def imageId
    def instanceType
    def subnetId
    def keyName
    def securityGroupIds
    def tags = [:]
    
    def username
    def keyfile
    def port = 22
    def metadata = [:]
    def usePublicIp = false
    
    def instanceId
    def privateIp
    def publicIp
    
    def state = ''
    
    
    public static AwsNode build(Map params, Closure definition = {}) {
        def node = new AwsNode(params)
        node.with(definition)
        node
    }
    
    
    public static AwsNode build(Closure definition = {})  {
        build([:], definition)
    }
    
    
    public static AwsNode convert(def instance) {
        build {
            instanceId       = instance.instanceId
            name             = instance.tags.find { it.key == 'Name' }?.value
            imageId          = instance.imageId
            instanceType     = instance.instanceType
            subnetId         = instance.subnetId
            keyName          = instance.keyName
            securityGroupIds = instance.securityGroups.collect { it.groupId }
            privateIp        = instance.privateIpAddress
            publicIp         = instance.publicIpAddress
            tags             = instance.tags.inject([:]) { tags, tag ->
                if (tag.key != 'Name') { 
                    tags << [(tag.key) : (tag.value)] 
                } 
                tags
            }
        }
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
        instanceId =  amazonEC2.runInstances(request).getReservation().getInstances().get(0).getInstanceId()
        updateTags(amazonEC2)
        
        def instance = waitForInstanceStateIsRunning(amazonEC2, instanceId, 50, 7000)
        publicIp  = instance.publicIpAddress
        privateIp = instance.privateIpAddress
    }
    
    
    def remove(def amazonEC2) {
        if (instanceId) {
            info "removing EC2: $this" 
            amazonEC2.terminateInstances(new TerminateInstancesRequest([instanceId]))
        }
    }
    
    
    def update(def amazonEC2) {
        info "updating EC2: $this"
        updateTags(amazonEC2)
        updateSecurityGroupIds(amazonEC2)
    }

    
    def waitForInstanceStateIsRunning(AmazonEC2 amazonEC2, String instanceId, int attempts, int interval) {
        for (int i = attempts; i > 0; i--) {
            DescribeInstancesRequest request = new DescribeInstancesRequest()
            request.setInstanceIds([instanceId])
            DescribeInstancesResult result = amazonEC2.describeInstances(request)
            Instance instance = result.getReservations().get(0).getInstances().get(0)
            debug "wait for instance '$instanceId' state is running, current state: ${instance.getState().getCode()}"
            if (instance.getState().getCode() == 16) { // instance is running
                return instance
            }
            sleep(interval)
        }
        throw new RuntimeException("timeout waiting for instance $instanceId state is running after $attempts attempts. node: $this")
    }
    
    
    public Object asType(Class clazz) {
        if (clazz == Node) {
            return new Node([
                    id: name, 
                    host: usePublicIp ? publicIp : privateIp, 
                    port: port, 
                    username: username, 
                    keyfile: keyfile, 
                    tags: prepareNodeTags(),
                    metadata: metadata])
        }
        
        throw new RuntimeException("Unable to convert AwsNode to $clazz")
    }
    

    private def prepareNodeTags() {
        def nodeTags = [:]
        if (state != '') { nodeTags << ['aws:state': state] }
        nodeTags << ['aws:name': name]
        nodeTags << ['aws:imageid': imageId]
        nodeTags << ['aws:subnetid': subnetId]
        nodeTags << ['aws:instancetype': instanceType]
        nodeTags << ['aws:keyname': keyName]
        nodeTags << ['aws:publicip': publicIp]
        nodeTags << ['aws:privateip': privateIp]
        tags.inject(nodeTags) { map, k, v -> map << [(k as String): (v as String)] }
        return nodeTags
    }
    
    
    private def updateTags(def amazonEC2) {
        debug "updating tags: $tags to the instance: $instanceId"
        CreateTagsRequest createTagsRequest = new CreateTagsRequest()
        createTagsRequest.withResources(instanceId)
        tags.each { key, value -> createTagsRequest.withTags(new Tag(key as String, value as String)) }
        createTagsRequest.withTags(new Tag("Name", name))
        amazonEC2.createTags(createTagsRequest)
    }
    
    
    private def updateSecurityGroupIds(def amazonEC2) {
        ModifyInstanceAttributeRequest request = new ModifyInstanceAttributeRequest();
        request.setInstanceId(instanceId);
        request.setGroups(securityGroupIds);
        amazonEC2.modifyInstanceAttribute(request);
    }
}

