package io.infrastructor.core.provisioning.actions

import javax.validation.constraints.NotNull

import static io.infrastructor.core.utils.CryptoUtils.decryptFull

class UploadAction {
    
    @NotNull
    def target
    @NotNull
    def source
    def group
    def owner
    def mode
    def decryptionKey
    def user
    def sudopass
    
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
            byte [] decrypted = decryptFull(decryptionKey as String, new File(local).text)
            node.writeFile(remote, new ByteArrayInputStream(decrypted), user, sudopass)
        } else {
            node.writeFile(remote, new FileInputStream(local), user, sudopass)
        }
            
        node.updateOwner(remote, owner, user, sudopass)
        node.updateGroup(remote, group, user, sudopass)
        node.updateMode(remote, mode, user, sudopass)
    }
}
