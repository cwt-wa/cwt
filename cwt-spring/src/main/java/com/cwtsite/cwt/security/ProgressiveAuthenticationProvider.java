package com.cwtsite.cwt.security;

import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.domain.user.service.AuthService;
import com.cwtsite.cwt.domain.user.service.JwtUserDetailsServiceImpl;
import com.cwtsite.cwt.domain.user.service.UserService;
import com.cwtsite.cwt.domain.user.view.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ProgressiveAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final JwtUserDetailsServiceImpl jwtUserDetailsService;
    private final AuthService authService;

    @Autowired
    public ProgressiveAuthenticationProvider(AuthService authService, UserService userService,
                                             JwtUserDetailsServiceImpl jwtUserDetailsService) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        final String logPrefix = "User trying to log in in with username " + username + " ";
        final JwtUser jwtUser = (JwtUser) jwtUserDetailsService.loadUserByUsername(username);

        if (jwtUser == null) {
            throw new UsernameNotFoundException(logPrefix + "not found.");
        }

        @SuppressWarnings("ConstantConditions")
        final User user = userService.getById(jwtUser.getId()).get();
        final boolean usesLegacyPassword = user.getPassword() == null;

        // TODO Log use of legacy password.

        final boolean validCredentials = usesLegacyPassword
                ? authService.createLegacyHash(password).equals(user.getPassword_legacy())
                : authService.createHash(password).equals(user.getPassword());

        if (!validCredentials) {
            throw new BadCredentialsException(logPrefix + "entered the wrong password.");
        }

        try {
            if (usesLegacyPassword) {
                user.setPassword_legacy(null);
                user.setPassword(authService.createHash(password));
                userService.saveUser(user);
            }
        } catch (Exception e) {
            // A user should be authenticated even if updating the password failed.
            // Of course this should still be taken care of, so here's the stack trace:
            e.printStackTrace();
        }

        return new UsernamePasswordAuthenticationToken(username, password, jwtUser.getAuthorities());
    }

    public void assertCredentials(String username, String password) {
        if (!authService.createLegacyHash(password).equals(password)) {
            throw new BadCredentialsException("User trying to log in in with username " + username + " entered wrong password.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }
}
