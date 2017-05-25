package io.infrastructor.cli.handlers

import org.testng.annotations.Test

public class DecryptHandlerTest {
    @Test
    public void decryptFull() {
        def file = 'build/resources/test/decryption/file.txt'
        DecryptHandler handler = new DecryptHandler(files: [file], password: 'test')
        handler.execute()
        assert "simple\ntest\n" == new File(file).text
    }
    
    @Test
    public void decryptPart() {
        def file = 'build/resources/test/decryption/template.txt'
        DecryptHandler handler = new DecryptHandler(files: [file], password: 'test', mode: 'PART')
        handler.execute()
        def text = new File(file).text
        assert "message: simple\ntest\n" == text
    }
}

