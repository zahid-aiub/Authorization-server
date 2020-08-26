package com.zahid.oauth.service;

import com.zahid.oauth.model.AuthenticatedUser;
import com.zahid.oauth.model.Role;
import com.zahid.oauth.model.User;
import com.zahid.oauth.properties.RedisServerProperties;
import com.zahid.oauth.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashSet;

@Service
@Slf4j
public class UserDetailService implements UserDetailsService {

    private final RedisServerProperties serverProperties;
    @Autowired
    private UsersRepository userRepository;

    public UserDetailService(final RedisServerProperties serverProperties) {
        Assert.notNull(serverProperties, "Server Configuration is missing");
        log.info("Default server configurations: {}", serverProperties);
        this.serverProperties = serverProperties;
    }

    public UserDetails loadUserByUsername(final String loginId) throws UsernameNotFoundException {
        UserDetailService.log.info("Requesting account info for username {}", loginId);
        final long t1 = System.currentTimeMillis();
        final Mono<User> accountData = Mono.just(this.userRepository.findByLoginId(loginId).get());
        User account;
        try {
            account = accountData.block();
        }
        catch (WebClientException ie) {
            throw new UsernameNotFoundException("User name not found");
        }
        final Collection<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (final Role role : account.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        final long t2 = System.currentTimeMillis();
        log.info("Request took {} ms", t2 - t1);
        Assert.notNull(account, "Account not found");
        return new AuthenticatedUser(loginId, account.getPassword(), account.getActive(), account.getActive(), Boolean.TRUE, account.getActive(), authorities);
    }

}
