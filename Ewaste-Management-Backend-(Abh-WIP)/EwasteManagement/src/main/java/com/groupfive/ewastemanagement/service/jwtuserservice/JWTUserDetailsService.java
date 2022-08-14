package com.groupfive.ewastemanagement.service.jwtuserservice;

import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.helper.Constants;
import com.groupfive.ewastemanagement.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JWTUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public JWTUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userInfo = userRepo.findUserByEmail(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException(Constants.USER_NOT_FOUND);
        }
        userRepo.save(userInfo);
        return new JWTUserDetails(userInfo);
    }
}
