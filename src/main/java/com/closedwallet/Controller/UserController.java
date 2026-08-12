package com.closedwallet.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    @GetMapping("/users")
	public String users() {
		return "Hello users";
	}

	@GetMapping("/")
	public String home() {
		return "Welcome to your closed wallet ";
	}
	


    
}
