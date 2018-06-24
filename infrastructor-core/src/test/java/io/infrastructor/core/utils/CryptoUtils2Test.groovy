package io.infrastructor.core.utils

import org.junit.Test

import static io.infrastructor.core.utils.CryptoUtils2.ENCODING

class CryptoUtils2Test {

    final String DATA = "this is a simple test"
    final String KEY  = "mykey"

    @Test
    void fullEncryptionAndDecryption() {
        def encrypted = CryptoUtils2.encryptFull(KEY, DATA.getBytes(ENCODING))
        assert encrypted.startsWith('INFRASTRUCTOR:AES/GCM/PKCS5Padding:BASE64:U0rHm3V3ZSsbJgFjbg3r0Q8FjKjeSUMDTbUm5kQ7Uig=:')

        byte [] decrypted = CryptoUtils2.decryptFull(KEY, encrypted)
        assert DATA == new String(decrypted, ENCODING)
    }
}
