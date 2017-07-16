package com.cwtsite.cwt.user.service;

import com.cwtsite.cwt.user.repository.UserRepository;
import com.cwtsite.cwt.user.repository.entity.User;
import com.cwtsite.cwt.user.repository.entity.UserProfile;
import com.cwtsite.cwt.user.repository.entity.UserSetting;
import com.cwtsite.cwt.user.view.model.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;

@Component
public class UserService {

    @Value("${password.salt}")
    private String salt;

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(UserRegistrationDto userRegistrationDto) {
        return userRepository.save(map(userRegistrationDto));
    }

    private User map(UserRegistrationDto dto) {
        User user = new User(new UserProfile(), new UserSetting());

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(createHash(dto.getPassword()));

        return user;
    }

    private String createHash(String plainPassword) {
        return plainPassword == null ? "" : BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    private String createLegacyHash(String plainPassword) {
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

}
