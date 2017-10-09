package io.infrastructor.core.inventory

import groovy.transform.ToString
import javax.validation.constraints.NotNull
import com.jcraft.jsch.JSchException

import static io.infrastructor.core.utils.RetryUtils.retry
import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.inventory.SshClient.sshClient

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
            retry(5, 2000) { client.connect() }
            if (!client.isConnected()) { throw new RuntimeException("unable to connect to node $this") }
        }
    }
    
    def disconnect() {
        if (client?.isConnected()) { 
            debug "Node($host:$port) :: disconnecting"
            client.disconnect() 
        }
    }
    
    def execute(Map command) {
        debug "ssh execute: $command"
        
        connect()
        
        lastResult = client.execute(command)
        
        if (stopOnError && lastResult.exitcode != 0) { 
            throw new RemoteExecutionException([*:command, result: lastResult] as String)
        }
            
        return lastResult
    }
    
    def readFile(def file, def stream, def sudo = false) {
        execute sudo: sudo, output: stream, command: "cat $file" 
    }
    
    def writeText(def target, def content, def sudo = false) {
        writeFile(target, new ByteArrayInputStream(content.getBytes()), sudo)
    }
    
    def writeFile(def target, def stream, def sudo = false) {
        execute sudo: sudo, command: "mkdir -p \$(dirname $target)"
        execute input: stream, command: "cat | " + (sudo ? "sudo tee $target" : "tee $target")
    }
    
    def updateOwner(def target, def owner, def sudo = false) {
        if (owner) execute sudo: sudo, command: "chown $owner: $target"
    }
    
    def updateGroup(def target, def group, def sudo = false) {
        if (group) execute sudo: sudo, command: "chown :$group $target"
    }
    
    def updateMode(def target, def mode, def sudo = false) {
        if (mode) execute sudo: sudo, command: "chmod $mode $target"
    }
    
    def allTags() { tags }
    
    def listTags() {
        def list = []
        allTags().each() { k, v -> list << ("$k:$v" as String) } 
        list
    }
    
    def getLogName() { id }
}