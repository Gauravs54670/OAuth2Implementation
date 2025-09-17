package com.gaurav.LMS.Service;

import com.gaurav.LMS.Entity.UserEntity;
import com.gaurav.LMS.Repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserService implements UserDetailsService {

    private final UserRepository userRepository;
    public CustomUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = this.userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword() == null ? "" : user.getPassword())
                .authorities("ROLE_USER")
                .build();
    }
}
