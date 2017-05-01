package io.infrastructor.core.inventory.aws

import io.infrastructor.core.utils.AwsTestBase
import org.testng.annotations.Test

import static io.infrastructor.core.utils.AmazonEC2Utils.assertInstanceExists
import static io.infrastructor.core.inventory.aws.ManagedAwsInventory.managedAwsInventory


public class ManagedAwsInventoryTest extends AwsTestBase  {
    
    @Test(groups = ['aws'])
    public void createInventory() {
        try {
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) {
                managedZone(tags: [managed: true]) {
                    ec2 {
                        name = 'simple-y'
                        imageId = 'ami-3f1bd150' // Ubuntu Server 16.04 LTS (HVM), SSD Volume Type
                        instanceType = 't2.micro'
                        subnetId = 'subnet-fd7b3b95' // EU Centra-1, default VPC with public IPs
                        keyName = 'aws_infrastructor_ci'
                        securityGroupIds = ['sg-8e6fcae5'] // default-ssh-only

                        username = "ubuntu"
                        keyfile = "resources/aws/aws_infrastructor_ci"
                        usePublicIp = true
                    }
                }
            }.setup()
            
            assertInstanceExists(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) {
                name = 'simple-y'
                imageId = 'ami-3f1bd150' 
                instanceType = 't2.micro' 
                subnetId = 'subnet-fd7b3b95'
                keyName = 'aws_infrastructor_ci'
                securityGroupIds = ['sg-8e6fcae5']
            }
            
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) {
                managedZone(tags: [managed: true]) {
                    ec2 {
                        name = 'simple-y'
                        imageId = 'ami-3f1bd150' // Ubuntu Server 16.04 LTS (HVM), SSD Volume Type
                        instanceType = 't2.micro'
                        subnetId = 'subnet-fd7b3b95' // EU Centra-1, default VPC with public IPs
                        keyName = 'aws_infrastructor_ci'
                        securityGroupIds = ['sg-8e6fcae5', 'sg-922799f9'] // default-ssh-only, test-sg
                        tags = ['newtag': 'simple']

                        username = "ubuntu"
                        keyfile = "resources/aws/aws_infrastructor_ci"
                        usePublicIp = true
                    }
                }
            }.setup()
            
            assertInstanceExists(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) {
                name = 'simple-y'
                imageId = 'ami-3f1bd150' 
                instanceType = 't2.micro' 
                subnetId = 'subnet-fd7b3b95'
                keyName = 'aws_infrastructor_ci'
                securityGroupIds = ['sg-8e6fcae5', 'sg-922799f9']
                tags = ['managed': 'true', 'Name': 'simple-y', 'newtag': 'simple']
            }
        } finally {
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) { 
                managedZone(tags: [managed: true]) {} 
            }.setup()
        }
    }
}

