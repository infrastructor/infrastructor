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

    private static SecretKeySpec prepareKey(String key) {
        return new SecretKeySpec(Arrays.copyOf(sha256(toBytes(key)), 32), "AES")
    }

    private static GCMParameterSpec prepareParameterSpec() {
        SecureRandom secureRandom = new SecureRandom()
        byte[] iv = new byte[12]
        secureRandom.nextBytes(iv)
        return new GCMParameterSpec(128, iv)
    }

    static String encryptFull(String key, byte[] data) {
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM)
            final SecretKeySpec secretKeySpec = prepareKey(key)
            final GCMParameterSpec parameterSpec = prepareParameterSpec()

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec)
            byte[] encrypted = cipher.doFinal(data)

            def result = header(secretKeySpec.getEncoded(), parameterSpec.getIV())
            result << "\n" << toBase64(encrypted).split("(?<=\\G.{$OUTPUT_BLOCK_SIZE})").join('\n')
            return result
        } catch (Exception ex) {
            throw new CryptoUtilsException("unable to encrypt data", ex)
        }
    }

    static byte[] decryptFull(String key, String data) {
        def (
            String tool,
            String algorithm,
            String encoding,
            String keyHash,
            String ivBase64,
            String body
        ) = parseEncrypted(data)

        assert TOOL == tool
        assert ALGORITHM == algorithm
        assert OUTPUT_ENCODING == encoding
        assert toBase64(sha256(sha256(toBytes(key)))) == keyHash

        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM)
            final GCMParameterSpec parameterSpec = new GCMParameterSpec(128, fromBase64(ivBase64))
            cipher.init(Cipher.DECRYPT_MODE, prepareKey(key), parameterSpec)

            return cipher.doFinal(fromBase64(body.replaceAll("[\n\r]", "")))
        } catch (Exception ex) {
            throw new CryptoUtilsException("unable to decrypt, did you provide encrypted data?", ex)
        }
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

    static List parseEncrypted(String data) {
        Pattern pattern = Pattern.compile("\n *")
        Matcher matcher = pattern.matcher(data)
        if (matcher.find()) {
            def header = data.substring(0, matcher.start()).split(':')
            def body = data.substring(matcher.end())
            return [*header, body]
        }
    }

    static String decryptString(String key, String ivBase64, String data) {
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM)
            final SecretKeySpec keySpec = prepareKey(key)
            final GCMParameterSpec parameterSpec = new GCMParameterSpec(128, fromBase64(ivBase64))
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec)

            return new String(cipher.doFinal(fromBase64(data)), ENCODING)
        } catch (Exception ex) {
            throw new CryptoUtilsException("unable to decrypt, did you provide encrypted data?", ex)
        }
    }

    static List encryptString(String key, String data) {
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM)
            final SecretKeySpec secretKeySpec = prepareKey(key)
            final GCMParameterSpec parameterSpec = prepareParameterSpec()
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec)

            return [toBase64(cipher.doFinal(toBytes(data))), toBase64(parameterSpec.getIV())]
        } catch (Exception ex) {
            throw new CryptoUtilsException("unable to encrypt data", ex)
        }
    }

    static String decryptPart(String key, String template, def bindings = [:]) {
        bindings.decrypt = { data, iv -> "${decryptString(key, iv, data)}" }
        new SimpleTemplateEngine().createTemplate(template).make(bindings).toString()
    }

    static String encryptPart(String key, String template, def bindings = [:]) {
        bindings.encrypt = { data ->
            def (encrypted, ivBase64) = encryptString(key, data)
            "\${decrypt('$encrypted', '$ivBase64')}"
        }

        new SimpleTemplateEngine().createTemplate(template).make(bindings).toString()
    }
}
