package com.cloud_idaas.core.implementation;

import com.cloud_idaas.core.cache.CachedResultSupplier;
import com.cloud_idaas.core.cache.PrefetchStrategy;
import com.cloud_idaas.core.cache.RefreshResult;
import com.cloud_idaas.core.cache.StaleValueBehavior;
import com.cloud_idaas.core.cache.strategy.NonBlockingPrefetchStrategy;
import com.cloud_idaas.core.cache.strategy.OneCallerBlocksPrefetchStrategy;
import com.cloud_idaas.core.provider.RefreshCredentialProvider;

import java.util.function.Supplier;

public abstract class AbstractRefreshedCredentialProvider<T> implements RefreshCredentialProvider<T> {

    protected transient final CachedResultSupplier<T> cachedResultSupplier;

    private final boolean asyncCredentialUpdateEnabled;

    protected AbstractRefreshedCredentialProvider(BuilderImpl<?, ?> builder) {
        this.asyncCredentialUpdateEnabled = builder.asyncCredentialUpdateEnabled;
        PrefetchStrategy prefetchStrategy = this.asyncCredentialUpdateEnabled ? new NonBlockingPrefetchStrategy() : new OneCallerBlocksPrefetchStrategy();
        Supplier<RefreshResult<T>> refreshTokenCallable = this::refreshCredential;
        this.cachedResultSupplier = new CachedResultSupplier.Builder<>(refreshTokenCallable)
                .staleValueBehavior(builder.staleValueBehavior)
                .prefetchStrategy(prefetchStrategy)
                .build();
    }

    public boolean isAsyncCredentialUpdateEnabled() {
        return asyncCredentialUpdateEnabled;
    }

    public CachedResultSupplier<T> getCachedResultSupplier() {
        return cachedResultSupplier;
    }

    public interface Builder<ProviderT extends AbstractRefreshedCredentialProvider, BuilderT extends Builder<ProviderT, BuilderT>> {

        BuilderT asyncCredentialUpdateEnabled(boolean asyncCredentialUpdateEnabled);

        ProviderT build();
    }

    protected abstract static class BuilderImpl<ProviderT extends AbstractRefreshedCredentialProvider, BuilderT extends Builder<ProviderT, BuilderT>>
            implements Builder<ProviderT, BuilderT> {

        boolean asyncCredentialUpdateEnabled = false;
        StaleValueBehavior staleValueBehavior = StaleValueBehavior.STRICT;

        protected BuilderImpl() {
        }

        @Override
        public BuilderT asyncCredentialUpdateEnabled(boolean asyncCredentialUpdateEnabled) {
            this.asyncCredentialUpdateEnabled = asyncCredentialUpdateEnabled;
            return (BuilderT) this;
        }

        BuilderT staleValueBehavior(StaleValueBehavior staleValueBehavior) {
            this.staleValueBehavior = staleValueBehavior;
            return (BuilderT) this;
        }
    }


}
