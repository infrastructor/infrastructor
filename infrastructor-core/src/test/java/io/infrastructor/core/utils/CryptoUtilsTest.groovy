package io.infrastructor.core.utils

import org.junit.Test
import static io.infrastructor.core.utils.CryptoUtils.*

class CryptoUtilsTest {

    final String DATA = "secret message"
    final byte[] DATA_BYTES = DATA.getBytes(ENCODING)
    final String KEY = "secret"
    final byte[] KEY_BYTES = KEY.getBytes(ENCODING)

    @Test
    void fullEncryptionAndDecryption() {
        def (String encrypted, String iv) = encryptFull(KEY, DATA_BYTES)
        assert encrypted.startsWith('INFRASTRUCTOR:AES/GCM/PKCS5Padding:BASE64:OIEhnQh92cY0Nz/TPfozostr/GxSC2S4u2DvLOtTSuc=:')

        byte [] decrypted = decryptFull(KEY, encrypted)
        assert decrypted == DATA_BYTES
    }

    @Test
    void encryptDecryptBytes() {

        byte[] iv = generateIV()
        byte[] encrypted = encrypt(KEY_BYTES, iv, DATA_BYTES)
        byte[] decrypted = decrypt(KEY_BYTES, iv, encrypted)

        assert decrypted == DATA_BYTES
    }


    @Test
    void encryptDecryptText() {
        def (String encrypted, String iv) = encryptText(KEY, DATA_BYTES)
        def decrypted = decryptText(KEY, iv, encrypted)

        assert decrypted == DATA
    }


    @Test
    void encryptDecryptFull() {
        def (String encrypted, String iv) = encryptFull(KEY, DATA_BYTES)
        def decrypted = decryptFull(KEY, encrypted)

        assert decrypted == DATA_BYTES
    }

    @Test
    void decryptPart() {
        def template = '''
            message: "${decrypt('Xi4Hwkd4apQSlRnAX/iJpNMRofIxay7rmhGWiQmc', '8WJJgQSAyHaFD3Ot')}"
        '''

        assert decryptPart(KEY, template).contains(DATA)
    }

    @Test
    void encryptPart() {
        def template = encryptPart(KEY, """
            message: "\${encrypt('$DATA')}"
        """)

        assert decryptPart(KEY, template).contains(DATA)
    }
}
