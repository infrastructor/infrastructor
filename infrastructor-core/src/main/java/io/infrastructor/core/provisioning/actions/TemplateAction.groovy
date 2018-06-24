package io.infrastructor.core.provisioning.actions

import groovy.text.SimpleTemplateEngine

import javax.validation.constraints.NotNull

import static io.infrastructor.core.utils.CryptoUtils2.decryptFull
import static io.infrastructor.core.utils.CryptoUtils2.decryptPart

class TemplateAction {
    
    @NotNull
    def target
    @NotNull
    def source
    def bindings = [:]
    def group
    def owner
    def mode
    def user
    def decryptionKey
    def decryptionMode = PART
    def engine = new SimpleTemplateEngine()

    static final FULL = 'FULL'
    static final PART = 'PART'

    def execute(def node) {
        def template = new File(source).text
        def content = ''

        if (!decryptionKey) {
            content = engine.createTemplate(template).make(bindings)
        } else if (decryptionMode == PART) {
            content = decryptPart(decryptionKey as String, template, bindings)
        } else if (decryptionMode == FULL) {
            content = engine.createTemplate(new String(decryptFull(decryptionKey as String, template))).make(bindings)
        } else {
            throw new ActionExecutionException("Unable to process template using decryption mode: $decryptionMode")
        }

        node.writeText(target, content.toString(), user)
        node.updateOwner(target, owner, user)
        node.updateGroup(target, group, user)
        node.updateMode(target, mode, user)
        node.lastResult
    }
}
