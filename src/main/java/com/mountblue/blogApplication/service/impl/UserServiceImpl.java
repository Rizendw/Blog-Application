package com.mountblue.blogApplication.service.impl;

import com.mountblue.blogApplication.entity.User;
import com.mountblue.blogApplication.repository.UserRepository;
import com.mountblue.blogApplication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(String name, String email, String password) {
        System.err.println("i'm in Registering user");
        System.err.println("passwordEncoder ne ye diya = " + passwordEncoder);

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .isAdmin(false)
                .build();
        return userRepository.save(user);
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser"))
            return null;

        String email = auth.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findById(Long authorId) {
        return userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + authorId));
    }
}
