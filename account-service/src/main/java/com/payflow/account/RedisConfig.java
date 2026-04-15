package com.payflow.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.time.Duration;

/**
 * @EnableCaching is the master switch.
 * Without this, ALL caching annotations are ignored.
 * Think of it like a light switch — this turns caching ON.
 */
@Configuration
@EnableCaching
public class RedisConfig {


    /**
     * Configures how data is stored in Redis.
     *
     * Key serializer: StringRedisSerializer
     * → keys are stored as plain strings
     * → example key: "accounts::1" or "accounts::ACC-DE4A9137"
     *
     * Value serializer: GenericJackson2JsonRedisSerializer
     * → values are stored as JSON
     * → human readable, easy to debug
     * → example value: {"id":1,"accountNumber":"ACC-DE4A9137",...}
     *
     * TTL: 10 minutes
     * → after 10 minutes, cached data auto-expires
     * → next request fetches fresh data from PostgreSQL
     */
    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory connectionFactory) {

        /**
         * We use JSON serialization instead of Java serialization.
         * This means cached data is stored as human-readable JSON.
         * You can actually see it in Redis and understand it.
         *
         * JavaTimeModule is needed because LocalDateTime
         * needs special handling to serialize/deserialize correctly.
         */
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //Telling Jackson to include class type information when
        //serializing so it knows exactly which class to deserialize
        //back to when reading from Redis
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))  // expire after 10 mins
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                )
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}