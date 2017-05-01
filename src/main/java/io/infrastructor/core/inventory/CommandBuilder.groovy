package io.infrastructor.core.inventory

import com.jcabi.ssh.SSH
import com.jcabi.ssh.SSHByPassword
import com.jcabi.ssh.Shell

import static io.infrastructor.cli.ConsoleLogger.error
import static io.infrastructor.cli.ConsoleLogger.debug


public class CommandBuilder {

    def command
    def sudo   = false
    def output = new ByteArrayOutputStream()
    def error  = new ByteArrayOutputStream()
    def input  = new ByteArrayInputStream()
      
    public def execute(Shell shell) {
        try {
            def result = [:]
            result.exitcode = -1 
            
            try { result.exitcode = shell.exec(withSudo(sudo, command), input, output, error) } 
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
    
    public static String withSudo(def sudo, def command) {
        return sudo ? "sudo $command" : command 
    }
}

