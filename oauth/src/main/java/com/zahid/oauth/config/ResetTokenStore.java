package com.zahid.oauth.config;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Component;

@Component
public class ResetTokenStore extends RedisTokenStore {

    public ResetTokenStore(final RedisConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    //todo:  uncomment bellow code if u have need every time new token
    /*
    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = super.getAccessToken(authentication);
        if(accessToken != null) {
            removeAccessToken(accessToken);
            removeRefreshToken(accessToken.getRefreshToken());
        }
        return null;
    }
    */
}
