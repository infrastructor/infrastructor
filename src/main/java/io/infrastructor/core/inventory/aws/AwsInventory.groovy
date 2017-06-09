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
    
    public Inventory build(def awsAccessKey, def awsSecretKey, def awsRegion) {
        def amazonEC2 = amazonEC2(awsAccessKey, awsSecretKey, awsRegion)
        def awsNodes  = AwsNodesBuilder.fromEC2(amazonEC2).filterByTags(tags).usePublicHost(usePublicIp)
        new Inventory(nodes: awsNodes.nodes)
    }

    public static Inventory awsInventory(def awsAccessKey, def awsSecretKey, def awsRegion, Closure definition) {
        def awsInventory = new AwsInventory()
        awsInventory.with(definition)
        awsInventory.build(awsAccessKey, awsSecretKey, awsRegion)
    }
}

