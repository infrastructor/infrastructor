package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import io.infrastructor.cli.ConsoleLogger
import io.infrastructor.cli.validation.FileValidator
import io.infrastructor.core.utils.CryptoUtils


public class EncryptHandler {
    
    @Parameter(names = ["-p", "--password"], required = true)
    String password

    @Parameter(names = ["-f", "--file"], required = true, validateWith = FileValidator)
    def file

    def usage() {
        ["infrastructor encrypt -f FILE -p SECRET", 
         "infrastructor encrypt --file FILE --password SECRET"]
    }
    
    def options() {
        ["--file, -f" : "File to encrypt. This file will be replaced with an encrypted one.",
         "--password, -p" : "Secret encryption password."]
    }
    
    def description() {
        "Encrypt specified file (AES algorithm + Base64 encoding)."
    }
    
    def execute() {
        file.each {
            def dataToEncrypt = new File(it).text
            def encrypted = CryptoUtils.encrypt(password, dataToEncrypt, 80)
            def output = new FileOutputStream(it, false)
            output.withCloseable { out ->
                out << encrypted
            }
        }
    }
}

