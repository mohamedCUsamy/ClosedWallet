
package com.closedwallet.Controller;

import com.closedwallet.Service.UserService;
import com.closedwallet.dto.UserRequest;
import com.closedwallet.dto.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {
	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}


	@PostMapping("create-user")
	public UserResponse createUser(UserRequest userRequest) {
		return userService.createUser(userRequest);
	}

	@GetMapping("/home")
	public String home() {
		return "Welcome to your closed wallet ";
	}
	


    

}
