package com.cloud_idaas.core.util;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * PKI utility class for handling PEM format keys
 */
public class PkiUtil {

    /**
     * Unified method to parse PEM format private key, supporting multiple formats of RSA and ECC private keys
     * Supported formats:
     * 1. -----BEGIN PRIVATE KEY----- (PKCS#8 generic format)
     * 2. -----BEGIN RSA PRIVATE KEY----- (PKCS#1 RSA format)
     * 3. -----BEGIN EC PRIVATE KEY----- (PKCS#8 ECC format)
     * 4. -----BEGIN DSA PRIVATE KEY----- (PKCS#8 DSA format)
     *
     * @param pemContent PEM formatted private key string
     * @return PrivateKey object
     * @throws Exception exceptions that may occur during parsing
     */
    public static PrivateKey parsePrivateKeyFromPem(String pemContent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (pemContent.startsWith("-----BEGIN RSA PRIVATE KEY-----")) {

            // Process PKCS#1 RSA private key format
            return parsePkcs1RsaPrivateKey(pemContent);
        } else if (pemContent.startsWith("-----BEGIN EC PRIVATE KEY-----")) {
            // Process PKCS#8 ECC private key format
            return parsePkcs8EccPrivateKey(pemContent);
        } else if (pemContent.startsWith("-----BEGIN DSA PRIVATE KEY-----")) {
            // Process PKCS#8 DSA private key format
            return parsePkcs8DsaPrivateKey(pemContent);
        } else if (pemContent.startsWith("-----BEGIN PRIVATE KEY-----")) {
            // Handle standard PKCS#8 private key format
            return parsePkcs8PrivateKey(pemContent);
        } else {
            throw new IllegalArgumentException("Nonsupported private key pem content.");
        }
    }

    /**
     * Parse standard PKCS#8 format private key
     */
    private static PrivateKey parsePkcs8PrivateKey(String pemContent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyPEM = pemContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);


        // Try different algorithms
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("EC");
                return keyFactory.generatePrivate(keySpec);
            } catch (Exception ex) {
                KeyFactory keyFactory = KeyFactory.getInstance("DSA");
                return keyFactory.generatePrivate(keySpec);
            }
        }
    }

    /**
     * Parse PKCS#1 RSA private key format
     */
    private static PrivateKey parsePkcs1RsaPrivateKey(String pemContent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyPEM = pemContent
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] pkcs1Bytes = Base64.getDecoder().decode(privateKeyPEM);

        // Add PKCS#8 wrapper header to convert to standard format
        byte[] pkcs8Bytes = wrapPkcs1InPkcs8(pkcs1Bytes, "RSA");

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8Bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * Parse PKCS#8 ECC private key format
     */
    private static PrivateKey parsePkcs8EccPrivateKey(String pemContent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyPEM = pemContent
                .replace("-----BEGIN EC PRIVATE KEY-----", "")
                .replace("-----END EC PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * Parse PKCS#8 DSA private key format
     */
    private static PrivateKey parsePkcs8DsaPrivateKey(String pemContent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyPEM = pemContent
                .replace("-----BEGIN DSA PRIVATE KEY-----", "")
                .replace("-----END DSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);

        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * Wrap PKCS#1 format as PKCS#8 format
     */
    private static byte[] wrapPkcs1InPkcs8(byte[] pkcs1Bytes, String algorithm) {

        // This is a simplified version, more complex ASN.1 encoding might be needed in actual applications
        byte[] pkcs8Bytes = new byte[pkcs1Bytes.length + 26];
        System.arraycopy(new byte[] { 0x30, (byte) 0x82 }, 0, pkcs8Bytes, 0, 2);
        System.arraycopy(new byte[] { (byte) ((pkcs1Bytes.length + 22) >> 8),
                                      (byte) (pkcs1Bytes.length + 22) }, 0, pkcs8Bytes, 2, 2);
        System.arraycopy(new byte[] { 0x02, 0x01, 0x00 }, 0, pkcs8Bytes, 4, 3);


        // Add OID based on algorithm
        if ("RSA".equals(algorithm)) {
            System.arraycopy(new byte[] { 0x30, 0x0d, 0x06, 0x09, 0x2a, (byte) 0x86, 0x48, (byte) 0x86,
                                          (byte) 0xf7, 0x0d, 0x01, 0x01, 0x01, 0x05, 0x00 }, 0, pkcs8Bytes, 7, 15);
        }

        System.arraycopy(new byte[] { 0x04, (byte) 0x82 }, 0, pkcs8Bytes, 22, 2);
        System.arraycopy(new byte[] { (byte) (pkcs1Bytes.length >> 8), (byte) pkcs1Bytes.length }, 0, pkcs8Bytes, 24, 2);
        System.arraycopy(pkcs1Bytes, 0, pkcs8Bytes, 26, pkcs1Bytes.length);

        return pkcs8Bytes;
    }
}
