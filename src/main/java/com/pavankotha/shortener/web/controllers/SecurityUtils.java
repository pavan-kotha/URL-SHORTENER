package com.pavankotha.shortener.web.controllers;

import com.pavankotha.shortener.domain.entities.User;
import com.pavankotha.shortener.domain.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtils {

    private final UserRepository userRepository;
    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            return userRepository.findByEmail(email).orElse(null);
        }
        return null;
    }
    public Long getCurrentUserId(){
        User user = getCurrentUser();
        return user!=null?user.getId():null;
    }
}
