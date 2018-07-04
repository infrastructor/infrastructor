package io.infrastructor.core.inventory

import groovy.transform.ToString

import javax.validation.constraints.NotNull

import static io.infrastructor.core.inventory.CommandBuilder.CMD
import static io.infrastructor.core.inventory.SshClient.sshClient
import static io.infrastructor.core.logging.ConsoleLogger.debug
import static io.infrastructor.core.utils.RetryUtils.retry

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
class Node {
    
    def id
    @NotNull
    def host
    @NotNull
    def port = 22
    @NotNull
    def username
    def password
    def keyfile
    def keypass
    Map tags = [:]
    Map metadata = [:]
    
    protected def client
    protected def lastResult = [:]
    protected def stopOnError = true
    
    def connect() {
        if (!client?.isConnected()) {
            client = sshClient(host: host, port: port, username: username, password: password, keyfile: keyfile, keypass: keypass)
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

    def readFile(def file, def output, def user = '') {
        execute output: output, command: CMD {
            add user, "sudo -s -u $user"
            add "cat '$file'"
        } 
    }
    
    def writeFile(def target, def input, def user = '') {
        execute command: CMD {
            add user, "sudo -s -u $user"
            add "sh -c \"dirname '$target' | xargs -I '{}' mkdir -p '{}'\""
        }
            
        execute input: input, command: CMD {
            add "cat | "
            add user, "sudo -s -u $user"
            add "tee '$target'"
        }
    }
    
    def writeText(def target, def content, def user = '') {
        writeFile(target, new ByteArrayInputStream(content.getBytes()), user)
    }
    
    def updateOwner(def target, def owner, def user = '') {
        if (owner) { 
            execute command: CMD {
                add user, "sudo -s -u $user"
                add "chown $owner: $target"
            }
        }
    }
    
    def updateGroup(def target, def group, def user = '') {
        if (group) { 
            execute command: CMD {
                add user, "sudo -s -u $user"
                add "chown :$group $target"
            }
        }
    }
    
    def updateMode(def target, def mode, def user = '') {
        if (mode) { 
            execute command: CMD {
                add user, "sudo -s -u $user"
                add "chmod $mode $target"
            }
        }
    }
    
    def allTags() { tags }
    
    def listTags() { allTags().collect { k, v -> "$k:$v" as String } }
    
    def getLogName() { id }
}