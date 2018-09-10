package io.infrastructor.core.utils

import org.junit.Test

import static io.infrastructor.core.utils.CryptoUtils.*

class CryptoUtilsEmptyDataTest {

    final String KEY = "secret"

    @Test
    void fullEncryptionOfEmptyData() {
        byte [] data = []
        assert encryptFull(KEY, data) == ["", ""]
    }

    @Test
    void fullEncryptionOfNullData() {
        byte [] data = null
        assert encryptFull(KEY, data) == ["", ""]
    }

    @Test
    void fullDecryptionOfEmptyData() {
        assert decryptFull(KEY, '') == []
    }

    @Test
    void fullDecryptionOfNullData() {
        assert decryptFull(KEY, null) == []
    }

    @Test
    void encryptPartNullData() {
        assert encryptPart(KEY, "") == ""
        assert encryptPart(KEY, (String) null) == ""
    }

    @Test
    void decryptPartNullData() {
        assert decryptPart(KEY, "") == ""
        assert decryptPart(KEY, (String) null) == ""
    }
}
