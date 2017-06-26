package io.infrastructor.core.inventory

import com.jcabi.ssh.SSH
import com.jcabi.ssh.SSHByPassword
import com.jcabi.ssh.Shell
import groovy.transform.ToString
import javax.validation.constraints.NotNull

import static io.infrastructor.cli.ConsoleLogger.debug
import static io.infrastructor.cli.ConsoleLogger.info

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
    
    private def shell = new ThreadLocal<Shell>() {
        @Override 
        public Shell initialValue() {
            if (keyfile != null) {
                return new SSH(host, port, username, new File(keyfile).text) 
            } else { 
                return new SSHByPassword(host, port, username, password)
            } 
        }
    }
    
    def execute(Map map) {
        debug "execute: $map"
        lastResult = new CommandBuilder(map).execute(shell.get())
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