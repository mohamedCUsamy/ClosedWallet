package com.closedwallet.Service;

import com.closedwallet.Entity.User;
import com.closedwallet.Exception.RequiredFieldException;
import com.closedwallet.Repository.UserRepository;
import com.closedwallet.dto.UserRequest;
import com.closedwallet.dto.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest userRequest)   {
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setName(userRequest.getName());
        user.setPhoneNumber(userRequest.getPhoneNumber());

        userRepository.save(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setResponseCode("200");
        userResponse.setResponseMessage("Success");
        userResponse.setResponseDescription("User Created Hamada");

        return userResponse;

    }





}
