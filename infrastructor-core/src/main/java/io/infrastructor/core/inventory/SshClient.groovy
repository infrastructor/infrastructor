package io.infrastructor.core.inventory

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Logger
import com.jcraft.jsch.Session
import groovy.transform.ToString

import static io.infrastructor.core.logging.ConsoleLogger.*

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
class SshClient {
    
    private def port = 22
    private def host
    private def username
    private def password
    private def keyfile
    
    private Session session
    
    public static SshClient sshClient(Map params) {
        new SshClient(params)
    }
    
    def isConnected() {
        session != null && session.isConnected()
    }
    
    private def connect() {
        if (!isConnected()) {
            
            JSch jsch = new JSch()
            JSch.setConfig("StrictHostKeyChecking", "no")
            JSch.setLogger(new Logger() {
                    @Override
                    public boolean isEnabled(int level) { return true; }

                    @Override
                    public void log(int level, String message) { trace "jsch: $message" }
                });
    
            if (keyfile) jsch.addIdentity(keyfile)
        
            session = jsch.getSession(username, host, port)
            if (password) session.setPassword(password)
            session.setServerAliveInterval(5000);
            session.setServerAliveCountMax(1000000);
            session.connect()
            return session.isConnected()
        }
    } 
    
    def disconnect() {
        if(isConnected()) { 
            session.disconnect() 
        }
        session = null
    }
    
    def execute(def command) {
        new SshCommand(command).execute()
    }
    
    private def executeSsh(String command, InputStream input, OutputStream output, OutputStream error) {
        ChannelExec channel = null
        try {
            debug "ssh: $command"
            
            channel = ChannelExec.class.cast(session.openChannel("exec"))
            channel.setCommand(command)
            channel.setInputStream(input)
            channel.setOutputStream(output)
            channel.setErrStream(error)
            channel.connect()
            while (!channel.isClosed()) {
                Thread.sleep(55)
                session.sendKeepAliveMsg()
            }
            return channel.getExitStatus()
        } catch (Exception ex) {
            throw new RuntimeException(ex)
        } finally {
            if (channel != null) {
                channel.disconnect()
            }
        }
    }
        
    class SshCommand {
        def command
        def sudo   = false
        def output = new ByteArrayOutputStream()
        def error  = new ByteArrayOutputStream()
        def input  = new ByteArrayInputStream()
        
        public def execute() {
            try {
                def result = [:]
                result.exitcode = -1 
            
                try { result.exitcode = executeSsh(withSudo(sudo, command), input, output, error) } 
                catch (IOException ex) { 
                    error "IO exception during command execution: $command"
                    error (ex.getMessage())
                } 
            
                result.output = output.toString()
                result.error  = error.toString()
                result.command = command
                result.sudo = sudo
                return result
            } finally {
                input.close()
                output.close()
                error.close()
            }
        }
    
        public def withSudo(def sudo, def command) {
            return sudo ? "sudo $command" : command 
        }
    }
}

