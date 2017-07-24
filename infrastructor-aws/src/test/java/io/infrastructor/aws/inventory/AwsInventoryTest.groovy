package io.infrastructor.aws.inventory

import org.junit.Test
import org.junit.experimental.categories.Category

import static io.infrastructor.aws.inventory.AwsInventory.awsInventory
import static io.infrastructor.aws.inventory.ManagedAwsInventory.managedAwsInventory
import static io.infrastructor.core.logging.ConsoleLogger.*

@Category(AwsCategory.class)
class AwsInventoryTest extends AwsTestBase {
    
    @Test
    public void findAwsNodes() {
        try {
            def inventory = managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) {
                ec2(parallel: 2, tags: [managed: true], usePublicIp: true, username: cfg.USERNAME, keyfile: cfg.KEYFILE) {
                    node {
                        name             = 'simple-y'
                        imageId          = cfg.IMAGE_ID 
                        instanceType     = cfg.T2_MICRO 
                        subnetId         = cfg.SUBNET_ID 
                        keyName          = cfg.KEYNAME 
                        securityGroupIds = cfg.SECURITY_GROUP_IDS
                    }
                    
                    node {
                        name             = 'simple-x'
                        imageId          = cfg.IMAGE_ID 
                        instanceType     = cfg.T2_MICRO 
                        subnetId         = cfg.SUBNET_ID 
                        keyName          = cfg.KEYNAME 
                        securityGroupIds = cfg.SECURITY_GROUP_IDS
                    }
                }
            }
            
            inventory.provision {
                task {
                    waitForPort port: 22, delay: 3000, attempts: 10
                    def result = shell "ls /var"
                    info "result: $result"
                }
            }
            
            assert inventory.nodes.size() == 2
            
            def readOnlyInventory = awsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) {
                username    = cfg.USERNAME
                keyfile     = cfg.KEYFILE
                usePublicIp = true
                tags = [managed: 'true']
            }
            
            assert readOnlyInventory.nodes.size() == 2
            
        } finally {
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) { 
                ec2(tags: [managed: true], usePublicIp: true, username: cfg.USERNAME, keyfile: cfg.KEYFILE) {} 
            }.provision()
        }
    }
}

