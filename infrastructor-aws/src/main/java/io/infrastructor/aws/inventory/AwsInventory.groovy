package io.infrastructor.aws.inventory

import io.infrastructor.core.inventory.BasicInventory

import static io.infrastructor.aws.inventory.utils.AmazonEC2Utils.amazonEC2
import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus

class AwsInventory {

    def awsAccessKeyId
    def awsAccessSecretKey
    def awsRegion
    def username
    def password
    def keyfile
    def port = 22
    def tags = [:]
    def usePublicIp = false

    BasicInventory build() {
        withTextStatus { statusLine ->
            statusLine "> initializing aws inventory"
            def amazonEC2 = amazonEC2(awsAccessKeyId, awsAccessSecretKey, awsRegion)

            debug 'AwsInventory :: connecting to AWS to retrieve a list of EC2 instances'
            def awsNodes = AwsNodesBuilder.
                    fromEC2(amazonEC2).
                    filterByTags(tags).
                    usePublicHost(usePublicIp).
                    each {
                        it.username = owner.username
                        it.password = owner.password
                        it.keyfile = owner.keyfile
                        it.port = owner.port
                    }

            debug "AwsInventory :: inventory is ready [${awsNodes.nodes.size()} node]: "
            awsNodes.nodes.each { debug("Node: ${defColor(it.name)}: ${yellow(it as String)}") }

            return new BasicInventory(nodes: awsNodes.nodes.collectEntries { [(it.id): it] })
        }
    }

    def static awsInventory(Map params, Closure definition) {
        def awsInventory = new AwsInventory(params)
        awsInventory.with(definition)
        awsInventory.build()
    }
}