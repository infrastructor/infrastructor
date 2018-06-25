package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import groovy.io.FileType
import groovy.time.TimeCategory
import io.infrastructor.cli.validation.FileValidator
import io.infrastructor.cli.validation.ModeValidator
import io.infrastructor.core.utils.CryptoUtils

import static io.infrastructor.core.utils.CryptoUtils.*
import static io.infrastructor.cli.validation.ModeValidator.*
import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.core.logging.status.ProgressStatusLogger.withProgressStatus
import static io.infrastructor.core.utils.CryptoUtils.ALGORITHM
import static io.infrastructor.core.utils.CryptoUtils.OUTPUT_ENCODING
import static io.infrastructor.core.utils.CryptoUtils.TOOL

class EncryptHandler extends LoggingAwareHandler {
    
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
            status "[ENCRYPT] collecting files to encrypt"
            
            files.collect { new File(it) }.each { file -> 
                file.isDirectory() ? file.eachFileRecurse (FileType.FILES) { toEncrypt << it } : toEncrypt << file
            }
        
            info "found ${toEncrypt.size()} file|s"
            
            withProgressStatus(toEncrypt.size(), 'file|s processed')  { progressLine ->
                toEncrypt.each { 
                    status "[ENCRYPT] encrypting: $it.canonicalPath"
                    encrypt(it) 
                    progressLine.increase()
                }
            }
            
            status "[ENCRYPT] encryption is done"
        }
        
        def duration = TimeCategory.minus(new Date(), timeStart)
        printLine "\n${green('EXECUTION COMPLETE')} in $duration"
    }
    
    def encrypt(File file) {

        if (mode == FULL) {
            // check if the file has already been encrypted
            def (
                String tool,
                String algorithm,
                String encoding,
                String keyHash
            ) = parse(file.text)

            if (tool == TOOL && algorithm == ALGORITHM && encoding == OUTPUT_ENCODING) {
                // file has already been encrypted
                if (encryptionKeyHash(password) == keyHash) {
                    info "${green('already encrypted:')} '${file.getCanonicalPath()}'"
                } else {
                    info "${yellow('already encrypted with a different key:')} '${file.getCanonicalPath()}'"
                }
                return
            }
        }

        // encrypt an unencrypted file
        String encrypted = (mode == FULL) ? encryptFull(password, file.bytes)[0] : encryptPart(password, file.text)

        def output = new FileOutputStream(file, false)
        output.withCloseable { it << encrypted }

        info "${green('encrypted:')} '${file.getCanonicalPath()}'"
    }

}
