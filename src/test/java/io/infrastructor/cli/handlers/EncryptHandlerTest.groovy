package io.infrastructor.cli.handlers

import org.testng.annotations.Test

public class EncryptHandlerTest {
    @Test
    public void encryptFile() {
        def file = 'build/resources/test/encryption.txt'
        EncryptHandler handler = new EncryptHandler(files: [file], password: 'test')
        handler.execute()
        assert "d69wE24VdwcW3dlrFrEDvg==" == new File(file).text
    }
}

