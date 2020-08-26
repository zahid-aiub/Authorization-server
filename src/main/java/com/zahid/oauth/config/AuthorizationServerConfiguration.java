package com.zahid.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.*;


@Configuration
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
	@Value("${access.token.validity.seconds}")
	private int accessTokenValidity;
	@Value("${refresh.token.validity.seconds}")
	private int refreshTokenValidity;

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	public void configure(final AuthorizationServerSecurityConfigurer configurer) {
		configurer.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')").checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
	}

	public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient("client").authorizedGrantTypes(new String[] { "password", "refresh_token" }).authorities(new String[] { "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" }).scopes(new String[] { "read", "write", "trust" }).secret(this.passwordEncoder.encode((CharSequence)"secret")).resourceIds(new String[] { "resource-1", "resource-2" });
	}

	public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenServices(this.tokenServices()).authenticationManager(this.authenticationManager).userDetailsService(this.userDetailsService).accessTokenConverter((AccessTokenConverter)this.accessTokenConverter());
	}

	@Bean
	public TokenStore tokenStore() {
		return new ResetTokenStore(this.redisConnectionFactory);
	}

	@Bean
	public AuthorizationServerTokenServices tokenServices() {
		final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(this.tokenStore());
		defaultTokenServices.setReuseRefreshToken(false);
		defaultTokenServices.setSupportRefreshToken(true);
		defaultTokenServices.setAccessTokenValiditySeconds(accessTokenValidity);
		defaultTokenServices.setRefreshTokenValiditySeconds(refreshTokenValidity);
		return defaultTokenServices;
	}

	@Bean
	public DefaultAccessTokenConverter accessTokenConverter() {
		return new DefaultAccessTokenConverter();
	}
}
