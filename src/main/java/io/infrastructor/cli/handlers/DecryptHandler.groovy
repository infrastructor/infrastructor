package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import io.infrastructor.cli.ConsoleLogger
import io.infrastructor.cli.validation.FileValidator
import io.infrastructor.core.utils.CryptoUtils


public class DecryptHandler {
    
    @Parameter(names = ["-p", "--password"], required = true)
    String password

    @Parameter(names = ["-f", "--file"], validateWith = FileValidator)
    def files
    
    @Parameter(names = ["-t", "--templates"], validateWith = FileValidator)
    def templates

    def usage() {
        ["infrastructor decrypt -f FILE -p SECRET", 
         "infrastructor decrypt -t TEMPLATE -p SECRET", 
         "infrastructor decrypt -f FILE1 -f FILE2 -t TEMPLATE1 -t TEMPLATE2 --password SECRET",
         "infrastructor decrypt --file FILE --template TEMPLATE --password SECRET"]
    }
    
    def options() {
        ["--file, -f" : "File to decrypt. This file will be replaced with a decrypted one.",
         "--template, -t" : "Template to decrypt. Replace all marked fields with decrypted values.",
         "--password, -p" : "Secret decryption password."]
    }
    
    def description() {
        "Decrypt a specified file or template (AES algorithm + Base64 encoding)."
    }
    
    def execute() {
        decryptFiles()
        decryptTemplates() 
    }
    
    def decryptFiles() {
        files?.each {
            def dataToDecrypt = new File(it).text
            def decrypted = CryptoUtils.decrypt(password, dataToDecrypt)
            def output = new FileOutputStream(it, false)
            output.withCloseable { out ->
                out << decrypted
            }
        }
    }
    
    def decryptTemplates() {
        templates?.each {
            def dataToDecrypt = new File(it).text
            def decrypted = CryptoUtils.decryptTemplate(password, dataToDecrypt)
            def output = new FileOutputStream(it, false)
            output.withCloseable { out ->
                out << decrypted
            }
        }
    }
}

