package io.infrastructor.core.inventory

import groovy.transform.ToString
import io.infrastructor.core.utils.FilteringUtils

import static io.infrastructor.core.processing.ProvisioningContext.provision
import static io.infrastructor.core.validation.ValidationHelper.validate

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
class Inventory {

    def nodes = []

    void leftShift(Node node) {
        nodes << validate(node)
    }

    def provision(Closure closure) {
        provision(nodes, closure)
        this
    }

    def size() {
        nodes.size()
    }

    def find(Closure closure) {
        nodes.find(closure)
    }

    def filter(Closure closure) {
        nodes.findAll { FilteringUtils.match(it.listTags(), closure) }
    }

    Node getAt(String id) {
        nodes.find { it.id == id }
    }
}

