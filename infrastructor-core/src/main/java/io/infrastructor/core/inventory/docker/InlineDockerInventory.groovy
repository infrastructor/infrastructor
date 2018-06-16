package io.infrastructor.core.inventory.docker

import io.infrastructor.core.inventory.Inventory

import static io.infrastructor.core.logging.status.ProgressStatusLogger.withProgressStatus
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.core.validation.ValidationHelper.validate

class InlineDockerInventory {

    def nodes = []

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
        nodes << validate(dockerNode)
    }

    def synchronized launch(def timeout = 0) {
        def inventory = new Inventory()
        withTextStatus("> launching docker nodes") {
            withProgressStatus(nodes.size(), 'nodes launched') { progressLine ->
                nodes.each {
                    inventory << it.launch()
                    progressLine.increase()
                }
            }
        }

        sleep timeout
        inventory
    }

    def synchronized shutdown() {
        withTextStatus("> shutting down docker nodes") {
            withProgressStatus(nodes.size(), 'nodes terminated') { progressLine ->
                nodes.each {
                    it.shutdown()
                    progressLine.increase()
                }
            }
        }
    }
}

