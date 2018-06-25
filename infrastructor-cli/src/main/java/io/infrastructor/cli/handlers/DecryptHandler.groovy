package io.infrastructor.cli.handlers

import com.beust.jcommander.Parameter
import groovy.io.FileType
import groovy.time.TimeCategory
import io.infrastructor.cli.validation.FileValidator
import io.infrastructor.cli.validation.ModeValidator
import io.infrastructor.core.utils.CryptoUtils
import io.infrastructor.core.utils.CryptoUtilsException

import static io.infrastructor.core.utils.CryptoUtils.*
import static io.infrastructor.cli.validation.ModeValidator.*
import static io.infrastructor.core.logging.ConsoleLogger.*
import static io.infrastructor.core.logging.status.TextStatusLogger.withTextStatus
import static io.infrastructor.core.logging.status.ProgressStatusLogger.withProgressStatus

class DecryptHandler extends LoggingAwareHandler {
    
    private String password
    
    @Parameter(names = ["-f", "--file"], validateWith = FileValidator)
    List<String> files = []
    
    @Parameter(names = ["-m", "--mode"], validateWith = ModeValidator)
    String mode = FULL

    def description() {
        "Decrypt specified files (AES + Base64)."
    }
    
    def options() {
        def options = super.options() 
        options << ["--file, -f" : "A file to decrypt. This file will be replaced with a decrypted one."]
        options << ["--mode, -m" : "Decryption mode: $FULL or $FULL. Full mode to decrypt entire file. Part mode to substitute 'decrypt' placeholders only."]
    }

    def usage() {
        ["infrastructor decrypt -f FILE1 -f FILE2 -p SECRET -m $FULL", 
         "infrastructor decrypt -f TEMPLATE1 -f TEMPLATE2 -p SECRET -m $PART"]
    }
    
    def execute() {
        super.execute()
        
        if (!password) { password = input('Decryption password: ', true) }
        
        def toDecrypt = []
        def hasError = false
        def timeStart = new Date()
        
        info "${blue('starting decryption with mode ' + mode)}"
        
        withTextStatus { status ->
            status "[DECRYPT] collecting files to decrypt"
            
            files.collect { new File(it) }.each { file -> 
                file.isDirectory() ? file.eachFileRecurse (FileType.FILES) { toDecrypt << it } : toDecrypt << file
            }
        
            info "found ${toDecrypt.size()} file|s"
            
            withProgressStatus(toDecrypt.size(), 'file|s processed')  { progressLine ->
                toDecrypt.each { 
                    try {
                        status "[DECRYPT] decrypting: $it.canonicalPath"
                        decrypt(it) 
                    } catch (CryptoUtilsException ex) {
                        error "decryption failed: ${it.canonicalPath}"
                        hasError = true
                    }
                    progressLine.increase()
                }
            }
            
            status "[DECRYPT] encryption is done"
        }
        
        def duration = TimeCategory.minus(new Date(), timeStart)
        
        if (hasError) {
            printLine "\n${bold(red('EXECUTION FAILED'))} in $duration"
            printLine "${red('Please check the log output. Use \'-l 3\' command line argument to activate debug logs.')}"
        } else {
            printLine "\n${green('EXECUTION COMPLETE')} in $duration"
        }
    }
    
    def decrypt(File file) {
        def decrypted

        if (mode == FULL) {
            def (
            String tool,
            String algorithm,
            String encoding,
            String keyHash
            ) = parse(file.text)

            if (tool == CryptoUtils.TOOL && algorithm == CryptoUtils.ALGORITHM && encoding == CryptoUtils.OUTPUT_ENCODING) {
                // file was encrypted, checking the key hash
                if (encryptionKeyHash(password) == keyHash) {
                    decrypted = decryptFull(password, file.text)
                } else {
                    info "${yellow('encrypted with a different key:')} '${file.getCanonicalPath()}'"
                    return
                }
            } else {
                // file was not encrypted, skipping
                info "${yellow('uncrypted file:')} '${file.getCanonicalPath()}'"
                return
            }
        } else {
            decrypted = decryptPart(password, file.text)
        }

        def output = new FileOutputStream(file, false)
        output.withCloseable { it << decrypted }
        info "${green('decrypted:')} '${file.getCanonicalPath()}'"
    }
}

