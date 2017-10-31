package io.infrastructor.core.inventory

import groovy.transform.ToString
import javax.validation.constraints.NotNull
import com.jcraft.jsch.JSchException

import static io.infrastructor.core.utils.RetryUtils.retry
import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.inventory.SshClient.sshClient
import static io.infrastructor.core.inventory.CommandBuilder.CMD

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
public class Node {
    
    def id
    @NotNull
    def host
    @NotNull
    def port = 22
    @NotNull
    def username
    def password
    def keyfile
    Map tags = [:]
    Map metadata = [:]
    
    protected def client
    protected def lastResult = [:]
    protected def stopOnError = true
    
    def connect() {
        if (!client?.isConnected()) {
            client = sshClient(host: host, port: port, username: username, password: password, keyfile: keyfile)
            retry(5, 2000) { 
                debug "connecting to node: ${getLogName()}, host: $host, port: $port"
                client.connect() 
            }
            if (!client.isConnected()) { throw new RuntimeException("unable to connect to node $this") }
        }
    }
    
    def disconnect() {
        if (client?.isConnected()) { 
            debug "disconnecting from node: ${getLogName()}, host: $host, port: $port"
            client.disconnect()
        }
    }
    
    def execute(Map command) {
        connect()
        
        lastResult = client.execute(command)
        
        if (stopOnError && lastResult.exitcode != 0) { 
            throw new RemoteExecutionException([*:command, result: lastResult] as String)
        }
            
        return lastResult
    }

    def readFile(def file, def output, def sudo = false) {
        execute output: output, command: CMD {
            add sudo, "sudo"
            add "cat '$file'"
        } 
    }
    
    def writeFile(def target, def input, def sudo = false) {
        execute command: CMD {
            add sudo, "sudo"
            add "sh -c \"dirname '$target' | xargs -I '{}' mkdir -p '{}'\""
        }
            
        execute input: input, command: CMD {
            add "cat | "
            add sudo, "sudo"
            add "tee '$target'"
        }
    }
    
    def writeText(def target, def content, def sudo = false) {
        writeFile(target, new ByteArrayInputStream(content.getBytes()), sudo)
    }
    
    def updateOwner(def target, def owner, def sudo = false) {
        if (owner) { 
            execute command: CMD {
                add sudo, "sudo"
                add "chown $owner: $target"
            }
        }
    }
    
    def updateGroup(def target, def group, def sudo = false) {
        if (group) { 
            execute command: CMD {
                add sudo, "sudo"
                add "chown :$group $target"
            }
        }
    }
    
    def updateMode(def target, def mode, def sudo = false) {
        if (mode) { 
            execute command: CMD {
                add sudo, "sudo"
                add "chmod $mode $target"
            }
        }
    }
    
    def allTags() { tags }
    
    def listTags() { allTags().collect { k, v -> "$k:$v" as String } }
    
    def getLogName() { id }
}