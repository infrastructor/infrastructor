package io.infrastructor.core.utils

import groovy.text.SimpleTemplateEngine

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Arrays
import java.util.Base64

class CryptoUtils {
    
    def static final ALGORITHM = "AES"
    def static final ENCODING  = StandardCharsets.UTF_8
    def static final BLOCK_SIZE = 80
    
    private static SecretKeySpec prepareKey(String key) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        byte[] keyBytes = digest.digest(key.getBytes(ENCODING))
        new SecretKeySpec(Arrays.copyOf(keyBytes, 16), ALGORITHM)
    }
    
    public static String encryptFull(String key, byte [] data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, prepareKey(key))
            byte [] encrypted = cipher.doFinal(data)
            byte [] encoded   = Base64.getEncoder().encode(encrypted)
            return new String(encoded, ENCODING).split("(?<=\\G.{$BLOCK_SIZE})").join('\n')
        } catch (Exception ex) {
            throw new CryptoUtilsException("unable to encrypt data", ex)
        }
    }

    public static byte [] decryptFull(String key, String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, prepareKey(key))
            byte [] decoded = Base64.getDecoder().decode(data.replaceAll("[\n\r]", "").getBytes(ENCODING))
            return cipher.doFinal(decoded)
        } catch (Exception ex) {
            throw new CryptoUtilsException("unable to decrypt, did you provide encrypted data?", ex)
        }
    }
    
    public static String encryptPart(String key, String template, def bindings = [:]) {
        bindings.encrypt = { "\${decrypt('${encryptFull(key, it.getBytes())}')}" }
        new SimpleTemplateEngine().createTemplate(template).make(bindings).toString()
    }
    
    public static String decryptPart(String key, String template, def bindings = [:]) {
        bindings.decrypt = { "${new String(decryptFull(key, it))}" }
        new SimpleTemplateEngine().createTemplate(template).make(bindings).toString()
    }
}
