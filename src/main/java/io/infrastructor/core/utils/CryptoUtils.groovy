package io.infrastructor.core.utils

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Arrays
import java.util.Base64

class CryptoUtils {
    
    static final String ALGORITHM = "AES"
    static final def ENCODING = StandardCharsets.UTF_8


    public static String encryptFullBytes(String key, byte [] data, int blockSize = 0) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, prepareKey(key))
            byte [] encrypted = cipher.doFinal(data)
            byte [] encoded   = Base64.getEncoder().encode(encrypted)
            
            return block(new String(encoded, ENCODING), blockSize)
        } catch (Exception ex) {
            throw new CryptoUtilsException("unable to encrypt data", ex)
        }
    }


    public static byte [] decryptFullBytes(String key, String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, prepareKey(key))
            byte [] decoded = Base64.getDecoder().decode(data.replaceAll("[\n\r]", "").getBytes(ENCODING))
            return cipher.doFinal(decoded)
        } catch (Exception ex) {
            throw new CryptoUtilsException("unable to decrypt, did you provide encrypted data?", ex)
        }
    }
    
    
    public static String encryptFull(String key, String data, int blockSize = 0) {
        return encryptFullBytes(key, data.getBytes(ENCODING), blockSize)
    }
    
    
    public static String decryptFull(String key, String data) {
        new String(decryptFullBytes(key, data), ENCODING)
    }
    
    
    public static String encryptPart(String key, String template, int blockSize = 0) {
        def bindings = [:]
        bindings.encrypt = { 
            field -> "\${decrypt('${encryptFull(key, field, blockSize)}')}"
        }

        def engine = new groovy.text.SimpleTemplateEngine()
        engine.createTemplate(template).make(bindings).toString()
    }
    
    
    public static String decryptPart(String key, String template, def bindings = [:]) {
        bindings.decrypt = {
            field -> "${decryptFull(key, field)}"
        }
        
        def engine = new groovy.text.SimpleTemplateEngine()
        engine.createTemplate(template).make(bindings).toString()
    }
    
    
    private static SecretKeySpec prepareKey(String key) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        byte[] keyBytes = digest.digest(key.getBytes(ENCODING))
        new SecretKeySpec(Arrays.copyOf(keyBytes, 16), ALGORITHM)
    }
    
    
    private static def block(def data, size) {
        doBlock([], data, size).join('\n')
    }

    
    private static def doBlock(def collection, tail, size) {
        if (size == 0) {
            collection << tail
            return collection
        }
        
        def length = tail.length()
        if (length > size) {
            collection << tail.take(size)
            doBlock(collection, tail.drop(size), size)
        } else if (length != 0) {
            collection << tail
        }
        
        return collection
    }
}
