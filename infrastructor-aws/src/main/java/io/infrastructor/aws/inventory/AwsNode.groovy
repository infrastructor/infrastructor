package io.infrastructor.aws.inventory

import com.amazonaws.services.ec2.model.*
import groovy.transform.ToString
import io.infrastructor.core.inventory.Node

import static io.infrastructor.aws.inventory.utils.AmazonEC2Utils.waitForInstanceState
import static io.infrastructor.core.logging.ConsoleLogger.debug

@ToString(includePackage = false, includeNames = true, ignoreNulls = true, includeSuperProperties = true)
class AwsNode extends Node {

    def name
    def imageId
    def instanceType
    def subnetId
    def keyName
    def securityGroupIds
    def privateIp
    def publicIp
    def blockDeviceMappings = [] as Set
    def state = ''

    int waitingCount = 100
    int waitingDelay = 3000

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

        if (state != '') {
            result << ['aws:state': state]
        }
        result << [
                'aws:name'        : name,
                'aws:imageid'     : imageId,
                'aws:subnetid'    : subnetId,
                'aws:instancetype': instanceType,
                'aws:keyname'     : keyName,
                'aws:publicip'    : publicIp,
                'aws:privateip'   : privateIp
        ]
        tags.each { k, v -> result << [(k as String): (v as String)] }

        result
    }

    def create(def amazonEC2, def usePublicIp = false) {
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

        debug "creating EC2: $this"
        id = amazonEC2.runInstances(request).getReservation().getInstances().get(0).getInstanceId()
        updateTags(amazonEC2)

        def instance = waitForInstanceState(amazonEC2, id, waitingCount, waitingDelay, 'running')

        debug "updating host to publicIp: ${usePublicIp}"
        host = usePublicIp ? instance.publicIpAddress : instance.privateIpAddress
        publicIp = instance.publicIpAddress
        privateIp = instance.privateIpAddress
    }

    def remove(def amazonEC2) {
        if (id) {
            debug "removing EC2: $this"
            amazonEC2.terminateInstances(new TerminateInstancesRequest([id]))
        }
    }

    def stop(def amazonEC2) {
        if (id) {
            debug "stopping EC2: $this"
            StopInstancesRequest stopInstancesRequest = new StopInstancesRequest()
            stopInstancesRequest.withInstanceIds(id)
            amazonEC2.stopInstances(stopInstancesRequest)
        }
    }

    def update(def amazonEC2) {
        debug "updating EC2: $this"
        updateTags(amazonEC2)
        updateSecurityGroupIds(amazonEC2)
    }

    private def updateTags(def amazonEC2) {
        debug "updating tags: $tags to the instance: $id"

        DescribeInstancesResult describeInstances = amazonEC2.describeInstances(new DescribeInstancesRequest().withInstanceIds(id))
        Instance instance = describeInstances.getReservations().get(0).getInstances().get(0)

        // remove old tags
        DeleteTagsRequest deleteTagsRequest = new DeleteTagsRequest()
        deleteTagsRequest.withResources(id)
        deleteTagsRequest.setTags(instance.getTags())
        amazonEC2.deleteTags(deleteTagsRequest)

        // create new tags
        CreateTagsRequest createTagsRequest = new CreateTagsRequest()
        createTagsRequest.withResources(id)
        tags.each { key, value -> createTagsRequest.withTags(new Tag(key as String, value as String)) }
        createTagsRequest.withTags(new Tag("Name", name))
        amazonEC2.createTags(createTagsRequest)
    }

    private def updateSecurityGroupIds(def amazonEC2) {
        debug "updating security groups: $securityGroupIds to the instance: $id"
        ModifyInstanceAttributeRequest request = new ModifyInstanceAttributeRequest()
        request.setInstanceId(id)
        request.setGroups(securityGroupIds)
        amazonEC2.modifyInstanceAttribute(request)
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

    def getLogName() { name ?: id }
}