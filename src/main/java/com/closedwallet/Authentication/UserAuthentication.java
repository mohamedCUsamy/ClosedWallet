package com.closedwallet.Authentication;

import com.closedwallet.Exception.InvalidFormatException;
import com.closedwallet.Exception.RequiredFieldException;
import com.closedwallet.Exception.UserExisitsException;
import com.closedwallet.Repository.UserRepository;
import com.closedwallet.dto.UserRequest;
import com.closedwallet.dto.UserResponse;

public class UserAuthentication {
    private final UserRepository userRepository;

    public UserAuthentication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public boolean isValidEmailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(emailRegex);
    }

    public boolean isValidEgyptianPhoneNumberFormat(String phoneNumber) {
        String phoneRegex = "^01[0125][0-9]{8}$";
        return phoneNumber != null && phoneNumber.matches(phoneRegex);
    }

    public boolean isValidPasswordFormat(String password) {
        String passwordRegex = "^(?=.*\\d).{8,}$";
        return password != null && password.matches(passwordRegex);
    }
    public void validate(UserRequest userRequest)throws Exception{
        if(userRequest.getPassword()==null || userRequest.getPassword().isEmpty()){
            throw new RequiredFieldException("Password Required");
        }
        if(userRequest.getEmail()==null || userRequest.getEmail().isEmpty()){
            throw new RequiredFieldException("Email Required");
        }
        if(userRequest.getName()==null || userRequest.getName().isEmpty()){
            throw new RequiredFieldException("Name Required");
        }
        if(userRequest.getPhoneNumber()==null || userRequest.getPhoneNumber().isEmpty()){
            throw new RequiredFieldException("Phone Number Required");
        }
        if(userRepository.existsByEmail(userRequest.getEmail())){
            throw new UserExisitsException("Email already registered");
        }
        if(userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())){
            throw new UserExisitsException("Phone number already registered");
        }
        if(!isValidEmailFormat(userRequest.getEmail())){
            throw new InvalidFormatException("Invalid Email");
        }
        if(!isValidEgyptianPhoneNumberFormat(userRequest.getPhoneNumber())){
            throw new InvalidFormatException("Invalid phone number");
        }
        if(!isValidPasswordFormat(userRequest.getPassword())){
            throw new InvalidFormatException("Invalid password format");
        }



    }
}
