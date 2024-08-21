package com.project.coupon.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.coupon.response.BaseResponse;
import com.project.coupon.user.request.CreateRoleRequest;
import com.project.coupon.user.request.CreateUserRequest;
import com.project.coupon.user.request.LoginRequest;
import com.project.coupon.user.request.ResetPasswordRequest;
import com.project.coupon.user.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	
	@Autowired
	UserService service;
	
	@PostMapping("/createrole")
	public BaseResponse createRole(@RequestBody CreateRoleRequest req)
	{
		return service.createRole(req);
	}
	
	@PostMapping("/updaterole")
	public BaseResponse updateRole(@RequestBody CreateRoleRequest req)
	{
		return service.updateRole(req);
	}
	
	@GetMapping("/getallroles")
	public BaseResponse getAllRoles()
	{
		return service.getAllRoles();
	}
	
	@PostMapping("/createuser")
	public BaseResponse createUser(@RequestBody CreateUserRequest req)
	{
		return service.createUser(req);
	}
	
	@PostMapping("/updateuser")
	public BaseResponse updateUser(@RequestBody CreateUserRequest req)
	{
		return service.updateUser(req);
	}
	
	@GetMapping("/getuser/{email}")
	public BaseResponse getUser(@PathVariable String email)
	{
		return service.getUser(email);
	}
	
	@PostMapping("/login")
	public BaseResponse login(@RequestBody LoginRequest loginRequest)
	{
		return service.login(loginRequest);
	}
	
	@PostMapping("/resetpassword")
	public BaseResponse resetPassword(@RequestBody ResetPasswordRequest loginRequest)
	{
		return service.resetPassword(loginRequest);
	}
}
