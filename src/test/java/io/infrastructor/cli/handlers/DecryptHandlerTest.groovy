package io.infrastructor.cli.handlers

import org.testng.annotations.Test

public class DecryptHandlerTest {
    @Test
    public void decryptFile() {
        def file = 'build/resources/test/decryption/file.txt'
        DecryptHandler handler = new DecryptHandler(files: [file], password: 'test')
        handler.execute()
        assert "simple\ntest\n" == new File(file).text
    }
    
    @Test
    public void decryptTemplate() {
        def file = 'build/resources/test/decryption/template.txt'
        DecryptHandler handler = new DecryptHandler(templates: [file], password: 'test')
        handler.execute()
        def text = new File(file).text
        assert "message: simple\ntest\n" == text
    }
}

