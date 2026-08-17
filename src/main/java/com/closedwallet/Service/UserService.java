package com.closedwallet.Service;

import com.closedwallet.Entity.User;
import com.closedwallet.Entity.Wallet;
import com.closedwallet.Exception.InvalidPassOrEmail;
import com.closedwallet.Exception.UserExisitsException;
import com.closedwallet.Repository.UserRepository;
import com.closedwallet.Repository.WalletRepository;
import com.closedwallet.dto.*;
import com.closedwallet.enums.Currency;
import com.closedwallet.enums.KycStatus;
import com.closedwallet.enums.Role;
import com.closedwallet.enums.WalletStatus;
import jakarta.validation.constraints.Null;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public UserService(UserRepository userRepository, WalletRepository walletRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(RegisterRequest registerRequest) throws Exception  {
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserExisitsException("Email already exists");
        }
        if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
            throw new UserExisitsException("Phone number already exists");
        }
        if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new Exception("Passwords do not match");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setRole(Role.USER);
        user.setKycStatus(KycStatus.PENDING);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(user);
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency(Currency.EGP);
        wallet.setStatus(WalletStatus.ACTIVE);
        walletRepository.save(wallet);
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setResponseCode("200");
        registerResponse.setResponseMessage("Success");
        registerResponse.setResponseDescription("User Created Hamada");

        return registerResponse;

    }
    public LoginResponse login(LoginRequest loginRequest) throws Exception {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() ->
                new InvalidPassOrEmail( "Invalid Email or Password"));
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidPassOrEmail("Invalid Email or Password");
        }
        String token = jwtService.generateToken(user.getEmail());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResponseCode("200");
        loginResponse.setResponseMessage("Success");
        loginResponse.setResponseDescription("Login successful");
        loginResponse.setToken(token);

        return loginResponse;

    }
    public UpdateResponse updateUser(String currentUserEmail,UpdateRequest updateRequest) throws Exception {
        User user = userRepository.findByEmail(currentUserEmail).orElseThrow(() -> new RuntimeException("User not found"));
        if(!user.getPhoneNumber().equals(updateRequest.getPhoneNumber())) {
            if(userRepository.existsByPhoneNumber(updateRequest.getPhoneNumber())) {
                throw new UserExisitsException("Phone number already exists");
            }
        }
        user.setName(updateRequest.getName());
        user.setPhoneNumber(updateRequest.getPhoneNumber());
        User updatedUser = userRepository.save(user);
        return new UpdateResponse();
    }


    public ProfileResponse getProfile(String email) {
       User user= userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
       return new ProfileResponse(user.getEmail(),user.getName(),user.getPhoneNumber());
    }
}
