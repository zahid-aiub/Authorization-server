package com.zahid.oauth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//@Configuration
@ConfigurationProperties("auth-server")
@Data
public class RedisServerProperties {
    private Boolean isRedisStandalone;
    private String redisHost;
    private String redisPort;
    private String redisSocketLocation;
}
