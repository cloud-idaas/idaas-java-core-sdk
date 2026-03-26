# Environment Setup

[![Java Version](https://img.shields.io/badge/java-8%2B-blue)](https://www.java.com/)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](LICENSE)

Java SDK for IDaaS (Identity as a Service) M2M products, providing convenient machine-to-machine authentication capabilities for developers.

[简体中文](README_zh.md)

## Features

- **Multiple Authentication Methods**: Supports various M2M authentication methods including JWT Client Secret, JWT Private Key, OIDC Token, PKCS7 Attested Document, etc.
- **Plugin Extension**: Supports custom credential providers to meet special scenario requirements
- **Intelligent Caching Mechanism**: Built-in credential caching strategy with prefetch and stale value handling to reduce unnecessary network requests
- **Flexible Configuration**: Supports configuration files, environment variables, and programmatic configuration
- **Token Exchange (RFC 8693)**: Supports token exchange to obtain access tokens with different scopes or audiences, suitable for token downgrading and service-to-service call scenarios

## Requirements

- JDK 1.8 or above
- Maven

## Install Java SDK

Add the following dependency in `pom.xml` via Maven:

```xml
<dependency>
    <groupId>com.cloud-idaas</groupId>
    <artifactId>idaas-java-core-sdk</artifactId>
    <!-- Please replace with the latest SDK version -->
    <version>0.0.4-beta</version>
</dependency>
```

[Latest SDK Version](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-core-sdk)

For scenarios like Function Compute (FC), IDaaS supports OpenAPI authentication using Alibaba Cloud credentials (AK/SK, STS) to obtain M2M client tokens. You also need to add the Alibaba Cloud authentication extension plugin in `pom.xml`:

```xml
<dependency>
    <groupId>com.cloud-idaas</groupId>
    <artifactId>idaas-java-core-alibabacloud-authentication-plugin</artifactId>
    <!-- Please replace with the latest version of Alibaba Cloud authentication extension plugin -->
    <version>0.0.1-beta</version>
</dependency>
```

[Latest Alibaba Cloud Authentication Plugin Version](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-core-alibabacloud-authentication-plugin)

## Specify Configuration File

The default path for the configuration file is: `~/.cloud_idaas/client-config.json`. If not explicitly specified, the configuration file will be loaded from this path by default.

You can specify the configuration file path via Java system property or environment variable:

- Java system property name: `cloud_idaas_config_path`
- Environment variable name: `CLOUD_IDAAS_CONFIG_PATH`

### Java System Property Example

```
-Dcloud_idaas_config_path=/.../client-config.json

# In SpringBoot projects, the configuration file can be placed in src/main/resources/ and specified via classpath
-Dcloud_idaas_config_path=classpath:client-config.json
```

### Environment Variable Example

```
CLOUD_IDAAS_CONFIG_PATH=/.../client-config.json
```

## Configuration File Description

Configuration file example:

```json
{
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
    "issuer": "https://xxx/api/v2/iauths_system/oauth2",
    "tokenEndpoint": "https://xxx/api/v2/iauths_system/oauth2/token",
    "scope": "api.example.com|read:file",
    "openApiEndpoint": "eiam.[region_id].aliyuncs.com",
    "developerApiEndpoint": "eiam-developerapi.[region_id].aliyuncs.com",
    "authnConfiguration": {
        "identityType": "CLIENT",
        "authnMethod": "CLIENT_SECRET_POST",
        "clientSecretEnvVarName": "IDAAS_CLIENT_SECRET"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

### Parameter Description

| Field Name | Description |
|------------|-------------|
| idaasInstanceId | Required, IDaaS EIAM instance ID. |
| clientId | Required, IDaaS application ID, can be viewed in the corresponding IDaaS application. |
| issuer | Required, IDaaS EIAM instance Issuer endpoint, can be viewed in any M2M application under the IDaaS EIAM instance. |
| tokenEndpoint | Required, IDaaS EIAM instance token endpoint, can be viewed in any M2M application under the IDaaS EIAM instance. |
| scope | Required, specifies the audience identifier and permission identifier of the M2M server application to access, format is `audience\|scope`. <br>For scenarios of obtaining STS Token or credentials of RAM roles hosted in IDaaS, it is fixed to `urn:cloud:idaas:pam\|.all`, representing the built-in scope of IDaaS. |
| openApiEndpoint | Optional, IDaaS OpenAPI address, used when using OpenAPI authentication. Service address can be obtained from [IDaaS EIAM - Alibaba Cloud OpenAPI Developer Portal](https://api.aliyun.com/product/Eiam). <br>If the application is deployed in Alibaba Cloud VPC and in the same region as the IDaaS instance, it can be accessed via intranet VPC address, see VPC address in Alibaba Cloud OpenAPI Developer Portal. |
| developerApiEndpoint | Optional, IDaaS DeveloperAPI address, used when obtaining STS Token or credentials of RAM roles hosted in IDaaS. Service address can be obtained from [IDaaS EIAM - Alibaba Cloud OpenAPI Developer Portal](https://api.aliyun.com/product/Eiam). <br>If the application is deployed in Alibaba Cloud VPC and in the same region as the IDaaS instance, it can be accessed via intranet VPC address, see VPC address in Alibaba Cloud OpenAPI Developer Portal. |
| authnConfiguration | - identityType: Optional, default value is `CLIENT`, currently only supports `CLIENT`, meaning M2M client application authenticates with machine identity. <br>- authnMethod: Required, authentication method. Different authentication methods require different authnConfiguration fields, see **authnMethod Field Values and authnConfiguration Field Mapping** for details. |
| httpConfiguration | HTTP protocol related configuration, contains 2 fields: <br>- connectTimeout: Optional, maximum wait time for client to establish connection with server (milliseconds), default is 5000. <br>- readTimeout: Optional, maximum wait time for client to wait for server data after connection is established (milliseconds), default is 10000. |

### authnMethod Field Values and authnConfiguration Field Mapping

| authnMethod | Required authnConfiguration Field | authnConfiguration Field Description                                                                                                                                                                                                                                                                                     |
|-------------|-----------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| CLIENT_SECRET_BASIC | clientSecretEnvVarName | Field value is the environment variable name, through which the M2M client application's Client Secret is read.                                                                                                                                                                                                          |
| CLIENT_SECRET_POST | clientSecretEnvVarName | Field value is the environment variable name, through which the M2M client application's Client Secret is read.                                                                                                                                                                                                          |
| CLIENT_SECRET_JWT | clientSecretEnvVarName | Field value is the environment variable name, through which the M2M client application's Client Secret is read.                                                                                                                                                                                                          |
| PRIVATE_KEY_JWT | privateKeyEnvVarName | Field value is the environment variable name, through which the M2M client application's Private Key is read.                                                                                                                                                                                                            |
| PKCS7 | applicationFederatedCredentialName | PKCS7 federated credential name. Federated trust source needs to be created in advance, related configuration can be referenced: Create Federated Credential.                                                                                                                                                            |
| PKCS7 | clientDeployEnvironment | Deployment environment, currently only supports `ALIBABA_CLOUD_ECS`.                                                                                                                                                                                                                                                     |
| OIDC | applicationFederatedCredentialName | OIDC federated credential name. Federated trust source needs to be created in advance, related configuration can be referenced: Create Federated Credential.                                                                                                                                                             |
| OIDC | clientDeployEnvironment | Deployment environment, currently only supports `KUBERNETES`.                                                                                                                                                                                                                                                            |
| OIDC | oidcTokenFilePath | Optional, used to specify the Service Account Token file path. If not configured, it will try to read the path through the environment variable specified by oidcTokenFilePathEnvVarName; if both are not set, it will use the Kubernetes standard path by default: /var/run/secrets/kubernetes.io/serviceaccount/token. |
| OIDC | oidcTokenFilePathEnvVarName | Optional, takes effect when oidcTokenFilePath is not specified, field value is the environment variable name, through which the Service Account Token file path is read.                                                                                                                                                 |
| PCA | applicationFederatedCredentialName | PCA federated credential name. Federated trust source needs to be created in advance, related configuration can be referenced: Create Federated Credential.                                                                                                                                                              |
| PCA | clientX509Certificate | End certificate, format: <br>`-----BEGIN CERTIFICATE----- xxx -----END CERTIFICATE-----`                                                                                                                                                                                                                                 |
| PCA | x509CertChains | Intermediate certificate list, multiple certificates are concatenated with newlines, format: <br>`-----BEGIN CERTIFICATE----- xxx -----END CERTIFICATE----- -----BEGIN CERTIFICATE----- xxx -----END CERTIFICATE-----`                                                                                                   |
| PCA | privateKeyEnvVarName | Field value is the environment variable name, through which the M2M client application's Private Key is read.                                                                                                                                                                                                            |
| PLUGIN | pluginName | pluginName is the extension plugin name, currently only supports `alibabacloudPluginCredentialProvider`, which is Alibaba Cloud OpenAPI authentication method.<br> *Configure RAM permissions, refer to [Alibaba Cloud OpenAPI Authentication](https://help.aliyun.com/zh/idaas/eiam/developer-reference/alibaba-cloud-openapi-authentication?spm=a2c4g.11186623.help-menu-111120.d_4_2_2_1.75c47c06DzCKYj&scm=20140722.H_3024776._.OR_help-T_cn~zh-V_1).                                                                                                                                                     |

## Configuration Examples

Configuration examples for different authentication methods.

### Client Secret Credential Configuration Example

```json
{
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
    "issuer": "https://xxx/api/v2/iauths_system/oauth2",
    "tokenEndpoint": "https://xxx/api/v2/iauths_system/oauth2/token",
    "scope": "api.example.com|read:file",
    "authnConfiguration": {
        "identityType": "CLIENT",
        "authnMethod": "CLIENT_SECRET_BASIC",
        "clientSecretEnvVarName": "IDAAS_CLIENT_SECRET"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

### Private Key Credential Configuration Example

```json
{
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
    "issuer": "https://xxx/api/v2/iauths_system/oauth2",
    "tokenEndpoint": "https://xxx/api/v2/iauths_system/oauth2/token",
    "scope": "api.example.com|read:file",
    "authnConfiguration": {
        "identityType": "CLIENT",
        "authnMethod": "PRIVATE_KEY_JWT",
        "privateKeyEnvVarName": "ENV_PRIVATE_KEY"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

### PKCS7 Federated Credential Configuration Example

```json
{
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
    "issuer": "https://xxx/api/v2/iauths_system/oauth2",
    "tokenEndpoint": "https://xxx/api/v2/iauths_system/oauth2/token",
    "scope": "api.example.com|read:file",
    "authnConfiguration": {
        "identityType": "CLIENT",
        "authnMethod": "PKCS7",
        "applicationFederatedCredentialName": "your_pkcs7_federated_credential_name",
        "clientDeployEnvironment": "ALIBABA_CLOUD_ECS"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

### OIDC Federated Credential Configuration Example

```json
{
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
    "issuer": "https://xxx/api/v2/iauths_system/oauth2",
    "tokenEndpoint": "https://xxx/api/v2/iauths_system/oauth2/token",
    "scope": "api.example.com|read:file",
    "authnConfiguration": {
        "identityType": "CLIENT",
        "authnMethod": "OIDC",
        "applicationFederatedCredentialName": "your_oidc_federated_credential_name",
        "clientDeployEnvironment": "KUBERNETES",
        "oidcTokenFilePath": "/var/run/secrets/.../token",
        "oidcTokenFilePathEnvVarName": "ENV_OIDC_TOKEN_FILE_PATH"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

### PCA Federated Credential Configuration Example

```json
{
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
    "issuer": "https://xxx/api/v2/iauths_system/oauth2",
    "tokenEndpoint": "https://xxx/api/v2/iauths_system/oauth2/token",
    "scope": "api.example.com|read:file",
    "authnConfiguration": {
        "identityType": "CLIENT",
        "authnMethod": "PCA",
        "applicationFederatedCredentialName": "your_pca_federated_credential_name",
        "clientX509Certificate": "-----BEGIN CERTIFICATE-----\nxxxxxx\n-----END CERTIFICATE-----",
        "x509CertChains": "-----BEGIN CERTIFICATE-----\nxxxxxx\n-----END CERTIFICATE-----\n-----BEGIN CERTIFICATE-----\nxxxxxx\n-----END CERTIFICATE-----",
        "privateKeyEnvVarName": "ENV_PRIVATE_KEY"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

### OpenAPI Authentication Configuration Example

```json
{
    "idaasInstanceId": "idaas_xxx",
    "clientId": "app_xxx",
    "issuer": "https://xxx/api/v2/iauths_system/oauth2",
    "tokenEndpoint": "https://xxx/api/v2/iauths_system/oauth2/token",
    "scope": "api.example.com|read:file",
    "openApiEndpoint": "eiam.[region_id].aliyuncs.com",
    "authnConfiguration": {
        "identityType": "CLIENT",
        "authnMethod": "PLUGIN",
        "pluginName": "alibabacloudPluginCredentialProvider"
    },
    "httpConfiguration": {
        "connectTimeout": 5000,
        "readTimeout": 10000
    }
}
```

## Code Integration

### SDK Initialization

Read the configuration file specified during the environment setup phase and complete the IDaaS configuration initialization.

```java
IDaaSCredentialProviderFactory.init();
```

> **Important**:
> - All SDK features depend on the `init()` initialization method, so the `init()` method must be completed first, otherwise getting `IDaaSCredentialProvider` will fail and cause business interruption.
> - Initialization will check the configuration and obtain the Access Token for the scope specified in the configuration file. If the configuration is missing or incorrect, causing the Access Token acquisition to fail, it will directly report an error and cause business interruption.

### Get Access Token

1. Get IDaaS credentialProvider to obtain Access Token.

   - Get IDaaS credentialProvider through no-argument constructor to obtain Access Token for the scope specified in the configuration file:

     ```java
     IDaaSCredentialProvider credentialProvider = IDaaSCredentialProviderFactory.getIDaaSCredentialProvider();
     ```

   - Get IDaaS credentialProvider through parameterized constructor, scope can be specified to obtain Access Token for the specified scope. Format is `audience|scope`, corresponding to the audience identifier and permission identifier of the M2M server application to access:

     ```java
     IDaaSCredentialProvider credentialProvider = IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(scope);
     ```

2. Access Token is of Bearer type, obtained through the `getBearerToken()` method of credentialProvider:

   ```java
   String accessToken = credentialProvider.getBearerToken();
   ```

### Code Example

For complete examples, see the `samples/` directory:

- `samples/ObtainTokenSample.java` - Get Access Token example

```java
public class ObtainTokenSample {

    public static void main(String[] args) {

        // Initialize the factory with configuration
        IDaaSCredentialProviderFactory.init();

        // Get credential provider with scope from config file
        // IDaaSCredentialProvider credentialProvider =
        //         IDaaSCredentialProviderFactory.getIDaaSCredentialProvider();

        // scope format: <audience>|<scope>
        String scope = "api.example.com|read:file";
        // Get credential provider with scope specified by parameter
        IDaaSCredentialProvider credentialProvider =
                IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(scope);

        String accessToken = credentialProvider.getBearerToken();

        System.out.println(accessToken);
    }
}
```

## Token Exchange

Token Exchange (RFC 8693) allows you to exchange a subject token for a new access token with different scopes or audiences. This is useful for token downgrading and service-to-service access scenarios.

### Basic Token Exchange

For complete examples, see the `samples/` directory:

- `samples/TokenExchangeSample.java` - Token exchange example

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

        // Get token exchange credential provider with scope from config file
        // IDaaSTokenExchangeCredentialProvider tokenExchangeCredentialProvider =
        //                IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider();

        // scope format: <audience>|<scope>
        String scope = "api.example.com|read:file";
        // Get token exchange credential provider with scope specified by parameter
        IDaaSTokenExchangeCredentialProvider tokenExchangeCredentialProvider =
                IDaaSCredentialProviderFactory.getIDaaSTokenExchangeCredentialProvider(scope);

        // Perform token exchange
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

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| subject_token | String | Yes | The token to exchange |
| subject_token_type | String | Yes | Subject token type (e.g., `urn:ietf:params:oauth:token-type:access_token`) |
| requested_token_type | String | No | Requested token type (default is access token) |

### Use Cases

1. **Token Downgrading**: Exchange a token with broader permissions for a token with limited scope
2. **Service-to-Service Access**: Pass the same user identity between services to obtain the required access token

## Support and Feedback

- **Email**: cloudidaas@list.alibaba-inc.com
- **Issue Feedback**: If you have questions or suggestions, please submit an Issue

## License

This project is licensed under the [Apache License 2.0](LICENSE).
