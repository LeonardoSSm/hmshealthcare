package com.medicore.infrastructure.security;

import com.medicore.infrastructure.persistence.user.UserEntity;
import com.medicore.infrastructure.persistence.user.UserJpaRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserJpaRepository userJpaRepository;

    public UserDetailsServiceImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userJpaRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("User inactive");
        }

        String rawRole = user.getRole() == null ? "" : user.getRole().trim().toUpperCase();
        String authority = rawRole.startsWith("ROLE_") ? rawRole : "ROLE_" + rawRole;

        return new User(
            user.getEmail(),
            user.getPasswordHash(),
            List.of(new SimpleGrantedAuthority(authority))
        );
    }
}
