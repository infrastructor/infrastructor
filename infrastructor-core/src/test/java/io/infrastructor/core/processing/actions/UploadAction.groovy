package io.infrastructor.core.processing.actions

import javax.validation.constraints.NotNull
import io.infrastructor.core.utils.CryptoUtils

public class UploadAction {
    
    @NotNull
    def target
    @NotNull
    def source
    def group
    def owner
    def mode
    def decryptionKey
    def sudo = false
    
    def execute(def node) {
        if (decryptionKey) {
            byte [] decrypted = CryptoUtils.decryptFullBytes(decryptionKey as String, new File(source).text)
            node.writeFile(target, new ByteArrayInputStream(decrypted), sudo)
        } else {
            node.writeFile(target, new FileInputStream(source), sudo)
        }
        
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.updateMode(target, mode, sudo)
        node.lastResult
    }
}

