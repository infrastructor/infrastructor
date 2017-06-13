package io.infrastructor.core.inventory.aws

import org.junit.Test
import org.junit.experimental.categories.Category

import static io.infrastructor.core.inventory.aws.ManagedAwsInventory.managedAwsInventory
import static io.infrastructor.core.inventory.aws.AwsInventory.awsInventory

@Category(AwsCategory.class)
public class AwsNodeCreationTest extends AwsTestBase {
    
    @Test
    public void rebuildNodeWhenDiskSizehasChanged() {
        try {
            
            def initialInventory = managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) {
                ec2(parallel: 2, tags: [managed: true]) {
                    node {
                        name = 'simple-y'
                        imageId = 'ami-3f1bd150' // Ubuntu Server 16.04 LTS (HVM), SSD Volume Type
                        instanceType = 't2.micro'
                        subnetId = 'subnet-fd7b3b95' // EU Centra-1, default VPC with public IPs
                        keyName = 'aws_infrastructor_ci'
                        securityGroupIds = ['sg-8e6fcae5'] // default-ssh-only
                        username = "ubuntu"
                        keyfile = "resources/aws/aws_infrastructor_ci"
                        usePublicIp = true
                        blockDeviceMapping {
                            name = '/dev/sda1'
                            deleteOnTermination = true
                            volumeSize = 20
                            volumeType = 'gp2'
                        }
                    }
                }
            }
            
            initialInventory.setup {}  
            assert initialInventory.managedNodes.size() == 1
            assert initialInventory.managedNodes[0].state == 'created'
            
            def updatedInventory = managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) {
                ec2(parallel: 2, tags: [managed: true]) {
                    node {
                        name = 'simple-y'
                        imageId = 'ami-3f1bd150' // Ubuntu Server 16.04 LTS (HVM), SSD Volume Type
                        instanceType = 't2.micro'
                        subnetId = 'subnet-fd7b3b95' // EU Centra-1, default VPC with public IPs
                        keyName = 'aws_infrastructor_ci'
                        securityGroupIds = ['sg-8e6fcae5'] // default-ssh-only
                        username = "ubuntu"
                        keyfile = "resources/aws/aws_infrastructor_ci"
                        usePublicIp = true
                        blockDeviceMapping {
                            name = '/dev/sda1'
                            deleteOnTermination = true
                            volumeSize = 16
                            volumeType = 'gp2'
                        }
                    }
                }
            }
            
            updatedInventory.setup {} 
            assert updatedInventory.managedNodes.size() == 2
            assert updatedInventory.managedNodes.find { it.state == 'created' }
            assert updatedInventory.managedNodes.find { it.state == 'removed' }
            
        } finally {
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) { 
                ec2(tags: [managed: true]) {} 
            }.setup {}
        }
    }
}

