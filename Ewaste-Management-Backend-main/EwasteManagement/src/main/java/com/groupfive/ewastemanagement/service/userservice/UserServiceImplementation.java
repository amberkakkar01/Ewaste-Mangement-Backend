package com.groupfive.ewastemanagement.service.userservice;

import com.groupfive.ewastemanagement.dto.EnvelopeMessage;
import com.groupfive.ewastemanagement.entity.*;
import com.groupfive.ewastemanagement.entity.collectorentity.Collector;
import com.groupfive.ewastemanagement.entity.customerentity.Customer;
import com.groupfive.ewastemanagement.entity.userentity.Role;
import com.groupfive.ewastemanagement.entity.userentity.User;
import com.groupfive.ewastemanagement.entity.vendorentity.Vendor;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.model.PasswordModel;
import com.groupfive.ewastemanagement.model.jwtmodel.JwtRequest;
import com.groupfive.ewastemanagement.model.UserModel;
import com.groupfive.ewastemanagement.model.jwtmodel.JwtResponse;
import com.groupfive.ewastemanagement.repository.collectorrepository.CollectorRepo;
import com.groupfive.ewastemanagement.repository.PasswordResetTokenRepository;
import com.groupfive.ewastemanagement.repository.customerrepository.CustomerRepo;
import com.groupfive.ewastemanagement.repository.userrepository.UserRepo;
import com.groupfive.ewastemanagement.repository.vendorrepository.VendorRepo;
import com.groupfive.ewastemanagement.service.jwtuserservice.JWTUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.groupfive.ewastemanagement.helper.Constants.*;

@Service
public class UserServiceImplementation implements UserService {

    public static final String EXPIRED = "expired";
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUserDetailsService customUserDetailsService;

    @Autowired
    private VendorRepo vendorRepo;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private CollectorRepo collectorRepo;

    @Override
    public User findUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken
                = new PasswordResetToken(user,token);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken
                = passwordResetTokenRepository.findByToken(token);

        if (passwordResetToken == null) {
            return INVALID;
        }

        Calendar cal = Calendar.getInstance();

        if ((passwordResetToken.getExpirationTime().getTime()
                - cal.getTime().getTime()) <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return EXPIRED;
        }

