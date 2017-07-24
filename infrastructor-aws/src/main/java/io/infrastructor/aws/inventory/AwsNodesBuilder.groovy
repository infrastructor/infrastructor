package io.infrastructor.aws.inventory

import com.amazonaws.services.ec2.model.DescribeVolumesRequest

import static io.infrastructor.aws.inventory.AwsBlockDeviceMapping.awsBlockDeviceMapping

public class AwsNodesBuilder {
    
    def nodes = []
    
    def node(Map params) { 
        node(params, {}) 
    }
    
    def node(def closure) { 
        node([:], closure) 
    }
    
    def node(Map params, def closure) { 
        def node = new AwsNode(params)
        node.with(closure)
        nodes << node
    }
    
    public static def build(def closure) {
        def builder = new AwsNodesBuilder()
        closure.delegate = builder
        closure()
        fromNodes(builder.nodes)
    }

    public static AwsNodes fromNodes(def nodes) {
        new AwsNodes(nodes: nodes)
    }
    
    public static AwsNodes fromEC2(def amazonEC2) {
        def reservations = amazonEC2.describeInstances().getReservations();
        def nodes = reservations.collect { 
            it.getInstances().findAll { 
                it.getState().getCode() == 16 // running
            } 
        }.flatten().collect { instance ->
            def mappings = instance.getBlockDeviceMappings().collect { deviceMappings ->
                def description = amazonEC2.describeVolumes(
                    new DescribeVolumesRequest([deviceMappings.ebs.volumeId]))
                description.volumes.collect { volume ->
                    awsBlockDeviceMapping {
                        name = deviceMappings.deviceName
                        deleteOnTermination = deviceMappings.ebs.deleteOnTermination
                        encrypted = volume.encrypted
                        iops = volume.iops
                        volumeSize = volume.size
                        volumeType = volume.volumeType
                    }
                }
            }.flatten() as Set
            fromEC2(instance, mappings) 
        }
        fromNodes(nodes)
    }
    
    public static AwsNode fromEC2(def instance, def deviceMappings) {
        def node = new AwsNode()
        node.with {
            id               = instance.instanceId
            name             = instance.tags.find { it.key == 'Name' }?.value
            imageId          = instance.imageId
            instanceType     = instance.instanceType
            subnetId         = instance.subnetId
            keyName          = instance.keyName
            securityGroupIds = instance.securityGroups.collect { it.groupId }
            publicIp         = instance.publicIpAddress
            privateIp        = instance.privateIpAddress
            blockDeviceMappings = deviceMappings
            tags             = instance.tags.inject([:]) { tags, tag ->
                if (tag.key != 'Name') { tags << [(tag.key) : (tag.value)] } 
                tags
            }
        }
        node
    }
}

