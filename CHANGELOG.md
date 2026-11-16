version: 0.0.8-beta
Unreleased
    Restore acyclic cross-thread PLUGIN provider delegation by running file-based plugin warm-up outside the factory monitor.
    Allow plugin workers to bootstrap default HTTP/OAuth clients from an isolated cold candidate without committing failed settings.
    Retain successful transport settings for workers during re-initialization; candidate timeout scope remains local to the initializer.
    Coordinate concurrent init completion outside the monitor and guard commit/cleanup against reset and superseded generations.
    Preserve non-PLUGIN initialization barriers; PLUGIN provider lookups during warm-up do not imply outer initialization success.
    Recheck published providers before registering new creation tasks to prevent duplicate authentication for one scope.
    Reject already-expired STRICT refresh results before publication, retaining still-valid cache on failed prefetch.
    Restore nonblocking configuration reads during file warm-up, including plugin worker threads; non-PLUGIN provider acquisition still waits.
    Freeze validated HTTP settings before plugin warm-up and commit that same snapshot only on successful initialization.
    Sanitize GCP default HTTP error bodies in logs, messages and causes, preserving numeric HTTP status (including 403/503).
    Read OIDC token files without stderr stack traces and classify directory/unreadable paths as safe read failures.
    Detect same-thread recursive provider creation and fail instead of waiting on its own FutureTask.
    Resolve direct EKS/ACK builder token paths from explicit settings, platform environment variables, then defaults.
    Avoid repeated prefetch of a newly fetched value already inside its prefetch window; stale refresh and failed retries remain active.
    Freeze committed HTTP settings at successful initialization; mutating getHttpConfig() results does not reconfigure transport.
    Retry completed failed provider creations before caller cleanup and retire successful creation tasks.
    Validate internal warm-up inputs and GCP empty identity responses; keep token-safe IO/parse diagnostics distinct.
    Reject invalid or null authnMethod/identityType at factory initialization; omitted fields keep NONE/CLIENT defaults.
    Previously non-exact identityType names deserialized to null and skipped HUMAN initialization, allowing machine authentication
    when the remaining configuration supported it; migrate those configurations to exact CLIENT or HUMAN before upgrading.
    Invalid authnMethod names now report configuration errors during init instead of an NPE at provider creation.
    Validation is limited to the factory boundary; generic Gson enum deserialization remains unchanged.
    Suggest exact environment enum spelling for PKCS7/OIDC case/whitespace mistakes without normalizing or accepting invalid values.
    Preserve generic JSON parsing and ignored environment fields for other authentication methods (except removed AWS_ESK diagnostics).
    Recognize case/whitespace variants of removed AWS_ESK only for migration diagnostics; all remain rejected.
    Diagnose empty/null/non-object factory configuration explicitly while retaining initialization error wrapping.
    M2M/human prefetch is calculated as expires_at - expires_in/3 (roughly 2/3 of an aligned lifetime).
    GCP prefetch is exp - min(20 minutes, remaining lifetime/3), so longer-lived tokens use the 20-minute advance.
    Cache prefetch/stale times add 5 to less than 10 minutes of positive jitter and are capped by known hard expiry;
    prefetch is also capped by stale time. Refresh is triggered by calls and the selected prefetch strategy, not a timer.
    Report an explicit AWS_ESK-to-AWS_EKS migration hint when loading factory configuration files.
    Return defensive transport configuration snapshots and document internal HTTP and plugin initialization boundaries.
    Preserve still-valid cached credentials when opportunistic prefetch fails; retain STRICT failure after cache expiry.
    Bound token cache lifetime by the earliest explicit expiresAt, expires_at and request-start plus expires_in.
    Sanitize GCP and projected OIDC token parsing errors without retaining token-bearing parser causes.
    Retain the last successful HTTP configuration across reset and isolate failed initialization warm-up transports.
    Reject expired or missing-exp projected OIDC tokens before caching or submitting them for EKS, ACK and Kubernetes.
    Resolve the default scope and provider creation atomically across reset/re-initialization.
    Read public configuration fields atomically and prevent stale HTTP snapshots from replacing newer clients.
    Coalesce refreshes queued for the same cached value without changing refresh jitter or reset compatibility.
    Prevent reset/first-provider deadlocks by using a transport configuration snapshot and warming providers outside cache locks.
    Isolate in-flight provider creation across reset while retaining initialization waiting and per-scope single creation.
    Refresh credentials during the prefetch window instead of waiting until stale time.
    Include hard expiry in RefreshResult value semantics and adapt GCP refresh lead times to remaining token lifetime.
    Report empty human refresh responses and preserve invalid issuer input in configuration diagnostics.
    Coordinate concurrent factory readers with initialization completion without changing public initialization APIs.
    Check the latest cached credential after prefetch and conservatively count expires_in from refresh start.
    Enforce GCP identity JWT expiration in the cache and report missing exp as a credential error.
    Isolate GCP factory HTTP mocks from global HTTP configuration state.
    Align GCP VM OIDC audience with Go by using the issuer origin instead of the IDaaS instance ID; trust sources must allow this origin.
    Trim IMDSv2 session tokens and reject empty token responses before fetching the EC2 identity document.
    Use the EC2 RSA-2048 identity signature endpoint and remove Base64 whitespace before IDaaS authentication.
    Support AWS EC2 instance identity authentication through IMDSv2 and PKCS7.
    Support EKS and ACK projected OIDC token files and complete the GCP VM OIDC provider.
    Use named Kubernetes, EKS and ACK file OIDC providers with builders, retaining file cache and path behavior.
    Prevent STRICT credential caching from returning a token past its known expiration.
    Apply re-initialized HTTP timeouts to OAuth and metadata requests without closing in-flight clients.
    Reject unsupported PKCS7 deployment environments during configuration validation.
    BREAKING: Remove the unimplemented AWS_ESK enum and alias handling; use AWS_EKS in source code and configuration. Existing references to AWS_ESK must be migrated and recompiled.
    Retain the public AWS EC2 provider no-argument constructor alongside its builder.
    Unify EKS/ACK OIDC file path precedence for credentials and Token Exchange.
    Restore human login refresh state and preserve unrotated refresh tokens.
    Clear failed factory initialization state so init() can retry.
    Add isolated regression tests for authentication and token caching fixes.

version: 0.0.7-beta
Support dynamic initialization with specified config file path: init(String configPath)
Support re-initialization via reset() method
version: 0.0.6-beta
Standardize endpoint format
version: 0.0.5-beta
staticIDaaSCredentialProvider support
version: 0.0.4-beta
Token Exchange support
Fix bug:
    Authentication method:
        PCA
version: 0.0.3-beta
Fix bug:
    Authentication method:
        CLIENT_SECRET_JWT
        PRIVATE_KEY_JWT
version: 0.0.2-beta
Authentication method support:
    PLUGIN
version: 0.0.1-beta
IDaaS core sdk authenticates with IDaaS using user-specified configurations to acquire a JWT-compliant Access Token.
Deployment environment support:
    Local computer
    Alibaba cloud ECS
    Alibaba cloud ACK
Authentication method support:
    CLIENT_SECRET_POST
    CLIENT_SECRET_JWT
    PRIVATE_KEY_JWT
    PKCS7
    OIDC
