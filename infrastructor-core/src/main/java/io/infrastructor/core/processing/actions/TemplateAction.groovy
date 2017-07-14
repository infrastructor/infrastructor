package io.infrastructor.core.processing.actions

import groovy.text.SimpleTemplateEngine
import javax.validation.constraints.NotNull
import io.infrastructor.core.utils.CryptoUtils

public class TemplateAction {
    
    static final FULL = 'FULL'
    static final PART = 'PART'
    
    @NotNull
    def target
    @NotNull
    def source
    def bindings
    def group
    def owner
    def mode
    def sudo = false
    def decryptionKey
    def decryptionMode = PART
    def engine = new SimpleTemplateEngine()
    
    def execute(def node) {
        def template = new File(source).text
        def content = ''
            
        if (!decryptionKey) {
            content = engine.createTemplate(template).make(bindings)
        } else if (decryptionMode == PART) {
            content = CryptoUtils.decryptPart(decryptionKey as String, template, bindings)
        } else if (decryptionMode == FULL) {
            def decrypted = CryptoUtils.decryptFull(decryptionKey as String, template)
            content = engine.createTemplate(decrypted).make(bindings)
        } else {
            throw new ActionProcessingException("Unable to process template using decryption mode: $decryptionMode")
        }
        
        node.writeText(target, content.toString(), sudo)
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.updateMode(target, mode, sudo)
        node.lastResult
    }
}