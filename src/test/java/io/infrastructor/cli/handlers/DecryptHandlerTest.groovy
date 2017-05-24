package io.infrastructor.cli.handlers

import org.testng.annotations.Test

public class DecryptHandlerTest {
    @Test
    public void decryptFile() {
        def file = 'build/resources/test/decryption.txt'
        DecryptHandler handler = new DecryptHandler(files: [file], password: 'test')
        handler.execute()
        assert "simple\ntest\n" == new File(file).text
    }
}

