package io.infrastructor.core.inventory

class FileInventoryMixin {
    
    def static fileInventory(Script script, String... files) {
        fileInventory(script, [files: files])
    }
    
    def static fileInventory(Script script, Map params) {
        fileInventory(script, params, {})
    }
    
    def static fileInventory(Script script, Closure setup) {
        fileInventory(script, [:], setup)
    }
    
    def static fileInventory(Script script, Map params, Closure setup) {
        FileInventory.fileInventory(params, setup)
    }
}

