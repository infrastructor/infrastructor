package io.infrastructor.cli.handlers

import org.junit.Test

class DecryptHandlerTest {
    
    @Test
    void decryptFull() {
        def file = 'build/resources/test/decryption/file.txt'
        DecryptHandler handler = new DecryptHandler(files: [file], password: 'test')
        handler.execute()
        assert "simple\ntest\n" == new File(file).text
    }
    
    @Test
    void decryptPart() {
        def file = 'build/resources/test/decryption/template.txt'
        DecryptHandler handler = new DecryptHandler(files: [file], password: 'secret', mode: 'PART')
        handler.execute()
        def text = new File(file).text
        assert "message: secret message" == text
    }
}

