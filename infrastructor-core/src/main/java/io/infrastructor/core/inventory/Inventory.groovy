package io.infrastructor.core.inventory

import groovy.transform.ToString
import io.infrastructor.core.utils.FilteringUtils

import static io.infrastructor.core.provisioning.ProvisioningContext.provision
import static io.infrastructor.core.validation.ValidationHelper.validate

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
class Inventory {

    def nodes = [:]

    void leftShift(Node node) {
        nodes << [(node.id) : validate(node)]
    }

    def provision(Closure closure) {
        provision(this, closure)
        this
    }

    def size() {
        nodes.size()
    }

    def find(Closure closure) {
        nodes.values().find(closure)
    }

    def filter(Closure closure) {
        nodes.values().findAll { FilteringUtils.match(it.listTags(), closure) }
    }

    Node getAt(String id) {
        nodes[id]
    }
}

