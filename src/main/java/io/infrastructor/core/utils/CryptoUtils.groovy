package io.infrastructor.core.utils

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Arrays
import java.util.Base64


public class CryptoUtils {
    
    private static final String ALGORITHM = "AES"
    private static final def ENCODING = StandardCharsets.UTF_8


    public static String encrypt(String key, String data, int blockSize = 0) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, prepareKey(key))
            byte [] encrypted = cipher.doFinal(data.getBytes(ENCODING))
            byte [] encoded   = Base64.getEncoder().encode(encrypted)
            
            if (blockSize == 0) {
                return new String(encoded, ENCODING)
            } else {
                return block(new String(encoded, ENCODING), blockSize)
            }
        } catch (Exception ex) {
            println ex
            throw new CryptoUtilsException("unable to encrypt data", ex)
        }
    }

    
    public static String decrypt(String key, String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, prepareKey(key))
            byte [] decoded   = Base64.getDecoder().decode(data.replaceAll("[\n\r]", "").getBytes(ENCODING))
            byte [] decrypted = cipher.doFinal(decoded)
            
            return new String(decrypted, ENCODING)
        } catch (Exception ex) {
            throw new CryptoUtilsException("unable to decrypt, did you provide encrypted data?", ex)
        }
    }

    
    private static SecretKeySpec prepareKey(String key) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        byte[] keyBytes = digest.digest(key.getBytes(ENCODING))
        new SecretKeySpec(Arrays.copyOf(keyBytes, 16), ALGORITHM)
    }
    
    
    private static def block(def data, size) {
        def result = ""
        doBlock([], data, size).each {
            result = (result + it + '\n')
        }
        result
    }

    
    private static def doBlock(def collection, tail, size) {
        def length = tail.length()
        if (length > size) {
            collection << tail.take(size)
            doBlock(collection, tail.drop(size), size)
        } else if (length != 0) {
            collection << tail
        }
        collection
    }
}
