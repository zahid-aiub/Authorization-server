package com.zahid.oauth.config;

import com.zahid.oauth.properties.RedisServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisSocketConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.Assert;

@Configuration
public class TokenStoreConfiguration {
	
	private final RedisServerProperties serverProperties;
	
	public TokenStoreConfiguration(RedisServerProperties serverProperties) {
		Assert.notNull(serverProperties, "Server Configuration is missing");
		Assert.notNull(serverProperties.getIsRedisStandalone(), "Redis standalone indicator is missing");
		if (serverProperties.getIsRedisStandalone()) {
			Assert.notNull(serverProperties.getRedisHost(), "Redis host is missing");
			Assert.notNull(serverProperties.getRedisPort(), "Redis port is missing");
		} else {
			Assert.notNull(serverProperties.getRedisSocketLocation(), "Redis socket location is missing");
		}
		this.serverProperties = serverProperties;
	}
	
	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(
				serverProperties.getIsRedisStandalone() ?
				new RedisStandaloneConfiguration(serverProperties.getRedisHost(), Integer.parseInt(serverProperties.getRedisPort())):
				new RedisSocketConfiguration(serverProperties.getRedisSocketLocation())
		);
	}

}
