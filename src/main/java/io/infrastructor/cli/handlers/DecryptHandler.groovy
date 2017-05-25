package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import io.infrastructor.cli.ConsoleLogger
import io.infrastructor.cli.validation.FileValidator
import io.infrastructor.cli.validation.ModeValidator
import io.infrastructor.core.utils.CryptoUtils


public class DecryptHandler {
    
    @Parameter(names = ["-p", "--password"], required = true)
    String password

    @Parameter(names = ["-f", "--file"], validateWith = FileValidator)
    def files = []
    
    @Parameter(names = ["-m", "--mode"], validateWith = ModeValidator)
    String mode = 'FULL'

    
    def usage() {
        ["infrastructor decrypt -f FILE1 -f FILE2 -p SECRET -m FULL", 
         "infrastructor decrypt -f TEMPLATE1 -f TEMPLATE2 -p SECRET -m PART"]
    }
    
    
    def options() {
        ["--file, -f" : "A file to decrypt. This file will be replaced with a decrypted one.",
         "--mode, -m" : "Decryption mode: FULL or PART. Full mode to decrypt entire file. Part mode to substitute 'decrypt' placeholders only.",
         "--password, -p" : "A decryption key."]
    }
    
    
    def description() {
        "Decrypt specified files (AES + Base64)."
    }
    
    
    def execute() {
        files?.each {
            def dataToDecrypt = new File(it).text
            def decrypted = (mode == 'FULL') ? 
                CryptoUtils.decryptFull(password, dataToDecrypt) : 
                CryptoUtils.decryptPart(password, dataToDecrypt)
            def output = new FileOutputStream(it, false)
            output.withCloseable { out ->
                out << decrypted
            }
        }
    }
}

