package org.example.cipher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    static final String PASSWORD_PATH = "src/test/java/org/example/cipher/password";
    static final String SECRET_KEY_PATH = "src/test/java/org/example/cipher/key";
    static String SUB_PATH = "";
    static Cipher cipher;

    public static void main(String[] args) throws Exception {
        SUB_PATH = PASSWORD_PATH.substring(0, PASSWORD_PATH.length() - 8);
        writeToFile("key", generateSecretKey());
        writeToFile("password", encrypt("Microfiber33?!", getSecretKey()));
        System.out.println(decrypt(getEncryptedPassword(), getSecretKey()));
        System.out.println(SUB_PATH);
    }

    public static String generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // block size is 128bits
        SecretKey secretKey = keyGenerator.generateKey();
        cipher = Cipher.getInstance("AES");
        byte[] rawData = secretKey.getEncoded();
        return Base64.getEncoder().withoutPadding().encodeToString(rawData);
    }

    public static void writeToFile(String fileName, String value) throws Exception {
        List<String> lines = Arrays.asList(value);
        Path file = Paths.get(SUB_PATH + fileName);
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    public static SecretKey getSecretKey() throws Exception {
        BufferedReader file = new BufferedReader(new FileReader(SECRET_KEY_PATH));
        String content = file.readLine();
        byte[] decodedKey = Base64.getDecoder().decode(content);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public static String getEncryptedPassword() throws Exception {
        BufferedReader file = new BufferedReader(new FileReader(PASSWORD_PATH));
        return file.readLine();
    }

    public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        byte[] plainTextByte = plainText.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);
        return encryptedText;
    }

    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }
}