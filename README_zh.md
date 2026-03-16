# idaas-java-core-sdk

[![Java Version](https://img.shields.io/badge/java-8%2B-blue)](https://www.java.com/)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](LICENSE)
[![Development Status](https://img.shields.io/badge/status-Beta-orange)](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-core-sdk)

[English](README.md)

IDaaS（身份即服务）M2M 产品的 Java SDK，为开发者提供便捷的机器对机器认证能力。

## 功能特性

- **多种认证方式**：支持 JWT Client Secret、JWT 私钥、OIDC Token、PKCS7 认证文档等多种 M2M 认证方式
- **智能缓存机制**：内置凭证缓存策略，支持预取和过期值处理，减少不必要的网络请求
- **灵活配置**：支持配置文件、环境变量和编程式配置
- **插件扩展**：支持自定义凭证提供器，满足特殊场景需求
- **云原生支持**：内置阿里云 ECS 和阿里云 ACK 的认证文档支持
- **令牌交换（RFC 8693）**：支持令牌交换以获取不同 scope 或 audience 的访问令牌，适用于令牌降权和服务间调用场景

## 环境要求

- Java >= 8
- Maven >= 3.6

## 安装

在 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>com.cloud-idaas</groupId>
    <artifactId>idaas-java-core-sdk</artifactId>
    <version>0.0.4-beta</version>
</dependency>
```
[最新版本](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-core-sdk)

## 快速开始

> **重要提示**：在使用任何 SDK 功能之前，必须调用 `IDaaSCredentialProviderFactory.init()` 初始化 SDK。此步骤是**必需的**，应在应用程序启动时执行一次。

### 1. 配置文件

创建配置文件 `~/.cloud_idaas/client_config.json`：

```json
{
    "idaasInstanceId": "your-idaas-instance-id",
    "clientId": "your-client-id",
    "issuer": "your-idaas-issuer-url",
    "tokenEndpoint": "your-idaas-token-endpoint",
    "scope": "your-requested-scope",
    "openApiEndpoint":"your-open-api-endpoint",
    "developerApiEndpoint": "your-developer-api-endpoint",
    "authnConfiguration": {
        "authenticationSubject": "CLIENT",
        "authnMethod": "CLIENT_SECRET_POST",
        "clientSecretEnvVarName": "IDAAS_CLIENT_SECRET"
    }
}
```

### 2. 环境变量

设置环境变量：

```bash
export IDAAS_CLIENT_SECRET="your-client-secret"
```

### 3. 代码中使用

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.provider.IDaaSCredentialProvider;
public class Sample {

    public static void main(String[] args) {
        // 初始化（自动加载配置文件）
        IDaaSCredentialProviderFactory.init();

        // 获取凭证提供器
        IDaaSCredentialProvider credentialProvider = IDaaSCredentialProviderFactory.getIDaaSCredentialProvider();

        // 获取访问令牌
        String accessToken = credentialProvider.getBearerToken();
        System.out.println("Access Token: " + accessToken);
    }
}
```

## 配置详情

### 配置文件路径

SDK 按以下顺序搜索配置文件：

1. Java 系统属性路径：    
    `-Dcloud_idaas_config_path=/path/to/client-config.json`    
    classpath：`-Dcloud_idaas_config_path=classpath:client-config.json`（将配置文件放置于 `src/main/resources/client-config.json`）
2. 环境变量路径：`CLOUD_IDAAS_CONFIG_PATH=/path/to/client-config.json`
3. 默认路径：`~/.cloud_idaas/client-config.json`

### 完整配置示例

```json
{
    "idaasInstanceId": "idaas_ue2jvisn35ea5lmthk267xxxxx",
    "clientId": "app_mkv7rgt4d7i4u7zqtzev2mxxxx",
    "issuer":"https://xxx/api/v2/iauths_system/oauth2",
    "tokenEndpoint": "https://xxx/api/v2/iauths_system/oauth2/token",
    "scope": "api.example.com|read:file",
    "openApiEndpoint":"eiam.[region_id].aliyuncs.com",
    "developerApiEndpoint":"eiam-developerapi.[region_id].aliyuncs.com",
    "authnConfiguration": {
        "authenticationSubject": "CLIENT",
        "authnMethod": "CLIENT_SECRET_POST",
        "clientSecretEnvVarName": "IDAAS_CLIENT_SECRET"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

### 配置项说明

| 配置项 | 类型 | 必填 | 描述 |
|-------------------|------|----------|-------------|
| idaasInstanceId | string | 是 | IDaaS 实例 ID |
| clientId | string | 是 | 用于认证的客户端 ID |
| issuer | string | 是 | OAuth2 签发者 URL |
| tokenEndpoint | string | 是 | OAuth2 令牌端点 URL |
| scope | string | 否 | 请求的范围 |
| openApiEndpoint | string | 否 | OpenAPI 端点 |
| developerApiEndpoint | string | 否 | 开发者 API 端点 |
| authnConfiguration | object | 是 | 认证配置 |
| httpConfiguration | object | 否 | HTTP 客户端配置 |

### Scope 格式

SDK 使用特定的 scope 格式，受众和 scope 值通过 `|` 分隔：

```
audience|scope_value
```

示例：
- `api.example.com|read:file`
- `api.example.com|write:file`
- `resource.server|admin`

可以请求同一受众的多个 scope 值：
```
api.example.com|read:file api.example.com|write:file
```

**注意**：不支持在单个请求中使用多个受众。

## 认证方式

### 客户端密钥认证

使用客户端密钥进行认证。支持 `CLIENT_SECRET_BASIC`、`CLIENT_SECRET_POST` 和 `CLIENT_SECRET_JWT` 方式。

```json
{
    "idaasInstanceId": "idaas_ue2jvisn35ea5lmthk267xxxxx",
    "clientId": "app_mkv7rgt4d7i4u7zqtzev2mxxxx",
    "issuer": "your-idaas-issuer-url",
    "tokenEndpoint": "your-idaas-token-endpoint",
    "scope": "your-requested-scope",
    "openApiEndpoint": "eiam.[region_id].aliyuncs.com",
    "developerApiEndpoint": "eiam-developerapi.[region_id].aliyuncs.com",
    "authnConfiguration": {
        "authenticationSubject": "CLIENT",
        "authnMethod": "CLIENT_SECRET_POST",
        "clientSecretEnvVarName": "IDAAS_CLIENT_SECRET"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

### 私钥认证

使用私钥进行认证，提供更高的安全性。

```json
{
    "idaasInstanceId": "idaas_ue2jvisn35ea5lmthk267xxxxx",
    "clientId": "app_mkv7rgt4d7i4u7zqtzev2mxxxx",
    "issuer": "your-idaas-issuer-url",
    "tokenEndpoint": "your-idaas-token-endpoint",
    "scope": "your-requested-scope",
    "openApiEndpoint": "eiam.[region_id].aliyuncs.com",
    "developerApiEndpoint": "eiam-developerapi.[region_id].aliyuncs.com",
    "authnConfiguration": {
        "authenticationSubject": "CLIENT",
        "authnMethod": "PRIVATE_KEY_JWT",
        "privateKeyEnvVarName": "ENV_PRIVATE_KEY"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

### PKCS7 联合认证

在云环境中使用 PKCS7 认证文档进行认证。

```json
{
    "idaasInstanceId": "idaas_ue2jvisn35ea5lmthk267xxxxx",
    "clientId": "app_mkv7rgt4d7i4u7zqtzev2mxxxx",
    "issuer": "your-idaas-issuer-url",
    "tokenEndpoint": "your-idaas-token-endpoint",
    "scope": "your-requested-scope",
    "openApiEndpoint": "eiam.[region_id].aliyuncs.com",
    "developerApiEndpoint": "eiam-developerapi.[region_id].aliyuncs.com",
    "authnConfiguration": {
        "authenticationSubject": "CLIENT",
        "authnMethod": "PKCS7",
        "applicationFederatedCredentialName": "your-pkcs7-credential-name",
        "clientDeployEnvironment": "ALIBABA_CLOUD_ECS"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

### OIDC 联合认证

使用 OIDC Token 进行认证。

```json
{
    "idaasInstanceId": "idaas_ue2jvisn35ea5lmthk267xxxxx",
    "clientId": "app_mkv7rgt4d7i4u7zqtzev2mxxxx",
    "issuer": "your-idaas-issuer-url",
    "tokenEndpoint": "your-idaas-token-endpoint",
    "scope": "your-requested-scope",
    "openApiEndpoint": "eiam.[region_id].aliyuncs.com",
    "developerApiEndpoint": "eiam-developerapi.[region_id].aliyuncs.com",
    "authnConfiguration": {
        "authenticationSubject": "CLIENT",
        "authnMethod": "OIDC",
        "applicationFederatedCredentialName": "your-oidc-credential-name",
        "clientDeployEnvironment": "KUBERNETES"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

### PCA（X.509 证书）认证

使用 X.509 证书进行认证。

```json
{
    "idaasInstanceId": "idaas_ue2jvisn35ea5lmthk267xxxxx",
    "clientId": "app_mkv7rgt4d7i4u7zqtzev2mxxxx",
    "issuer": "your-idaas-issuer-url",
    "tokenEndpoint": "your-idaas-token-endpoint",
    "scope": "your-requested-scope",
    "openApiEndpoint": "eiam.[region_id].aliyuncs.com",
    "developerApiEndpoint": "eiam-developerapi.[region_id].aliyuncs.com",
    "authnConfiguration": {
        "authenticationSubject": "CLIENT",
        "authnMethod": "PCA",
        "applicationFederatedCredentialName": "your_pca_federated_credential_name",
        "clientX509Certificate": "-----BEGIN CERTIFICATE-----\nxxx\n-----END CERTIFICATE-----",
        "x509CertChains": "-----BEGIN CERTIFICATE-----\nxxx\n-----END CERTIFICATE-----",
        "privateKeyEnvVarName": "ENV_PRIVATE_KEY"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

### 插件认证

使用基于插件的凭证提供器进行认证。

```json
{
    "idaasInstanceId": "idaas_ue2jvisn35ea5lmthk267xxxxx",
    "clientId": "app_mkv7rgt4d7i4u7zqtzev2mxxxx",
    "issuer": "your-idaas-issuer-url",
    "tokenEndpoint": "your-idaas-token-endpoint",
    "scope": "your-requested-scope",
    "openApiEndpoint": "eiam.[region_id].aliyuncs.com",
    "developerApiEndpoint": "eiam-developerapi.[region_id].aliyuncs.com",
    "authnConfiguration": {
        "authenticationSubject": "CLIENT",
        "authnMethod": "PLUGIN",
        "pluginName": "alibabacloudPluginCredentialProvider"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

## 令牌交换

令牌交换（RFC 8693）允许您将主体令牌交换为具有不同范围或受众的新访问令牌。这对于令牌降级和服务间访问场景非常有用。

### 基本令牌交换

完整示例请参见 `samples/` 目录：

- `samples/TokenExchangeSample.java` - 令牌交换示例

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.provider.IDaaSTokenExchangeCredentialProvider;
import com.cloud_idaas.core.util.OAuth2Constants;

public class TokenExchangeSample {

    public static void main(String[] args) {

        // 使用配置初始化工厂
        IDaaSCredentialProviderFactory.init();

        // 要交换的主体令牌
        String subjectToken = "";

        // 使用配置文件中的 scope 获取令牌交换凭证提供器
        // IDaaSTokenExchangeCredentialProvider tokenExchangeCredentialProvider =
        //                IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();

        // scope 格式：<audience>|<scope>
        String scope = "api.example.com|read:file";
        // 通过参数指定 scope 获取令牌交换凭证提供器
        IDaaSTokenExchangeCredentialProvider tokenExchangeCredentialProvider =
                IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider(scope);

        // 执行令牌交换
        String accessToken = tokenExchangeCredentialProvider.getIssuedToken(
                subjectToken,
                OAuth2Constants.ACCESS_TOKEN_TYPE,
                OAuth2Constants.ACCESS_TOKEN_TYPE
        );

        System.out.println(accessToken);
    }
}
```

### 令牌交换参数

| 参数 | 类型   | 必填 | 描述 |
|-----------|--------|----------|-------------|
| subject_token | String | 是 | 要交换的令牌 |
| subject_token_type | String | 是 | 主体令牌类型（如 `urn:ietf:params:oauth:token-type:access_token`） |
| requested_token_type | String | 否 | 请求的令牌类型（默认为访问令牌） |

### 使用场景

1. **令牌降级**：将具有较宽权限的令牌交换为具有有限范围的令牌
2. **服务间访问**：在服务之间传递相同的用户身份以获取所需的访问令牌

## 支持与反馈

- **邮箱**：cloudidaas@list.alibaba-inc.com
- **问题反馈**：如有问题或建议，请提交 Issue

## 许可证

本项目基于 [Apache License 2.0](LICENSE) 许可证授权。
