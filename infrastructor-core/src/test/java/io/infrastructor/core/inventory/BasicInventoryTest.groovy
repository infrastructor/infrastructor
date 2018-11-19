package io.infrastructor.core.inventory

import org.junit.Test

class BasicInventoryTest {

    private static final String ID_X = 'ID_X'
    private static final String ID_Y = 'ID_Y'
    
    @Test
    void searchNodeById() {
        def inventory = new BasicInventory()
        inventory << new Node(id: ID_X, host: "10.0.0.1", username: "root")
        inventory << new Node(id: ID_Y, host: "10.0.0.2", username: "root")

        assert inventory[ID_X].host == "10.0.0.1"
        assert inventory[ID_Y].host == "10.0.0.2"
    }

    @Test
    void searchNodesUsingFilters() {
        def inventory = new BasicInventory()
        inventory << new Node(id: ID_X, host: "10.0.0.1", username: "root", tags: [role: 'gateway'])
        inventory << new Node(id: ID_Y, host: "10.0.0.2", username: "root", tags: [role: 'service'])

        def gateways = inventory.filter { 'role:gateway' }

        assert gateways.size() == 1
        assert gateways.find { it.id == ID_X }

        def services = inventory.filter { 'role:service' }

        assert services.size() == 1
        assert services.find { it.id == ID_Y }

        def all = inventory.filter { 'role:gateway' || 'role:service' }

        assert all.size() == 2
        assert all.find { it.id == ID_X }
        assert all.find { it.id == ID_Y }
    }

    @Test
    void checkInventorySize() {
        def inventory = new BasicInventory()

        inventory << new Node(id: ID_X, host: "10.0.0.1", username: "root")
        assert inventory.size() == 1

        inventory << new Node(id: ID_Y, host: "10.0.0.2", username: "root")
        assert inventory.size() == 2

        inventory << new Node(id: ID_Y, host: "10.0.0.3", username: "root")
        assert inventory.size() == 2
        assert inventory[ID_Y].host == "10.0.0.3"
    }


    @Test
    void iterateUsingForEachMethod() {
        def inventory = new BasicInventory()
        inventory << new Node(id: ID_X, host: "10.0.0.1", username: "root", tags: [role: 'gateway'])
        inventory << new Node(id: ID_Y, host: "10.0.0.2", username: "root", tags: [role: 'service'])

        int counter = 0
        inventory.each { id, node ->
            assert id instanceof String
            assert node instanceof Node
            counter++
        }

        assert counter == 2
    }
}
