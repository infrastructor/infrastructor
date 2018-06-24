package io.infrastructor.core.utils

import org.junit.Test

import static io.infrastructor.core.utils.CryptoUtils2.ENCODING

class CryptoUtils2Test {

    final String DATA = "secret message"
    final String KEY  = "secret"

    @Test
    void fullEncryptionAndDecryption() {
        def encrypted = CryptoUtils2.encryptFull(KEY, DATA.getBytes(ENCODING))
        println encrypted

        assert encrypted.startsWith('INFRASTRUCTOR:AES/GCM/PKCS5Padding:BASE64:OIEhnQh92cY0Nz/TPfozostr/GxSC2S4u2DvLOtTSuc=:')

        byte [] decrypted = CryptoUtils2.decryptFull(KEY, encrypted)
        assert DATA == new String(decrypted, ENCODING)
    }

    @Test
    void decryptPart() {
        def template = '''
            message: "${decrypt('Xi4Hwkd4apQSlRnAX/iJpNMRofIxay7rmhGWiQmc', '8WJJgQSAyHaFD3Ot')}"
        '''
        assert CryptoUtils2.decryptPart(KEY, template).contains(DATA)
    }

    @Test
    void encryptPart() {
        def template = CryptoUtils2.encryptPart(KEY, """
            message: "\${encrypt('$DATA')}"
        """)
        assert CryptoUtils2.decryptPart(KEY, template).contains(DATA)
    }
}
