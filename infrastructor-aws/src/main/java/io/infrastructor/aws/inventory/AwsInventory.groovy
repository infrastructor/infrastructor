package io.infrastructor.aws.inventory

import io.infrastructor.core.inventory.Inventory

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.aws.inventory.utils.AmazonEC2Utils.amazonEC2

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
            def awsNodes = AwsNodesBuilder.
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

    def static awsInventory(def awsAccessKey, def awsSecretKey, def awsRegion, Closure definition) {
        def awsInventory = new AwsInventory()
        awsInventory.with(definition)
        awsInventory.build(awsAccessKey, awsSecretKey, awsRegion)
    }
}

