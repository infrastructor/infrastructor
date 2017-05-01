package io.infrastructor.core.tasks

import groovy.text.SimpleTemplateEngine
import io.infrastructor.core.inventory.CommandExecutionException
import javax.validation.constraints.NotNull
import io.infrastructor.core.inventory.Node


public class TemplateAction {
    
    @NotNull
    def target
    @NotNull
    def source
    def bindings
    def group
    def owner
    def mode
    def sudo = false
    def engine = new SimpleTemplateEngine()
    
    def execute(Node node) {
        def template = new File(source).text
        def content = engine.createTemplate(template).make(bindings).toString()
        node.writeText(target, content, sudo)
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.updateMode(target, mode, sudo)
    }
}