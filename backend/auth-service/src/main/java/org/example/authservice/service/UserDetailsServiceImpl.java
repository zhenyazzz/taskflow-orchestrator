package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.authservice.model.User;
import org.example.authservice.model.UserDetailsImpl;
import org.example.authservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        log.info("User: {}", user);
        return new UserDetailsImpl(user);
    }
}
