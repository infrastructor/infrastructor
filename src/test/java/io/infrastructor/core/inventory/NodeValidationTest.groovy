package io.infrastructor.core.inventory

import io.infrastructor.core.validation.ValidationException
import org.testng.annotations.Test

import static io.infrastructor.core.inventory.InlineInventory.inlineInventory


public class NodeValidationTest {
    
    @Test(expectedExceptions = [ValidationException])
    public void nodeMustHaveAHost() {
        def inventory = inlineInventory {
            node(port: 10000, username: "root")
        }    
    }
    
    @Test(expectedExceptions = [ValidationException])
    public void portMustNotBeNull() {
        def inventory = inlineInventory {
            node(host: "host", port: null, username: "root")
        }    
    }
    
    @Test(expectedExceptions = [ValidationException])
    public void usernameMustNotBeNull() {
        def inventory = inlineInventory {
            node(host: "host", port: 10000, username: null)
        }    
    }
}
