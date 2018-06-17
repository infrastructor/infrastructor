package io.infrastructor.aws.inventory

import static io.infrastructor.aws.inventory.AwsNodesBuilder.fromEC2
import static io.infrastructor.aws.inventory.AwsNodesBuilder.fromNodes
import static io.infrastructor.core.logging.ConsoleLogger.debug
import static io.infrastructor.core.logging.ConsoleLogger.info
import static io.infrastructor.core.logging.status.ProgressStatusLogger.withProgressStatus
import static io.infrastructor.core.utils.ParallelUtils.executeParallel

public class EC2 {

    def tags = [:]
    def parallel = 1

    def username
    def keyfile
    def usePublicIp = false

    private def targetState = []

    def node(Map params) {
        node(params, {})
    }

    def node(Closure closure) {
        node([:], closure)
    }

    def node(Map params, Closure closure) {
        def awsNode = new AwsNode(params)
        awsNode.with(closure)
        targetState << awsNode
    }

    def initialize(def amazonEC2) {
        info "EC2 :: Initializing managed AWS inventory"

        def existing = fromEC2(amazonEC2).filterByTags(tags)
        existing.each {
            it.username = username
            it.keyfile = keyfile
        }.usePublicHost(usePublicIp)

        debug "existing nodes:"
        existing.nodes.each { debug "node.name: $it.name, node.host: $it.host" }

        def defined = fromNodes(targetState)
        defined.each {
            if (it.username == null) it.username = username
            if (it.keyfile == null) it.keyfile = keyfile
            it.tags << tags
        }.usePublicHost(usePublicIp)

        debug "defined nodes:"
        defined.nodes.each { debug "node.name: $it.name, node.host: $it.host" }

        targetState = defined.merge(existing).usePublicHost(usePublicIp).nodes

        debug "after merge:"
        targetState.each { debug "node.name: $it.name, node.host: $it.host" }
    }

    def createInstances(def amazonEC2) {
        debug "EC2 :: Creating instances"
        def created = targetState.findAll { it.state == 'created' }
        withProgressStatus(created.size(), 'nodes have been created') { progressLine ->
            executeParallel(created, parallel) {
                it.create(amazonEC2, usePublicIp)
                progressLine.increase()
            }
        }
    }

    def removeInstances(def amazonEC2) {
        debug "EC2 :: Removing instances"
        def removed = targetState.findAll { it.state == 'removed' }
        withProgressStatus(removed.size(), 'nodes have been removed') { progressLine ->
            executeParallel(removed, parallel) {
                it.remove(amazonEC2)
                progressLine.increase()
            }
        }
    }

    def updateInstances(def amazonEC2) {
        debug "EC2 :: Updating instances"
        def updated = targetState.findAll { it.state == 'updated' }
        withProgressStatus(updated.size(), 'nodes have been updated') { progressLine ->
            executeParallel(updated, parallel) {
                it.update(amazonEC2)
                progressLine.increase()
            }
        }
    }

    def getInventory() {
        targetState
    }
}
