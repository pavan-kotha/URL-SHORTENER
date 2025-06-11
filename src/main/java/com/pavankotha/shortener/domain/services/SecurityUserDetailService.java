package com.pavankotha.shortener.domain.services;

import com.pavankotha.shortener.domain.entities.User;
import com.pavankotha.shortener.domain.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    public SecurityUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user= userRepository.findByEmail(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found with email id :"+username));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail()
                ,user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name())));
    }
}
