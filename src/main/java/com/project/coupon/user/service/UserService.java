package com.project.coupon.user.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.project.coupon.response.BaseResponse;
import com.project.coupon.constants.CouponConstant;
import com.project.coupon.exceptions.BadApiRequestException;
import com.project.coupon.exceptions.CouponException;
import com.project.coupon.user.entity.RoleEntity;
import com.project.coupon.user.entity.UserEntity;
import com.project.coupon.user.repository.RoleRepository;
import com.project.coupon.user.repository.UserRepository;
import com.project.coupon.user.request.CreateRoleRequest;
import com.project.coupon.user.request.CreateUserRequest;
import com.project.coupon.user.request.LoginRequest;
import com.project.coupon.user.request.ResetPasswordRequest;
import com.project.coupon.user.response.AllRoleResponse;
import com.project.coupon.user.response.RoleDetails;
import com.project.coupon.user.response.RoleResponse;
import com.project.coupon.user.response.UserDetails;
import com.project.coupon.user.response.UserResponse;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	public UserResponse createUser(CreateUserRequest request)
	{
		UserResponse response=new UserResponse();
		String errorMessage=null;
		log.info("Start creating user for : {}", request.getEmail());
		try {
		UserEntity user=userRepository.findByEmail(request.getEmail());
		if(user==null) {
			user=new UserEntity();
			user.setCreateDate(LocalDateTime.now());
			BeanUtils.copyProperties(request, user);
			userRepository.save(user);	
			UserDetails userDetails=new UserDetails();
			BeanUtils.copyProperties(request, userDetails);
			response.setMessage("customer successfully created");
			response.setStatus(CouponConstant.SUCCESS);
			response.setUserDetails(userDetails);
			log.info("Successfully created user : {}", request.getEmail());
			return response;
		}
		else {
			 log.info("User {} already exists", request.getEmail());
			 errorMessage="User already exists";
			
		}
		}
		catch(Exception ex) {
			log.error(CouponConstant.EXCEPTIONOCCURED,ex.getMessage());
			errorMessage=ex.getMessage();
		}
		throw new CouponException(errorMessage);
	}
	
	public RoleResponse createRole(CreateRoleRequest request)
	{
		RoleResponse response=new RoleResponse();
		String errorMessage=null;
		log.info("Start creating role for : {}", request.getRoleName());
		try {
		RoleEntity role=roleRepository.findByRoleName(request.getRoleName());
		if(role==null) {
			role=new RoleEntity();
			role.setCreateDate(LocalDateTime.now());
			BeanUtils.copyProperties(request, role);
			roleRepository.save(role);
			role=roleRepository.findByRoleName(request.getRoleName());
			RoleDetails roleDetails=new RoleDetails();
			BeanUtils.copyProperties(role, roleDetails);
			response.setMessage("role successfully created");
			response.setStatus(CouponConstant.SUCCESS);
			response.setRoleDetails(roleDetails);
			
			log.info("Successfully created role : {}", request.getRoleName());
			return response;
		}
		else {
			 log.info("Role {} already exists", request.getRoleName());
			 errorMessage="Role already exists";
			
		}
		}
		catch(Exception ex) {
			log.error(CouponConstant.EXCEPTIONOCCURED,ex.getMessage());
			errorMessage=ex.getMessage();
		}
		throw new CouponException(errorMessage);
	}
	
	public AllRoleResponse getAllRoles()
	{
		String errorMessage=null;
		try {
		AllRoleResponse response=new AllRoleResponse();
		List <RoleEntity> roles= roleRepository.findAll();
		List <RoleDetails> roleDetails=new ArrayList<>();
		for(RoleEntity role: roles) {
			RoleDetails roleDetail=new RoleDetails();
			BeanUtils.copyProperties(role, roleDetail);
			roleDetails.add(roleDetail);
			
		}
		response.setStatus(CouponConstant.SUCCESS);
		response.setMessage("Role data fetched successfully");
		log.info("Role data fetched successfully");
		response.setRoleDetails(roleDetails);
		return response;
		}
		catch(Exception ex) {
			log.error(CouponConstant.EXCEPTIONOCCURED,ex.getMessage());
			errorMessage=ex.getMessage();
		}
		throw new CouponException(errorMessage);
		
	}
	public RoleResponse updateRole(CreateRoleRequest request)
	{
		RoleResponse response=new RoleResponse();
		String errorMessage=null;
		log.info("Start modifying role for : {}", request.getRoleName());
		try {
		RoleEntity role=roleRepository.findByRoleName(request.getRoleName());
		if(role!=null) {
			
			
			BeanUtils.copyProperties(request, role);
			role.setModifiedDate(LocalDateTime.now());
			roleRepository.save(role);
			RoleDetails roleDetails=new RoleDetails();
			BeanUtils.copyProperties(role, roleDetails);
			response.setMessage("role successfully modified");
			response.setStatus(CouponConstant.SUCCESS);
			response.setRoleDetails(roleDetails);
			
			log.info("Successfully modified role : {}", request.getRoleName());
			return response;
		}
		else {
			 log.info("Role {} does not exist", request.getRoleName());
			 errorMessage="Role does not exist";
			
		}
		}
		catch(Exception ex) {
			log.error(CouponConstant.EXCEPTIONOCCURED,ex.getMessage());
			errorMessage=ex.getMessage();
		}
		throw new CouponException(errorMessage);
	}
	
	public UserResponse updateUser(CreateUserRequest request)
	{
		UserResponse response=new UserResponse();
		String errorMessage=null;
		log.info("Start modifying user for : {}", request.getEmail());
		try {
		UserEntity user=userRepository.findByEmail(request.getEmail());
		if(user!=null) {
			
			user.setModifiedDate(LocalDateTime.now());
			BeanUtils.copyProperties(request, user);
			userRepository.save(user);	
			UserDetails userDetails=new UserDetails();
			BeanUtils.copyProperties(request, userDetails);
			response.setMessage("user successfully updated");
			response.setStatus(CouponConstant.SUCCESS);
			response.setUserDetails(userDetails);
			log.info("Successfully modified user : {}", request.getEmail());
			return response;
		}
		else {
			 log.info("User {} does not exist", request.getEmail());
			 errorMessage="User does not exist";
			
		}
		}
		catch(Exception ex) {
			log.error(CouponConstant.EXCEPTIONOCCURED,ex.getMessage());
			errorMessage=ex.getMessage();
		}
		throw new CouponException(errorMessage);
	}
	
	public UserResponse getUser(String email)
	{
		UserResponse response=new UserResponse();
		String errorMessage=null;
		log.info("Start creating user for : {}", email);
		try {
		UserEntity user=userRepository.findByEmail(email);
		if(user!=null) {
	
			UserDetails userDetails=new UserDetails();
			BeanUtils.copyProperties(user, userDetails);
			response.setMessage("user details successfully fetched");
			response.setStatus(CouponConstant.SUCCESS);
			response.setUserDetails(userDetails);
			log.info("Successfully fetched user : {}", email);
			return response;
			
		}
		else {
			 log.info("User {} already exists", email);
			 errorMessage="Customer does not exist";
			
		}
		}
		catch(Exception ex) {
			log.error(CouponConstant.EXCEPTIONOCCURED,ex.getMessage());
			errorMessage=ex.getMessage();
		}
		throw new CouponException(errorMessage);
	}
	
	public UserResponse login(LoginRequest loginRequest)
	{
		UserResponse response=new UserResponse();
		String errorMessage=null;
		log.info("Start fetching user data for : {}", loginRequest.getEmail());
		try {
		UserEntity user=userRepository.findByEmail(loginRequest.getEmail());
		if(user!=null) {
	
			UserDetails userDetails=new UserDetails();
			BeanUtils.copyProperties(user, userDetails);
			response.setMessage("user details successfully fetched");
			if(loginRequest.getPassword().equalsIgnoreCase(user.getPassword())) {
				response.setStatus(CouponConstant.SUCCESS);
				response.setUserDetails(userDetails);
				log.info("Successfully fetched user : {}", loginRequest.getEmail());
				return response;
			}
			else {
				log.info("Password for user {} does not match", loginRequest.getEmail());
				errorMessage="Password does not match";
			}
			
		}
		else {
			 log.info("User {} does not exists", loginRequest.getEmail());
			 errorMessage="User does not exist";
			
		}
		}
		catch(Exception ex) {
			log.error(CouponConstant.EXCEPTIONOCCURED,ex.getMessage());
			errorMessage=ex.getMessage();
			
		}
		throw new CouponException(errorMessage);
	}
	
	public UserResponse resetPassword(ResetPasswordRequest loginRequest)
	{
		UserResponse response=new UserResponse();
		String errorMessage=null;
		log.info("Start fetching user data for : {}", loginRequest.getEmail());
		try {
		UserEntity user=userRepository.findByEmail(loginRequest.getEmail());
		if(user!=null) {
			UserDetails userDetails=new UserDetails();
			BeanUtils.copyProperties(user, userDetails);			
			if(loginRequest.getPassword().equalsIgnoreCase(user.getPassword())) {
				response.setMessage("user password changed");
				user.setPassword(loginRequest.getNewPassword());
				userRepository.save(user);
				response.setStatus(CouponConstant.SUCCESS);
				response.setUserDetails(userDetails);
				log.info("Successfully reset password for user : {}", loginRequest.getEmail());
				return response;
			}
			else {
				log.info("Password for user {} does not match", loginRequest.getEmail());
				errorMessage="Password does not match";
			}
			
		}
		else {
			 log.info("User {} does not exists", loginRequest.getEmail());
			 errorMessage="User does not exist";
			
		}
		}
		catch(Exception ex) {
			log.error(CouponConstant.EXCEPTIONOCCURED,ex.getMessage());
			errorMessage=ex.getMessage();
			
		}
		throw new CouponException(errorMessage);
	}
}
