package io.infrastructor.core.inventory.aws

import org.junit.Test
import org.junit.experimental.categories.Category

import static io.infrastructor.core.inventory.aws.ManagedAwsInventory.managedAwsInventory
import static io.infrastructor.core.inventory.aws.AwsInventory.awsInventory

@Category(AwsCategory.class)
public class AwsInventoryTest extends AwsTestBase {
    
    @Test
    public void findAwsNodes() {
        try {
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) {
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
                    }
                    
                    node {
                        name = 'simple-x'
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
            }.setup {
                nodes {
                    println "Setup Node: $node"
                }
            }
            
            awsInventory(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) {
                username = 'ubuntu'
                keyfile  = 'resources/aws/aws_infrastructor_ci'
                usePublicIp = true
                tags = [managed: 'true']
            }.setup {
                nodes {
                    println "AwsInventory Node: $node"
                }
            }
            
        } finally {
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) { 
                ec2(tags: [managed: true]) {} 
            }.setup {}
        }
    }
}

