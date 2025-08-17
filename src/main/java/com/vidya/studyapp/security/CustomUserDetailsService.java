package com.vidya.studyapp.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vidya.studyapp.entity.User;
import com.vidya.studyapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(" CustomUserDetailsService - Loading user: " + username);
        
        User user = userRepository.findByUsername(username)
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        System.out.println(" CustomUserDetailsService - User found: " + user.getUsername());
        System.out.println(" CustomUserDetailsService - User role: " + user.getRole());
        
        String roleAuthority = "ROLE_" + user.getRole().name();
       // String roleAuthority =  user.getRole().name();

        System.out.println(" CustomUserDetailsService - Creating authority: " + roleAuthority);
        
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleAuthority));
        System.out.println(" CustomUserDetailsService - Authorities created: " + authorities);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
