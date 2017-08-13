package com.cwtsite.cwt.user.service;

import com.cwtsite.cwt.user.repository.UserRepository;
import com.cwtsite.cwt.user.repository.entity.AuthorityName;
import com.cwtsite.cwt.user.repository.entity.User;
import com.cwtsite.cwt.user.repository.entity.UserProfile;
import com.cwtsite.cwt.user.repository.entity.UserSetting;
import com.cwtsite.cwt.user.view.model.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UserService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, AuthService authService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @Transactional
    public User registerUser(UserRegistrationDto userRegistrationDto) {
        return userRepository.save(map(userRegistrationDto));
    }

    private User map(UserRegistrationDto dto) {
        User user = new User(new UserProfile(), new UserSetting(), AuthorityName.ROLE_USER);

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(authService.createHash(dto.getPassword()));
        user.setActivated(true);

        return user;
    }

    public List<User> getAllOrderedByUsername() {
        return userRepository.findAll(new Sort("username"));
    }
}
