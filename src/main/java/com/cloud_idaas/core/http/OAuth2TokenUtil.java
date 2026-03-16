package com.cloud_idaas.core.http;

import com.cloud_idaas.core.credential.IDaaSTokenResponse;
import com.cloud_idaas.core.domain.DeviceCodeResponse;
import com.cloud_idaas.core.domain.constants.ClientAssertionType;
import com.cloud_idaas.core.domain.constants.HttpConstants;
import com.cloud_idaas.core.domain.constants.OAuth2Constants;
import com.cloud_idaas.core.exception.ClientException;
import com.cloud_idaas.core.provider.PluginCredentialProvider;
import com.cloud_idaas.core.util.JSONUtil;
import com.cloud_idaas.core.util.PluginCredentialProviderUtil;

import java.util.*;

public class OAuth2TokenUtil {

    private static final String DEFAULT_GRANT_TYPE = OAuth2Constants.CLIENT_CREDENTIALS_GRANT_TYPE_VALUE;

    private static final String AUTHORIZATION_PENDING = "authorization_pending";

    private static final String SLOW_DOWN = "slow_down";

    private static final HttpClient HTTP_CLIENT = HttpClientFactory.getDefaultHttpClient();

    public static IDaaSTokenResponse getTokenWithClientSecretBasic(String clientId, String clientSecret,
                                                                   String tokenEndpoint, String scope) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(HttpConstants.CONTENT_TYPE_HEADER, Collections.singletonList(ContentType.FORM.getType()));
        String credential = clientId + HttpConstants.COLON + clientSecret;
        headers.put(HttpConstants.AUTHORIZATION_HEADER, Collections.singletonList(HttpConstants.BASIC + HttpConstants.SPACE + Base64.getEncoder().encodeToString(credential.getBytes())));
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.GRANT_TYPE, Collections.singletonList(DEFAULT_GRANT_TYPE));
        formBody.put(OAuth2Constants.SCOPE, Collections.singletonList(scope));
        HttpRequest request = new HttpRequest.Builder()
                .url(tokenEndpoint)
                .httpMethod(HttpMethod.POST)
                .headers(headers)
                .formBody(formBody)
                .contentType(ContentType.FORM)
                .build();
        HttpResponse response = HTTP_CLIENT.send(request);
        IDaaSTokenResponse oidcTokenCredential = JSONUtil.parseObject(response.getBody(), IDaaSTokenResponse.class);
        return oidcTokenCredential;
    }

    public static IDaaSTokenResponse getTokenWithClientSecretPost(String clientId, String clientSecret,
                                                                  String tokenEndpoint, String scope) {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.CLIENT_SECRET, Collections.singletonList(clientSecret));
        formBody.put(OAuth2Constants.GRANT_TYPE, Collections.singletonList(DEFAULT_GRANT_TYPE));
        formBody.put(OAuth2Constants.SCOPE, Collections.singletonList(scope));
        return postTokenEndpoint(formBody, tokenEndpoint);
    }

    public static IDaaSTokenResponse postTokenEndpoint(Map<String, List<String>> formBody, String tokenEndpoint) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(HttpConstants.CONTENT_TYPE_HEADER, Collections.singletonList(ContentType.FORM.getType()));
        HttpRequest httpRequest = new HttpRequest.Builder()
                .url(tokenEndpoint)
                .httpMethod(HttpMethod.POST)
                .headers(headers)
                .formBody(formBody)
                .contentType(ContentType.FORM)
                .build();
        HttpResponse response;
        try {
            response = HTTP_CLIENT.send(httpRequest);
        } catch (ClientException e){
            if (!AUTHORIZATION_PENDING.equals(e.getErrorCode()) && !SLOW_DOWN.equals(e.getErrorCode())) {
                throw e;
            }
            return null;
        }
        String responseBody = response.getBody();
        IDaaSTokenResponse oidcTokenCredential = JSONUtil.parseObject(responseBody, IDaaSTokenResponse.class);
        return oidcTokenCredential;
    }

    public static IDaaSTokenResponse getTokenWithClientAssertion(String clientId, String clientAssertion,
                                                                 String tokenEndpoint, String scope) {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION_TYPE, Collections.singletonList(ClientAssertionType.OAUTH_JWT_BEARER));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION, Collections.singletonList(clientAssertion));
        formBody.put(OAuth2Constants.GRANT_TYPE, Collections.singletonList(DEFAULT_GRANT_TYPE));
        formBody.put(OAuth2Constants.SCOPE, Collections.singletonList(scope));
        return postTokenEndpoint(formBody, tokenEndpoint);
    }

    public static IDaaSTokenResponse getTokenWithPCA(String clientId, String applicationFederatedCredentialName,
                                                     String clientX509Certificate, String x509CertChains, String clientAssertion,
                                                     String tokenEndpoint, String scope) {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.APPLICATION_FEDERATED_CREDENTIAL_NAME, Collections.singletonList(applicationFederatedCredentialName));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION_TYPE, Collections.singletonList(ClientAssertionType.PRIVATE_CA_JWT_BEARER));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION, Collections.singletonList(clientAssertion));
        formBody.put(OAuth2Constants.CLIENT_X509_CERTIFICATE, Collections.singletonList(clientX509Certificate));
        formBody.put(OAuth2Constants.X509_CERT_CHAINS, Collections.singletonList(x509CertChains));
        formBody.put(OAuth2Constants.GRANT_TYPE, Collections.singletonList(DEFAULT_GRANT_TYPE));
        formBody.put(OAuth2Constants.SCOPE, Collections.singletonList(scope));
        return postTokenEndpoint(formBody, tokenEndpoint);
    }

    public static IDaaSTokenResponse getTokenWithPKCS7AttestedDocument(String clientId, String applicationFederatedCredentialName,
                                                                       String pkcs7AttestedDocument,
                                                                       String tokenEndpoint, String scope) {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.APPLICATION_FEDERATED_CREDENTIAL_NAME, Collections.singletonList(applicationFederatedCredentialName));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION_TYPE, Collections.singletonList(ClientAssertionType.PKCS7_BEARER));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION, Collections.singletonList(pkcs7AttestedDocument));
        formBody.put(OAuth2Constants.GRANT_TYPE, Collections.singletonList(DEFAULT_GRANT_TYPE));
        formBody.put(OAuth2Constants.SCOPE, Collections.singletonList(scope));
        return postTokenEndpoint(formBody, tokenEndpoint);
    }

    public static IDaaSTokenResponse getTokenWithOIDCFederatedCredential(String clientId, String applicationFederatedCredentialName,
                                                                         String oidcToken,
                                                                         String tokenEndpoint, String scope) {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.APPLICATION_FEDERATED_CREDENTIAL_NAME, Collections.singletonList(applicationFederatedCredentialName));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION_TYPE, Collections.singletonList(ClientAssertionType.OIDC_BEARER));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION, Collections.singletonList(oidcToken));
        formBody.put(OAuth2Constants.GRANT_TYPE, Collections.singletonList(DEFAULT_GRANT_TYPE));
        formBody.put(OAuth2Constants.SCOPE, Collections.singletonList(scope));
        return postTokenEndpoint(formBody, tokenEndpoint);
    }

    public static DeviceCodeResponse getDeviceCode(String clientId, String scope, String deviceAuthorization){
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(HttpConstants.CONTENT_TYPE_HEADER, Collections.singletonList(ContentType.FORM.getType()));
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.SCOPE, Collections.singletonList(scope));
        HttpRequest request = new HttpRequest.Builder()
                .url(deviceAuthorization)
                .httpMethod(HttpMethod.POST)
                .headers(headers)
                .formBody(formBody)
                .contentType(ContentType.FORM)
                .build();
        HttpResponse httpResponse = HTTP_CLIENT.send(request);
        return JSONUtil.parseObject(httpResponse.getBody(), DeviceCodeResponse.class);
    }

    public static IDaaSTokenResponse getTokenByDeviceCode(String clientId, String deviceCode, String tokenEndpoint) {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.GRANT_TYPE, Collections.singletonList(OAuth2Constants.DEVICE_CODE_GRANT_TYPE_VALUE));
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.DEVICE_CODE, Collections.singletonList(deviceCode));
        return postTokenEndpoint(formBody, tokenEndpoint);
    }

    public static IDaaSTokenResponse refreshToken(String clientId, String refreshToken, String tokenEndpoint) {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.GRANT_TYPE, Collections.singletonList(OAuth2Constants.REFRESH_TOKEN_GRANT_TYPE_VALUE));
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.REFRESH_TOKEN_PARAMETER, Collections.singletonList(refreshToken));
        return postTokenEndpoint(formBody, tokenEndpoint);
    }

    public static IDaaSTokenResponse getTokenWithPlugin(String pluginName, String scope){
        PluginCredentialProvider pluginCredentialProvider = PluginCredentialProviderUtil.getPluginCredentialProvider(pluginName);
        return pluginCredentialProvider.getIDaaSCredential(scope);
    }

    public static IDaaSTokenResponse tokenExchangeWithClientSecretBasic(String clientId, String clientSecret,
                                                                        String subjectToken, String subjectTokenType, String requestedTokenType,
                                                                        String tokenEndpoint, String scope, String actorToken, String actorTokenType) {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(HttpConstants.CONTENT_TYPE_HEADER, Collections.singletonList(ContentType.FORM.getType()));
        String credential = clientId + HttpConstants.COLON + clientSecret;
        headers.put(HttpConstants.AUTHORIZATION_HEADER, Collections.singletonList(HttpConstants.BASIC + HttpConstants.SPACE + Base64.getEncoder().encodeToString(credential.getBytes())));
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        addTokenExchangeFormBody(formBody, subjectToken, subjectTokenType, requestedTokenType, scope, actorToken, actorTokenType);
        HttpRequest request = new HttpRequest.Builder()
                .url(tokenEndpoint)
                .httpMethod(HttpMethod.POST)
                .headers(headers)
                .formBody(formBody)
                .contentType(ContentType.FORM)
                .build();
        HttpResponse response = HTTP_CLIENT.send(request);
        IDaaSTokenResponse oidcTokenCredential = JSONUtil.parseObject(response.getBody(), IDaaSTokenResponse.class);
        return oidcTokenCredential;
    }

    public static IDaaSTokenResponse tokenExchangeWithClientSecretPost(String clientId, String clientSecret,
                                                                       String subjectToken, String subjectTokenType, String requestedTokenType,
                                                                       String tokenEndpoint, String scope, String actorToken, String actorTokenType) {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.CLIENT_SECRET, Collections.singletonList(clientSecret));
        addTokenExchangeFormBody(formBody, subjectToken, subjectTokenType, requestedTokenType, scope, actorToken, actorTokenType);
        return postTokenEndpoint(formBody, tokenEndpoint);
    }

    public static IDaaSTokenResponse tokenExchangeWithClientAssertion(String clientId, String clientAssertion,
                                                                      String subjectToken, String subjectTokenType, String requestedTokenType,
                                                                      String tokenEndpoint, String scope, String actorToken, String actorTokenType) {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION_TYPE, Collections.singletonList(ClientAssertionType.OAUTH_JWT_BEARER));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION, Collections.singletonList(clientAssertion));
        addTokenExchangeFormBody(formBody, subjectToken, subjectTokenType, requestedTokenType, scope, actorToken, actorTokenType);
        return postTokenEndpoint(formBody, tokenEndpoint);
    }

    public static IDaaSTokenResponse tokenExchangeWithPCA(String clientId, String applicationFederatedCredentialName,
                                                          String clientX509Certificate, String x509CertChains, String clientAssertion,
                                                          String subjectToken, String subjectTokenType, String requestedTokenType,
                                                          String tokenEndpoint, String scope, String actorToken, String actorTokenType) {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.APPLICATION_FEDERATED_CREDENTIAL_NAME, Collections.singletonList(applicationFederatedCredentialName));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION_TYPE, Collections.singletonList(ClientAssertionType.PRIVATE_CA_JWT_BEARER));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION, Collections.singletonList(clientAssertion));
        formBody.put(OAuth2Constants.CLIENT_X509_CERTIFICATE, Collections.singletonList(clientX509Certificate));
        formBody.put(OAuth2Constants.X509_CERT_CHAINS, Collections.singletonList(x509CertChains));
        addTokenExchangeFormBody(formBody, subjectToken, subjectTokenType, requestedTokenType, scope, actorToken, actorTokenType);
        return postTokenEndpoint(formBody, tokenEndpoint);
    }

    public static IDaaSTokenResponse tokenExchangeWithPKCS7AttestedDocument(String clientId, String applicationFederatedCredentialName, String pkcs7AttestedDocument,
                                                                            String subjectToken, String subjectTokenType, String requestedTokenType,
                                                                            String tokenEndpoint, String scope, String actorToken, String actorTokenType) {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.APPLICATION_FEDERATED_CREDENTIAL_NAME, Collections.singletonList(applicationFederatedCredentialName));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION_TYPE, Collections.singletonList(ClientAssertionType.PKCS7_BEARER));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION, Collections.singletonList(pkcs7AttestedDocument));
        addTokenExchangeFormBody(formBody, subjectToken, subjectTokenType, requestedTokenType, scope, actorToken, actorTokenType);
        return postTokenEndpoint(formBody, tokenEndpoint);
    }

    public static IDaaSTokenResponse tokenExchangeWithOIDCFederatedCredential(String clientId, String applicationFederatedCredentialName, String oidcToken,
                                                                              String subjectToken, String subjectTokenType, String requestedTokenType,
                                                                              String tokenEndpoint, String scope, String actorToken, String actorTokenType) {
        Map<String, List<String>> formBody = new HashMap<>();
        formBody.put(OAuth2Constants.CLIENT_ID, Collections.singletonList(clientId));
        formBody.put(OAuth2Constants.APPLICATION_FEDERATED_CREDENTIAL_NAME, Collections.singletonList(applicationFederatedCredentialName));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION_TYPE, Collections.singletonList(ClientAssertionType.OIDC_BEARER));
        formBody.put(OAuth2Constants.CLIENT_ASSERTION, Collections.singletonList(oidcToken));
        addTokenExchangeFormBody(formBody, subjectToken, subjectTokenType, requestedTokenType, scope, actorToken, actorTokenType);
        return postTokenEndpoint(formBody, tokenEndpoint);
    }

    private static void addTokenExchangeFormBody(Map<String, List<String>> formBody, String subjectToken,
                                                 String subjectTokenType, String requestedTokenType, String scope,
                                                 String actorToken, String actorTokenType){
        formBody.put(OAuth2Constants.GRANT_TYPE, Collections.singletonList(OAuth2Constants.TOKEN_EXCHANGE_GRANT_TYPE_VALUE));
        formBody.put(OAuth2Constants.SUBJECT_TOKEN, Collections.singletonList(subjectToken));
        formBody.put(OAuth2Constants.SUBJECT_TOKEN_TYPE, Collections.singletonList(subjectTokenType));
        formBody.put(OAuth2Constants.REQUESTED_TOKEN_TYPE, Collections.singletonList(requestedTokenType));
        formBody.put(OAuth2Constants.SCOPE, Collections.singletonList(scope));
        if (actorToken != null) {
            formBody.put(OAuth2Constants.ACTOR_TOKEN, Collections.singletonList(actorToken));
            formBody.put(OAuth2Constants.ACTOR_TOKEN_TYPE, Collections.singletonList(actorTokenType));
        }
    }
}