package com.groupfive.ewastemanagement.security.oauth2;

import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.exception.OAuth2AuthenticationProcessingException;
import com.groupfive.ewastemanagement.repository.UserRepo;
import com.groupfive.ewastemanagement.security.UserPrincipal;
import com.groupfive.ewastemanagement.security.oauth2.user.OAuth2UserInfo;
import com.groupfive.ewastemanagement.security.oauth2.user.OAuth2UserInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepo userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if(!StringUtils.hasLength(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        User user = userRepository.findUserByEmail(oAuth2UserInfo.getEmail());
            if (user!=null){

            user = updateExistingUser(user, oAuth2UserInfo);
        }
        else {
            throw new OAuth2AuthenticationProcessingException("NoUserFound");
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setEmail(oAuth2UserInfo.getEmail());
        return userRepository.save(existingUser);
    }

}
