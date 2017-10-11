package io.infrastructor.core.processing.actions

import groovy.util.FileNameFinder
import javax.validation.constraints.NotNull
import io.infrastructor.core.utils.CryptoUtils

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus

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
    
    @NotNull
    def includes = ''
    @NotNull
    def excludes = ''

    def execute(def node) {
        def sourceFile = new File(source)
        sourceFile.isDirectory() ? uploadFolder(node) : uploadFile(node, sourceFile, target) 
        node.lastResult
    }
    
    def uploadFile(def node, def sourceFile, def targetFile) {
        
        debug "uploading file: '$sourceFile' to '$targetFile'"
        
        if (decryptionKey) {
            byte [] decrypted = CryptoUtils.decryptFullBytes(decryptionKey as String, sourceFile.text)
            node.writeFile(targetFile, new ByteArrayInputStream(decrypted), sudo)
        } else {
            node.writeFile(targetFile, new FileInputStream(sourceFile), sudo)
        }
        
        node.updateOwner(target, owner, sudo)
        node.updateGroup(target, group, sudo)
        node.updateMode(target, mode, sudo)
    }
    
    def uploadFolder(def node) {
        def canonicalSource = new File(source).getCanonicalPath()
        def files = new FileNameFinder().getFileNames(canonicalSource, includes, excludes)
        
        withTextStatus { status ->
            files.each { 
                status "> uploading file: $it"
                uploadFile(node, it, it.replace(canonicalSource, target))
            }
        }
    }
    
}
