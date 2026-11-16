package com.cloud_idaas.core.provider;

import com.cloud_idaas.core.credential.IDaaSTokenResponse;

/** Supplies plugin credentials during authentication, including synchronous factory initialization. */
public interface PluginCredentialProvider {

    String getName();

    /**
     * Obtains a credential for the requested scope.
     *
     * <p>File-based PLUGIN warm-up runs outside the factory monitor. Acyclic delegation to
     * another scope on an existing worker thread is supported. Configuration and provider
     * lookup during that warm-up do not imply that the outer initialization has succeeded.
     * Same-thread recursive creation of the same provider (including a scope cycle) fails fast;
     * cross-thread dependency cycles must still be avoided. A callback must not wait for a
     * worker to call init, because concurrent init waits for this warm-up to finish.
     * Reset may supersede warm-up; an obsolete initializer cannot commit into the new generation.
     * On first initialization workers may use the isolated candidate HTTP client. If a previous
     * initialization succeeded, workers retain its transport settings until the new commit;
     * initialization-thread timeout overrides are not inherited by workers.
     *
     * @param scope requested authorization scope
     * @return the plugin credential
     */
    IDaaSTokenResponse getIDaaSCredential(String scope);
}
