package io.infrastructor.core.inventory

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

class FileInventory {
    def files = []
    def provision = {}
   
    def static fileInventory(String... files) {
        fileInventory(files: files)
    }
    
    def static fileInventory(Map params, Closure setup = {}) {
        def fileInventory = new FileInventory(params)
        fileInventory.with(setup)
        fileInventory.load()
    }
    
    def load() {
        ImportCustomizer imports = new ImportCustomizer()
        imports.addStaticStars("io.infrastructor.core.logging.ConsoleLogger")
        imports.addStaticStars("io.infrastructor.core.logging.ConsoleInput")
        imports.addStaticStars("io.infrastructor.core.utils.ConfigUtils")
        
        CompilerConfiguration configuration = new CompilerConfiguration()
        configuration.addCompilationCustomizers(imports)
        configuration.setScriptBaseClass(FileInventoryScript.class.name)
        
        def inventory = new Inventory()
        def shell = new GroovyShell(new Binding([inventory: inventory]), configuration)
        files.each { file -> shell.evaluate(new File(file)) }
        inventory.provision(provision)
        inventory
    }
}

