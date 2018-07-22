package io.infrastructor.core.utils

import groovy.text.SimpleTemplateEngine

import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.regex.Matcher
import java.util.regex.Pattern

import static java.nio.charset.StandardCharsets.UTF_8

class CryptoUtils {

    def static final TOOL = "INFRASTRUCTOR"
    def static final ALGORITHM = "AES/GCM/PKCS5Padding"
    def static final ENCODING = UTF_8
    def static final OUTPUT_ENCODING = "BASE64"
    def static final OUTPUT_BLOCK_SIZE = 80

    private static SecretKeySpec prepareKey(byte[] key) {
        return new SecretKeySpec(Arrays.copyOf(key, 16), "AES")
    }

    private static GCMParameterSpec prepareParams(byte[] iv) {
        return new GCMParameterSpec(128, iv)
    }

    static byte[] generateIV() {
        SecureRandom secureRandom = new SecureRandom()
        byte[] iv = new byte[12]
        secureRandom.nextBytes(iv)
        return iv
    }

    static byte[] encrypt(byte[] encryption_key, byte[] iv, byte[] data) {
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, prepareKey(encryption_key), prepareParams(iv))
            return cipher.doFinal(data)
        } catch (Exception ex) {
            throw new CryptoUtilsException("unable to encrypt data", ex)
        }
    }

    static byte[] decrypt(byte[] encryption_key, byte[] iv, byte[] data) {
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, prepareKey(encryption_key), prepareParams(iv))
            return cipher.doFinal(data)
        } catch (Exception ex) {
            throw new CryptoUtilsException("unable to decrypt data", ex)
        }
    }

    static List<String> encryptText(String encryption_key, byte[] data) {
        byte[] iv = generateIV()
        byte[] key = sha256(encryption_key)
        return [toBase64(encrypt(key, iv, data)), toBase64(iv)]
    }

    static String decryptText(String encryption_key, String iv, String data) {
        return new String(decrypt(sha256(encryption_key), fromBase64(iv), fromBase64(data)), ENCODING)
    }

    static List<String> encryptFull(String encryption_key, byte[] data) {
        byte[] iv  = generateIV()
        byte[] key = sha256(encryption_key)

        String encrypted  = toBase64(encrypt(key, iv, data))
        String normalized = encrypted.split("(?<=\\G.{$OUTPUT_BLOCK_SIZE})").join('\n')
        String result     = header(key, iv) + "\n" + normalized

        return [result, toBase64(iv)]
    }

    static byte[] decryptFull(String encryption_key, String data) {
        def (
        String tool,
        String algorithm,
        String encoding,
        String keyHash,
        String ivBase64,
        String body
        ) = parse(data)

        assert TOOL == tool
        assert ALGORITHM == algorithm
        assert OUTPUT_ENCODING == encoding
        assert toBase64(sha256(sha256(encryption_key))) == keyHash

        return decrypt(sha256(encryption_key), fromBase64(ivBase64), fromBase64(body.replaceAll("[\n\r]", "")))
    }

    static String decryptPart(String key, String template, def bindings = [:]) {
        bindings.decrypt = { data, iv -> "${decryptText(key, iv, data)}" }
        new SimpleTemplateEngine().createTemplate(template).make(bindings).toString()
    }

    static String encryptPart(String key, String template, def bindings = [:]) {
        bindings.encrypt = { data ->
            def (encrypted, ivBase64) = encryptText(key, toBytes(data))
            "\${decrypt('$encrypted', '$ivBase64')}"
        }

        new SimpleTemplateEngine().createTemplate(template).make(bindings).toString()
    }

    static byte[] toBytes(String data) {
        return data.getBytes(ENCODING)
    }

    static String toBase64(byte[] data) {
        return new String((Base64.getEncoder().encode(data)), ENCODING)
    }

    static byte[] fromBase64(String data) {
        return Base64.getDecoder().decode(data)
    }

    static byte[] sha256(byte[] data) {
        return MessageDigest.getInstance("SHA-256").digest(data)
    }

    static byte[] sha256(String data) {
        return sha256(toBytes(data))
    }

    static StringBuilder header(byte[] key, byte[] iv) {
        def header = new StringBuilder()
        header << TOOL << ":"
        header << ALGORITHM << ":"
        header << OUTPUT_ENCODING << ":"
        header << toBase64(sha256(key)) << ":"
        header << toBase64(iv)
        return header
    }

    static List parse(String data) {
        Pattern pattern = Pattern.compile("\n *")
        Matcher matcher = pattern.matcher(data)
        if (matcher.find()) {
            def header = data.substring(0, matcher.start()).split(':')
            def body = data.substring(matcher.end())
            return [*header, body]
        }
    }

    static encryptionKeyHash(String key) {
        toBase64(sha256(sha256(toBytes(key))))
    }
}
