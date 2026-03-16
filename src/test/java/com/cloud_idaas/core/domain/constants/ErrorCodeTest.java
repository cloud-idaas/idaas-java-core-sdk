package com.cloud_idaas.core.domain.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ErrorCode 单元测试
 */
class ErrorCodeTest {

    // ==================== 枚举值存在性测试 ====================

    @Test
    @DisplayName("枚举值: IDAAS_INSTANCE_ID_NOT_FOUND 应存在")
    void enumValue_IdaasInstanceIdNotFound_ShouldExist() {
        ErrorCode errorCode = ErrorCode.IDAAS_INSTANCE_ID_NOT_FOUND;
        assertNotNull(errorCode);
        assertEquals("IDaaSInstanceIdNotFound", errorCode.getCode());
    }

    @Test
    @DisplayName("枚举值: CLIENT_ID_NOT_FOUND 应存在")
    void enumValue_ClientIdNotFound_ShouldExist() {
        assertNotNull(ErrorCode.CLIENT_ID_NOT_FOUND);
        assertEquals("ClientIdNotFound", ErrorCode.CLIENT_ID_NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("枚举值: TOKEN_ENDPOINT_NOT_FOUND 应存在")
    void enumValue_TokenEndpointNotFound_ShouldExist() {
        assertNotNull(ErrorCode.TOKEN_ENDPOINT_NOT_FOUND);
    }

    @Test
    @DisplayName("枚举值: IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT 应存在")
    void enumValue_FactoryNotInit_ShouldExist() {
        assertNotNull(ErrorCode.IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT);
        assertEquals("IDaaSCredentialProviderFactoryNotInit", ErrorCode.IDAAS_CREDENTIAL_PROVIDER_FACTORY_NOT_INIT.getCode());
    }

    @Test
    @DisplayName("枚举值: UNSUPPORTED_AUTHENTICATION_METHOD 应存在")
    void enumValue_UnsupportedAuthnMethod_ShouldExist() {
        assertNotNull(ErrorCode.UNSUPPORTED_AUTHENTICATION_METHOD);
    }

    @Test
    @DisplayName("枚举值: INVALID_SCOPE 应存在")
    void enumValue_InvalidScope_ShouldExist() {
        assertNotNull(ErrorCode.INVALID_SCOPE);
        assertEquals("InvalidScope", ErrorCode.INVALID_SCOPE.getCode());
    }

    // ==================== getCode() 测试 ====================

    @Test
    @DisplayName("getCode: 应返回正确的错误码字符串")
    void getCode_ShouldReturnCorrectString() {
        assertEquals("IDaaSInstanceIdNotFound", ErrorCode.IDAAS_INSTANCE_ID_NOT_FOUND.getCode());
        assertEquals("ClientIdNotFound", ErrorCode.CLIENT_ID_NOT_FOUND.getCode());
        assertEquals("TokenEndpointNotFound", ErrorCode.TOKEN_ENDPOINT_NOT_FOUND.getCode());
        assertEquals("InvalidScope", ErrorCode.INVALID_SCOPE.getCode());
    }

    @Test
    @DisplayName("getCode: 所有错误码不应为空")
    void getCode_AllCodes_ShouldNotBeEmpty() {
        for (ErrorCode errorCode : ErrorCode.values()) {
            assertNotNull(errorCode.getCode());
            assertFalse(errorCode.getCode().isEmpty());
        }
    }

    // ==================== values() 测试 ====================

    @Test
    @DisplayName("values: 应返回所有枚举值")
    void values_ShouldReturnAllValues() {
        ErrorCode[] values = ErrorCode.values();
        assertTrue(values.length > 0);
    }

    @Test
    @DisplayName("values: 应包含特定的错误码")
    void values_ShouldContainSpecificCodes() {
        ErrorCode[] values = ErrorCode.values();
        boolean foundClientIdNotFound = false;
        boolean foundInvalidScope = false;

        for (ErrorCode code : values) {
            if (code == ErrorCode.CLIENT_ID_NOT_FOUND) foundClientIdNotFound = true;
            if (code == ErrorCode.INVALID_SCOPE) foundInvalidScope = true;
        }

        assertTrue(foundClientIdNotFound);
        assertTrue(foundInvalidScope);
    }

    // ==================== valueOf() 测试 ====================

    @Test
    @DisplayName("valueOf: 应正确解析枚举名称")
    void valueOf_ShouldParseCorrectly() {
        ErrorCode errorCode = ErrorCode.valueOf("CLIENT_ID_NOT_FOUND");
        assertEquals(ErrorCode.CLIENT_ID_NOT_FOUND, errorCode);
    }

    @Test
    @DisplayName("valueOf: 无效名称应抛出 IllegalArgumentException")
    void valueOf_InvalidName_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            ErrorCode.valueOf("NON_EXISTENT_ERROR");
        });
    }

    // ==================== name() 测试 ====================

    @Test
    @DisplayName("name: 应返回正确的枚举名称")
    void name_ShouldReturnCorrectName() {
        assertEquals("IDAAS_INSTANCE_ID_NOT_FOUND", ErrorCode.IDAAS_INSTANCE_ID_NOT_FOUND.name());
        assertEquals("CLIENT_ID_NOT_FOUND", ErrorCode.CLIENT_ID_NOT_FOUND.name());
    }

    // ==================== ordinal() 测试 ====================

    @Test
    @DisplayName("ordinal: 第一个枚举值序号应为 0")
    void ordinal_FirstValue_ShouldBeZero() {
        assertEquals(0, ErrorCode.IDAAS_INSTANCE_ID_NOT_FOUND.ordinal());
    }

    // ==================== 比较测试 ====================

    @Test
    @DisplayName("比较: 相同枚举值应相等")
    void comparison_SameEnumValues_ShouldBeEqual() {
        ErrorCode code1 = ErrorCode.CLIENT_ID_NOT_FOUND;
        ErrorCode code2 = ErrorCode.CLIENT_ID_NOT_FOUND;
        assertEquals(code1, code2);
        assertSame(code1, code2);
    }

    @Test
    @DisplayName("比较: 不同枚举值不应相等")
    void comparison_DifferentEnumValues_ShouldNotBeEqual() {
        assertNotEquals(ErrorCode.CLIENT_ID_NOT_FOUND, ErrorCode.INVALID_SCOPE);
    }

    // ==================== toString 测试 ====================

    @Test
    @DisplayName("toString: 应返回枚举名称")
    void toString_ShouldReturnEnumName() {
        assertEquals("CLIENT_ID_NOT_FOUND", ErrorCode.CLIENT_ID_NOT_FOUND.toString());
    }
}
