package io.infrastructor.core.inventory

import groovy.transform.ToString
import io.infrastructor.core.utils.FilteringUtils

import static io.infrastructor.core.provisioning.ProvisioningContext.provision
import static io.infrastructor.core.validation.ValidationHelper.validate

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
class BasicInventory implements Inventory {

    def nodes = [:]

    void leftShift(Node node) {
        nodes << [(node.id) : node]
    }

    BasicInventory provision(Closure closure) {
        nodes.values().each { validate (it) }
        provision(this, closure)
        this
    }

    int size() {
        nodes.size()
    }

    Node find(Closure closure) {
        nodes.values().find(closure)
    }

    Node [] filter(Closure closure) {
        nodes.values().findAll { FilteringUtils.match(it.listTags(), closure) }
    }

    Node getAt(String id) {
        nodes[id]
    }
}

