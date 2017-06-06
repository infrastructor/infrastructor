package io.infrastructor.core.inventory

import org.junit.Test
import io.infrastructor.core.validation.ValidationException

import static io.infrastructor.core.inventory.InlineInventory.inlineInventory

public class NodeValidationTest {
    
    @Test(expected = ValidationException)
    public void nodeMustHaveAHost() {
        def inventory = inlineInventory {
            node(port: 10000, username: "root")
        }    
    }
    
    @Test(expected = ValidationException)
    public void portMustNotBeNull() {
        def inventory = inlineInventory {
            node(host: "host", port: null, username: "root")
        }    
    }
    
    @Test(expected = ValidationException)
    public void usernameMustNotBeNull() {
        def inventory = inlineInventory {
            node(host: "host", port: 10000, username: null)
        }    
    }
}
