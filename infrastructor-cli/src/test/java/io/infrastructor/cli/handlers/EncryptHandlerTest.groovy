package io.infrastructor.cli.handlers

import org.junit.Test

class EncryptHandlerTest {
    
    @Test
    void encryptFull() {
        def file = 'build/resources/test/encryption/file.txt'
        EncryptHandler handler = new EncryptHandler(files: [file], password: 'test')
        handler.execute()
        assert new File(file).text.contains('INFRASTRUCTOR:AES/GCM/PKCS5Padding:BASE64:lU1aSf1w2bi82zXSUiZ4KZV/fvf6bHT4hBm9xegiCfQ=:')
    }
    
    @Test
    void encryptPart() {
        def file = 'build/resources/test/encryption/template.txt'
        EncryptHandler handler = new EncryptHandler(files: [file], password: 'test', mode: 'PART')
        handler.execute()
        assert new File(file).text.contains('message: ${decrypt(')
    }
}

