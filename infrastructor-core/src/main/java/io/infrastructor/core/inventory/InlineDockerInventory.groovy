package io.infrastructor.core.inventory

import static io.infrastructor.core.logging.status.ProgressStatusLogger.withProgressStatus
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.core.validation.ValidationHelper.validate

class InlineDockerInventory implements ManagedInventory {

    def nodes = [:]

    def static inlineDockerInventory(Closure closure) {
        def dockerNodes = new InlineDockerInventory()
        dockerNodes.with(closure)
        dockerNodes
    }

    def node(Map params) {
        node(params, {})
    }

    def node(Closure closure) {
        node([:], closure)
    }

    def node(Map params, Closure closure) {
        def dockerNode = new DockerNode(params)
        dockerNode.with(closure)
        nodes << [(dockerNode.id): validate(dockerNode)]
    }

    Inventory provision(Closure closure) {
        launch().provision(closure)
    }

    synchronized Inventory launch() {
        def inventory = new BasicInventory()
        withTextStatus("> launching docker nodes") {
            withProgressStatus(nodes.size(), 'nodes launched') { progressLine ->
                nodes.values().each {
                    inventory << it.launch()
                    progressLine.increase()
                }
            }
        }

        inventory
    }

    synchronized void shutdown() {
        withTextStatus("> shutting down docker nodes") {
            withProgressStatus(nodes.size(), 'nodes terminated') { progressLine ->
                nodes.values().each {
                    it.shutdown()
                    progressLine.increase()
                }
            }
        }
    }

    def inventory() {
        def inventory = new BasicInventory()
        nodes.values().each { inventory << it.asNode() }
        inventory
    }

    int size() {
        inventory().size()
    }

    Node find(Closure closure) {
        inventory().find(closure)
    }

    @java.lang.Override
    Node[] filter(Closure closure) {
        inventory().filter(closure)
    }

    @java.lang.Override
    Node getAt(String id) {
        inventory().getAt(id)
    }
}

