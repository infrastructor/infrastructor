package io.infrastructor.core.inventory

interface Inventory {
    Inventory provision(Closure closure)
    int size()
    Node find(Closure closure)
    Node [] filter(Closure closure)
    Node getAt(String id)
}
