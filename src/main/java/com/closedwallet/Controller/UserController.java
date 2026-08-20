package com.closedwallet.Controller;

import com.closedwallet.Service.UserService;
import com.closedwallet.dto.*;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public RegisterResponse register(
		 @Valid @RequestBody RegisterRequest registerRequest) throws Exception {

		return userService.register(registerRequest);
	}
    @PostMapping("/login")
    public LoginResponse login(
           @Valid @RequestBody LoginRequest loginRequest) throws Exception {

        return userService.login(loginRequest);
    }
	@PutMapping("/updateuser")
	public UpdateResponse updateUser(Authentication authentication,@Valid@RequestBody UpdateRequest updateRequest) throws Exception {
		String email = authentication.getName();
		return userService.updateUser(email,updateRequest);
	}
	@GetMapping("/profile")
	public ProfileResponse getProfile(Authentication authentication) {
		String email = authentication.getName();
		return userService.getProfile(email);
	}
	@PutMapping("/changepassword")
	public ChangePassResponse changePassword(Authentication authentication, @Valid @RequestBody ChangePassRequest changePassRequest) throws Exception {
		String email = authentication.getName();
		return userService.changePassword(email,changePassRequest);
	}

	@PostMapping("/topupwallet")
	public boolean topUpWallet(@RequestBody TopUpRequest topUpRequest, Authentication authentication) {
		String email = authentication.getName();
		return userService.topUpWallet(topUpRequest.getAmount(), email);
	}

	@GetMapping("/wallet")
	public WalletResponse getWallet(Authentication authentication) {
		String email = authentication.getName();
		return userService.getWallet(email);
	}

//	@GetMapping("/users")
//	public String users() {
//		return "Hello users";
//	}

	
}