        return VALID;
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }


    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));

        Set<Role>set=user.getRoles();
        Iterator<Role> iterator = set.iterator();
        Role role=new Role();
        while(iterator.hasNext()) {
           role=iterator.next();
        }

        if (role.getName().equals(CUSTOMER))
        {
            Customer customer=customerRepo.findByEmail(user.getEmail());
            customer.setPassword(passwordEncoder.encode(newPassword));
            customerRepo.save(customer);
        }
        else if (role.getName().equals(COLLECTOR))
        {
            Collector collector=collectorRepo.findByEmail(user.getEmail());
            collector.setPassword(passwordEncoder.encode(newPassword));
            collectorRepo.save(collector);
        }
        else if (role.getName().equals(VENDOR)){
            Vendor vendor=vendorRepo.findByEmail(user.getEmail());
            vendor.setPassword(passwordEncoder.encode(newPassword));
            vendorRepo.save(vendor);
        }
        userRepo.save(user);
    }

    @Override
    public ResponseEntity<EnvelopeMessage> registerCustomer(UserModel userModel){

        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        Customer user=new Customer();

        Customer c=customerRepo.findByEmail(userModel.getEmail());

        User e= userRepo.findByEmail(userModel.getEmail());

        if(c!=null) {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData("Customer already registered with same e-mail Id");
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
        else if (e!=null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData("Customer already registered with same e-mail Id in Another Profile");
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
        else {
            user.setEmail(userModel.getEmail());
            user.setFirstName(userModel.getFirstName());
            user.setLastName(userModel.getLastName());

            user.setUid(user.getUid());

            user.setPassword(passwordEncoder.encode(userModel.getPassword()));
            user.setMobileNo(userModel.getMobileNo());
            user.setAddress1(userModel.getAddress1());
            user.setCity(userModel.getCity());
            user.setState(userModel.getState());
            user.setPinCode(userModel.getPinCode());

            customerRepo.save(user);

            User userEntity = new User();
            userEntity.setEmail(userModel.getEmail());
            userEntity.setFirstName(userModel.getFirstName());
            userEntity.setLastName(userModel.getLastName());
            userEntity.setMobileNo(userModel.getMobileNo());

            Set<Role>set=new HashSet<>();
            set.add(userModel.getRole());
            userEntity.setRoles(set);

            userRepo.save(userEntity);
            userEntity.setPassword(passwordEncoder.encode(userModel.getPassword()));

            userRepo.save(userEntity);

            envelopeMessage.setStatus(SUCCESS);
            envelopeMessage.setData(user);
            return new ResponseEntity<>(envelopeMessage ,HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<EnvelopeMessage> registerCollector(UserModel userModel) {
        Collector user=new Collector();
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        Collector c=collectorRepo.findByEmail(userModel.getEmail());
        User e=userRepo.findByEmail(userModel.getEmail());

        if(c!=null) {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData("Collector already registered with same e-mail Id");
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
        else if (e!=null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData("Collector already registered with same e-mail Id in Another Profile");
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
        else {
            user.setEmail(userModel.getEmail());
            user.setFirstName(userModel.getFirstName());
            user.setLastName(userModel.getLastName());
            user.setPassword(passwordEncoder.encode(userModel.getPassword()));
            user.setMobileNo(userModel.getMobileNo());

            user.setUid(user.getUid());

            user.setAddress1(userModel.getAddress1());
            user.setCity(userModel.getCity());
            user.setState(userModel.getState());
            user.setPinCode(userModel.getPinCode());
            user.setShopTime(userModel.getShopTime());
            user.setGstNo(userModel.getGstNo());
            user.setRegistrationNo(userModel.getRegistrationNo());

            user.setCategoriesAcceptedSet(userModel.getCategoriesAcceptedSet());

            collectorRepo.save(user);

            User userEntity = new User();
            userEntity.setEmail(userModel.getEmail());
            userEntity.setFirstName(userModel.getFirstName());
            userEntity.setLastName(userModel.getLastName());
            userEntity.setMobileNo(userModel.getMobileNo());
            userEntity.setPassword(passwordEncoder.encode(userModel.getPassword()));
            Set<Role>set=new HashSet<>();
            set.add(userModel.getRole());
            userEntity.setRoles(set);

            userRepo.save(userEntity);


            envelopeMessage.setStatus(SUCCESS);
            envelopeMessage.setData(user);
            return new ResponseEntity<>(envelopeMessage ,HttpStatus.OK);
        }
    }

    @Override
    public boolean checkValidUser(JwtRequest jwtRequest) {
        User user=userRepo.findByEmail(jwtRequest.getEmail());
        return user != null;

    }

    @Override
    public String savePassword(String token, PasswordModel passwordModel) {
        String result = validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase(VALID)) {
            return INVALID_TOKEN;
        }
        Optional<User> user = getUserByPasswordResetToken(token);
        if(user.isPresent()) {
            changePassword(user.get(), passwordModel.getNewPassword());
            return PASSWORD_RESET_SUCCESSFULLY;
        } else {
            return INVALID_TOKEN;
        }
    }

    @Override
    public String resetPassword(PasswordModel passwordModel, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        User user = findUserByEmail(passwordModel.getEmail());
        String url = "";
        if(user!=null) {
            String token = UUID.randomUUID().toString();
            createPasswordResetTokenForUser(user,token);
            url = passwordResetTokenMail(user,token);
        }
        return url;
    }

    @Override
    public ResponseEntity<EnvelopeMessage> signIn(JwtRequest jwtRequest, HttpServletResponse response) {
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        if (checkValidUser(jwtRequest))
        {
            try {
                this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getEmail(),jwtRequest.getPassword()));

            }
            catch (UsernameNotFoundException | BadCredentialsException e)
            {
                envelopeMessage.setStatus(FAIL);
                envelopeMessage.setData(BAD_CREDENTIALS);
                return new ResponseEntity<>(envelopeMessage, HttpStatus.NOT_FOUND);
            }

            UserDetails userDetails=this.customUserDetailsService.loadUserByUsername(jwtRequest.getEmail());


            String token=this.jwtUtil.generateToken(userDetails);

            Cookie emailCookie=new Cookie(EMAIL,jwtRequest.getEmail());
            Cookie tokenCookie=new Cookie("token",token);
            response.addCookie(emailCookie);
            response.addCookie(tokenCookie);
            //Setting header
            response.setHeader(AUTHORIZATION,BEARER + " " + token);
            response.setHeader(EMAIL,jwtRequest.getEmail());

            envelopeMessage.setStatus(SUCCESS);
            envelopeMessage.setData(new JwtResponse(token));

            return ResponseEntity.ok(envelopeMessage);
        }
        else {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_USER_FOUND);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }

    }

    @Override
    public ResponseEntity<EnvelopeMessage> registerUser(UserModel userModel, HttpServletRequest request) {
        if(userModel.getRole().getName().equals(CUSTOMER)){
            return registerCustomer(userModel);
        }
        else if(userModel.getRole().getName().equals(COLLECTOR))
        {
            return registerCollector(userModel);
        }
        else{
            return registerVendor(userModel);
        }
    }

    @Override
    public ResponseEntity<EnvelopeMessage> signInWithGoogle(String email, HttpServletResponse response) {
        User user=userRepo.findByEmail(email);
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();
        if (user!=null)
        {
            UserDetails userDetails=this.customUserDetailsService.loadUserByUsername(email);

            String token=this.jwtUtil.generateToken(userDetails);

            Cookie emailCookie=new Cookie(EMAIL,email);
            Cookie tokenCookie=new Cookie("token",token);
            response.addCookie(emailCookie);
            response.addCookie(tokenCookie);
            //Setting header
            response.setHeader(AUTHORIZATION,BEARER + " " + token);
            response.setHeader(EMAIL,email);

            envelopeMessage.setStatus(SUCCESS);
            envelopeMessage.setData(new JwtResponse(token));

            return ResponseEntity.ok(envelopeMessage);
        }
        else {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData(NO_USER_FOUND);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
    }

    public void sendResetPasswordMail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "ewastemanagementindia@gmail.com";
        String senderName = E_WASTE_MANAGEMENT;
        String subject = RESET_PASSWORD;
        String content = "Dear [[name]],<br>"
                + "Please click the link below to reset your Password:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">Reset Password</a></h3>"
                + "Thank you,<br>"
                + senderName;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getFirstName());

        content = content.replace("[[URL]]", siteURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

    String passwordResetTokenMail(User user, String token) throws MessagingException, UnsupportedEncodingException {
        String url =
                URL_EMAIL_RESETPASSWORD
                        + token;
        sendResetPasswordMail(user,url);
        return url;
    }

    @Override
    public ResponseEntity<EnvelopeMessage> registerVendor(UserModel userModel) {
        Vendor user=new Vendor();
        EnvelopeMessage envelopeMessage=new EnvelopeMessage();

        Vendor c=vendorRepo.findByEmail(userModel.getEmail());
        User e=userRepo.findByEmail(userModel.getEmail());

        if(c!=null) {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData("Vendor already registered with same e-mail Id");
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
        else if (e!=null)
        {
            envelopeMessage.setStatus(FAIL);
            envelopeMessage.setData("Vendor already registered with same e-mail Id in Another Profile");
            return new ResponseEntity<>(envelopeMessage,HttpStatus.NOT_FOUND);
        }
        else {
            user.setEmail(userModel.getEmail());
            user.setFirstName(userModel.getFirstName());
            user.setLastName(userModel.getLastName());
            user.setPassword(passwordEncoder.encode(userModel.getPassword()));
            user.setMobileNo(userModel.getMobileNo());

            user.setUid(user.getUid());

            user.setAddress1(userModel.getAddress1());
            user.setCity(userModel.getCity());
            user.setState(userModel.getState());
            user.setPinCode(userModel.getPinCode());

            user.setGstNo(userModel.getGstNo());
            user.setRegistrationNo(userModel.getRegistrationNo());

            vendorRepo.save(user);

            User userEntity = new User();
            userEntity.setEmail(userModel.getEmail());
            userEntity.setFirstName(userModel.getFirstName());
            userEntity.setLastName(userModel.getLastName());
            userEntity.setMobileNo(userModel.getMobileNo());
            userEntity.setPassword(passwordEncoder.encode(userModel.getPassword()));
            Set<Role>set=new HashSet<>();
            set.add(userModel.getRole());
            userEntity.setRoles(set);

            userRepo.save(userEntity);


            envelopeMessage.setStatus(SUCCESS);
            envelopeMessage.setData(user);
            return new ResponseEntity<>(envelopeMessage,HttpStatus.OK);
        }
    }

}