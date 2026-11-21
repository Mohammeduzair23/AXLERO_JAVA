package com.axlero.dashboardbuilder.config;

import com.axlero.dashboardbuilder.dto.response.MetricResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/** Redis-backed caching for dashboard widget data. */
@Slf4j
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    public static final String METRICS_CACHE = "metrics";

    /**
     * Entries expire after a few minutes, which is the safety net if an eviction is ever missed.
     * Values are stored as readable JSON (check them with redis-cli).
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer metricsCacheCustomizer(
            @Value("${app.cache.metrics-ttl-minutes:5}") long ttlMinutes) {
        RedisCacheConfiguration metrics = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(ttlMinutes))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new Jackson2JsonRedisSerializer<>(MetricResponse.class)));
        return builder -> builder.withCacheConfiguration(METRICS_CACHE, metrics);
    }

    /** A Redis outage must slow the API down, not break it: log and carry on with the database. */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException ex, Cache cache, Object key) {
                log.warn("Cache '{}' read failed, falling back to database: {}", cache.getName(), ex.toString());
            }

            @Override
            public void handleCachePutError(RuntimeException ex, Cache cache, Object key, Object value) {
                log.warn("Cache '{}' write failed: {}", cache.getName(), ex.toString());
            }

            @Override
            public void handleCacheEvictError(RuntimeException ex, Cache cache, Object key) {
                log.warn("Cache '{}' eviction failed, entries expire by TTL instead: {}", cache.getName(), ex.toString());
            }

            @Override
            public void handleCacheClearError(RuntimeException ex, Cache cache) {
                log.warn("Cache '{}' clear failed, entries expire by TTL instead: {}", cache.getName(), ex.toString());
            }
        };
    }
}