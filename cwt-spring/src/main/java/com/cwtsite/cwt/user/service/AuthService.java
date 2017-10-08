package com.cwtsite.cwt.user.service;

import com.cwtsite.cwt.user.repository.UserRepository;
import com.cwtsite.cwt.user.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

@Component
public class AuthService {

    @Value("${jwt.header}")
    private String tokenHeaderName;

    @Value("${password.salt}")
    private String salt;

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthService(JwtTokenUtil jwtTokenUtil, JwtUserDetailsServiceImpl userDetailsService, UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }


    public User getUserFromToken(String token) {
        return userRepository.findByUsername(jwtTokenUtil.getUsernameFromToken(token));
    }

    public String createHash(String plainPassword) {
        return plainPassword == null ? "" : BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public String createLegacyHash(String plainPassword) {
        if (plainPassword == null) {
            return "";
        }

        plainPassword = salt + plainPassword;
        char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            MessageDigest engine = MessageDigest.getInstance("SHA-1");
            byte[] result = engine.digest(plainPassword.getBytes("UTF-8"));
            StringBuilder buffer = new StringBuilder(result.length * 2);
            for (byte aData : result) {
                int value1 = (int) aData & 0xFF;
                buffer.append(chars[value1 / 16]);
                buffer.append(chars[value1 & 0x0F]);
            }
            return buffer.toString().toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("No hash implementation.", e);
        }
    }

    public String getTokenHeaderName() {
        return tokenHeaderName;
    }
}
