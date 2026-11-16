# 环境准备

[![Java Version](https://img.shields.io/badge/java-8%2B-blue)](https://www.java.com/)
[![License](https://img.shields.io/badge/license-Apache%202.0-green.svg)](LICENSE)

IDaaS（身份即服务）M2M 产品的 Java SDK，为开发者提供便捷的机器对机器认证能力。

[English](README.md)

## 功能特性

- **多种认证方式**：支持 JWT Client Secret、JWT 私钥、OIDC Token、PKCS7 认证文档等多种 M2M 认证方式
- **插件扩展**：支持自定义凭证提供器，满足特殊场景需求
- **智能缓存机制**：内置凭证缓存策略，支持预取和过期值处理，减少不必要的网络请求
- **灵活配置**：支持配置文件、环境变量和编程式配置
- **令牌交换（RFC 8693）**：支持令牌交换以获取不同 scope 或 audience 的访问令牌，适用于令牌降权和服务间调用场景

## 环境要求

- 安装 JDK 1.8 或以上版本
- 安装 Maven

## 安装 Java SDK

通过 Maven 方式引入依赖，在 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>com.cloud-idaas</groupId>
    <artifactId>idaas-java-core-sdk</artifactId>
    <!--以下版本号请替换为 SDK 的最新版本号-->
    <version>0.0.8-beta</version>
</dependency>
```

[SDK 最新版本](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-core-sdk)

函数计算（FC）等场景下，IDaaS 支持 OpenAPI 认证方式，使用阿里云的身份凭证（AK/SK、STS）获取 M2M 客户端令牌，还需要在 `pom.xml` 中添加阿里云认证方式扩展插件：

```xml
<dependency>
    <groupId>com.cloud-idaas</groupId>
    <artifactId>idaas-java-core-alibabacloud-authentication-plugin</artifactId>
    <!--以下版本号请替换为阿里云认证扩展插件的最新版本号-->
    <version>0.0.1-beta</version>
</dependency>
```

[阿里云认证扩展插件最新版本](https://mvnrepository.com/artifact/com.cloud-idaas/idaas-java-core-alibabacloud-authentication-plugin)

## 指定配置文件

配置文件的默认路径：`~/.cloud_idaas/client-config.json`。如未明确指定，则默认从该路径下获取配置文件。

可以通过 Java 系统属性、环境变量或初始化参数指定配置文件路径：

- Java 系统属性名：`cloud_idaas_config_path`
- 环境变量名：`CLOUD_IDAAS_CONFIG_PATH`

### Java 系统属性配置示例

```
-Dcloud_idaas_config_path=/.../client-config.json

# 在 SpringBoot 项目中，配置文件可放在 src/main/resources/ 下，直接通过类路径指定
-Dcloud_idaas_config_path=classpath:client-config.json
```

### 环境变量配置示例

```
CLOUD_IDAAS_CONFIG_PATH=/.../client-config.json
```

### 初始化参数示例

```java
IDaaSCredentialProviderFactory.init("/.../client-config.json");
```

## 配置文件说明

配置文件示例如下：

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

### 参数说明

| 字段名 | 备注                                                                                                                                                                                                                                   |
|--------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| idaasInstanceId | 必填，IDaaS EIAM 的实例 ID。                                                                                                                                                                                                                |
| clientId | 必填，IDaaS 应用的应用 ID，可在对应 IDaaS 应用中查看。                                                                                                                                                                                                  |
| issuer | 必填，IDaaS EIAM 实例的 Issuer 端点，可在 IDaaS EIAM 实例下的任意 M2M 应用中查看。                                                                                                                                                                          |
| tokenEndpoint | 必填，IDaaS EIAM 实例的令牌端点，可在 IDaaS EIAM 实例下的任意 M2M 应用中查看。                                                                                                                                                                                |
| scope | 必填，指定要访问的 M2M 服务端应用的受众标识和权限标识，格式为 `受众标识\|权限标识`。<br>在获取托管到 IDaaS 的 RAM 角色的 STS Token 或凭据的场景下，固定为 `urn:cloud:idaas:pam\|.all`，表示 IDaaS 内置的 scope。                                                                                      |
| openApiEndpoint | 可选，IDaaS 的 OpenAPI 地址，使用 OpenAPI 认证时使用。服务地址从 [云身份服务 (IDaaS EIAM)-阿里云OpenAPI开发者门户](https://api.aliyun.com/product/Eiam) 中获取。<br>若应用部署在阿里云 VPC 中，且与 IDaaS 实例在同一地域，则可以通过内网 VPC 地址访问，见阿里云 OpenAPI 开发者门户中的 VPC 地址。                        |
| developerApiEndpoint | 可选，IDaaS 的 DeveloperAPI 地址，获取托管到 IDaaS 的 RAM 角色的 STS Token 或凭据时使用。服务地址从 [云身份服务 (IDaaS EIAM)-阿里云OpenAPI开发者门户](https://api.aliyun.com/product/Eiam) 中获取。<br>若应用部署在阿里云 VPC 中，且与 IDaaS 实例在同一地域，则可以通过内网 VPC 地址访问，见阿里云 OpenAPI 开发者门户中的 VPC 地址。 |
| authnConfiguration | - identityType：可选，默认值为 `CLIENT`；精确值 `CLIENT` 表示机器身份认证，`HUMAN` 表示人工登录。<br>- authnMethod：必填，认证方式。不同认证方式所需的 authnConfiguration 字段不同，详细对应关系参见 **authnMethod 字段值和 authnConfiguration 字段对应关系**。                                                |
| httpConfiguration | http 协议相关配置，包含 2 个字段：<br>- connectTimeout：可选，客户端与服务端建立连接的最大等待时间（毫秒），默认为 5000。<br>- readTimeout：可选，连接建立后，客户端等待服务端返回数据的最大等待时间（毫秒），默认为 10000。                                                                                                   |

### authnMethod 字段值和 authnConfiguration 字段对应关系

| authnMethod | 需要的 authnConfiguration 字段 | authnConfiguration 字段说明                                                                                                                                                                                                                                                                                                       |
|-------------|-------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| CLIENT_SECRET_BASIC | clientSecretEnvVarName | 字段值为环境变量名称，通过该环境变量读取 M2M 客户端应用的 Client Secret。                                                                                                                                                                                                                                                                                |
| CLIENT_SECRET_POST | clientSecretEnvVarName | 字段值为环境变量名称，通过该环境变量读取 M2M 客户端应用的 Client Secret。                                                                                                                                                                                                                                                                                |
| CLIENT_SECRET_JWT | clientSecretEnvVarName | 字段值为环境变量名称，通过该环境变量读取 M2M 客户端应用的 Client Secret。                                                                                                                                                                                                                                                                                |
| PRIVATE_KEY_JWT | privateKeyEnvVarName | 字段值为环境变量名称，通过该环境变量读取 M2M 客户端应用的 Private Key。                                                                                                                                                                                                                                                                                  |
| PKCS7 | applicationFederatedCredentialName | PKCS7 的联邦凭证名称。需提前创建联邦信任源，相关配置可参考：创建联邦凭证。                                                                                                                                                                                                                                                                                      |
| PKCS7 | clientDeployEnvironment | 部署环境，支持 `ALIBABA_CLOUD_ECS`、`AWS_EC2`。 |
| OIDC | applicationFederatedCredentialName | OIDC 的联邦凭证名称。需提前创建联邦信任源，相关配置可参考：创建联邦凭证。                                                                                                                                                                                                                                                                                       |
| OIDC | clientDeployEnvironment | 部署环境，支持 `KUBERNETES`、`AWS_EKS`、`ALIBABA_CLOUD_ACK`、`GOOGLE_VM`、`COMPUTER`（复用已初始化的人工登录 Provider）。 |
| OIDC | oidcTokenFilePath | 可选，文件型 OIDC 身份的 Token 路径。`KUBERNETES` 按显式路径、`oidcTokenFilePathEnvVarName`、默认路径 `/var/run/secrets/kubernetes.io/serviceaccount/token` 回退。EKS/ACK 使用各自的平台变量和默认路径，详见下文“EKS 配置与工厂重试”。GCP 使用元数据服务，COMPUTER 复用人工登录，均不使用该文件配置。 |
| OIDC | oidcTokenFilePathEnvVarName | 可选，仅用于 Kubernetes/EKS/ACK 等文件型身份；未指定 `oidcTokenFilePath` 时，使用该字段指定的环境变量读取 Token 路径。各环境的缺失变量处理与后续回退顺序见下文说明。 |
| PCA | applicationFederatedCredentialName | PCA 的联邦凭证名称。需提前创建联邦信任源，相关配置可参考：创建联邦凭证。                                                                                                                                                                                                                                                                                        |
| PCA | clientX509Certificate | 终端证书，格式为：<br>`-----BEGIN CERTIFICATE----- xxx -----END CERTIFICATE-----`                                                                                                                                                                                                                                                      |
| PCA | x509CertChains | 中间证书列表，多张证书使用换行拼接，格式为：<br>`-----BEGIN CERTIFICATE----- xxx -----END CERTIFICATE----- -----BEGIN CERTIFICATE----- xxx -----END CERTIFICATE-----`                                                                                                                                                                               |
| PCA | privateKeyEnvVarName | 字段值为环境变量名称，通过该环境变量读取 M2M 客户端应用的 Private Key。                                                                                                                                                                                                                                                                                  |
| PLUGIN | pluginName | pluginName 为扩展插件名，目前只支持 `alibabacloudPluginCredentialProvider`，即阿里云 OpenAPI 认证方式。<br>*配置 RAM 权限，参考[阿里云 OpenAPI 认证](https://help.aliyun.com/zh/idaas/eiam/developer-reference/alibaba-cloud-openapi-authentication?spm=a2c4g.11186623.help-menu-111120.d_4_2_2_1.75c47c06DzCKYj&scm=20140722.H_3024776._.OR_help-T_cn~zh-V_1)。 |

## 配置参数示例

不同认证方式下的具体的配置示例。

### Client Secret 凭证配置示例

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

### 公私钥凭证配置示例

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

### PKCS7 联邦凭证配置示例

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

### OIDC 联邦凭证配置示例

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

### PCA 联邦凭证配置示例

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

### OpenAPI 认证配置参考

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

## 代码集成

### SDK 初始化

读取用户在环境准备阶段指定的配置文件，完成 IDaaS 配置的初始化。

```java
IDaaSCredentialProviderFactory.init();
```

> **重要提示**：
> - SDK 功能均依赖 `init()` 初始化方法，因此 `init()` 初始化方法必须先完成，否则获取 `IDaaSCredentialProvider` 等均会报错，导致业务中断。
> - 初始化会检查配置并获取访问配置文件中指定的 scope 的 Access Token，若配置缺失或配置错误导致获取 Access Token 失败，会直接报错，导致业务中断。

### 获取 Access Token

1. 获取 IDaaS credentialProvider，用于获取 Access Token。

   - 通过无参构造方法获取 IDaaS credentialProvider，获取访问配置文件中指定的 scope 的 Access Token：

     ```java
     IDaaSCredentialProvider credentialProvider = IDaaSCredentialProviderFactory.getIDaaSCredentialProvider();
     ```

   - 通过有参构造方法获取 IDaaS credentialProvider，scope 可自行指定，获取访问指定的 scope 的 Access Token。格式为 `受众标识|权限标识`，对应所要访问的 M2M 服务端应用的受众标识和权限标识：

     ```java
     IDaaSCredentialProvider credentialProvider = IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(scope);
     ```

2. Access Token 是 Bearer 类型，通过 credentialProvider 的 `getBearerToken()` 方法获取：

   ```java
   String accessToken = credentialProvider.getBearerToken();
   ```

### 代码示例
完整示例请参见 `samples/` 目录：

- `samples/ObtainTokenSample.java` - 获取 Access Token 示例

```java
public class ObtainTokenSample{

    public static void main(String[] args) {

        // Initialize the factory with configuration
        IDaaSCredentialProviderFactory.init();

        // Get credential provider with scope from config file
        // IDaaSCredentialProvider credentialProvider =
        //         IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(scope);

        // scope format: <audience>|<scope>
        String scope = "api.example.com|read:file";
        // Get credential provider with scope specified by parameter
        IDaaSCredentialProvider credentialProvider =
                IDaaSCredentialProviderFactory.getIDaaSCredentialProvider(scope);

        // perform token exchange
        String accessToken = credentialProvider.getBearerToken();

        System.out.println(accessToken);
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

## EKS 配置与工厂重试

GCP VM OIDC认证的audience取配置中issuer的origin（`scheme://host[:port]`），不包含路径、查询参数或片段。
IDaaS的GCP OIDC联邦信任源受众列表需允许此值；此前只允许IDaaS实例ID的配置需要增加该origin。

AWS EC2的PKCS7身份认证使用IMDSv2获取`/latest/dynamic/instance-identity/rsa2048`，
并在提交前移除Base64换行等空白；IDaaS信任源需配置实例所在区域的AWS RSA-2048证书。

EKS OIDC 认证使用 `"clientDeployEnvironment": "AWS_EKS"`。
旧配置中的 `AWS_ESK` 已移除，文件初始化时会提示迁移到 `AWS_EKS`。
master／0.0.7 不支持当前 EKS 配置，不能作为保留 EKS 能力的直接回滚目标；请保留已验证支持 EKS 的构建。
切换为原生 Kubernetes OIDC 是替代方案，需要另行验证 Token 路径、挂载、IDaaS 信任源及 audience。
普通凭证和 Token Exchange 统一按以下顺序选择文件：
`oidcTokenFilePath` → `oidcTokenFilePathEnvVarName` →
`AWS_WEB_IDENTITY_TOKEN_FILE` → EKS 默认路径。
显式配置的路径环境变量必须存在且非空。
ACK 使用相同顺序，平台变量为 `ALIBABA_CLOUD_OIDC_TOKEN_FILE`，并保留 ACK 默认路径。
直接使用 EKS/ACK builder 时，路径同样按显式 `oidcTokenFilePath`、对应平台环境变量、平台默认路径选择；空白平台变量按未设置处理。
历史 Kubernetes 路径回退不变。

`identityType` 省略时仍默认为 `CLIENT`；显式配置时必须使用精确的 `CLIENT` 或 `HUMAN`。
此前小写、未知字符串或 `null` 会被解析为 `null`，跳过 HUMAN 分支；其他配置满足机器认证条件时，仍可能继续认证。
升级后这类配置会在初始化阶段被拒绝，请先修正配置。该校验只发生在工厂初始化入口：配置文件 `init` 和 `init(config)` 的空枚举校验；
通用 Gson 解析行为未改变，未知枚举名仍会解析为 `null`。
`authnMethod` 也必须使用精确枚举名；此前非法枚举名可能在创建 Provider 时以 NPE 失败，现在初始化时明确报配置错误。省略时仍保留 `NONE` 默认值。
PKCS7/OIDC 下，环境名称的大小写、空白错误会提示正确拼写，但不会自动纠正或接受；其他认证方式继续忽略无关环境字段（已移除的 `AWS_ESK` 除外）。
文件初始化失败后，可继续使用原 `init()` 重试。
`reset()` 清理工厂状态；已返回给调用方的 Provider 仍可使用。
重新初始化后，SDK OAuth 和元数据请求使用新的连接/读取超时；不强制关闭旧 HTTP Client 引用及在途请求。
PKCS7 工厂配置仅接受 `ALIBABA_CLOUD_ECS`、`AWS_EC2`，其他环境在初始化时明确报错。
默认STRICT凭证缓存受真实Token过期时间约束；刷新失败或等待锁超时不会返回已过期Token。
直接使用缓存Supplier时，原有ALLOW选项保持可用。
若新取回的值已经处于预取窗口，不会立即为该版本再次预取；仍未到软刷新时间时复用该值，到 `staleTime` 后照常刷新，失败后的重试及硬过期检查保持有效。
同一线程递归创建同一个 Provider（包括 scope 依赖环）会明确报错，避免自等待；不同 scope 的无环依赖仍可使用。
配置 getter 读取已校验的独立快照，不等待文件初始化预热，插件工作线程也可读取；若预热失败，读快照会撤销，下一次已校验配置发布前查询会报未初始化。配置查询成功不表示预热或认证成功。
文件方式的 `PLUGIN` 预热会释放工厂锁，支持通过已有工作线程获取另一个无环 scope 的 Provider；因此 PLUGIN 预热期间允许查询 Provider，与 master 一致，但查询成功不表示外层初始化完成。其他认证方式保留初始化等待屏障。
并发 `init` 在工厂锁外等待 PLUGIN 预热结束；插件不得同步等待工作线程再次调用 `init`，也应避免跨线程 scope 依赖环。`reset` 可以替换正在预热的代次，旧初始化不得回写新代状态。
首次启动、尚无成功的 HTTP 配置时，工作线程可使用隔离的候选客户端；预热失败后，后续全局查询不再使用该候选，但已发出的原始客户端引用不会被强制关闭或撤销。若已有成功配置，工作线程和其他线程继续使用它，直到新初始化成功提交。初始化线程的临时超时作用域不会继承到工作线程。
`getHttpConfig()` 查询已校验的工厂配置；传输配置在校验后立即复制，预热与成功提交使用同一份冻结配置，修改查询结果不会更新传输设置，请通过 `reset()` 后重新 `init` 应用新超时。传输快照及预热作用域是 SDK 内部桥接机制。
初始化支持 `init(String)` 和 `init(IDaaSClientConfig)`；默认路径请用 `init()`，避免裸 `init(null)` 的重载歧义。

## 支持与反馈

- **邮箱**：cloudidaas@list.alibaba-inc.com
- **问题反馈**：如有问题或建议，请提交 Issue

## 许可证

本项目基于 [Apache License 2.0](LICENSE) 许可证授权。
