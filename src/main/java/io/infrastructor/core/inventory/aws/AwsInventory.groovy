package io.infrastructor.core.inventory.aws

import io.infrastructor.core.inventory.Inventory
import io.infrastructor.core.inventory.Node

import static io.infrastructor.cli.logging.ConsoleLogger.*
import static io.infrastructor.core.utils.AmazonEC2Utils.amazonEC2
import static io.infrastructor.cli.logging.status.TextStatusLogger.withTextStatus

public class AwsInventory {

    def username
    def password
    def keyfile
    def port = 22
    def tags = [:]
    def usePublicIp = false
    
    public Inventory build(def awsAccessKey, def awsSecretKey, def awsRegion) {
        withTextStatus { statusLine -> 
            statusLine "> initializing aws inventory"
            def amazonEC2 = amazonEC2(awsAccessKey, awsSecretKey, awsRegion)

            debug 'AwsInventory :: connecting to AWS to retrieve a list of EC2 instances'
            def awsNodes  = AwsNodesBuilder.
                                fromEC2(amazonEC2).
                                filterByTags(tags).
                                usePublicHost(usePublicIp).
                                each {
                                    it.username = owner.username
                                    it.password = owner.password
                                    it.keyfile  = owner.keyfile
                                    it.port     = owner.port
                                }
                                
            debug "AwsInventory :: inventory is ready [${awsNodes.nodes.size()} node]: "
            awsNodes.nodes.each { debug( "Node: ${defColor(it.name)}: ${yellow(it as String)}")}

            return new Inventory(nodes: awsNodes.nodes)
        }
    }

    public static Inventory awsInventory(def awsAccessKey, def awsSecretKey, def awsRegion, Closure definition) {
        def awsInventory = new AwsInventory()
        awsInventory.with(definition)
        awsInventory.build(awsAccessKey, awsSecretKey, awsRegion)
    }
}

