package com.cloud_idaas.core.domain.constants;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClientDeployEnvironmentEnum 单元测试
 */
class ClientDeployEnvironmentEnumTest {

    // ==================== 枚举值存在性测试 ====================

    @Test
    @DisplayName("枚举值: COMMON 应存在")
    void enumValue_Common_ShouldExist() {
        ClientDeployEnvironmentEnum common = ClientDeployEnvironmentEnum.COMMON;
        assertNotNull(common);
        assertEquals("COMMON", common.name());
    }

    @Test
    @DisplayName("枚举值: COMPUTER 应存在")
    void enumValue_Computer_ShouldExist() {
        assertNotNull(ClientDeployEnvironmentEnum.COMPUTER);
    }

    @Test
    @DisplayName("枚举值: KUBERNETES 应存在")
    void enumValue_Kubernetes_ShouldExist() {
        assertNotNull(ClientDeployEnvironmentEnum.KUBERNETES);
    }

    @Test
    @DisplayName("枚举值: ALIBABA_CLOUD_ECS 应存在")
    void enumValue_AlibabaCloudEcs_ShouldExist() {
        assertNotNull(ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ECS);
    }

    @Test
    @DisplayName("枚举值: ALIBABA_CLOUD_ECI 应存在")
    void enumValue_AlibabaCloudEci_ShouldExist() {
        assertNotNull(ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ECI);
    }

    @Test
    @DisplayName("枚举值: ALIBABA_CLOUD_ACK 应存在")
    void enumValue_AlibabaCloudAck_ShouldExist() {
        assertNotNull(ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ACK);
    }

    @Test
    @DisplayName("枚举值: AWS_EC2 应存在")
    void enumValue_AwsEc2_ShouldExist() {
        assertNotNull(ClientDeployEnvironmentEnum.AWS_EC2);
    }

    @Test
    @DisplayName("枚举值: AWS_ESK 应存在")
    void enumValue_AwsEsk_ShouldExist() {
        assertNotNull(ClientDeployEnvironmentEnum.AWS_ESK);
    }

    @Test
    @DisplayName("枚举值: GOOGLE_VM 应存在")
    void enumValue_GoogleVm_ShouldExist() {
        assertNotNull(ClientDeployEnvironmentEnum.GOOGLE_VM);
    }

    @Test
    @DisplayName("枚举值: HUAWEI_CLOUD_ECS 应存在")
    void enumValue_HuaweiCloudEcs_ShouldExist() {
        assertNotNull(ClientDeployEnvironmentEnum.HUAWEI_CLOUD_ECS);
    }

    @Test
    @DisplayName("枚举值: CUSTOM 应存在")
    void enumValue_Custom_ShouldExist() {
        assertNotNull(ClientDeployEnvironmentEnum.CUSTOM);
    }

    // ==================== values() 测试 ====================

    @Test
    @DisplayName("values: 应返回 11 个枚举值")
    void values_ShouldReturnElevenValues() {
        ClientDeployEnvironmentEnum[] values = ClientDeployEnvironmentEnum.values();
        assertEquals(11, values.length);
    }

    // ==================== valueOf() 测试 ====================

    @Test
    @DisplayName("valueOf: 各枚举值应正确解析")
    void valueOf_AllValues_ShouldBeCorrect() {
        assertEquals(ClientDeployEnvironmentEnum.COMMON, ClientDeployEnvironmentEnum.valueOf("COMMON"));
        assertEquals(ClientDeployEnvironmentEnum.COMPUTER, ClientDeployEnvironmentEnum.valueOf("COMPUTER"));
        assertEquals(ClientDeployEnvironmentEnum.KUBERNETES, ClientDeployEnvironmentEnum.valueOf("KUBERNETES"));
        assertEquals(ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ECS, ClientDeployEnvironmentEnum.valueOf("ALIBABA_CLOUD_ECS"));
        assertEquals(ClientDeployEnvironmentEnum.CUSTOM, ClientDeployEnvironmentEnum.valueOf("CUSTOM"));
    }

    @Test
    @DisplayName("valueOf: 无效名称应抛出 IllegalArgumentException")
    void valueOf_InvalidName_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            ClientDeployEnvironmentEnum.valueOf("INVALID");
        });
    }

    // ==================== ordinal() 测试 ====================

    @Test
    @DisplayName("ordinal: COMMON 的序号应为 0")
    void ordinal_Common_ShouldBeZero() {
        assertEquals(0, ClientDeployEnvironmentEnum.COMMON.ordinal());
    }

    @Test
    @DisplayName("ordinal: CUSTOM 的序号应为 10")
    void ordinal_Custom_ShouldBeTen() {
        assertEquals(10, ClientDeployEnvironmentEnum.CUSTOM.ordinal());
    }

    // ==================== 比较测试 ====================

    @Test
    @DisplayName("比较: 不同枚举值不应相等")
    void comparison_DifferentEnumValues_ShouldNotBeEqual() {
        assertNotEquals(ClientDeployEnvironmentEnum.COMMON, ClientDeployEnvironmentEnum.CUSTOM);
        assertNotEquals(ClientDeployEnvironmentEnum.ALIBABA_CLOUD_ECS, ClientDeployEnvironmentEnum.AWS_EC2);
    }

    @Test
    @DisplayName("比较: 相同枚举值应相等")
    void comparison_SameEnumValues_ShouldBeEqual() {
        assertEquals(ClientDeployEnvironmentEnum.KUBERNETES, ClientDeployEnvironmentEnum.KUBERNETES);
    }
}
