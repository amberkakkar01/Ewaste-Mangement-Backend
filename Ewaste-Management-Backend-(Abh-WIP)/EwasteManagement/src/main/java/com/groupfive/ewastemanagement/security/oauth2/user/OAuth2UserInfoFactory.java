package com.groupfive.ewastemanagement.security.oauth2.user;

import com.groupfive.ewastemanagement.exception.OAuth2AuthenticationProcessingException;
import com.groupfive.ewastemanagement.dto.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory(){

    }

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        }else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}
