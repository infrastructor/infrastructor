package io.infrastructor.core.inventory.aws

import io.infrastructor.core.inventory.Inventory
import io.infrastructor.core.inventory.Node

import static io.infrastructor.cli.logging.ProgressLogger.*
import static io.infrastructor.core.utils.AmazonEC2Utils.amazonEC2
import io.infrastructor.cli.logging.status.TextStatusLogger

public class AwsInventory {

    def username
    def keyfile
    def port = 22
    def tags = [:]
    def usePublicIp = false
    
    public Inventory build(def awsAccessKey, def awsSecretKey, def awsRegion) {
        
        def statusLine = addStatusLogger(new TextStatusLogger()) 
        statusLine.status("> initializing aws inventory")
        
        def amazonEC2 = amazonEC2(awsAccessKey, awsSecretKey, awsRegion)

        debug 'AwsInventory :: connecting to AWS to retrieve a list of EC2 instances'
        def awsNodes  = AwsNodesBuilder.fromEC2(amazonEC2).filterByTags(tags).usePublicHost(usePublicIp)

        debug 'AwsInventory :: updating username, keyfile and port information for all inventory nodes'
        awsNodes.nodes.each {
            it.username = owner.username
            it.keyfile  = owner.keyfile
            it.port     = owner.port
        }
        
        def inventory = new Inventory(nodes: awsNodes.nodes)
        
        debug "AwsInventory :: inventory is ready [${inventory.nodes.size()} node]: "
        inventory.nodes.each { debug( "Node: ${defColor(it.name)}: ${yellow(it as String)}")}
        
        removeStatusLogger(statusLine)
        
        inventory
    }

    public static Inventory awsInventory(def awsAccessKey, def awsSecretKey, def awsRegion, Closure definition) {
        def awsInventory = new AwsInventory()
        awsInventory.with(definition)
        awsInventory.build(awsAccessKey, awsSecretKey, awsRegion)
    }
}

