package com.groupfive.ewastemanagement.service.jwtuserservice;

import com.groupfive.ewastemanagement.entity.userentity.User;
import com.groupfive.ewastemanagement.helper.Constants;
import com.groupfive.ewastemanagement.repository.userrepository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JWTUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User userInfo=userRepo.findByEmail(username);

        if (userInfo==null)
        {
            throw new UsernameNotFoundException(Constants.USER_NOT_FOUND);
        }
        return new JWTUserDetails(userInfo);
    }
}
