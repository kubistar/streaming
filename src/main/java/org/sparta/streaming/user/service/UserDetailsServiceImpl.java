package org.sparta.streaming.user.service;


import lombok.RequiredArgsConstructor;
import org.sparta.streaming.user.entity.User;
import org.sparta.streaming.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String useremail) throws UsernameNotFoundException {
        User user = userRepository.findByUseremail(useremail)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found " + useremail));

        return new UserDetailsImpl(user);
    }
}