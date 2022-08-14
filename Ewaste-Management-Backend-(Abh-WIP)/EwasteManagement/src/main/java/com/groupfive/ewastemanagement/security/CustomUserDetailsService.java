package com.groupfive.ewastemanagement.security;


import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepo userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);

        if (user==null)
            throw new UsernameNotFoundException("User not found with email : " + email);

        return UserPrincipal.create(user);
    }

}