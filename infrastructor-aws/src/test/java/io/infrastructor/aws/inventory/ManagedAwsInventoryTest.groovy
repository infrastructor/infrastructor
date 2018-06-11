package io.infrastructor.aws.inventory

import org.junit.Test
import org.junit.experimental.categories.Category

import static io.infrastructor.aws.inventory.ManagedAwsInventory.managedAwsInventory
import static io.infrastructor.aws.inventory.utils.AmazonEC2Utils.assertInstanceExists

@Category(AwsCategory.class)
class ManagedAwsInventoryTest extends AwsTestBase  {
    
    @Test
    void createInventory() {
        try {
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) {
                ec2(tags: [managed: true], usePublicIp: true) {
                    node {
                        name             = 'dummy-x'
                        imageId          = cfg.IMAGE_ID 
                        instanceType     = cfg.T2_MICRO 
                        subnetId         = cfg.SUBNET_ID 
                        keyName          = cfg.KEYNAME 
                        securityGroupIds = cfg.SECURITY_GROUP_IDS
                        username         = cfg.USERNAME
                        keyfile          = cfg.KEYFILE
                    }
                }
            }.provision()
                
            assertInstanceExists(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) {
                name             = 'dummy-x'
                imageId          = cfg.IMAGE_ID 
                instanceType     = cfg.T2_MICRO 
                subnetId         = cfg.SUBNET_ID 
                keyName          = cfg.KEYNAME 
                securityGroupIds = cfg.SECURITY_GROUP_IDS
            }
                
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) {
                ec2(tags: [managed: true], usePublicIp: true) {
                    node {
                        name             = 'dummy-x'
                        imageId          = cfg.IMAGE_ID 
                        instanceType     = cfg.T2_MICRO 
                        subnetId         = cfg.SUBNET_ID 
                        keyName          = cfg.KEYNAME 
                        securityGroupIds = [cfg.SG_DEFAULT_SSH_ONLY, cfg.SG_TEST]
                        username         = cfg.USERNAME
                        keyfile          = cfg.KEYFILE
                        tags = ['newtag': 'simple']
                    }
                }
            }.provision()
            
            assertInstanceExists(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) {
                name             = 'dummy-x'
                imageId          = cfg.IMAGE_ID 
                instanceType     = cfg.T2_MICRO 
                subnetId         = cfg.SUBNET_ID 
                keyName          = cfg.KEYNAME 
                securityGroupIds = [cfg.SG_DEFAULT_SSH_ONLY, cfg.SG_TEST]
                tags = ['managed': 'true', 'Name': 'dummy-x', 'newtag': 'simple']
            }
        } finally {
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) { 
                ec2(tags: [managed: true], username: cfg.USERNAME, keyfile: cfg.KEYFILE) {} 
            }.provision()
        }
    }
    
    @Test
    void updateTags() {
        try {
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) {
                ec2(tags: [managed: true], usePublicIp: true) {
                    node {
                        name             = 'dummy-x'
                        imageId          = cfg.IMAGE_ID 
                        instanceType     = cfg.T2_MICRO 
                        subnetId         = cfg.SUBNET_ID 
                        keyName          = cfg.KEYNAME 
                        securityGroupIds = cfg.SECURITY_GROUP_IDS
                        username         = cfg.USERNAME
                        keyfile          = cfg.KEYFILE
                        
                        tags = ['simple': 1, 'test': 2]
                    }
                }
            }.provision()
            
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) {
                ec2(tags: [managed: true], usePublicIp: true) {
                    node {
                        name             = 'dummy-x'
                        imageId          = cfg.IMAGE_ID 
                        instanceType     = cfg.T2_MICRO 
                        subnetId         = cfg.SUBNET_ID 
                        keyName          = cfg.KEYNAME 
                        securityGroupIds = cfg.SECURITY_GROUP_IDS
                        username         = cfg.USERNAME
                        keyfile          = cfg.KEYFILE
                        
                        tags = ['simple': 1, 'another': 2]
                    }
                }
            }.provision()
            
            assertInstanceExists(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) {
                name             = 'dummy-x'
                imageId          = cfg.IMAGE_ID 
                instanceType     = cfg.T2_MICRO 
                subnetId         = cfg.SUBNET_ID 
                keyName          = cfg.KEYNAME 
                securityGroupIds = cfg.SECURITY_GROUP_IDS
                tags = ['simple': '1', 'another': '2', 'managed': 'true', 'Name': 'dummy-x']
            }
        } finally {
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) { 
                ec2(tags: [managed: true], username: cfg.USERNAME, keyfile: cfg.KEYFILE) {} 
            }.provision()
        }
        
    }
}