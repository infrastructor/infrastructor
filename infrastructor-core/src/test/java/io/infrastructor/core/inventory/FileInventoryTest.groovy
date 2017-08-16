package io.infrastructor.core.inventory

import org.junit.Test
import static io.infrastructor.core.inventory.FileInventory.*

class FileInventoryTest {
    
    def resource(String name) { "build/resources/test/fileinventory/$name" }
    
    @Test
    void inlineDefinition() {
        def inventory = fileInventory(resource("inventory.groovy"))
        assert inventory.nodes.size() == 2
    }
    
    @Test
    void mapLikeDefinitionWithASingleFile() {
        def inventory = fileInventory files: resource("inventory.groovy")
        assert inventory.nodes.size() == 2
    }
    
    @Test
    void mapLikeDefinition() {
        def inventory = fileInventory files: [resource("inventory.groovy")]
        assert inventory.nodes.size() == 2
    }
    
    @Test
    void multiFileInlineDefinition() {
        def inventory = fileInventory(resource("inventory.groovy"), resource("another.groovy"))
        assert inventory.nodes.size() == 3
    }
    
    @Test
    void multiFileMapLikeDefinition() {
        def inventory = fileInventory files: [
            resource("inventory.groovy"), 
            resource("another.groovy")
        ]
        
        assert inventory.nodes.size() == 3
    }
    
    @Test
    void multiFilesMapLikeDefinitionWithProvisioning() {
        def counter = 0 
        def inventory = fileInventory files: [
            resource("inventory.groovy"), 
            resource("another.groovy")
        ], provision: {
            task name: "simple", actions: {
                counter++
            }
        }
        
        assert inventory.nodes.size() == 3
        assert counter == 3
    }
}

