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
    <version>0.0.8-beta</version>
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

You can specify the configuration file path via Java system property, environment variable, or initialization parameter:

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

### Initialization Parameter Example

```java
IDaaSCredentialProviderFactory.init("/.../client-config.json");
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
| authnConfiguration | - identityType: Optional, defaults to `CLIENT`; exact value `CLIENT` selects machine authentication and `HUMAN` selects interactive login. <br>- authnMethod: Required, authentication method. Different authentication methods require different authnConfiguration fields, see **authnMethod Field Values and authnConfiguration Field Mapping** for details. |
| httpConfiguration | HTTP protocol related configuration, contains 2 fields: <br>- connectTimeout: Optional, maximum wait time for client to establish connection with server (milliseconds), default is 5000. <br>- readTimeout: Optional, maximum wait time for client to wait for server data after connection is established (milliseconds), default is 10000. |

### authnMethod Field Values and authnConfiguration Field Mapping

| authnMethod | Required authnConfiguration Field | authnConfiguration Field Description                                                                                                                                                                                                                                                                                     |
|-------------|-----------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| CLIENT_SECRET_BASIC | clientSecretEnvVarName | Field value is the environment variable name, through which the M2M client application's Client Secret is read.                                                                                                                                                                                                          |
| CLIENT_SECRET_POST | clientSecretEnvVarName | Field value is the environment variable name, through which the M2M client application's Client Secret is read.                                                                                                                                                                                                          |
| CLIENT_SECRET_JWT | clientSecretEnvVarName | Field value is the environment variable name, through which the M2M client application's Client Secret is read.                                                                                                                                                                                                          |
| PRIVATE_KEY_JWT | privateKeyEnvVarName | Field value is the environment variable name, through which the M2M client application's Private Key is read.                                                                                                                                                                                                            |
| PKCS7 | applicationFederatedCredentialName | PKCS7 federated credential name. Federated trust source needs to be created in advance, related configuration can be referenced: Create Federated Credential.                                                                                                                                                            |
| PKCS7 | clientDeployEnvironment | Deployment environment; supports `ALIBABA_CLOUD_ECS` and `AWS_EC2`. |
| OIDC | applicationFederatedCredentialName | OIDC federated credential name. Federated trust source needs to be created in advance, related configuration can be referenced: Create Federated Credential.                                                                                                                                                             |
| OIDC | clientDeployEnvironment | Deployment environment; supports `KUBERNETES`, `AWS_EKS`, `ALIBABA_CLOUD_ACK`, `GOOGLE_VM` and `COMPUTER` (reuses an initialized human interactive-login provider). |
| OIDC | oidcTokenFilePath | Optional token path for file-backed OIDC identities. `KUBERNETES` falls back from an explicit path to `oidcTokenFilePathEnvVarName`, then `/var/run/secrets/kubernetes.io/serviceaccount/token`. EKS/ACK use their own platform variables and default paths; see “EKS configuration and factory retry” below. GCP uses metadata and COMPUTER reuses human login; neither uses this file setting. |
| OIDC | oidcTokenFilePathEnvVarName | Optional for file-backed Kubernetes/EKS/ACK identities only. When `oidcTokenFilePath` is absent, names the environment variable containing the token path. Missing-variable handling and subsequent fallback depend on the environment; see the path-resolution details below. |
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

## EKS configuration and factory retry

GCP VM OIDC authentication uses the configured issuer's origin (`scheme://host[:port]`) as its audience, excluding path, query and fragment.
Allow this value in the IDaaS GCP OIDC trust source's audience list; configurations accepting only the IDaaS instance ID must add this origin.

AWS EC2 identity authentication obtains `/latest/dynamic/instance-identity/rsa2048`
through IMDSv2 and removes Base64 whitespace before submission. Configure the
IDaaS trust source with the AWS RSA-2048 certificate for the instance's Region.

