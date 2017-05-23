package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import io.infrastructor.cli.ConsoleLogger
import io.infrastructor.cli.validation.FileValidator
import io.infrastructor.core.utils.CryptoUtils


public class DecryptHandler {
    
    @Parameter(names = ["-p", "--password"], required = true)
    String password

    @Parameter(names = ["-f", "--file"], required = true, validateWith = FileValidator)
    def file

    def usage() {
        ["infrastructor decrypt -f FILE -p SECRET", 
         "infrastructor decrypt --file FILE --password SECRET"]
    }
    
    def options() {
        ["--file, -f" : "File to decrypt. This file will be replaced with a decrypted one.",
         "--password, -p" : "Secret decryption password."]
    }
    
    def description() {
        "Decrypt specified file (AES algorithm + Base64 encoding)."
    }
    
    def execute() {
        file.each {
            def dataToDecrypt = new File(it).text
            def decrypted = CryptoUtils.decrypt(password, dataToDecrypt)
            def output = new FileOutputStream(it, false)
            output.withCloseable { out ->
                out << decrypted
            }
        }
    }
}

