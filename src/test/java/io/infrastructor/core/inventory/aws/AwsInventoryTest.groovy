package io.infrastructor.core.inventory.aws

import org.junit.Test
import org.junit.experimental.categories.Category

import static io.infrastructor.core.inventory.aws.managed.ManagedAwsInventory.managedAwsInventory
import static io.infrastructor.core.inventory.aws.AwsInventory.awsInventory
import static io.infrastructor.core.processing.actions.Actions.*
import static io.infrastructor.cli.logging.ConsoleLogger.*

@Category(AwsCategory.class)
class AwsInventoryTest extends AwsTestBase {
    
    @Test
    public void findAwsNodes() {
        try {
            def inventory = managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) {
                ec2(parallel: 2, tags: [managed: true], usePublicIp: true, username: "ubuntu", keyfile: "resources/aws/aws_infrastructor_ci") {
                    node {
                        name = 'simple-y'
                        imageId = 'ami-3f1bd150' // Ubuntu Server 16.04 LTS (HVM), SSD Volume Type
                        instanceType = 't2.micro'
                        subnetId = 'subnet-fd7b3b95' // EU Centra-1, default VPC with public IPs
                        keyName = 'aws_infrastructor_ci'
                        securityGroupIds = ['sg-8e6fcae5'] // default-ssh-only
                    }
                    
                    node {
                        name = 'simple-x'
                        imageId = 'ami-3f1bd150' // Ubuntu Server 16.04 LTS (HVM), SSD Volume Type
                        instanceType = 't2.micro'
                        subnetId = 'subnet-fd7b3b95' // EU Centra-1, default VPC with public IPs
                        keyName = 'aws_infrastructor_ci'
                        securityGroupIds = ['sg-8e6fcae5'] // default-ssh-only
                    }
                }
            }
            
            inventory.setup {
                nodes {
                    waitForPort port: 22, delay: 3000, attempts: 10
                    def result = shell "ls /var"
                    info "result: $result"
                }
            }
            
            assert inventory.nodes.size() == 2
            
            def readOnlyInventory = awsInventory(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) {
                username = 'ubuntu'
                keyfile  = 'resources/aws/aws_infrastructor_ci'
                usePublicIp = true
                tags = [managed: 'true']
            }
            
            assert readOnlyInventory.nodes.size() == 2
            
        } finally {
            managedAwsInventory(AWS_ACCESS_KEY_ID, AWS_SECRET_KEY, AWS_REGION) { 
                ec2(tags: [managed: true]) {} 
            }.setup {}
        }
    }
}

