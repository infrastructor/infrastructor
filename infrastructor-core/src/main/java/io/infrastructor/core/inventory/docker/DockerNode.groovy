package io.infrastructor.core.inventory.docker

import groovy.transform.ToString
import io.infrastructor.core.inventory.Node
import javax.validation.constraints.NotNull

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.utils.NodeUtils.randomPort
import static io.infrastructor.core.utils.FlatUUID.flatUUID

@ToString(includePackage = false, includeNames = true)
public class DockerNode {
    
    def id 
    def port
    @NotNull
    def image
    @NotNull
    def username 
    def password
    def keyfile
    Map tags = [:]
    
    def stopOnError = false
    
    def launch() {
        port = port ?: randomPort()
        id = id ?: flatUUID()
        
        def command = "docker run -d -p $port:22 --name $id $image"
        debug "Lanuching docker node with command: $command"
        
        def process = command.execute()
        process.waitFor()
        
        def exitValue = process.exitValue()
        if (exitValue) {
            throw new RuntimeException("Unable to launch docker node: '$this', exit value: '$exitValue', output: ${process.text}")
        }
        return this as Node
    }
    
    def shutdown() {
        if (id) {
            def command = "docker rm -f $id"
            debug "Shutting down docker node with command: $command"
            
            def process = command.execute()
            process.waitFor()
            
            def exitValue = process.exitValue()
            if (exitValue) {
                throw new RuntimeException("Unable to remove docker node: '$this', exit value: '$exitValue', output: ${process.text}")
            }
        }
    }
    
    public Object asType(Class clazz) {
        if (clazz == Node) {
            return new Node(
                id: id, 
                host: 'localhost', 
                port: port, 
                username: username, 
                password: password, 
                keyfile: keyfile, 
                tags: tags, 
                stopOnError: stopOnError)
        }
        
        return null
    }
}

