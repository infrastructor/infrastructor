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

    private def finder = new FileNameFinder()
    
    def execute(def node) {
        def uploads = [:]
        def sourceFile = new File(source)
        
        if (sourceFile.isDirectory()) {
            def path = sourceFile.getCanonicalPath()
            finder.getFileNames(path, includes, excludes).each {
                uploads[it] = it.replace(path, target)
            }
        } else {
            uploads[source] = target
        }
        
        uploads.each { local, remote -> upload(node, local, remote) }
        
        node.lastResult
    }
    
    def upload(def node, def local, def remote) {
        if (decryptionKey) {
            byte [] decrypted = CryptoUtils.decryptFull(decryptionKey as String, new File(local).text)
            node.writeFile(remote, new ByteArrayInputStream(decrypted), sudo)
        } else {
            node.writeFile(remote, new FileInputStream(local), sudo)
        }
            
        node.updateOwner(remote, owner, sudo)
        node.updateGroup(remote, group, sudo)
        node.updateMode(remote, mode, sudo)
    }
}
