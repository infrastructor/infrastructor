package io.infrastructor.core.inventory

import io.infrastructor.core.validation.ValidationException
import org.junit.Test

import static io.infrastructor.core.inventory.InlineInventory.inlineInventory

public class NodeValidationTest {
    
    @Test(expected = ValidationException)
    public void nodeMustHaveAHost() {
        inlineInventory {
            node(port: 10000, username: "root")
        }.provision()  
    }
    
    @Test(expected = ValidationException)
    public void portMustNotBeNull() {
        inlineInventory {
            node(host: "host", port: null, username: "root")
        }.provision()    
    }
    
    @Test(expected = ValidationException)
    public void usernameMustNotBeNull() {
        def inventory = inlineInventory {
            node(host: "host", port: 10000, username: null)
        }.provision()   
    }
}
