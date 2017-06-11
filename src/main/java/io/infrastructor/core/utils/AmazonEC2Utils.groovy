package io.infrastructor.core.utils

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest
import com.amazonaws.services.ec2.model.Filter


public class AmazonEC2Utils {
    
    public static AmazonEC2 amazonEC2(def awsAccessKey, def awsSecretKey, def awsRegion) {
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
    
    public static void assertInstanceExists(def awsAccessKey, def awsSecretKey, def awsRegion, def definition) {
        def amazonEC2 = amazonEC2(awsAccessKey, awsSecretKey, awsRegion)
        
        def reservations = amazonEC2.describeInstances().getReservations()
        def allExistingRunningInstances = reservations.collect { 
            it.getInstances().findAll { 
                it.getState().getCode() == 16 // running
            } 
        }.flatten()
        
        def expected = [:]
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
    
    public static def findSubnetIdByName(def awsAccessKey, def awsSecretKey, def awsRegion, def name) {
        def amazonEC2 = amazonEC2(awsAccessKey, awsSecretKey, awsRegion) 
        def result = amazonEC2.describeSubnets(
            new DescribeSubnetsRequest().withFilters(new Filter("tag:Name", [name])))
         
        if (result.getSubnets().size() == 0) {
            throw new RuntimeException("Unable to find subnet with name '$name'")
        }
         
        if (result.getSubnets().size() > 1) {
            throw new RuntimeException("Multiple subnets with the same name ($name) has been found")
        }
        
        return result.getSubnets()[0].subnetId
    }
}

