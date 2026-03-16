# idaas-java-core-sdk

[![Java Version](https://img.shields.io/badge/java-8%2B-blue)](https://www.java.com/)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](LICENSE)
[![Development Status](https://img.shields.io/badge/status-Beta-orange)](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-core-sdk)

[简体中文](README_zh.md)

Java SDK for IDaaS (Identity as a Service) M2M product, providing developers with convenient machine-to-machine authentication capabilities.

## Features

- **Multiple Authentication Methods**: Supports JWT Client Secret, JWT Private Key, OIDC Token, PKCS7 Attested Document, and other M2M authentication methods
- **Intelligent Caching Mechanism**: Built-in credential caching strategy with prefetch and stale value handling to reduce unnecessary network requests
- **Flexible Configuration**: Supports configuration files, environment variables, and programmatic configuration
- **Plugin Extensions**: Supports custom credential providers for special scenarios
- **Cloud-Native Support**: Built-in attested document support for Alibaba Cloud ECS and Alibaba Cloud ACK
- **Token Exchange (RFC 8693)**: Exchange tokens for different scopes or audiences, supporting token downscoping and service-to-service access scenarios

## Requirements

- Java >= 8
- Maven >= 3.6

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.cloud-idaas</groupId>
    <artifactId>idaas-java-core-sdk</artifactId>
    <version>0.0.4-beta</version>
</dependency>
```
[Latest version](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-core-sdk)

## Quick Start

> **Important**: Before using any SDK features, you must call `IDaaSCredentialProviderFactory.init()` to initialize the SDK. This step is **required** and should be done once at application startup.

### 1. Configuration File

Create a configuration file `~/.cloud_idaas/client_config.json`:

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

### 2. Environment Variables

Set environment variables:

```bash
export IDAAS_CLIENT_SECRET="your-client-secret"
```

### 3. Use in code

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

## Configuration Details

### Configuration File Paths

The SDK searches for configuration files in the following order:

1. Java System Property path:     
    `-Dcloud_idaas_config_path=/path/to/client-config.json`     
    classpath: `-Dcloud_idaas_config_path=classpath:client-config.json` (Place the configuration file at `src/main/resources/client-config.json`)
2. Environment variable path: `CLOUD_IDAAS_CONFIG_PATH=/path/to/client-config.json`
3. Default path: `~/.cloud_idaas/client-config.json`

### Complete Configuration Example

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

### Configuration Items

| Configuration Item | Type | Required | Description |
|-------------------|------|----------|-------------|
| idaasInstanceId | string | Yes | IDaaS instance ID |
| clientId | string | Yes | Client ID for authentication |
| issuer | string | Yes | OAuth2 issuer URL |
| tokenEndpoint | string | Yes | OAuth2 token endpoint URL |
| scope | string | No | Requested scope |
| openApiEndpoint | string | No | OpenAPI endpoint |
| developerApiEndpoint | string | No | Developer API endpoint |
| authnConfiguration | object | Yes | Authentication configuration |
| httpConfiguration | object | No | HTTP client configuration |

### Scope Format

The SDK uses a specific scope format with audience and scope values separated by `|`:

```
audience|scope_value
```

Examples:
- `api.example.com|read:file`
- `api.example.com|write:file`
- `resource.server|admin`

Multiple scope values for the same audience can be requested:
```
api.example.com|read:file api.example.com|write:file
```

**Note**: Multiple audiences in a single request are not supported.

## Authentication Methods

### Client Secret Authentication

Use Client Secret for authentication. Supports `CLIENT_SECRET_BASIC`, `CLIENT_SECRET_POST`, and `CLIENT_SECRET_JWT` methods.

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

### Private Key Authentication

Use private key for authentication, offering higher security.

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

### PKCS7 Federated Authentication

Use PKCS7 attested document for authentication in cloud environments.

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

### OIDC Federated Authentication

Use OIDC token for authentication.

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

### PCA (X.509 Certificate) Authentication

Use X.509 certificate for authentication.

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

### Plugin Authentication

Use plugin-based credential provider for authentication.

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

## Token Exchange

Token Exchange (RFC 8693) allows you to exchange a subject token for a new access token with different scope or audience. This is useful for token downscoping and service-to-service access scenarios.

### Basic Token Exchange

For working examples, see the `samples/` directory:

- `samples/TokenExchangeSample.java` - Token Exchange sample

```java
import com.cloud_idaas.core.factory.IDaaSCredentialProviderFactory;
import com.cloud_idaas.core.provider.IDaaSTokenExchangeCredentialProvider;
import com.cloud_idaas.core.util.OAuth2Constants;

public class TokenExchangeSample {

    public static void main(String[] args) {

        // Initialize the factory with configuration
        IDaaSCredentialProviderFactory.init();

        // The subject token to exchange
        String subjectToken = "";

        //  Get Token Exchange credential provider with scope from config file
        // IDaaSTokenExchangeCredentialProvider tokenExchangeCredentialProvider =
        //                IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();

        // scope format: <audience>|<scope>
        String scope = "api.example.com|read:file";
        // Get Token Exchange credential provider with scope specified by parameter
        IDaaSTokenExchangeCredentialProvider tokenExchangeCredentialProvider =
                IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider(scope);

        // perform token exchange
        String accessToken = tokenExchangeCredentialProvider.getIssuedToken(
                subjectToken,
                OAuth2Constants.ACCESS_TOKEN_TYPE,
                OAuth2Constants.ACCESS_TOKEN_TYPE
        );

        System.out.println(accessToken);
    }
}
```

### Token Exchange Parameters

| Parameter | Type   | Required | Description |
|-----------|--------|----------|-------------|
| subject_token | String | Yes | The token to be exchanged |
| subject_token_type | String | Yes | Type of the subject token (e.g., `urn:ietf:params:oauth:token-type:access_token`) |
| requested_token_type | String | No | Type of token requested (defaults to access token) |

### Use Cases

1. **Token Downscoping**: Exchange a token with broader permissions for one with limited scope
2. **Service-to-Service Access**: Transfer the same user identity across services to obtain the required access token

### Supported Authentication Methods
Token Exchange supports the following authentication methods:
- `CLIENT_SECRET_BASIC` - Client secret in HTTP Basic Auth header
- `CLIENT_SECRET_POST` - Client secret in request body
- `CLIENT_SECRET_JWT` - JWT assertion signed with client secret
- `PRIVATE_KEY_JWT` - JWT assertion signed with private key
- `PKCS7` - PKCS7 attested document
- `OIDC` - OIDC token
- `PCA` - X.509 certificate authentication

**Note**: `PLUGIN` authentication method is currently not supported for Token Exchange.

## Support and Feedback

- **Email**: cloudidaas@list.alibaba-inc.com
- **Issues**: Please submit an Issue for questions or suggestions

## License

This project is licensed under the [Apache License 2.0](LICENSE).
