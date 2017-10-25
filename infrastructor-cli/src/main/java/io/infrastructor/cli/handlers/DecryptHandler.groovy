package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import groovy.io.FileType
import groovy.time.TimeCategory
import io.infrastructor.cli.validation.FileValidator
import io.infrastructor.cli.validation.ModeValidator
import io.infrastructor.core.utils.CryptoUtils
import io.infrastructor.core.utils.CryptoUtilsException

import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.core.logging.status.ProgressStatusLogger.withProgressStatus

public class DecryptHandler extends LoggingAwareHandler {
    
    private String password
    
    @Parameter(names = ["-f", "--file"], validateWith = FileValidator)
    List<String> files = []
    
    @Parameter(names = ["-m", "--mode"], validateWith = ModeValidator)
    String mode = 'FULL'

    def description() {
        "Decrypt specified files (AES + Base64)."
    }
    
    def options() {
        def options = super.options() 
        options << ["--file, -f" : "A file to decrypt. This file will be replaced with a decrypted one."]
        options << ["--mode, -m" : "Decryption mode: FULL or PART. Full mode to decrypt entire file. Part mode to substitute 'decrypt' placeholders only."]
    }

    def usage() {
        ["infrastructor decrypt -f FILE1 -f FILE2 -p SECRET -m FULL", 
         "infrastructor decrypt -f TEMPLATE1 -f TEMPLATE2 -p SECRET -m PART"]
    }
    
    def execute() {
        super.execute()
        
        if (!password) { password = input('Decryption password: ', true) }
        
        def timeStart = new Date()
        
        info "${blue('starting decryption with mode ' + mode)}"
        
        def toDecrypt = []
        
        withTextStatus { status ->
            status "> collecting files to decrypt"
            
            files.collect { new File(it) }.each { file -> 
                file.isDirectory() ? file.eachFileRecurse (FileType.FILES) { toDecrypt << it } : toDecrypt << file
            }
        
            info "found ${toDecrypt.size()} file|s to decrypt"
            
            withProgressStatus(toDecrypt.size(), 'file|s processed')  { progressLine ->
                toDecrypt.each { 
                    status "> decrypting: $it.canonicalPath"
                    decrypt(it) 
                    progressLine.increase()
                }
            }
            
            status "> encryption is done"
        }
        
        
        def duration = TimeCategory.minus(new Date(), timeStart)
        printLine "\n${green('EXECUTION COMPLETE')} in $duration"
    }
    
    def decrypt(def file) {
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

