package io.infrastructor.aws.inventory

import org.junit.Test
import org.junit.experimental.categories.Category

import static io.infrastructor.aws.inventory.ManagedAwsInventory.managedAwsInventory

@Category(AwsCategory.class)
class AwsNodeCreationTest extends AwsTestBase {
    
    @Test
    void rebuildNodeWhenDiskSizehasChanged() {
        try {
            
            def initialInventory = managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) {
                ec2(tags: [managed: true], usePublicIp: true) {
                    node {
                        name             = 'dummy'
                        imageId          = cfg.IMAGE_ID 
                        instanceType     = cfg.T2_MICRO 
                        subnetId         = cfg.SUBNET_ID 
                        keyName          = cfg.KEYNAME 
                        securityGroupIds = cfg.SECURITY_GROUP_IDS
                        username         = cfg.USERNAME
                        keyfile          = cfg.KEYFILE
                        blockDeviceMapping {
                            name = '/dev/sda1'
                            deleteOnTermination = true
                            volumeSize = 20
                            volumeType = 'gp2'
                        }
                    }
                }
            }
            
            initialInventory.provision {}  
            assert initialInventory.nodes.size() == 1
            assert initialInventory.nodes[0].state == 'created'
            
            def updatedInventory = managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) {
                ec2(tags: [managed: true], usePublicIp: true) {
                    node {
                        name             = 'dummy'
                        imageId          = cfg.IMAGE_ID 
                        instanceType     = cfg.T2_MICRO 
                        subnetId         = cfg.SUBNET_ID 
                        keyName          = cfg.KEYNAME 
                        securityGroupIds = cfg.SECURITY_GROUP_IDS
                        username         = cfg.USERNAME
                        keyfile          = cfg.KEYFILE
                        blockDeviceMapping {
                            name = '/dev/sda1'
                            deleteOnTermination = true
                            volumeSize = 16
                            volumeType = 'gp2'
                        }
                    }
                }
            }
            
            updatedInventory.provision {} 
            assert updatedInventory.nodes.size() == 2
            assert updatedInventory.nodes.find { it.state == 'created' }
            assert updatedInventory.nodes.find { it.state == 'removed' }
            
        } finally {
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) { 
                ec2(tags: [managed: true], usePublicIp: true, username: cfg.USERNAME, keyfile: cfg.KEYFILE) {} 
            }.provision {}
        }
    }
}

