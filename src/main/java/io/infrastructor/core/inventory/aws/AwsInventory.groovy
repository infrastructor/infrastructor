package io.infrastructor.core.inventory.aws

import io.infrastructor.core.inventory.Inventory
import io.infrastructor.core.inventory.Node

import static io.infrastructor.core.utils.AmazonEC2Utils.amazonEC2
import static io.infrastructor.cli.ConsoleLogger.debug

public class AwsInventory {

    def username
    def keyfile
    def port = 22
    def tags = [:]
    def usePublicIp = false
    
    private final def amazonEC2
    private final def awsNodes = AwsNode.&convert
    private final def withTags = { it.tags.intersect(tags) == tags }
    
    public AwsInventory(def awsAccessKey, def awsSecretKey, def awsRegion) {
        this.amazonEC2 = amazonEC2(awsAccessKey, awsSecretKey, awsRegion)
    }
    
    public Inventory build() {
        amazonEC2.describeInstances().getReservations().collect { 
            it.getInstances().findAll { 
                it.getState().getCode() == 16 // running
            } 
        }.flatten().
            collect(awsNodes).
            findAll(withTags).
            inject(new Inventory()) { 
                inventory, node -> 
                    node.keyfile = keyfile
                    node.port = port
                    node.username = username
                    node.usePublicIp = usePublicIp
                    inventory << (node as Node) 
                    inventory
            }
    }

    public static Inventory awsInventory(def awsAccessKey, def awsSecretKey, def awsRegion, Closure definition) {
        def awsInventory = new AwsInventory(awsAccessKey, awsSecretKey, awsRegion)
        awsInventory.with(definition)
        return awsInventory.build()
    }
}

