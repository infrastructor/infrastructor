package io.infrastructor.cli.handlers

import org.testng.annotations.Test

public class EncryptHandlerTest {
    @Test
    public void encryptFull() {
        def file = 'build/resources/test/encryption/file.txt'
        EncryptHandler handler = new EncryptHandler(files: [file], password: 'test')
        handler.execute()
        assert "d69wE24VdwcW3dlrFrEDvg==" == new File(file).text
    }
    
    @Test
    public void encryptPart() {
        def file = 'build/resources/test/encryption/template.txt'
        EncryptHandler handler = new EncryptHandler(files: [file], password: 'test', mode: 'PART')
        handler.execute()
        assert 'message: ${decrypt(\'d69wE24VdwcW3dlrFrEDvg==\')}' == new File(file).text
    }
}

