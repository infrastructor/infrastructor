package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import io.infrastructor.cli.validation.FileValidator
import io.infrastructor.cli.validation.ModeValidator
import io.infrastructor.core.utils.CryptoUtils
import io.infrastructor.core.utils.CryptoUtilsException

import static io.infrastructor.cli.ConsoleLogger.*

public class DecryptHandler extends LoggingAwareHandler {
    
    @Parameter(names = ["-p", "--password"], required = true, description = "Decryption password", password = true)
    String password

    @Parameter(names = ["-f", "--file"], validateWith = FileValidator)
    def files = []
    
    @Parameter(names = ["-m", "--mode"], validateWith = ModeValidator)
    String mode = 'FULL'

    def description() {
        "Decrypt specified files (AES + Base64)."
    }
    
    def options() {
        def options = super.options() 
        options << ["--file, -f" : "A file to decrypt. This file will be replaced with a decrypted one."]
        options << ["--mode, -m" : "Decryption mode: FULL or PART. Full mode to decrypt entire file. Part mode to substitute 'decrypt' placeholders only."]
        options << ["--password, -p" : "A decryption key."]
    }

    def usage() {
        ["infrastructor decrypt -f FILE1 -f FILE2 -p SECRET -m FULL", 
         "infrastructor decrypt -f TEMPLATE1 -f TEMPLATE2 -p SECRET -m PART"]
    }
    
    
    def execute() {
        super.execute()
        info "${blue('starting decryption with mode ' + mode)}"
        decryptFiles(files.collect { new File(it) }) 
    }
    
    
    def decryptFiles(def files) {
        files?.each { it.isDirectory() ? decryptFiles(it.listFiles()) : decryptFile(it) }
    }

    
    def decryptFile(def file) {
        
        try {
            def encrypted = (mode == 'FULL') ?
            CryptoUtils.decryptFullBytes(password, file.getText()) :
            CryptoUtils.decryptPart(password, file.getText())
            def output = new FileOutputStream(file, false)
            output.withCloseable { out -> 
                out << encrypted
            }
            
            info "${green('decrypted')}: ${file.getCanonicalPath()}"
            
        } catch(CryptoUtilsException ex) {
            info "${red('decryption failed:')} ${file.getCanonicalPath()}"
        }
    }
}