For EKS OIDC authentication, use `"clientDeployEnvironment": "AWS_EKS"`.
The removed `AWS_ESK` value produces an explicit migration hint during configuration-file initialization.
Master/0.0.7 does not support the current EKS configuration and is not a direct rollback target that preserves EKS support.
Keep a verified EKS-capable build; switching to generic Kubernetes OIDC requires separate validation of token paths,
mounts, IDaaS trust settings and audience.
Both ordinary credentials and Token Exchange use the same EKS path resolution:
`oidcTokenFilePath`, then `oidcTokenFilePathEnvVarName`, then
`AWS_WEB_IDENTITY_TOKEN_FILE`, then the default EKS token path. An explicitly
configured path environment variable must exist and contain a nonblank value.
ACK uses the same order with `ALIBABA_CLOUD_OIDC_TOKEN_FILE` and its own default path.
Direct EKS/ACK builders also resolve explicit token paths before platform environment variables and platform defaults;
blank platform variables are treated as unset. Existing Kubernetes path fallback is unchanged.

When `identityType` is omitted it still defaults to `CLIENT`; when present it must be exactly `CLIENT` or `HUMAN`.
Previously lowercase, unknown string or null values deserialized to null and skipped the HUMAN branch;
requests could still proceed if the remaining settings supported machine authentication. Such configurations
are now rejected during initialization. This check applies only at the factory initialization boundary
(file-based `init` and null-enum validation in `init(config)`); generic Gson deserialization is unchanged
and still maps unknown enum names to null.
`authnMethod` also requires an exact enum name. Invalid names could previously fail with an NPE at
provider creation; initialization now reports a configuration error instead. Omission still keeps the `NONE` default.
PKCS7/OIDC case/whitespace variants of known environment names
receive exact-name hints without normalization or acceptance. Other authentication methods continue ignoring
irrelevant environment fields, except for the removed `AWS_ESK` migration diagnostic.
Failed file initialization can be retried with the existing `init()` call.
`reset()` discards factory state; providers already returned remain usable.
After re-initialization, SDK OAuth and metadata requests use the new connect/read timeouts.
Existing raw HTTP client references and in-flight requests are not forcibly closed.
PKCS7 factory configuration accepts `ALIBABA_CLOUD_ECS` and `AWS_EC2`; other environments
are rejected during initialization rather than failing later with a missing provider.
Default STRICT credential caching rejects tokens past their known expiry, including refresh
failures and lock timeouts. Direct Supplier users retain the existing ALLOW option.
A newly fetched value already inside its prefetch window does not trigger another immediate prefetch for that version.
It is reused until stale time, when normal refresh still runs; failed-fetch retries and hard-expiry checks remain active.
Same-thread recursive creation of the same provider, including a scope dependency cycle, fails fast instead of self-waiting;
acyclic delegation to a different scope remains supported. Configuration getters read a validated snapshot without waiting for file-initialization warm-up, including from plugin worker threads. Failed warm-up clears this read snapshot; subsequent reads fail until a validated retry configuration is published. A successful configuration query does not imply authentication success.
File-based PLUGIN warm-up releases the factory monitor and supports acyclic different-scope provider delegation on existing worker threads. Provider lookup is therefore allowed during PLUGIN warm-up, as on master, and is not proof that the outer initialization has completed. Other authentication methods retain their initialization barrier.
Concurrent `init` calls wait outside the monitor for PLUGIN warm-up to finish; callbacks must not synchronously wait for a worker calling `init`, or introduce cross-thread scope cycles. `reset` can supersede warm-up; old work cannot commit into the new generation.
On a cold start with no successful HTTP configuration, workers may use an isolated candidate client. Failed warm-up detaches that candidate from future global lookups; raw references already obtained are not forcibly closed or revoked. When a successful HTTP configuration already exists, workers and other threads retain it until the new initialization commits. Initialization-thread timeout overrides remain thread-local. `getHttpConfig()` queries validated factory settings.
Transport settings are copied after validation and the same frozen settings are used for warm-up and successful commit; mutating query results does not reconfigure
transport. Use `reset()` followed by `init` to apply new timeouts. Transport snapshots and warm-up scopes are internal SDK bridges.
Initialization supports both `init(String)` and `init(IDaaSClientConfig)`;
use `init()` for the default configuration path instead of an ambiguous `init(null)` call.

## Support and Feedback

- **Email**: cloudidaas@list.alibaba-inc.com
- **Issue Feedback**: If you have questions or suggestions, please submit an Issue

## License

This project is licensed under the [Apache License 2.0](LICENSE).
