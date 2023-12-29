package com.myblog8.controller;

import com.myblog8.entity.User;
import com.myblog8.payload.JWTAuthResponse;
import com.myblog8.payload.LoginDto;
import com.myblog8.payload.SignUpDto;
import com.myblog8.repository.UserRepository;
import com.myblog8.security.JwtTokenProvider;
import com.myblog8.util.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private OtpService otpService;

    //http://localhost:8080/api/auth/signin
    @PostMapping("/signin")
    public ResponseEntity<JWTAuthResponse> authenticateUser(@RequestBody LoginDto
                                                                    loginDto){
        Authentication authentication = authenticationManager.authenticate(new
                UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // get token form tokenProvider
        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JWTAuthResponse(token));
    }





    //http://localhost:8080/api/auth/signup
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){

        // add check for username exists in a DB
        Boolean usernameExists = userRepository.existsByUsername(signUpDto.getUsername());
        if(usernameExists){
            return new ResponseEntity<>("Username is already taken!",HttpStatus.BAD_REQUEST);
        }
        // add check for email exists in DB
        Boolean emailExists = userRepository.existsByEmail(signUpDto.getEmail());
        if(emailExists){
            return new ResponseEntity<>("Email is already taken!",HttpStatus.BAD_REQUEST);
        }

        //Otp generation and sending
        otpService.sendOtp("+918223086877");

        // create user object
        User user = new User();
        user.setName(signUpDto.getName());
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        userRepository.save(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }

    //http://localhost:8080/api/auth/verify-otp
    //http://localhost:8080/api/auth/verify-otp?phoneNumber=%2B918223086877&otp=719334
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String phoneNumber, @RequestParam String otp) {
        boolean isVerified = otpService.verifyOtp(phoneNumber, otp);
        if (isVerified) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            if (otpService.isOtpExpired(phoneNumber)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Your OTP has expired");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
            }
        }
    }}