package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import io.infrastructor.cli.ConsoleLogger
import io.infrastructor.cli.validation.FileValidator
import io.infrastructor.cli.validation.ModeValidator
import io.infrastructor.core.utils.CryptoUtils


public class EncryptHandler {
    
    @Parameter(names = ["-p", "--password"], required = true)
    String password

    @Parameter(names = ["-f", "--file"], validateWith = FileValidator)
    def files = []
    
    @Parameter(names = ["-m", "--mode"], validateWith = ModeValidator)
    String mode = 'FULL'

    
    def usage() {
        ["infrastructor encrypt -f FILE1 -f FILE2 -p SECRET -m FULL", 
         "infrastructor encrypt -f TEMPLATE1 -f TEMPLATE2 -p SECRET -m PART"]
    }
    
    
    def options() {
        ["--file, -f" : "A file to encrypt. This file will be replaced with an encrypted one.",
         "--mode, -m" : "Encryption mode: FULL or PART. Full mode to encrypt entire file. Part mode to substitute 'encrypt' placeholders only.",
         "--password, -p" : "An encryption key."]
    }
    
    
    def description() {
        "Encrypt specified files (AES + Base64)."
    }
    
    
    def execute() {
        encryptFiles(files.collect { new File(it) }) 
    }
    
    
    def encryptFiles(def files) {
        files?.each { file ->
            file.isDirectory() ? encryptFiles(file.listFiles()) : encryptFile(file)
        }
    }
    
    
    def encryptFile(def file) {
        def encrypted = (mode == 'FULL') ?
        CryptoUtils.encryptFullBytes(password, file.getBytes(), 80) :
        CryptoUtils.encryptPart(password, file.getText(), 80)
        def output = new FileOutputStream(file, false)
        output.withCloseable { out ->
            out << encrypted
        }
    }
}
