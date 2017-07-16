package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import io.infrastructor.cli.validation.FileValidator
import io.infrastructor.cli.validation.ModeValidator
import io.infrastructor.core.utils.CryptoUtils

import static io.infrastructor.core.logging.ConsoleLogger.*

public class EncryptHandler extends LoggingAwareHandler {
    
    private String password

    @Parameter(names = ["-f", "--file"], required = true, validateWith = FileValidator)
    List<String> files
    
    @Parameter(names = ["-m", "--mode"], validateWith = ModeValidator)
    String mode = 'FULL'
    
    def description() {
        "Encrypt specified files (AES + Base64)."
    }

    def options() {
        def options = super.options() 
        options << ["--file, -f" : "A file to encrypt. This file will be replaced with an encrypted one."]
        options << ["--mode, -m" : "Encryption mode: FULL or PART. Full mode to encrypt entire file. Part mode to substitute 'encrypt' placeholders only."]
    }
    
    def usage() {
        ["infrastructor encrypt -f FILE1 -f FILE2 -p SECRET -m FULL", 
         "infrastructor encrypt -f TEMPLATE1 -f TEMPLATE2 -p SECRET -m PART"]
    }
    
    def execute() {
        super.execute()
        
        if (!password) { password = input('Encryption password: ', true) }
        
        info "${blue('starting encryption with mode ' + mode)}"
        encryptFiles( files?.collect { new File(it) } )  
    }
    
    def encryptFiles(def files) {
        files?.each { it.isDirectory() ? encryptFiles(it.listFiles()) : encryptFile(it) }
    }
    
    def encryptFile(def file) {
        def encrypted = (mode == 'FULL') ?
            CryptoUtils.encryptFullBytes(password, file.getBytes(), 80) :
            CryptoUtils.encryptPart(password, file.getText(), 80)
        def output = new FileOutputStream(file, false)
        output.withCloseable { out ->
            out << encrypted
        }
        
        info "${green('encrypted')}: ${file.getCanonicalPath()}"
    }
}
