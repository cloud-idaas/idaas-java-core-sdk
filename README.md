# idaas-java-core-sdk

[![Java Version](https://img.shields.io/badge/java-8%2B-blue)](https://www.java.com/)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](LICENSE)
[![Development Status](https://img.shields.io/badge/status-Beta-orange)](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-core-sdk)

Java SDK for IDaaS (Identity as a Service) M2M product, providing developers with convenient machine-to-machine authentication capabilities.

## Features

- **Multiple Authentication Methods**: Supports JWT Client Secret, JWT Private Key, OIDC Token, PKCS7 Attested Document, and other M2M authentication methods
- **Intelligent Caching Mechanism**: Built-in credential caching strategy with prefetch and stale value handling to reduce unnecessary network requests
- **Flexible Configuration**: Supports configuration files, environment variables, and programmatic configuration
- **Plugin Extensions**: Supports custom credential providers for special scenarios
- **Cloud-Native Support**: Built-in attested document support for Alibaba Cloud ECS and Alibaba Cloud ACK

## Requirements

- Java >= 8
- Maven >= 3.6

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.cloud-idaas</groupId>
    <artifactId>idaas-java-core-sdk</artifactId>
    <version>0.0.3-beta</version>
</dependency>
```

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

// Initialize (automatically loads configuration file)
IDaaSCredentialProviderFactory.init();

// Get credential provider
IDaaSCredentialProvider credentialProvider = IDaaSCredentialProviderFactory.getIDaaSCredentialProvider();

// Get access token
String accessToken = credentialProvider.getBearerToken();
System.out.println("Access Token: " + accessToken);
```

## Configuration Details

### Configuration File Paths

The SDK searches for configuration files in the following order:

1. Java System Property path: `-Dcloud_idaas_config_path=/.../client-config.json`
2. Environment variable path: `CLOUD_IDAAS_CONFIG_PATH=/.../client-config.json`
3. Default path: `~/.cloud_idaas/client-config.json`

### Complete Configuration Example

```json
{
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
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
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
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
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
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
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
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
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
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
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
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
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
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

## Support and Feedback

- **Email**: cloudidaas@list.alibaba-inc.com
- **Issues**: Please submit an Issue for questions or suggestions

## License

This project is licensed under the [Apache License 2.0](LICENSE).
