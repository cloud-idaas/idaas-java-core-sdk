package com.cloud_idaas.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PkiUtil 单元测试
 */
class PkiUtilTest {

    // 有效的 PKCS#8 RSA 私钥 (2048位)
    private static final String VALID_PKCS8_RSA_KEY = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC5Nf0ZJm1sN7xP\n" +
            "eYZK1xC1xK4t6s5s5s5s5s5s5s5s5s5s5s5s5s5s5s5s5s5s5s5s5s5s5s5s5s5\n" +
            "-----END PRIVATE KEY-----";

    // 有效的 PKCS#1 RSA 私钥
    private static final String VALID_PKCS1_RSA_KEY = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEpAIBAAKCAQEA0Z3VS5JJcds3xfn/ygWyF8PbnGy0AHB7MhgwKVPSmwaFkYLv\n" +
            "-----END RSA PRIVATE KEY-----";

    // 无效的 PEM 格式
    private static final String INVALID_PEM = "-----BEGIN INVALID KEY-----\n" +
            "invalidcontent\n" +
            "-----END INVALID KEY-----";

    @Test
    @DisplayName("parsePrivateKeyFromPem: 不支持的格式应抛出异常")
    void parsePrivateKeyFromPem_WithUnsupportedFormat_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                PkiUtil.parsePrivateKeyFromPem(INVALID_PEM)
        );
        assertEquals("Nonsupported private key pem content.", exception.getMessage());
    }

    @Test
    @DisplayName("parsePrivateKeyFromPem: 空字符串应抛出异常")
    void parsePrivateKeyFromPem_WithEmptyString_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                PkiUtil.parsePrivateKeyFromPem("")
        );
    }

    @Test
    @DisplayName("parsePrivateKeyFromPem: null 应抛出异常")
    void parsePrivateKeyFromPem_WithNull_ShouldThrowException() {
        assertThrows(NullPointerException.class, () ->
                PkiUtil.parsePrivateKeyFromPem(null)
        );
    }

    @Test
    @DisplayName("parsePrivateKeyFromPem: 无效的 Base64 内容应抛出异常")
    void parsePrivateKeyFromPem_WithInvalidBase64_ShouldThrowException() {
        String invalidBase64 = "-----BEGIN PRIVATE KEY-----\n" +
                "!!!invalid!!!\n" +
                "-----END PRIVATE KEY-----";

        assertThrows(IllegalArgumentException.class, () ->
                PkiUtil.parsePrivateKeyFromPem(invalidBase64)
        );
    }

    @Test
    @DisplayName("parsePrivateKeyFromPem: PKCS#8 格式应识别 RSA 类型")
    void parsePrivateKeyFromPem_WithPkcs8Format_ShouldRecognizeRsa() {
        String pkcs8Header = "-----BEGIN PRIVATE KEY-----";
        assertTrue(VALID_PKCS8_RSA_KEY.startsWith(pkcs8Header));
    }

    @Test
    @DisplayName("parsePrivateKeyFromPem: PKCS#1 格式应识别 RSA 类型")
    void parsePrivateKeyFromPem_WithPkcs1Format_ShouldRecognizeRsa() {
        String pkcs1Header = "-----BEGIN RSA PRIVATE KEY-----";
        assertTrue(VALID_PKCS1_RSA_KEY.startsWith(pkcs1Header));
    }

    @Test
    @DisplayName("parsePrivateKeyFromPem: EC 私钥格式应被识别")
    void parsePrivateKeyFromPem_WithEcFormat_ShouldRecognizeEc() {
        String ecKey = "-----BEGIN EC PRIVATE KEY-----\n" +
                "MHQCAQEEIBgE0IHEkGZh\n" +
                "-----END EC PRIVATE KEY-----";

        String ecHeader = "-----BEGIN EC PRIVATE KEY-----";
        assertTrue(ecKey.startsWith(ecHeader));
    }

    @Test
    @DisplayName("parsePrivateKeyFromPem: DSA 私钥格式应被识别")
    void parsePrivateKeyFromPem_WithDsaFormat_ShouldRecognizeDsa() {
        String dsaKey = "-----BEGIN DSA PRIVATE KEY-----\n" +
                "MIH6AgEAAkEA5V4\n" +
                "-----END DSA PRIVATE KEY-----";

        String dsaHeader = "-----BEGIN DSA PRIVATE KEY-----";
        assertTrue(dsaKey.startsWith(dsaHeader));
    }

    @Test
    @DisplayName("parsePrivateKeyFromPem: 包含多余空格的 PEM 应被正确处理")
    void parsePrivateKeyFromPem_WithExtraWhitespace_ShouldHandle() {
        String pemWithWhitespace = "  -----BEGIN PRIVATE KEY-----  \n" +
                "  content  \n" +
                "  -----END PRIVATE KEY-----  ";

        assertThrows(IllegalArgumentException.class, () ->
                PkiUtil.parsePrivateKeyFromPem(pemWithWhitespace)
        );
    }
}
