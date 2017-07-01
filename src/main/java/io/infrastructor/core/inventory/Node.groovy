package io.infrastructor.core.inventory

import groovy.transform.ToString
import javax.validation.constraints.NotNull
import io.infrastructor.core.inventory.ssh.SshClient
import com.jcraft.jsch.JSchException

import static io.infrastructor.core.utils.RetryUtils.retry
import static io.infrastructor.cli.logging.ConsoleLogger.*
import static io.infrastructor.core.inventory.ssh.SshClient.sshClient

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
    
    def stopOnError = true
    
    protected def lastResult = [:]
    
    private def client
    
    def connect() {
        if (isDisconnected()) {
            
            client = sshClient {
                host = owner.host
                port = owner.port
                username = owner.username
                password = owner.password
                keyfile = owner.keyfile
            }
        
            debug "Node($host:$port) :: connecting"
            
            retry(3, 1000) { client.connect() }
            
            if (isDisconnected()) { throw new RuntimeException("unable to connect to node $this") }
        }
    }
    
    def disconnect() {
        debug "Node($host:$port) :: disconnecting"
        if (client != null) { client.disconnect() }
    }
    
    def isDisconnected() {
        client == null || !client.isConnected()
    }
    
    def execute(Map map) {
        debug "ssh execute: $map"
        
        if (isDisconnected()) { connect() }
        
        lastResult = client.execute(map)
        
        if (stopOnError && lastResult.exitcode != 0) { 
            throw new CommandExecutionException(lastResult)
        } else {
            return lastResult
        }
    }
    
    def readFile(def file, def stream, def sudo = false) {
        execute(command: "cat $file", output: stream, sudo: sudo)
    }
    
    def writeText(def target, def content, def sudo = false) {
        writeFile(target, new ByteArrayInputStream(content.getBytes()), sudo)
    }
    
    def writeFile(def target, def stream, def sudo = false) {
        execute(command: "cat | " + (sudo ? "sudo tee $target" : "tee $target"), input: stream)
    }
    
    def updateOwner(def target, def owner, def sudo = false) {
        if (owner) execute(command: "chown $owner: $target", sudo: sudo) 
    }
    
    def updateGroup(def target, def group, def sudo = false) {
        if (group) execute(command: "chown :$group $target", sudo: sudo)
    }
    
    def updateMode(def target, def mode, def sudo = false) {
        if (mode) execute(command: "chmod $mode $target", sudo: sudo)
    }
    
    def allTags() {
        return tags
    }
    
    def listTags() {
        def list = []
        allTags().each() { k, v -> list << ("$k:$v" as String) } 
        list
    }
}