package com.cloud_idaas.core.implementation.authentication.oidc;

import com.cloud_idaas.core.domain.constants.AuthenticationConstants;
import com.cloud_idaas.core.exception.CredentialException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlatformFileOidcTokenProviderTest {
    @TempDir Path directory;

    @Test void buildersUsePlatformDefaultPaths() {
        assertEquals(AuthenticationConstants.KUBERNETES_SERVICE_ACCOUNT_TOKEN_PATH,
                KubernetesOidcTokenProvider.builder().build().getOidcTokenFilePath());
        assertEquals(AuthenticationConstants.AWS_EKS_OIDC_TOKEN_DEFAULT_PATH,
                AwsEksOidcTokenProvider.builder().build().getOidcTokenFilePath());
        assertEquals(AuthenticationConstants.ALIBABA_CLOUD_ACK_OIDC_TOKEN_DEFAULT_PATH,
                AlibabaCloudAckOidcTokenProvider.builder().build().getOidcTokenFilePath());
    }

    @Test void allProvidersReadExplicitFileAndRetainValidCachedToken() throws Exception {
        Path file = directory.resolve("token");
        String first = jwt("first", 3600), second = jwt("second", 3600);
        for (FileOidcTokenProvider provider : providers(file)) {
            Files.write(file, first.getBytes(StandardCharsets.UTF_8));
            assertEquals(first, provider.getOidcToken());
            Files.write(file, second.getBytes(StandardCharsets.UTF_8));
            assertEquals(first, provider.getOidcToken());
            assertEquals(file.toString(), provider.getOidcTokenFilePath());
        }
    }

    @Test void allProvidersReloadRotatedFileWhenCachedTokenIsNearExpiry() throws Exception {
        Path file = directory.resolve("rotating");
        String first = jwt("first", 30), second = jwt("rotated", 3600);
        for (FileOidcTokenProvider provider : providers(file)) {
            Files.write(file, first.getBytes(StandardCharsets.UTF_8));
            assertEquals(first, provider.getOidcToken());
            Files.write(file, second.getBytes(StandardCharsets.UTF_8));
            assertEquals(second, provider.getOidcToken());
        }
    }

    @Test void missingAndMalformedFilesKeepCredentialExceptionBehavior() throws Exception {
        for (FileOidcTokenProvider provider : providers(directory.resolve("missing"))) {
            assertThrows(CredentialException.class, provider::getOidcToken);
        }
        Path bad = directory.resolve("bad");
        Files.write(bad, "invalid-jwt".getBytes(StandardCharsets.UTF_8));
        for (FileOidcTokenProvider provider : providers(bad)) {
            assertThrows(CredentialException.class, provider::getOidcToken);
        }
    }

    @Test void platformBuildersRejectBlankPaths() {
        for (String path : new String[]{null, "", "  "}) {
            assertThrows(IllegalArgumentException.class, () -> KubernetesOidcTokenProvider.builder().oidcTokenFilePath(path));
            assertThrows(IllegalArgumentException.class, () -> AwsEksOidcTokenProvider.builder().oidcTokenFilePath(path));
            assertThrows(IllegalArgumentException.class, () -> AlibabaCloudAckOidcTokenProvider.builder().oidcTokenFilePath(path));
        }
    }

    @Test void expiredOrMissingExpTokensAreRejectedAndRotationRecovers() throws Exception {
        Path file = directory.resolve("expired");
        for (FileOidcTokenProvider provider : providers(file)) {
            for (long lifetime : new long[]{-60, 0}) {
                Files.write(file, jwt("expired", lifetime).getBytes(StandardCharsets.UTF_8));
                CredentialException failure = assertThrows(CredentialException.class, provider::getOidcToken);
                assertEquals("ExpiredOidcToken", failure.getErrorCode());
            }
            String noExp = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8))
                    + "." + Base64.getUrlEncoder().withoutPadding().encodeToString("{\"sub\":\"synthetic\"}".getBytes(StandardCharsets.UTF_8)) + ".";
            Files.write(file, noExp.getBytes(StandardCharsets.UTF_8));
            assertEquals("MissingOidcTokenExpiration",
                    assertThrows(CredentialException.class, provider::getOidcToken).getErrorCode());
            String good = jwt("recovered", 3600);
            Files.write(file, good.getBytes(StandardCharsets.UTF_8));
            assertEquals(good, provider.getOidcToken());
        }
    }

    @Test void malformedFileBodyIsAbsentFromPublicExceptionAndCause() throws Exception {
        Path file = directory.resolve("malformed");
        String body = "synthetic-sensitive-jwt-body";
        Files.write(file, body.getBytes(StandardCharsets.UTF_8));
        for (FileOidcTokenProvider provider : providers(file)) {
            CredentialException failure = assertThrows(CredentialException.class, provider::getOidcToken);
            assertEquals("InvalidOidcToken", failure.getErrorCode());
            assertFalse(failure.getMessage().contains(body));
            assertNull(failure.getCause());
        }
    }

    @Test void readAndParseFailuresHaveSafeDistinctDiagnosticsWithoutRawCauses() throws Exception {
        CredentialException read = assertThrows(CredentialException.class,
                () -> new FileOidcTokenProvider(directory.resolve("missing-sensitive-path").toString()).getOidcToken());
        assertEquals("InvalidOidcToken", read.getErrorCode());
        assertTrue(read.getMessage().contains("could not be read"));
        assertFalse(read.getMessage().contains("missing-sensitive-path"));
        assertNull(read.getCause());
        Path file = directory.resolve("malformed-diagnostic");
        Files.write(file, "synthetic-sensitive-body".getBytes(StandardCharsets.UTF_8));
        CredentialException parse = assertThrows(CredentialException.class,
                () -> new FileOidcTokenProvider(file.toString()).getOidcToken());
        assertEquals("InvalidOidcToken", parse.getErrorCode());
        assertTrue(parse.getMessage().contains("could not be parsed"));
        assertFalse(parse.getMessage().contains("synthetic-sensitive-body"));
        assertNull(parse.getCause());
    }

    private static List<FileOidcTokenProvider> providers(Path file) {
        return Arrays.asList(KubernetesOidcTokenProvider.builder().oidcTokenFilePath(file.toString()).build(),
                AwsEksOidcTokenProvider.builder().oidcTokenFilePath(file.toString()).build(),
                AlibabaCloudAckOidcTokenProvider.builder().oidcTokenFilePath(file.toString()).build(),
                new FileOidcTokenProvider(file.toString()));
    }

    private static String jwt(String subject, long lifetime) {
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8)) + "."
                + encoder.encodeToString(("{\"sub\":\"" + subject + "\",\"exp\":"
                + Instant.now().plusSeconds(lifetime).getEpochSecond() + "}").getBytes(StandardCharsets.UTF_8)) + ".";
    }
}
