package com.groupfive.ewastemanagement.service.implementation;

import com.groupfive.ewastemanagement.dto.request.PasswordResetDTO;
import com.groupfive.ewastemanagement.dto.request.RefreshTokenDTO;
import com.groupfive.ewastemanagement.dto.request.UserDTO;
import com.groupfive.ewastemanagement.dto.response.ResponseMessage;
import com.groupfive.ewastemanagement.entity.CategoriesAccepted;
import com.groupfive.ewastemanagement.entity.Role;
import com.groupfive.ewastemanagement.entity.User;
import com.groupfive.ewastemanagement.entity.UserDetails;
import com.groupfive.ewastemanagement.exception.BadRequestException;
import com.groupfive.ewastemanagement.exception.InvalidUserException;
import com.groupfive.ewastemanagement.helper.Constants;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.repository.CategoriesAcceptedRepo;
import com.groupfive.ewastemanagement.repository.RoleRepo;
import com.groupfive.ewastemanagement.repository.UserDetailsRepo;
import com.groupfive.ewastemanagement.repository.UserRepo;
import com.groupfive.ewastemanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.groupfive.ewastemanagement.helper.Constants.*;

@Service
public class UserServiceImplementation implements UserService {
    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;
    private final UserDetailsRepo userDetailsRepo;
    private final RoleRepo roleRepo;
    private final CategoriesAcceptedRepo categoriesAcceptedRepo;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImplementation.class);


    @Autowired
    public UserServiceImplementation(PasswordEncoder passwordEncoder, UserRepo userRepo, JwtUtil jwtUtil, UserDetailsRepo userDetailsRepo, RoleRepo roleRepo, CategoriesAcceptedRepo categoriesAcceptedRepo) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.userDetailsRepo = userDetailsRepo;
        this.roleRepo = roleRepo;
        this.categoriesAcceptedRepo = categoriesAcceptedRepo;
    }


    /**
     * This function of service is for finding user by email
     *
     * @param email String parameter
     * @return user details if user is valid
     */
    @Override
    public User findUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }


    /**
     * This function of service is for saving password
     *
     * @param token         String parameter
     * @param passwordResetDTO PasswordModel parameter
     * @return SUCCESS/FAILURE if password is changed successfully
     */
    @Override
    public ResponseEntity<ResponseMessage> savePassword(String token, PasswordResetDTO passwordResetDTO) {
        String email = jwtUtil.getUsernameFromToken(token);
        if (userRepo.findUserByEmail(email) != null) {
            User user = userRepo.findUserByEmail(email);
            LOGGER.info("User with id '{}' is changing Password", user.getEmail());
            user.setPassword(passwordResetDTO.getNewPassword());
            userRepo.save(user);
            LOGGER.info("User with id '{}' is has changed Password", user.getEmail());
            return new ResponseEntity<>(new ResponseMessage(SUCCESS, "Password Saved"), HttpStatus.OK);
        } else {
            LOGGER.error(NO_USER_FOUND);
            return new ResponseEntity<>(new ResponseMessage(FAIL, NO_USER_FOUND), HttpStatus.NOT_FOUND);
        }
    }


    /**
     * This function of the service extracts the user details from the user model and saves it to the database
     *
     * @param userDTO which contains field (firstName, lastName, email, role, mobileNo, password, address1, city, state, pinCode, gstNo, registrationNo)
     * @return Response Entity with status 200 containing the user authorization(JWT)token
     */

    @Override
    public ResponseEntity<ResponseMessage> registerUser(UserDTO userDTO, HttpServletRequest request) {
        ResponseMessage responseMessage = new ResponseMessage();
        LOGGER.info("User with id '{}' is signing up", userDTO.getEmail());
        User checkUser = userRepo.findUserByEmail(userDTO.getEmail());
        User user = new User();
        if (checkUser != null) {
            LOGGER.error("User with id '{}' is already exist can't sign up", userDTO.getEmail());
            responseMessage.setStatus(FAIL);
            responseMessage.setData("Already Registered with same E-Mail Id");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        } else {
            String uid = user.getUid();
            user.setEmail(userDTO.getEmail());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setMobileNo(userDTO.getMobileNo());

            user.setUid(uid);

            user.setAddress1(userDTO.getAddress1());
            user.setCity(userDTO.getCity());
            user.setState(userDTO.getState());
            user.setPinCode(userDTO.getPinCode());


            Set<Role> set = new HashSet<>();

            if (userDTO.getRole().getName().equals(COLLECTOR)) {
                set = registerCollector(userDTO, uid, user);
            } else if (userDTO.getRole().getName().equals(VENDOR)) {
                set = registerVendor(userDTO, uid, user);
            } else {
                if (roleRepo.findRoleByName(CUSTOMER) == null) {
                    set.add(userDTO.getRole());
                } else {
                    set.add(roleRepo.findRoleByName(CUSTOMER));
                }
            }
            user.setRoles(set);
            userRepo.save(user);
            LOGGER.info("User with id '{}' is has registered successfully", user.getEmail());

            responseMessage.setStatus(SUCCESS);
            responseMessage.setData(user);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
    }



    /**
     * This function of service is for refreshing Access Token
     *
     * @param refreshTokenDTO which contains Access Token
     * @return SUCCESS with new access token and refresh token
     */

    @Override
    public ResponseEntity<ResponseMessage> refreshToken(RefreshTokenDTO refreshTokenDTO) {
        String token1 = refreshTokenDTO.getToken();

        String userName = jwtUtil.getUsernameFromToken(token1);
        Map<String, String> token = jwtUtil.createTokenFromUserName(userName, this.userRepo.findUserByEmail(userName));

        return new ResponseEntity<>(new ResponseMessage(SUCCESS, token), HttpStatus.OK);
    }

    /**
     * This function of service is for viewing profile
     *
     * @param request HTTPServletRequest
     * @return Response Entity with status code 200 and displays all the details of user in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> viewProfile(HttpServletRequest request) throws BadRequestException, InvalidUserException {
        String email = request.getHeader(EMAIL);
        ResponseMessage responseMessage = new ResponseMessage();
        if (email == null) {
            LOGGER.error(Constants.EMPTY_EMAIL);
            throw new BadRequestException(EMAIL_CANNOT_BE_EMPTY);
        }
        User user = userRepo.findUserByEmail(email);//cookies

        if (user == null) {
            LOGGER.error(NO_SUCH_USER_FOUND);

            responseMessage.setStatus(FAIL);
            responseMessage.setData(NO_SUCH_USER_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }

        LOGGER.info("User with id '{}' is viewing profile details", user.getEmail());

        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(user);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of service is for editing profile of user
     *
     * @param userDTO which contains field (firstName, lastName, email, role, mobileNo, password, address1, city, state, pinCode, gstNo, registrationNo)
     * @param request   HTTPServletRequest
     * @return Response Entity with status code 200 and display all the details of user in the body
     * @throws BadRequestException  throws exception for empty email in request
     * @throws InvalidUserException throws exception for user not found in records
     */

    @Override
    public ResponseEntity<ResponseMessage> editProfile(UserDTO userDTO, HttpServletRequest request) throws BadRequestException, InvalidUserException {

        String email = request.getHeader(EMAIL);
        if (email == null) {
            LOGGER.error(Constants.EMPTY_EMAIL);
            throw new BadRequestException(EMAIL_CANNOT_BE_EMPTY);
        }
        ResponseMessage responseMessage = new ResponseMessage();
        User user = userRepo.findUserByEmail(email);
        if (user == null) {
            responseMessage.setStatus(FAIL);
            responseMessage.setData(NO_SUCH_USER_FOUND);
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }

        LOGGER.info("User with id '{}' is modifying profile details", user.getEmail());

        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setMobileNo(userDTO.getMobileNo());

        user.setAddress1(userDTO.getAddress1());
        user.setCity(userDTO.getCity());
        user.setState(userDTO.getState());
        user.setPinCode(userDTO.getPinCode());

        Set<Role> roles = user.getRoles();
        Role role = new Role();
        for (Role value : roles) {
            role = value;
        }

        if (userDetailsRepo.findUserByUid(user.getUid()) != null) {
            UserDetails userDetailsCollector = userDetailsRepo.findUserByUid(user.getUid());
            userDetailsCollector.setGstNo(userDTO.getGstNo());
            userDetailsCollector.setRegistrationNo(userDTO.getRegistrationNo());

            if (role.getName().equals(COLLECTOR)) {
                userDetailsCollector.setCategoriesAccepted(userDTO.getCategoriesAcceptedSet());
                userDetailsCollector.setShopTime(userDTO.getShopTime());

            }
            userDetailsRepo.save(userDetailsCollector);
        }

        userRepo.save(user);

        responseMessage.setStatus(SUCCESS);
        responseMessage.setData(user);

        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    /**
     * This function of the service extracts the user details for collector from the user model and saves it to the database
     *
     * @param userDTO which contains field (firstName, lastName, email, role, mobileNo, password, address1, city, state, pinCode, gstNo, registrationNo)
     * @return Response Entity with status 200 containing the user authorization(JWT)token
     */

    public Set<Role> registerCollector(UserDTO userDTO, String uid, User user) {
        Set<Role> set = new HashSet<>();
        UserDetails userDetails = new UserDetails();
        userDetails.setShopTime(userDTO.getShopTime());
        userDetails.setGstNo(userDTO.getGstNo());
        userDetails.setRegistrationNo(userDTO.getRegistrationNo());
        Set<CategoriesAccepted> finalSet = new HashSet<>();

        Set<CategoriesAccepted> categoriesAcceptedSet = userDTO.getCategoriesAcceptedSet();
        for (CategoriesAccepted categoriesAccepted : categoriesAcceptedSet) {
            if (categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(categoriesAccepted.getCategoryAccepted()) == null) {
                finalSet.add(categoriesAccepted);
            } else {
                finalSet.add(categoriesAcceptedRepo.findCategoriesAcceptedByCategoryAccepted(categoriesAccepted.getCategoryAccepted()));
            }
        }
        userDetails.setCategoriesAccepted(finalSet);
        userDetails.setUid(uid);
        userDetails.setUser(user);
        userDetailsRepo.save(userDetails);

        if (roleRepo.findRoleByName(COLLECTOR) == null) {
            set.add(userDTO.getRole());
        } else {
            set.add(roleRepo.findRoleByName(COLLECTOR));
        }

        return set;
    }

    /**
     * This function of the service extracts the user details for collector from the user model and saves it to the database
     *
     * @param userDTO which contains field (firstName, lastName, email, role, mobileNo, password, address1, city, state, pinCode, gstNo, registrationNo)
     * @return Response Entity with status 200 containing the user authorization(JWT)token
     */

    public Set<Role> registerVendor(UserDTO userDTO, String uid, User user) {
        Set<Role> set = new HashSet<>();
        UserDetails userDetails = new UserDetails();
        userDetails.setGstNo(userDTO.getGstNo());
        userDetails.setRegistrationNo(userDTO.getRegistrationNo());
        userDetails.setUid(uid);
        userDetails.setUser(user);
        userDetailsRepo.save(userDetails);


        if (roleRepo.findRoleByName(VENDOR) == null) {
            set.add(userDTO.getRole());
        } else {
            set.add(roleRepo.findRoleByName(VENDOR));
        }
        return set;
    }

}