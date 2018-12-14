package com.cwtsite.cwt.domain.user.view.controller;

import com.cwtsite.cwt.domain.core.exception.BadRequestException;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.domain.user.service.AuthService;
import com.cwtsite.cwt.domain.user.service.JwtTokenUtil;
import com.cwtsite.cwt.domain.user.service.UserService;
import com.cwtsite.cwt.domain.user.view.model.JwtAuthenticationRequest;
import com.cwtsite.cwt.domain.user.view.model.JwtAuthenticationResponse;
import com.cwtsite.cwt.domain.user.view.model.JwtUser;
import com.cwtsite.cwt.domain.user.view.model.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "api/auth")
public class AuthenticationRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public AuthenticationRestController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil,
                                        UserDetailsService userDetailsService, UserService userService,
                                        AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.authService = authService;
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto userRegistrationDto) {
        final User user;
        try {
            user = userService.registerUser(
                    userRegistrationDto.getUsername(), userRegistrationDto.getEmail(), userRegistrationDto.getPassword());
        } catch (UserService.UserExistsByEmailOrUsernameException e) {
            throw new BadRequestException("User already exists.");
        } catch (UserService.InvalidUsernameException e) {
            throw new BadRequestException("Username invalid.");
        } catch (UserService.InvalidEmailException e) {
            throw new BadRequestException("Email invalid.");
        }
        return createAuthenticationToken(
                new JwtAuthenticationRequest(user.getUsername(), userRegistrationDto.getPassword()));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public ResponseEntity<JwtAuthenticationResponse> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String token = request.getHeader(authService.getTokenHeaderName());
        if (token == null) {
            return ResponseEntity.ok(null);
        }

        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);

        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getResetDate())) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

