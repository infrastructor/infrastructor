package io.infrastructor.core.inventory.aws;

import org.junit.Test
import org.junit.experimental.categories.Category
import io.infrastructor.core.utils.AmazonRoute53Utils

import static io.infrastructor.core.inventory.aws.managed.ManagedAwsInventory.managedAwsInventory
import static io.infrastructor.core.inventory.aws.AwsInventory.awsInventory

@Category(AwsCategory.class)
public class AwsRoute53Test extends AwsTestBase {
    
    @Test
    public void findAwsNodes() {
        try {
            def inventory = managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) {
                ec2(parallel: 2, tags: [managed: true], usePublicIp: true) {
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
                    
                    node {
                        name             = 'dummy-y'
                        imageId          = cfg.IMAGE_ID 
                        instanceType     = cfg.T2_MICRO 
                        subnetId         = cfg.SUBNET_ID 
                        keyName          = cfg.KEYNAME 
                        securityGroupIds = cfg.SECURITY_GROUP_IDS
                        username         = cfg.USERNAME
                        keyfile          = cfg.KEYFILE
                        usePublicIp      = true
                    }
                }
                
                route53(hostedZoneId: cfg.HOSTED_ZONE_ID) {
                    recordSet(type: 'A', name: 'simple.test.internal', ttl: 500, resources: {'managed:true'})
                }
            }
            
            inventory.provision {}
            
            def nodes = inventory.getNodes()
            
            assert nodes
            assert nodes.size() == 2
            
            def amazonRoute53 = AmazonRoute53Utils.amazonRoute53(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION)
            def recordSet = AmazonRoute53Utils.findDnsRecordSet(amazonRoute53, cfg.HOSTED_ZONE_ID, 'simple.test.internal')
            
            assert recordSet
            assert recordSet.records.size() == 2
            assert recordSet.records.contains(nodes[0].privateIp)
            assert recordSet.records.contains(nodes[1].privateIp) 
            
        } finally {
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_ACCESS_SECRET_KEY, AWS_REGION) { 
                ec2(tags: [managed: true], usePublicIp: true, username: cfg.USERNAME, keyfile: cfg.KEYFILE) {} 

                route53(hostedZoneId: cfg.HOSTED_ZONE_ID) {
                    recordSet(type: 'A', name: 'simple.test.internal', ttl: 500, resources: {'managed:true'})
                }
            }.provision()
        }
    }
}
