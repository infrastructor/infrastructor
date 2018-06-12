package io.infrastructor.aws.inventory.utils

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model.*
import io.infrastructor.aws.inventory.AwsNode

import static io.infrastructor.core.logging.ConsoleLogger.debug
import static io.infrastructor.core.utils.RetryUtils.retry

class AmazonEC2Utils {
    
    def static AmazonEC2 amazonEC2(def awsAccessKey, def awsSecretKey, def awsRegion) {
        AmazonEC2ClientBuilder standard = AmazonEC2ClientBuilder.standard()
        standard.setCredentials(new AWSStaticCredentialsProvider(new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() { awsAccessKey }

                    @Override
                    public String getAWSSecretKey() { awsSecretKey }
                }))
        standard.setRegion(awsRegion)
        standard.build()
    }
    
    def static waitForInstanceState(def amazonEC2, def instanceId, int count, int delay, def state) {
        retry(count, delay) {
            DescribeInstancesRequest request = new DescribeInstancesRequest()
            request.setInstanceIds([instanceId])
            DescribeInstancesResult result = amazonEC2.describeInstances(request)
            Instance instance = result.getReservations().get(0).getInstances().get(0)
            debug "waiting for instance $instanceId state is $state, current state: ${instance.getState().getName()}"
            assert instance.getState().getName() == state
            instance
        }
    }
    
    def static waitForImageState(def amazonEC2, def imageId, int count, int delay, def state) {
        retry(count, delay) {
            DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest()
            describeImagesRequest.withImageIds(imageId)
            DescribeImagesResult describeImagesResult = amazonEC2.describeImages(describeImagesRequest)
            def actual = describeImagesResult.getImages().get(0).getState()
            debug "waiting for image $imageId is available, current state is $actual"
            assert actual == state
        }
    }
    
    def static findImage(def amazonEC2, def imageName, def state = 'available') {
        DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest()
        describeImagesRequest.withFilters(new Filter().withName("name").withValues(imageName))
        DescribeImagesResult describeImagesResult = amazonEC2.describeImages(describeImagesRequest)
        def images = describeImagesResult.getImages()
        
        if (!images.isEmpty() && images.get(0).getState() == state) {
            return images.get(0)
        }
        
        return null
    }
    
    def static deregisterImage(def amazonEC2, def imageId) {
        amazonEC2.deregisterImage(new DeregisterImageRequest(imageId))
    }
    
    def static createImage(def amazonEC2, def imageName, def instanceId) {
        CreateImageRequest createImageRequest = new CreateImageRequest()
        createImageRequest.withName(imageName)
        createImageRequest.withInstanceId(instanceId)
        amazonEC2.createImage(createImageRequest).imageId
    }
        
    public static void assertInstanceExists(def awsAccessKey, def awsSecretKey, def awsRegion, def definition) {
        def amazonEC2 = amazonEC2(awsAccessKey, awsSecretKey, awsRegion)
    
        def reservations = amazonEC2.describeInstances().getReservations()
        def allExistingRunningInstances = reservations.collect { 
            it.getInstances().findAll { 
                it.getState().getCode() == 16 // running
            }
        }.flatten()
    
        def expected = new AwsNode()
        expected.with(definition)
        
        def instance = allExistingRunningInstances.find { it.tags.find { it.key == 'Name' }?.value == expected.name }
        assert instance
        if (expected.imageId)          assert expected.imageId                   == instance.imageId
        if (expected.instanceType)     assert expected.instanceType              == instance.instanceType
        if (expected.subnetId)         assert expected.subnetId                  == instance.subnetId
        if (expected.keyName)          assert expected.keyName                   == instance.keyName
        if (expected.securityGroupIds) assert (expected.securityGroupIds as Set) == (instance.securityGroups.collect { it.groupId } as Set)
        if (expected.tags)             assert expected.tags                      == instance.tags.collectEntries { [(it.key as String) : (it.value as String)] } 
    }
}