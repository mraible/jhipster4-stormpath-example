package com.stormpath.example.config;

import io.github.jhipster.config.JHipsterProperties;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.jsr107.Eh107Configuration;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = { MetricsConfiguration.class })
@AutoConfigureBefore(value = { WebConfigurer.class, DatabaseConfiguration.class })
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache =
            jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(Expirations.timeToLiveExpiration(Duration.of(ehcache.getTimeToLiveSeconds(), TimeUnit.SECONDS)))
                .build());
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            cm.createCache(com.stormpath.example.domain.User.class.getName(), jcacheConfiguration);
            cm.createCache(com.stormpath.example.domain.Authority.class.getName(), jcacheConfiguration);
            cm.createCache(com.stormpath.example.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            // Stormpath cacheable objects
            cm.createCache(com.stormpath.sdk.tenant.Tenant.class.getName(), jcacheConfiguration);
            cm.createCache(com.stormpath.sdk.application.Application.class.getName(), jcacheConfiguration);
            cm.createCache(com.stormpath.sdk.application.ApplicationAccountStoreMapping.class.getName(), jcacheConfiguration);
            cm.createCache(com.stormpath.sdk.application.webconfig.ApplicationWebConfig.class.getName(), jcacheConfiguration);
            cm.createCache(com.stormpath.sdk.directory.Directory.class.getName(), jcacheConfiguration);
            cm.createCache("com.stormpath.sdk.servlet.nonces", jcacheConfiguration);
            cm.createCache(com.stormpath.sdk.oauth.AccessToken.class.getName(), jcacheConfiguration);
            cm.createCache(com.stormpath.sdk.oauth.RefreshToken.class.getName(), jcacheConfiguration);
            cm.createCache(com.stormpath.sdk.provider.Provider.class.getName(), jcacheConfiguration);
            cm.createCache(com.stormpath.sdk.account.Account.class.getName(), jcacheConfiguration);
            cm.createCache(com.stormpath.sdk.group.Group.class.getName(), jcacheConfiguration);
            cm.createCache(com.stormpath.sdk.group.GroupMembership.class.getName(), jcacheConfiguration);
            cm.createCache(com.stormpath.sdk.directory.CustomData.class.getName(), jcacheConfiguration);
            // jhipster-needle-ehcache-add-entry
        };
    }
}
