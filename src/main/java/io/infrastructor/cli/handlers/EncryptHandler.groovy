package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import io.infrastructor.cli.ConsoleLogger
import io.infrastructor.cli.validation.FileValidator
import io.infrastructor.core.utils.CryptoUtils


public class EncryptHandler {
    
    @Parameter(names = ["-p", "--password"], required = true)
    String password

    @Parameter(names = ["-f", "--file"], validateWith = FileValidator)
    def files = []
    
    @Parameter(names = ["-t", "--template"], validateWith = FileValidator)
    def templates = []

    def usage() {
        ["infrastructor encrypt -f FILE -p SECRET", 
         "infrastructor encrypt -t TEMPLATE -p SECRET", 
         "infrastructor encrypt -f FILE1 -f FILE2 -t TEMPLATE1 -t TEMPLATE2 --password SECRET",
         "infrastructor encrypt --file FILE --template TEMPLATE --password SECRET"]
    }
    
    def options() {
        ["--file, -f" : "File to encrypt. This file will be replaced with an encrypted one.",
         "--template, -t" : "Template to encrypt. Replace all marked fields with decryption placeholders.",
         "--password, -p" : "Secret encryption password."]
    }
    
    def description() {
        "Encrypt a specified file or template (AES algorithm + Base64 encoding)."
    }
    
    def execute() {
        encryptFiles()
        encryptTemplates()
    }
    
    
    def encryptFiles() {
        files?.each {
            def dataToEncrypt = new File(it).text
            def encrypted = CryptoUtils.encrypt(password, dataToEncrypt, 80)
            def output = new FileOutputStream(it, false)
            output.withCloseable { out ->
                out << encrypted
            }
        }
    }
    
    def encryptTemplates() {
        templates?.each {
            def dataToEncrypt = new File(it).text
            def encrypted = CryptoUtils.encryptTemplate(password, dataToEncrypt, 80)
            def output = new FileOutputStream(it, false)
            output.withCloseable { out ->
                out << encrypted
            }
        }
    }
}
