package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import groovy.io.FileType
import groovy.time.TimeCategory
import io.infrastructor.cli.validation.FileValidator
import io.infrastructor.cli.validation.ModeValidator

import static io.infrastructor.core.utils.CryptoUtils2.*
import static io.infrastructor.cli.validation.ModeValidator.*
import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.core.logging.status.ProgressStatusLogger.withProgressStatus

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
        options << ["--mode, -m" : "Encryption mode: $FULL or $PART. Full mode to encrypt entire file. Part mode to substitute 'encrypt' placeholders only."]
    }
    
    def usage() {
        ["infrastructor encrypt -f FILE1 -f FILE2 -p SECRET -m $FULL", 
         "infrastructor encrypt -f TEMPLATE1 -f TEMPLATE2 -p SECRET -m $PART"]
    }
    
    def execute() {
        super.execute()
        
        if (!password) { password = input('encryption password: ', true) }
        
        def toEncrypt = []
        def timeStart = new Date()
        
        info "${blue('starting encryption with mode ' + mode)}"
        
        withTextStatus { status ->
            status "> collecting files to encrypt"
            
            files.collect { new File(it) }.each { file -> 
                file.isDirectory() ? file.eachFileRecurse (FileType.FILES) { toEncrypt << it } : toEncrypt << file
            }
        
            info "found ${toEncrypt.size()} file|s to encrypt"
            
            withProgressStatus(toEncrypt.size(), 'file|s processed')  { progressLine ->
                toEncrypt.each { 
                    status "> encrypting: $it.canonicalPath"
                    encrypt(it) 
                    progressLine.increase()
                }
            }
            
            status "> encryption is done"
        }
        
        def duration = TimeCategory.minus(new Date(), timeStart)
        printLine "\n${green('EXECUTION COMPLETE')} in $duration"
    }
    
    def encrypt(def file) {
        def encrypted = (mode == FULL) ? encryptFull(password, file.bytes) : encryptPart(password, file.text)

        def output = new FileOutputStream(file, false)
        output.withCloseable { it << encrypted }
        
        info "${green('encrypted:')} ${file.getCanonicalPath()}"
    }
}
